package com.example.firebaseemailaccount;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityFreetalkActivity extends Fragment {
    Call<Community_model> call;
    ListView listView;
    ListViewAdapter adapter;
    Button listButton;
    static int list_count;
//    int i;
    public static CommunityFreetalkActivity newInstance() {
        return new CommunityFreetalkActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_freetalk, null);

        adapter = new ListViewAdapter();

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listButton = view.findViewById(R.id.listButton);

        // ----------------------------------- 자유토크글 list view -----------------------------------
        // '자유토크(free)' 카테고리에 속하는 게시글 list view
        call = Retrofit_client.getApiService().community_detail_get(1);
        call.enqueue(new Callback<Community_model>() {
            @Override
            public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                list_count = response.body().getRowCount();
                for(int i=1; i<=list_count; i++) {
                    call = Retrofit_client.getApiService().community_detail_get(i);
                    call.enqueue(new Callback<Community_model>() {
                        @Override
                        public void onResponse(Call<Community_model> call, Response<Community_model> response) {
                            Community_model result = response.body();
                            if(response.isSuccessful() && result.getCategory().equals("free")) {
                                adapter.addItem(result.getId(), result.getTitle(), result.getContent());
                                adapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onFailure(Call<Community_model> call, Throwable t) {
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Community_model> call, Throwable t) {
            }
        });

        // listView 항목 클릭이벤트
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            ListViewItem item = (ListViewItem) adapterView.getItemAtPosition(i);
            String title = item.getItemTitle();
            int id = item.getItemId();

            CommunityViewActivity fragment = CommunityViewActivity.newInstance(id);

            Bundle bundle = new Bundle();
            bundle.putInt("id", id);
            fragment.setArguments(bundle);

            ((PageActivity)getActivity()).replaceFragment(fragment);
        });

        // ----------------------------------- 전체 게시글 목록으로 돌아가는 버튼 -----------------------------------
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PageActivity)getActivity()).replaceFragment(ListActivity.newInstance());
            }
        });

        return view;
    }
}