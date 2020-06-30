package com.cebrail.botum;
import com.cebrail.botum.DTO.Entry.EntryRepositoryImpl;
import com.cebrail.botum.DTO.Quiz.QuizRepository;
import com.cebrail.botum.Exceptions.HataliEntryException;
import com.cebrail.botum.Exceptions.QuizIsFinishedException;
import com.cebrail.botum.Model.Entry;
import com.cebrail.botum.Model.MyPollOption;
import com.cebrail.botum.Model.MySendPoll;
import com.cebrail.botum.Model.Quiz;

import com.cebrail.botum.Util.DistributedRandomNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumb;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonPollType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:config/credentials.properties")
public class Botum extends TelegramLongPollingBot implements EnvironmentAware {
    public static Environment properties;


    @Override
    public void setEnvironment(Environment environment) {
        this.properties = environment;
    }


    @Autowired
    public EntryRepositoryImpl entryRepository;

    @Autowired
    public QuizRepository quizRepository;

    public Botum() {

    }

    public Botum(DefaultBotOptions options) {
        super(options);
    }


    @Override
    public void onUpdatesReceived(List<Update> updates) {
        for(Update up: updates) {

            System.out.println("Update Received: "+up.toString());

            if(up.hasPollAnswer()) {
                onPollAnswer(up);
            }
            else if(up.hasMessage()) {
                String userMessage = up.getMessage().getText();

                if(userMessage.startsWith("/quiz")) {
                    quiz(up);
                }
                else if(userMessage.startsWith("/entry")) {
                        entry(up);
                }
                else if(userMessage.startsWith("/goster")){
                        goster(up);
                }
                else if(userMessage.startsWith("/sil")) {
                        sil(up);
                }
                else if(userMessage.startsWith("/kelime-duzenle")) {
                    sendMessage(up.getMessage().getChatId(), "Kelime düzenleme henüz desteklenmiyor");
                    //duzenle(update);
                }
                else if(userMessage.startsWith("/anlam-duzenle")) {
                    sendMessage(up.getMessage().getChatId(), "Anlam düzenleme henüz desteklenmiyor");
                    //anlam_duzenle(update)
                }
                else if(userMessage.startsWith("/q_goster")) {
                    quizlerimiGoster(up);
                }
                else if(userMessage.startsWith("/bilgi")) {
                    kelimeBilgileri(up);
                }
                else if(userMessage.startsWith("/help")) {
                    showHelp(up);
                }
                else if(userMessage.startsWith("/deleteAll")) {
                    onDeleteAll(up);
                }
                else if(userMessage.startsWith("/button")){
                    sendMessage(up.getMessage().getChatId(), "Buton henüz desteklenmiyor");
                    //onButtonRequested(up);
                }
                else if(userMessage.startsWith("/inline")) {
                    sendMessage(up.getMessage().getChatId(), "Inline henüz desteklenmiyor");
                    //onInlineRequested(up);
                }
                else if(userMessage.startsWith("/closeinline")) {
                    sendMessage(up.getMessage().getChatId(), "Bu özellik henüz desteklenmiyor");
                    //removeReplyKeyboard(up);
                }
                else if(userMessage.startsWith("/force")) {
                    onForceRequested(up);
                }
            }

            else if (up.hasCallbackQuery()) {
                onCallbackQuery(up);
            }
        }
    }

