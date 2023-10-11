package com.example.firebaseemailaccount;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.TimetableView;

import java.util.ArrayList;

public class DiaryActivity extends Fragment implements View.OnClickListener {
    private View view;
    private String TAG = "DiaryFragment";
    private Context context;
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private Button addBtn;
    private Button clearBtn;
    private Button saveBtn;
    private Button loadBtn;
    private Button timerButton;

    private TimetableView timetable;

    public DiaryActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        Log.i(TAG,"onCreateView");

        view=inflater.inflate(R.layout.activity_diary_main,container,false);
        init();

        return view;
    }

    private void init(){
        // this.context = this;
        // 수정
        this.context = getContext();
        addBtn = view.findViewById(R.id.add_btn);
        addBtn.setBackgroundColor(Color.parseColor("#E893CF"));
        clearBtn = view.findViewById(R.id.clear_btn);
        clearBtn.setBackgroundColor(Color.parseColor("#9376E0"));
        saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setBackgroundColor(Color.parseColor("#F6FFA6"));
        loadBtn = view.findViewById(R.id.load_btn);
        loadBtn.setBackgroundColor(Color.parseColor("#F3BCC8"));
        timerButton = view.findViewById(R.id.timer_btn);
        timerButton.setBackgroundColor(Color.parseColor("#7aa5e9"));
        timetable = view.findViewById(R.id.timetable);

        initView();
    }

    private void initView(){
        addBtn.setOnClickListener(this);
        clearBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        loadBtn.setOnClickListener(this);
        timerButton.setOnClickListener(this);
        timetable.setOnStickerSelectEventListener((idx, schedules) -> {
            Intent i = new Intent(context, EditActivity.class);
            i.putExtra("mode",REQUEST_EDIT);
            i.putExtra("idx", idx);
            i.putExtra("schedules", schedules);
            startActivityForResult(i,REQUEST_EDIT);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_btn:
                // 수정
                Intent i = new Intent(getContext(),EditActivity.class);
                i.putExtra("mode",REQUEST_ADD);
                startActivityForResult(i,REQUEST_ADD);
                break;
            case R.id.clear_btn:
                timetable.removeAll();
                break;
            case R.id.save_btn:
                saveByPreference(timetable.createSaveData());
                break;
            case R.id.load_btn:
                loadSavedData();
                break;
            case R.id.timer_btn:
                Intent intent = new Intent(getContext(), TimerActivity.class);
                startActivity(intent);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ADD:
                if(resultCode == EditActivity.RESULT_OK_ADD){
                    ArrayList<Schedule> item = (ArrayList<Schedule>)data.getSerializableExtra("schedules");
                    timetable.add(item);
                }
                break;
            case REQUEST_EDIT:
                /** Edit -> Submit */
                if(resultCode == EditActivity.RESULT_OK_EDIT){
                    int idx = data.getIntExtra("idx",-1);
                    ArrayList<Schedule> item = (ArrayList<Schedule>)data.getSerializableExtra("schedules");
                    timetable.edit(idx,item);
                }
                /** Edit -> Delete */
                else if(resultCode == EditActivity.RESULT_OK_DELETE){
                    int idx = data.getIntExtra("idx",-1);
                    timetable.remove(idx);
                }
                break;
        }
    }

    /** save timetableView's data to SharedPreferences in json format */
    private void saveByPreference(String data){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("timetable_demo",data);
        editor.commit();
        Toast.makeText(getContext(),"저장 성공",Toast.LENGTH_SHORT).show();
    }

    /** get json data from SharedPreferences and then restore the timetable */
    private void loadSavedData(){
        timetable.removeAll();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String savedData = mPref.getString("timetable_demo","");
        if(savedData == null && savedData.equals("")) return;
        timetable.load(savedData);
        Toast.makeText(getContext(),"불러오기 성공",Toast.LENGTH_SHORT).show();
    }
}