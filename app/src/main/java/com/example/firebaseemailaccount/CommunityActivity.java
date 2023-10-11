package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityActivity extends Fragment {
    private View view;
    private String TAG = "CommunityFragment";
    // public Integer post_id;
    Call<Community_model> call;
    EditText title_et, content_et;
    TextView title_txt, content_txt, created_at_txt;
    Button reg_button, comment_button, list_button;

    public static CommunityActivity newInstance() {
        return new CommunityActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Log.i(TAG,"onCreateView");

        // ----------------------------------- 게시글 upload -----------------------------------
        view = inflater.inflate(R.layout.activity_upload,container,false);
        title_et = view.findViewById(R.id.title_et);
        content_et = view.findViewById(R.id.content_et);
        reg_button = view.findViewById(R.id.reg_button);

        // reg_button을 '누르면'
        // 그 때 title과 content 값 설정하도록 수정
        reg_button.setOnClickListener(view -> {
            Community_model post = new Community_model(title_et.getText().toString(), content_et.getText().toString());
            call = Retrofit_client.getApiService().community_post(post);
            call.enqueue(new Callback<Community_model>() { //콜백 받는 부분
                @Override
                public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(getContext(), "게시글이 등록되었습니다", Toast.LENGTH_LONG).show();
                    }else{
                    }

                    // 게시글 업로드 직후 전체 게시글 list view로 화면 전환
                    ((PageActivity)getActivity()).replaceFragment(ListActivity.newInstance());
                }
                @Override
                public void onFailure(Call<Community_model> call, Throwable t) {
                }
            });
        });

        return view;
    }
}