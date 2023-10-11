package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityViewActivity extends Fragment {
    Call<Community_model> call;
    Call<Comment_model> call2;
    static Integer post_id;
    TextView title_txt, content_txt, like_count_txt;
    EditText comment_txt;
    Button comment_button, list_button;
    ListView listView;
    ListViewAdapter adapter;
    ScaleAnimation scaleAnimation;
    BounceInterpolator bounceInterpolator;
    CompoundButton button_like;
    static int comments_count;
    static int like_count;
    static int all_comments_count;

    // ------------------------------ 화면 전환 시 사용 ------------------------------
    public static CommunityViewActivity newInstance(int id) {
        post_id = id;
        return new CommunityViewActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        View view = inflater.inflate(R.layout.activity_community_view, null); // Fragment로 불러올 xml파일을 view로 가져옴
        adapter = new ListViewAdapter();
        listView = (ListView) view.findViewById(R.id.listView);
        title_txt = view.findViewById(R.id.title_txt);
        content_txt = view.findViewById(R.id.content_txt);
        comment_txt = view.findViewById(R.id.comment_txt);
        comment_button = view.findViewById(R.id.comment_button);
        list_button = view.findViewById(R.id.list_button);
        like_count_txt = view.findViewById(R.id.like_count_txt);
        button_like = view.findViewById(R.id.button_like);

        listView.setAdapter(adapter);

        // ------------------------------ 게시글 view ------------------------------
        call = Retrofit_client.getApiService().community_detail_get(post_id);
        call.enqueue(new Callback<Community_model>() {
            //콜백 받는 부분
            @Override
            public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                Community_model CommunityResult = response.body();
                comments_count = CommunityResult.getCommentsCount(); // 게시글별 댓글의 갯수
                all_comments_count = CommunityResult.getAllCommentsCount(); // 게시글별 댓글의 갯수
                like_count = CommunityResult.getLikeCount();
                System.out.println(all_comments_count);

                title_txt.setText(CommunityResult.getTitle());
                // created_at_txt.setText(result.getCreatedAt().toString());
                content_txt.setText(CommunityResult.getContent());

                like_count_txt.setText(String.valueOf(like_count));

                // ----------------------------------- 해당 게시글 전체 댓글 list view -----------------------------------
                // comment/{id}에 대해서 response의 serializer에서 created_at과 updated_at 제외시키고 돌리니 get 성공
                int count = 0;
                for(int i = 1; i <= all_comments_count; i++) {
                    int commentId = CommunityResult.getId(); // 댓글의 ID 또는 순서를 나타내는 변수
                    call2 = Retrofit_client.getCommentApiService().comment_get(i); // 각 댓글 순서대로 가져옴
                    call2.enqueue(new Callback<Comment_model>() {
                        @Override
                        public void onResponse(Call<Comment_model> call, Response<Comment_model> response) {
                            if (response.isSuccessful() && commentId == response.body().getCommunityId()) {
                                Comment_model commentResult = response.body();
                                adapter.addCommentItem(commentResult.getContent());
                                adapter.notifyDataSetChanged();

                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<Comment_model> call, Throwable t) {
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Community_model> call, Throwable t) {
            }
        });


        // ----------------------------------- 댓글 작성 후 등록 -----------------------------------
        comment_button.setOnClickListener(v -> {
            Comment_model cm = new Comment_model(comment_txt.getText().toString());
            call2 = Retrofit_client.getCommentApiService().comment_post(cm, post_id);
            call2.enqueue(new Callback<Comment_model>() {
                //콜백 받는 부분
                @Override
                public void onResponse(Call<Comment_model> call, Response<Comment_model> response) {
                    comment_txt.setText(""); // 댓글 등록 후 지워버리기
                }
                @Override
                public void onFailure(Call<Comment_model> call, Throwable t) {
                }
            });
        });

        // ----------------------------------- 좋아요 toggle + 좋아요 count db에 반영하기 -----------------------------------
        scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        button_like.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            compoundButton.startAnimation(scaleAnimation);
            call = Retrofit_client.getApiService().community_detail_get(post_id);
            call.enqueue(new Callback<Community_model>() {
                //콜백 받는 부분
                @Override
                public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                    Integer temp_count = like_count + 1;
                    like_count_txt.setText(temp_count.toString());

                    // DB에 추가된 좋아요 수 반영
                    Community_model user_like = new Community_model(temp_count);
                    call = Retrofit_client.getApiService().community_like_put(user_like, post_id);
                    call.enqueue(new Callback<Community_model>() {
                        //콜백 받는 부분
                        @Override
                        public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                        }
                        @Override
                        public void onFailure(Call<Community_model> call, Throwable t) {
                        }
                    });
                }
                @Override
                public void onFailure(Call<Community_model> call, Throwable t) {
                }
            });
        });


        // ----------------------------------- 전체 게시글 목록으로 돌아가는 버튼 -----------------------------------
        list_button.setOnClickListener(v -> ((PageActivity)getActivity()).replaceFragment(ListActivity.newInstance()));
        return view;
    }
}