package com.example.firebaseemailaccount;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends Fragment {
    private String TAG = "MyPageFragment";
    Call<UserAccount> call;
    Button update_button, logout_button;
    TextView nickname_view, baby_birthday_view, baby_gender_view;

    public static MyPageActivity newInstance() {
        return new MyPageActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Log.i(TAG,"onCreateView");

        View view = inflater.inflate(R.layout.activity_mypage, container, false);

        update_button = view.findViewById(R.id.update_button);
        logout_button = view.findViewById(R.id.logout_button);
        nickname_view = view.findViewById(R.id.nickname_view);
        baby_birthday_view = view.findViewById(R.id.baby_birthday_view);
        baby_gender_view = view.findViewById(R.id.baby_gender_view);

        // 각각 자동로그인과 일반로그인 쿠키가 있는지 가져와봄
        String autoLogin_cookie = LoginActivity.sharedPref.getString("cookie", null);

        // 마이페이지 회원정보 get
        if (autoLogin_cookie != null) {
            call = Retrofit_client.getUserApiService().user_view(autoLogin_cookie);
            call.enqueue(new Callback<UserAccount>() {
                @Override
                public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                    if (response.isSuccessful()) {
                        UserAccount result = response.body();
                        nickname_view.setText(result.getNickname());
                        baby_birthday_view.setText(result.getBabyBirthday());
                        baby_gender_view.setText(result.getBabyGender());
                    }
                }
                @Override
                public void onFailure(Call<UserAccount> call, Throwable t) {
                }
            });
        }

        // '프로필 편집' 버튼 클릭 시 수정 화면으로 전환
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PageActivity)getActivity()).replaceFragment(MyPageUpdateActivity.newInstance());
            }
        });

        // '로그아웃' 버튼 클릭 시 cookie 삭제
        // 로그인 화면으로 되돌아감
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoLogin_cookie != null) {
                    call = Retrofit_client.getUserApiService().user_logout(autoLogin_cookie);
                    call.enqueue(new Callback<UserAccount>() {
                        @Override
                        public void onResponse(Call<UserAccount> call, Response<UserAccount> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "로그아웃 성공", Toast.LENGTH_LONG).show();
                                // 서버 상에서 로그아웃 완료 + 안드로이드 상에서 저장해놨던 cookie 삭제하기
                                // 일반 로그인이었을 경우 cookie만 삭제
                                SharedPreferences.Editor editor = LoginActivity.sharedPref.edit();
                                editor.remove("cookie");
                                if (LoginActivity.sharedPref.getString("email", null) != null && LoginActivity.sharedPref.getString("password", null) != null) {
                                    editor.remove("email");
                                    editor.remove("password");
                                }
                                editor.apply();
                                // 1) 로그아웃 후 로그인 전의 마이페이지로 리디렉션할 경우
                                // ((PageActivity)getActivity()).replaceFragment(MyPageActivity.newInstance());
                                // 2) 로그아웃 후 처음 로그인 화면으로 리디렉션할 경우
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "로그아웃 실행되지 않음", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<UserAccount> call, Throwable t) {
                            Toast.makeText(getContext(), "로그아웃 실패", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        return view;
    }
}