package com.example.firebaseemailaccount;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageUpdateActivity extends Fragment {
    Call<UserAccount> call;
    EditText email, nickname, baby_birthday, baby_gender;
    ImageView user_image_view;
    Button update_button;
    Bitmap bitmap;

    public static MyPageUpdateActivity newInstance() {
        return new MyPageUpdateActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        @SuppressLint("InflateParams") View view1 = inflater.inflate(R.layout.activity_update_mypage, null);

        email = view1.findViewById(R.id.email);
        nickname = view1.findViewById(R.id.nickname);
        baby_birthday = view1.findViewById(R.id.baby_birthday);
        baby_gender = view1.findViewById(R.id.baby_gender);
        update_button = view1.findViewById(R.id.update_button);
        user_image_view = view1.findViewById(R.id.user_image_view);

        String autoLogin_cookie = LoginActivity.sharedPref.getString("cookie", null);

        call = Retrofit_client.getUserApiService().user_view(autoLogin_cookie);
        call.enqueue(new Callback<UserAccount>() {
            @Override
            public void onResponse(@NonNull Call<UserAccount> call, @NonNull Response<UserAccount> response) {
                if (response.isSuccessful()) {
                    UserAccount result = response.body();

                    // -------------- 프로필 이미지 url 가져와서 화면에 표시하기 --------------
                    Thread uThread = new Thread() {
                        @Override
                        public void run(){
                            try{
                                // 이미지 URL 경로
                                assert result != null;
                                URL url = new URL(Retrofit_client.BASE_URL + result.getUserImage());
                                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                                conn.setDoInput(true);
                                conn.connect();

                                InputStream is = conn.getInputStream();
                                bitmap = BitmapFactory.decodeStream(is);

                            } catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    };
                    uThread.start();
                    try{
                        uThread.join();
                        user_image_view.setImageBitmap(bitmap);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<UserAccount> call, @NonNull Throwable t) {
            }
        });

        // update_button을 '누르면' 값 설정하고 반영
        update_button.setOnClickListener(view -> {
            UserAccount user = new UserAccount(
                    email.getText().toString(),
                    nickname.getText().toString(),
                    baby_birthday.getText().toString(),
                    baby_gender.getText().toString()
            );

            call = Retrofit_client.getUserApiService().user_update(autoLogin_cookie, user);
            call.enqueue(new Callback<UserAccount>() {
                //콜백 받는 부분
                @Override
                public void onResponse(@NonNull Call<UserAccount> call, @NonNull Response<UserAccount> response) {
                    ((PageActivity)getActivity()).replaceFragment(MyPageActivity.newInstance());
                }
                @Override
                public void onFailure(@NonNull Call<UserAccount> call, @NonNull Throwable t) {
                }
            });
        });

        return view1;
    }
}