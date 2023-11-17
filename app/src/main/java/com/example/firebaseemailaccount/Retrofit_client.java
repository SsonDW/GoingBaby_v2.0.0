package com.example.firebaseemailaccount;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_client {
    // private static final String BASE_URL = "";
    // login activity에서 사용하기 위해 public으로 수정
    public static final String BASE_URL = "http://3.37.192.69"; // http 기입 필수

    private static final Gson gson = new GsonBuilder().setLenient().create();

    public static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new Cookie_jar())
            .build();
    private static final Retrofit network_client = new Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    public static CommunityInterface getApiService(){return network_client.create(CommunityInterface.class);}

    public static CommentInterface getCommentApiService(){return network_client.create(CommentInterface.class);}

    public static UserInterface getUserApiService(){return network_client.create(UserInterface.class);}
}