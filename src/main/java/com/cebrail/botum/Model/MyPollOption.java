package com.cebrail.botum.Model;

public class MyPollOption {
    public Integer id;
    public Integer mySendPollId;
    public String optionContent;

    public MyPollOption() {
    }

    public MyPollOption(String optionContent) {
        this.optionContent = optionContent;
    }

    public Integer getMySendPollId() {
        return mySendPollId;
    }

    public MyPollOption setMySendPollId(Integer mySendPollId) {
        this.mySendPollId = mySendPollId;
        return this;
    }

    public String getOptionContent() {
        return optionContent;
    }

    public MyPollOption setOptionContent(String optionContent) {
        this.optionContent = optionContent;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MyPollOption{ \n" +
                "    id='" + id + "\n" +
                "    mySendPollId='" + mySendPollId + "\n" +
                "    optionContent='" + optionContent + "\n" +
                '}';
    }
}
