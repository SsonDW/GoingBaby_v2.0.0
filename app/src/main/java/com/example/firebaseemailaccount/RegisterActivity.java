package com.example.firebaseemailaccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    Call<UserAccount> call;
    private EditText et_email, et_pwd; //회원가입 입력필드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email = findViewById(R.id.et_email);
        et_pwd = findViewById(R.id.et_pwd);
        //회원가입 버튼
        Button btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(view -> {
            UserAccount user = new UserAccount(et_email.getText().toString(), et_pwd.getText().toString());
            call = Retrofit_client.getUserApiService().user_register(user);

            call.enqueue(new Callback<UserAccount>() {
                //콜백 받는 부분
                @Override
                public void onResponse(@NonNull Call<UserAccount> call, @NonNull Response<UserAccount> response) {
                    if(response.isSuccessful()){
                        // 회원가입 성공하면 로그인 화면으로 전환
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
                @Override
                public void onFailure(@NonNull Call<UserAccount> call, @NonNull Throwable t) {
                }
            });
        });

    }
}