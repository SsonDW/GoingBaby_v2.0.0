package com.example.firebaseemailaccount;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

// User 로그인 후 cookie 받아서 로그인 유지하는 용도
public class Cookie_jar implements CookieJar {
    private List<Cookie> cookies;

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
        this.cookies =  cookies;
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        if (cookies != null)
            return cookies;
        return new ArrayList<>();

    }
}