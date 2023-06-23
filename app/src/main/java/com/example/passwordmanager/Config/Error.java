package com.example.passwordmanager.Config;

public class Error {
    private String message = new String();
    private String suggestion = new String();

    public Error(String message, String suggestion){
        this.message = message;
        this.suggestion = suggestion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
