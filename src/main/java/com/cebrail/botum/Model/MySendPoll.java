package com.cebrail.botum.Model;

import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MySendPoll extends SendPoll {
    public Integer id;
    public Integer quizId;
    public  List<MyPollOption> myOptions;


    public MySendPoll() {
        super();
        this.myOptions = new ArrayList<>();
    }
    public MySendPoll(Integer quizId) {
        this.quizId = quizId;
    }
    public MySendPoll(Integer id, Integer quizId) {
        this.id = id;
        this.quizId = quizId;
    }

    public MySendPoll(String chatId, String question, List<String> options, Integer quizId) {
        super(chatId, question, options);
        this.myOptions = this.getMyOptions();
        this.quizId = quizId;
    }

    public MySendPoll(Long chatId, String question, List<String> options,Integer quizId) {
        super(chatId, question, options);
        this.quizId = quizId;
    }


    public void setMyOptions(List<MyPollOption> options) {
        this.myOptions = options;
        List<String> theOptions = new ArrayList<>();
        for(MyPollOption mpo: options) {
            theOptions.add(mpo.getOptionContent());
        }
        setOptions(theOptions);
    }

    public List<MyPollOption> getMyOptions() {
        return this.getOptions().stream().map((strOpt) -> {
            return new MyPollOption(strOpt);
        }).collect(Collectors.toList());
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void addToOptions(MyPollOption pollOption) {
        if(myOptions != null) {
            myOptions.add(pollOption);
            List<String> ooptions = super.getOptions();
            ooptions.add(pollOption.getOptionContent());
            super.setOptions(ooptions);
        }
        else {
            System.err.println("eklenmedi");
            boolean f = this.myOptions == null? true: false;
            if(f) {
                System.err.println("options null");
            }
            else
            {
                System.err.println("options null degil");
            }
        }
    }

    @Override
    public String toString() {
        return "MySendPoll{\n" +
                "    id='" + id + "\n" +
                "    quizId='" + quizId + "\n" +
                "    options=" + myOptions.toString() + "\n"+
                '}';
    }
}
