package com.example.firebaseemailaccount;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentInterface {
    //---------- 댓글 관련 ----------
    @POST("/comment/create/{id}") // 지정한 게시물 댓글 upload
    Call<Comment_model> comment_post(@Body Comment_model comment, @Path("id") Integer id);

    @GET("/comment/{id}") // 지정한 게시물 댓글 get
    Call<Comment_model> comment_get(@Path("id") Integer id);
}