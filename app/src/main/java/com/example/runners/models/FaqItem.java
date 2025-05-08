package com.example.runners.models;

public class FaqItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_QUESTION = 1;

    private int type;
    private String question;
    private String answer;
    private boolean expanded;

    // Constructor para preguntas
    public FaqItem(String question, String answer) {
        this.type = TYPE_QUESTION;
        this.question = question;
        this.answer = answer;
        this.expanded = false;
    }

    // Constructor para encabezados
    public FaqItem(String headerTitle) {
        this.type = TYPE_HEADER;
        this.question = headerTitle;
        this.answer = "";
        this.expanded = false;
    }

    public FaqItem(String s, String s1, int typeQuestion) {
    }

    public int getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
