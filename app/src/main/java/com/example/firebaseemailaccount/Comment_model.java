package com.example.firebaseemailaccount;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.sql.Date;

public class Comment_model {
    @SerializedName("community_id")
    @Expose
    private Integer community_id;

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("created_at")
    @Expose
    private Date created_at; // java.sql.Date (yyyy-mm-dd)

    @SerializedName("updated_at")
    @Expose
    private Date updated_at;

    // set 댓글
    public Comment_model(String content){
        this.content = content;
    }

    public Integer getCommunityId(){
        return community_id;
    }

    public Integer getId(){
        return id;
    }

    public String getContent(){
        return content;
    }
}