package com.example.firebaseemailaccount;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class PageActivity extends AppCompatActivity {
    BottomNavigationView buttonview;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //화면에 보여짐
        setContentView(R.layout.bottom_navigation_container);
        buttonview = findViewById(R.id.bottomNav);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new MapFragment()).commit();

        // BottomNavigationView 선택 이벤트 처리
        buttonview.setOnItemSelectedListener((NavigationBarView.OnItemSelectedListener) menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.map:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MapFragment()).commit();
                    break;
                case R.id.community:
                    // getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new CommunityActivity()).commit();
                    // 게시글 전체 목록 먼저 띄우도록 수정
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new ListActivity()).commit();
                    break;
                case R.id.diary:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new DiaryActivity()).commit();
                    break;
                case R.id.mypage:
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new MyPageActivity()).commit();
                    break;
            }
            return true;
        });
    }

    // 화면 전환용 메서드
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Fragment로 사용할 MainActivity내의 layout공간을 선택
        fragmentTransaction.replace(R.id.main_frame, fragment).commit();
    }
}