package com.cebrail.botum.Model;

import com.cebrail.botum.Exceptions.QuizIsFinishedException;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private Integer id;
    private String userId;
    private Integer testingHangiSorusundayiz = 0;
    public List<MySendPoll> test = new ArrayList<>();
    public Boolean ended = false;

    public Integer toplamDogruSayisi = 0;
    public Integer toplamYanlisSayisi = 0;

    public Quiz(){};

    public Quiz(String userId, List<MySendPoll> test) {
        this.userId = userId;
        this.test = test;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getTestingHangiSorusundayiz() {
        return testingHangiSorusundayiz;
    }

    public void setTestingHangiSorusundayiz(Integer testingHangiSorusundayiz) {
        this.testingHangiSorusundayiz = testingHangiSorusundayiz;
    }

    public MySendPoll sonrakiSoruyaGec() throws QuizIsFinishedException {
        if(this.testingHangiSorusundayiz< this.test.size()-1) {
            return this.test.get(++this.testingHangiSorusundayiz);
        }
        else
        {
            this.testingHangiSorusundayiz = -1;
            this.ended = true;
            throw new QuizIsFinishedException("Quiz bitti o yüzden sonraki soruya gecilemez.");
        }
    }

    public Boolean quizDevamEdiyorMu() {
        if(!(this.testingHangiSorusundayiz<=this.test.size()-1))
        {
            ended=true;
            return false;
        }
        else return true;
    }

    public List<MySendPoll> getTest() {
        return test;
    }

    public void setTest(List<MySendPoll> test) {
        this.test = test;
    }

    public Boolean isEnded() {
        return ended;
    }

    public void setEnded(Boolean ended) {
        this.ended = ended;
    }

    public Boolean getEnded() {
        return ended;
    }

    public Integer getToplamDogruSayisi() {
        return toplamDogruSayisi;
    }

    public void setToplamDogruSayisi(Integer toplamDogruSayisi) {
        this.toplamDogruSayisi = toplamDogruSayisi;
    }

    public Integer getToplamYanlisSayisi() {
        return toplamYanlisSayisi;
    }

    public void setToplamYanlisSayisi(Integer toplamYanlisSayisi) {
        this.toplamYanlisSayisi = toplamYanlisSayisi;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void addToTest(MySendPoll mySendPoll) {
        this.test.add(mySendPoll);
    }

    @Override
    public String toString() {
        return "Quiz{" +
                "   id='" + id + "\n" +
                "   userId='" + userId + "\n" +
                "   testingHangiSorusundayiz=" + testingHangiSorusundayiz + "\n"+
                "   ended=" + ended + "\n" +
                "   toplamDogruSayisi=" + toplamDogruSayisi + "\n"+
                "   toplamYanlisSayisi=" + toplamYanlisSayisi + "\n"+
                "   test=" + test.toString() + "\n"+
                "}\n\n";
    }
}
