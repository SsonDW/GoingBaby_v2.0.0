package com.example.firebaseemailaccount;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListActivity extends Fragment {
    Call<Community_model> call;

    // 사용할 컴포넌트 선언
    ListView listView;
    ListViewAdapter adapter;
    Button reg_button, button1, button2, button3;
    SearchView search_input; // 검색어를 입력할 Input 창
    static int list_count;

    HashMap<Integer, String> id_title_list;

    public static ListActivity newInstance() {
        return new ListActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.activity_list, null); // Fragment로 불러올 xml파일을 view로 가져옴
        adapter = new ListViewAdapter();

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        reg_button = view.findViewById(R.id.reg_button);
        button1 = view.findViewById(R.id.button1);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        search_input = view.findViewById(R.id.search_input);

        id_title_list = new HashMap<>();

        // ----------------------------------- 게시글 전체 list view -----------------------------------
        call = Retrofit_client.getApiService().community_detail_get(1);
        call.enqueue(new Callback<Community_model>() {
            @Override
            public void onResponse(@NonNull Call<Community_model> call, @NonNull Response<Community_model> response) {
                assert response.body() != null;
                list_count = response.body().getRowCount();

                for(int i=1;i<=list_count;i++) {
                    call = Retrofit_client.getApiService().community_detail_get(i);
                    call.enqueue(new Callback<Community_model>() {
                        @Override
                        public void onResponse(@NonNull Call<Community_model> call, @NonNull Response<Community_model> response) {
                            Community_model result = response.body();
                            assert result != null;
                            id_title_list.put(result.getId(), result.getTitle());
                            // titleList.add(result.getTitle());
                            adapter.addItem(result.getId(), result.getTitle(), result.getContent());
                            adapter.notifyDataSetChanged(); // 꼭 반영해줘야 list에 제대로 addItem됨
                        }

                        @Override
                        public void onFailure(@NonNull Call<Community_model> call, @NonNull Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Community_model> call, @NonNull Throwable t) {
            }
        });

        // ----------------------------------- 검색어 입력 시 -----------------------------------
        search_input.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.clearItems();
                for (Integer id : id_title_list.keySet()) {
                    String title = id_title_list.get(id);
                    assert title != null;
                    if (title.toLowerCase().contains(s.toLowerCase())) {
                        // Add the matching items to the adapter
                        adapter.addItem(id, title, "");
                    }
                }
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        // listView의 항목 중 하나 클릭 시
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            ListViewItem item = (ListViewItem) adapterView.getItemAtPosition(i);
            int id = item.getItemId();

            CommunityViewActivity fragment = CommunityViewActivity.newInstance(id);

            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            fragment.setArguments(bundle);

            ((PageActivity)getActivity()).replaceFragment(fragment);
        });

        // 추천글 버튼 클릭 시
        button1.setOnClickListener(v -> ((PageActivity)getActivity()).replaceFragment(CommunityRecommendActivity.newInstance()));

        // 자유토크 버튼 클릭 시
        button2.setOnClickListener(v -> ((PageActivity)getActivity()).replaceFragment(CommunityFreetalkActivity.newInstance()));

        // 질문답변 버튼 클릭 시
        button3.setOnClickListener(v -> ((PageActivity)getActivity()).replaceFragment(CommunityQnaActivity.newInstance()));

        // 게시글 등록 버튼 클릭 시
        reg_button.setOnClickListener(v -> ((PageActivity)getActivity()).replaceFragment(CommunityActivity.newInstance()));

        return view;
    }
}