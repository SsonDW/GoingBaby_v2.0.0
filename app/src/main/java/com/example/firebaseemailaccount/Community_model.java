package com.example.firebaseemailaccount;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Date;

public class Community_model {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("view_count")
    @Expose
    private int view_count;

    @SerializedName("created_at")
    @Expose
    private Date created_at; // java.sql.Date -> yyyy-mm-dd 형식이어야 함

    @SerializedName("row_count")
    @Expose
    private int row_count;

    @SerializedName("comments_count")
    @Expose
    private int comments_count;

    @SerializedName("like_count")
    @Expose
    private int like_count;

    @SerializedName("all_comments_count")
    @Expose
    private int all_comments_count;

    @SerializedName("category")
    @Expose
    private String category;

    public Community_model(String title, String content){
        this.title = title;
        this.content = content;
    }

    public Community_model(int like_count){
        this.like_count = like_count;
    }

    public int getId(){
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public int getViewCount(){
        return view_count;
    }

    public Date getCreatedAt(){
        return created_at;
    }

    public int getRowCount(){
        return row_count;
    }

    public int getCommentsCount(){
        return comments_count;
    }

    public int getLikeCount(){
        return like_count;
    }

    public String getCategory(){
        return category;
    }

    public Integer getAllCommentsCount() { return all_comments_count; }
}