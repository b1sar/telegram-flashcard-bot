package com.cebrail.botum.Model;


import com.cebrail.botum.Exceptions.HataliEntryException;

import java.time.LocalDateTime;

public class Entry {
    private String kelime;
    private String anlami;
    private LocalDateTime tarihi;
    private Long id;
    private Long userChatId;

    private Integer howManyTimesAsked      =0;//kaç defa soruldu
    private Integer correctAnswerCount     =0;//doğru cevaplanma sayısı
    private Integer incorrectAnswerCount   =0;//yanlış cevaplanma sayısı
    private Double correctnesPercentage    =0.0;//doğru bilinme yüzdesi

    public Entry(){
        this.kelime = null;
        this.anlami = null;
        this.tarihi = null;
    }
    public Entry(String kelime, String anlami, LocalDateTime tarihi, Long id) {
        this.kelime = kelime;
        this.anlami = anlami;
        this.tarihi = tarihi;
        this.id     = id;
    }

    public Entry(String kelime, String anlami, Long userChatId) {
        this.kelime = kelime;
        this.anlami = anlami;
        this.userChatId = userChatId;
    }

    public void setKelimeAndAnlami(String kelimeanlam) throws HataliEntryException {
        if(!kelimeanlam.contains("#")){
            throw new HataliEntryException("Kelime ile anlamini # ile ayirmaniz gerekiyor");
        }
        else
        {
            Integer index = kelimeanlam.indexOf("#");
            setKelime(kelimeanlam.substring(0,index).trim());
            setAnlami(kelimeanlam.substring(index+1).trim());
        }
    }
    public String getKelime() {
        return kelime;
    }

    public void setKelime(String kelime) {
        this.kelime = kelime;
    }

    public String getAnlami() {
        return anlami;
    }

    public void setAnlami(String anlami) {
        this.anlami = anlami;
    }

    public LocalDateTime getTarihi() {
        return tarihi;
    }

    public void setTarihi(LocalDateTime tarihi) {
        this.tarihi = tarihi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserChatId() {
        return userChatId;
    }

    public void setUserChatId(Long userChatId) {
        this.userChatId = userChatId;
    }

    public Integer getHowManyTimesAsked() {
        return howManyTimesAsked;
    }

    public void setHowManyTimesAsked(Integer howManyTimesAsked) {
        this.howManyTimesAsked = howManyTimesAsked;
    }

    public Integer getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public void setCorrectAnswerCount(Integer correctAnswerCount) {
        this.correctAnswerCount = correctAnswerCount;
        calculatePercentage();
    }

    public Integer getIncorrectAnswerCount() {
        return incorrectAnswerCount;
    }

    public void setIncorrectAnswerCount(Integer incorrectAnswerCount) {
        this.incorrectAnswerCount = incorrectAnswerCount;
        calculatePercentage();
    }

    public Double getCorrectnesPercentage() {
        return correctnesPercentage;
    }

    public void setCorrectnesPercentage(Double correctnesPercentage) {
        this.correctnesPercentage = correctnesPercentage;
    }

    //TODO: division by zero gibi hataları ayıkla
    //Not: Double tipindeki sayılar 0'a bölündüklerinde exception atmazlar
    //çünkü +infinity -infinity veya NaN gibi değerler alırlar. bu değerleri de
    //aşağıda if ile kontrol ettim.
    public void calculatePercentage() {
        this.correctnesPercentage = Double.valueOf(this.correctAnswerCount)/Double.valueOf(this.howManyTimesAsked);
        if(Double.isNaN(this.correctnesPercentage) || !Double.isFinite(this.correctnesPercentage) || Double.isNaN(this.correctnesPercentage)) {
            this.correctnesPercentage = 0.0;
        }
    }

    public void oneMoreCorrectAnswer(){
        this.correctAnswerCount++;
        askedOneMoreTime();
    }
    public void oneMoreInCorrectAnswer(){
        this.incorrectAnswerCount++;
        askedOneMoreTime();
    }
    public void askedOneMoreTime(){
        this.howManyTimesAsked++;
        calculatePercentage();
    }


}