    private void onPollAnswer(Update up) {
        PollAnswer pollAnswer = up.getPollAnswer();
        Integer selectedOption = pollAnswer.getOptionIds().get(0);
        User user = pollAnswer.getUser();
        String userId = user.getId().toString();


        Quiz currentQuiz = retreiveCurrentQuiz(userId);
        if(currentQuiz==null) {
            System.err.println("quiz bitmiş demektir, line 125");
        }

        MySendPoll mySendPoll = currentQuiz.getTest()
                .get(currentQuiz.getTestingHangiSorusundayiz());
        System.err.println("Current quizid = " + currentQuiz.getId());
        Integer currentCorrectOptionId = mySendPoll.getCorrectOptionId();
        String sorulanKelime = mySendPoll.getQuestion();

        //cevabı doğru  mu değil mi onu buluyoruz.
        cevabinDogrulugunuKontrolEt(pollAnswer, currentQuiz, currentCorrectOptionId,sorulanKelime);

        System.err.println("Current testinghangisorusundayiz: " + currentQuiz.getTestingHangiSorusundayiz());
        if(!currentQuiz.isEnded()) {
            try {
                System.err.println("Sonraki sorudayiz");
                SendPoll sonrakiSoru=( SendPoll) currentQuiz.sonrakiSoruyaGec();
                quizRepository.sonrakiSoruyaGec(currentQuiz.getId(), currentQuiz.getTestingHangiSorusundayiz());
                System.err.println("sonraki soruya geçtikten sonraki testinhangisrsndayız: " + currentQuiz.getTestingHangiSorusundayiz());
                execute(sonrakiSoru);

            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch ( QuizIsFinishedException e) {
                quizRepository.sonrakiSoruyaGec(currentQuiz.getId(), currentQuiz.getTestingHangiSorusundayiz());
                sendMessage(userId, "Test bitti");
                //Burada testin sonuçlarını göster, toplam doğru sayısı, toplam yanlış sayısı, ne kadar sürede bitirildiği falan.
                e.printStackTrace();
            }
        }
        else {
            currentQuiz.setEnded(true);
            quizRepository.update(currentQuiz);
            sendMessage(userId, "Test bitti");
        }
    }

    public Quiz retreiveCurrentQuiz(String userChatId) {
        Quiz currentQuiz = null;
        for(Quiz q: quizRepository.getAllByUserId(userChatId))
        {
            if(q.getUserId().equals(userChatId) && !q.isEnded()) {
                currentQuiz = q;
            }
        }
        return currentQuiz;
    }

    public void cevabinDogrulugunuKontrolEt(PollAnswer pollAnswer, Quiz currentQuiz, Integer currentCorrectOptionId, String sorulanKelime) {
        String userId = pollAnswer.getUser().getId().toString();
        if(pollAnswer.getOptionIds().get(0).equals(currentCorrectOptionId)) {
            sendMessage(userId, "doğru");

            entryRepository.getEntriesByUserChatId(userId).forEach((entry) -> {
                if(entry.getKelime().equals(sorulanKelime)) {
                    entry.oneMoreCorrectAnswer();
                    entryRepository.updateEntry(entry);
                }
            });
            //quizde doğru cevaplanan sayısı bir artmıştır, dolayısıyla bir arttırıyoruz.
            currentQuiz.setToplamDogruSayisi(currentQuiz.getToplamDogruSayisi()+1);
        }
        else {
            sendMessage(userId, "yanlış");
            entryRepository.getEntriesByUserChatId(userId).forEach((entry) -> {
                if(entry.getKelime().equals(sorulanKelime)) {
                    entry.oneMoreInCorrectAnswer();
                    entryRepository.updateEntry(entry);
                }
            });
            //quizde yanlış cevaplanan soru sayısı bir artmıştır, dolayısıyla bir arttırıyoruz.
            currentQuiz.setToplamYanlisSayisi(currentQuiz.getToplamYanlisSayisi()+1);
            quizRepository.update(currentQuiz);//quizi update ediyoruz ki kayıt altına alınsın.
        }
    }

    public void quiz(Update update)
    {
        //TODO: Bir kullanıcıya ait halihazırda bitmemiş bir quiz varsa, yenisi oluşturulamaz. Kullanıcıya hata mesajı gönder
        //daha önce hiç kelime eklenmemişse buranın çalışmaması gerekir. database'de kelime olmadığı için hata veriyor.

        //yeni bir quiz başlatmadan önce veritabanındaki tüm quizlerin kapalı olduğundan emin olmak için
        //hepsini kapatıyoruz.
        quizRepository.closeAllQuizzes(update.getMessage().getChatId().toString());


        Quiz newQuiz = new Quiz(update.getMessage().getChatId().toString(), quizOlustur(update));

        Integer generatedId = quizRepository.save(newQuiz);
        newQuiz.setId(generatedId);
        //ilk soruyu gönder
        try {
            execute((SendPoll) newQuiz.sonrakiSoruyaGec());
            quizRepository.update(newQuiz);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (QuizIsFinishedException e) {
            e.printStackTrace();
        }
    }

    public List<MySendPoll> quizOlustur(Update update) {
        List<MySendPoll> list = new ArrayList<>();
        Long userChatId = update.getMessage().getChatId();

        for(int i = 0; i<entryRepository.getEntriesByUserChatId(userChatId.toString()).size(); i++) {
            MySendPoll mySendPoll = pollOlustur(update);
            list.add(mySendPoll);
        }
        return list;
    }

    public MySendPoll pollOlustur(Update update) {

        //TODO: Option sayısı 2 ile 10 arasında olmalı
        //TODO: Sadece bir veya iki tane kelime varsa quiz oluşturulamasın.
        List<String> soru               = new ArrayList<>();
        Long userChatId                 = update.getMessage().getChatId();

        List<Entry> userEntries         = entryRepository.getEntriesByUserChatId(userChatId.toString());

        Integer indexOfTheQuestionEntry = getRandomNumberInRange(0, userEntries.size()-1);

        //random üretilen indexOfTheQuestionEntry indexinin karşılığı olan entry, isteği yapan user'a ait olana kadar
        //random index üret.

        /**
         * Tüm Kelimelerin distribution'unu hesapla, sonra bu hesaplamaları DistributedRandomNumberGenerator'a besle
         * random bi numara al.
         *
         */
        DistributedRandomNumberGenerator generator = new DistributedRandomNumberGenerator();

        Double totalPercentage = userEntries.stream().collect(Collectors.summingDouble(Entry::getCorrectnesPercentage));
        for(Entry e: userEntries) {
            generator.addNumber(userEntries.indexOf(e), 1-e.getCorrectnesPercentage()/totalPercentage);
        }


        /**
         *
         *
         */

        indexOfTheQuestionEntry = generator.getDistributedRandomNumber();

        Entry entry                      = userEntries.get(indexOfTheQuestionEntry);
        soru.add(entry.getAnlami());

        generateOptions(soru, indexOfTheQuestionEntry, userChatId, userEntries.size());

        Collections.shuffle(soru);

        Integer correctAnswer = soru.indexOf(entry.getAnlami());


        List<MyPollOption> myPollOptions = new ArrayList<>();
        for(String s: soru) {
            MyPollOption myPollOption = new MyPollOption();
            myPollOption.setOptionContent(s);
            myPollOptions.add(myPollOption);
        }

        MySendPoll mypoll =(MySendPoll) new MySendPoll().setQuestion(entry.getKelime())
                .setAllowMultipleAnswers(false)
                .setAnonymous(false)
                .setClosed(false)
                .setChatId(update.getMessage().getChatId());
        mypoll.setMyOptions(myPollOptions);
        mypoll.setType("quiz");
        mypoll.setCorrectOptionId(correctAnswer);

        return mypoll;

    }
    private void generateOptions(List<String> soru, Integer indexOfTheQuestionEntry, Long userChatId, Integer dbSize) {
        List<Entry> entries =  entryRepository.getEntriesByUserChatId(userChatId.toString());

        ArrayList<Integer> indexes = new ArrayList<>();
        indexes.add(indexOfTheQuestionEntry);
        Integer theIndex = getRandomNumberInRange(0,dbSize-1);

        while(soru.size()<=dbSize-1 && soru.size() <=8) {
            if(!indexes.contains(theIndex)) {
                soru.add(entries.get(theIndex).getAnlami());
                indexes.add(theIndex);
            }
            else {
                theIndex = getRandomNumberInRange(0, dbSize-1);
            }
        }
    }
    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public void entry(Update update){
        String userMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if(entryRepository.exists(userMessage.substring(6).trim(), chatId.toString())){

        }
        Entry entry = new Entry();
        entry.setUserChatId(chatId);
        try {
            entry.setKelimeAndAnlami(userMessage.substring(6));
            //TODO: entry tarihini set edecek kodu yaz.
            Instant instant = Instant.ofEpochMilli(update.getMessage().getDate());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            entry.setTarihi(localDateTime);
            if(entryRepository.exists(entry.getKelime(), entry.getUserChatId().toString())) {
                sendMessage(chatId.toString(), "Kelime halihazırda var, değiştirmek için /goster + /sil(kelimeİd)");
                return;
            }
            /**/                entryRepository.saveEntry(entry);

            String botSuccessMessage = "Kelime: " + entry.getKelime() + "\nAnlami: "+ entry.getAnlami();
            sendMessage(chatId, botSuccessMessage);
        } catch (HataliEntryException e) {
            e.printStackTrace();
            sendMessage(chatId, "Lutfen kelime ile anlamini # ile ayiriniz");
        }
    }

    public void goster(Update update) {
        String entries =  "";
        for(Entry e:entryRepository.getEntriesByUserChatId(update.getMessage().getChatId().toString())) {
                entries += e.getId() + "\n" + e.getKelime() + "\n" + e.getAnlami() + "\n" +
                        e.getTarihi().toString() + "\n\n";
        }
        if(entries == "") {
            sendMessage(update.getMessage().getChatId(), "Gösterecek kelime yok");
            return;
        }
        sendMessage(update.getMessage().getChatId(), entries);
    }

    public void sil(Update update) {
        String message = update.getMessage().getText().substring(4).trim();
        String userChatId = update.getMessage().getChatId().toString();
        Integer deletedEntryCount = entryRepository.deleteBy("kelime", message, userChatId);
        sendMessage(userChatId, ""+deletedEntryCount+" kelime silindi.");
    }

    public void quizlerimiGoster(Update update) {
        String id = update.getMessage().getChatId().toString();
        StringBuilder quizInfo = new StringBuilder();
        for(Quiz q: quizRepository.getAllByUserId(id)) {
            quizInfo.append("Quiz Id: " + q.getId() + "\n");
            quizInfo.append("Tarih: henüs desteklenmiyor \n");
            quizInfo.append("Doğru sayısı: " + q.getToplamDogruSayisi()+"\n");
            quizInfo.append("Yanlış Sayısı: " + q.getToplamYanlisSayisi()+"\n\n");
        }
        sendMessage(id, quizInfo.toString());
    }

    public void kelimeBilgileri(Update update) {
        String kelime = update.getMessage().getText().substring(6).trim();
        String userChatId = update.getMessage().getChatId().toString();
        Entry entry = entryRepository.getEntryByKelime(kelime, userChatId);

        if(entry == null) {
            sendMessage(update.getMessage().getChatId().toString(), "Böyle bir kelime yok");
        }
        else {

            String newS = String.format("%.2f", entry.getCorrectnesPercentage()*100);
            newS = "%"+newS;
            sendMessage(userChatId, "Percentage: "+newS +
                    "\nCorrect Count: " + entry.getCorrectAnswerCount() +
                    "\nIncorrect Count: " + entry.getIncorrectAnswerCount());
        }
    }

    public void onButtonRequested(Update up) {
        SendMessage message = new SendMessage();
        message.setChatId(up.getMessage().getChatId());
        message.setText("Custom message text");

        // Create ReplyKeyboardMarkup object
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        // Create the keyboard (list of keyboard rows)
        List<KeyboardRow> keyboard = new ArrayList<>();
        // Create a keyboard row
        KeyboardRow row = new KeyboardRow();

        // Set each button, you can also use KeyboardButton objects if you need something else than text
        KeyboardButtonPollType buttonPollType = new KeyboardButtonPollType();
        buttonPollType.setType("quiz");
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setRequestPoll(buttonPollType);
        keyboardButton.setText("make quiz");

        KeyboardRow newRow = new KeyboardRow();
        newRow.add(keyboardButton);
        keyboard.add(newRow);


        row.add("Say Hello");
        row.add("Say Merhaba");
        row.add("Say Çavayi Başi");

        // Add the first row to the keyboard
        keyboard.add(row);
        // Create another keyboard row
        row = new KeyboardRow();
        // Set each button for the second line
        row.add("Marda tonne te fenkerdo");
        row.add("Seri xirre me");
        row.add("eheheh");
        // Add the second row to the keyboard
        keyboard.add(row);
        // Set the keyboard to the markup
        keyboardMarkup.setKeyboard(keyboard);
        // Add it to the message
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onInlineRequested(Update up) {
        SendMessage message = new SendMessage();
        message.setChatId(up.getMessage().getChatId()).setText("CustomMessage Text");

        InlineKeyboardButton keyboardButton = new InlineKeyboardButton();
        keyboardButton.setText("Hello");
        keyboardButton.setCallbackData("callbackData");

        List<InlineKeyboardButton> buttons = new ArrayList<>();
        buttons.add(keyboardButton);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(buttons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void removeReplyKeyboard(Update up) {
        ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
        SendMessage message = new SendMessage();
        message.setReplyMarkup(remove);
        message.setChatId(up.getMessage().getChatId());
        message.setText("keyboard should be removed");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onForceRequested(Update up) {
        ForceReplyKeyboard replyKeyboard = new ForceReplyKeyboard();
        replyKeyboard.setSelective(false);

        SendMessage message = new SendMessage();
        message.setText("forcedreply").setChatId(up.getMessage().getChatId());
        message.setReplyMarkup(replyKeyboard);



        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onCallbackQuery(Update up) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(up.getCallbackQuery().getId());
        answerCallbackQuery.setShowAlert(true);
        answerCallbackQuery.setCacheTime(10);
        answerCallbackQuery.setText("Tebrikler bildiniz");

        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void showHelp(Update update) {
        String helpMessage = "*/help*\n"+
                "Bu help mesajını gösterir\n\n"+
                "*/entry kelime # anlamı*\n" +
                "Bir kelime ile anlamını kaydetmenizi sağlar. <kelime> yerine kelimenizi, <anlam> yerine kelimenin anlamını yazabilirsiniz.\n\n" +
                "*/goster*\n" +
                "Şu ana kadar kaydettiğiniz tüm kelimeleri gösterir.\n\n" +
                "*/sil id*\n" +
                "id kısmında belirttiğiniz id'ye sahip kelimeyi siler\n\n" +
                "*/quiz*\n" +
                "Size ait kelimelerle size quiz yapar\n\n" +
                "*/q_goster*\n" +
                "Quizleriizi gösteirr\n\n" +
                "*/bilgi kelime*\n" +
                "kelime'ye ait bilgileri gösterir";
        sendMessage(update.getMessage().getChatId(), helpMessage);
    }

    public void onDeleteAll(Update up) {
        String userId = up.getMessage().getChatId().toString();
        entryRepository.deleteAllEntriesOfUserId(userId);
        sendMessage(userId, "tum kelimeleriniz silindi :/");
    }

    public synchronized void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
            System.out.println();
        } catch (TelegramApiException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }


    public synchronized void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
            System.out.println();
        } catch (TelegramApiException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        System.out.println("onUpdateRecieved");
    }

    @Override
    public String getBotUsername() {
        return properties.getProperty("mybotusername");
    }

    @Override
    public String getBotToken() {
        return properties.getProperty("mybottoken");
    }

    @Override
    public Boolean execute(SetStickerSetThumb setStickerSetThumb) throws TelegramApiException {
        return null;
    }
}
