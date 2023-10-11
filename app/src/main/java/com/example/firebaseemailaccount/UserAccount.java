package com.example.firebaseemailaccount;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserAccount {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("email")
    @Expose
    private String email; // 필수

    @SerializedName("password")
    @Expose
    private String password; // 필수

    @SerializedName("nickname")
    @Expose
    private String nickname; // 필수X

    @SerializedName("baby_birthday")
    @Expose
    private String baby_birthday; // YYMMDD // 필수X

    @SerializedName("baby_gender")
    @Expose
    private String baby_gender; // 남아(m), 여아(f) // 필수X

    @SerializedName("user_image")
    @Expose
    private String user_image;

    // 추가
    @SerializedName("jwt")  // JSON 키 이름과 매핑
    private String jwt;

    // 추가
    public String getJwt() {
        return jwt;
    }

    // Set 역할 수행
    public UserAccount(String email, String password){
        this.email = email;
        this.password = password;
    }

    // 추가
    public UserAccount(String email, String nickname, String baby_birthday, String baby_gender){
        this.email = email;
        this.nickname = nickname;
        this.baby_birthday = baby_birthday;
        this.baby_gender = baby_gender;
    }

    public int getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    public String getNickname(){
        return nickname;
    }

    public String getBabyBirthday(){
        return baby_birthday;
    }

    public String getBabyGender(){
        return baby_gender;
    }

    public String getUserImage(){
        return user_image;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    public void setBabyBirthday(String baby_birthday){
        this.baby_birthday = baby_birthday;
    }

    public void setBabyGender(String baby_gender){
        this.baby_gender = baby_gender;
    }
}