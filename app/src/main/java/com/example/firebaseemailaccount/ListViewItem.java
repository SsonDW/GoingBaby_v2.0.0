package com.example.firebaseemailaccount;

public class ListViewItem {
    private int id;
    private String contentStr;
    private String titleStr;

    public void setItemId(int id) {
        this.id = id;
    }

    public void setItemTitle(String title) {
        titleStr = title;
    }

    public void setItemContent(String content) {
        contentStr = content;
    }

    public int getItemId() {
        return this.id;
    }

    public String getItemContent() {
        return this.contentStr;
    }

    public String getItemTitle() {
        return this.titleStr;
    }
}