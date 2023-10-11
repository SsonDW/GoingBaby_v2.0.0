package com.example.firebaseemailaccount;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserInterface {
    @POST("/user/register/") // user 회원가입
    Call<UserAccount> user_register(@Body UserAccount user);

    @POST("/user/login/") // user 로그인
    Call<UserAccount> user_login(@Body UserAccount user);

    @POST("/user/logout/") // user 로그아웃
    Call<UserAccount> user_logout(@Header("token") String token);

    // 토큰을 기반으로 POST 이외의 통신을 할 때 @Header의 방식으로 Token만을 가지고 통신하는 방식도 존재
    // Call<UserAccount> user_get(@Header("Authorization") String token);
    @PUT("/user/view/") // user account 수정(mypage 안에서 동작)
    Call<UserAccount> user_update(@Header("token") String token, @Body UserAccount user);

    @GET("/user/view/") // user account 확인(mypage 안에서 동작)
    Call<UserAccount> user_view(@Header("token") String token);

    @PUT("user/view/{id}") // user account 수정(mypage 안에서 동작)
    Call<UserAccount> user_update(@Body UserAccount user, @Path("id") int id);

    @GET("user/view/{id}") // user account 확인(mypage 안에서 동작)
    Call<UserAccount> user_view(@Path("id") int id);
}