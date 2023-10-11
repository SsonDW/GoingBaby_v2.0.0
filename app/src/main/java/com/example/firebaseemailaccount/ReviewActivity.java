package com.example.firebaseemailaccount;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.PropertyName;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    private EditText reviewText;
    private RadioButton Ramp;
    private RadioButton tableWare;
    private RadioButton babyChair;
    private RadioButton nursingRoom;
    private RadioButton playRoom;
    private RadioButton automaticDoor;
    private RatingBar ratingBar;
    public TextView storeName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewText = findViewById(R.id.reviewtext);
        Button saveButton = findViewById(R.id.savebutton);
        Ramp = findViewById(R.id.ramp);
        tableWare = findViewById(R.id.tableware);
        babyChair = findViewById(R.id.babychair);
        nursingRoom = findViewById(R.id.nursingroom);
        playRoom = findViewById(R.id.playroom);
        automaticDoor = findViewById(R.id.automaticdoor);
        ratingBar = findViewById(R.id.ratingBar);
        storeName = findViewById(R.id.storeName);

        // ReviewActivity의 onCreate() 메서드 내부에서 실행
        Intent intent = getIntent();
        if (intent != null) {
            System.out.println("ReviewActivity: " + intent.getStringExtra("key"));
            storeName.setText(intent.getStringExtra("key")); // "key"에 해당하는 데이터 가져오기
            // 데이터를 사용하여 필요한 작업 수행
        }

        saveButton.setOnClickListener(v -> {
            // 입력된 내용 가져오기
            String name = storeName.getText().toString();
            String review = reviewText.getText().toString();
            boolean isRampChecked = Ramp.isChecked();
            boolean isTableWareChecked = tableWare.isChecked();
            boolean isBabyChairChecked = babyChair.isChecked();
            boolean isNursingRoomChecked = nursingRoom.isChecked();
            boolean isPlayRoomChecked = playRoom.isChecked();
            boolean isAutomaticDoorChecked = automaticDoor.isChecked();
            float rating = ratingBar.getRating();

            // Firestore 인스턴스 가져오기
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // "Reviews" 컬렉션 참조
            CollectionReference reviewsCollectionRef = db.collection("Reviews");

            // 새로운 Review 객체 생성
            Review newReview = new Review(name, review, isRampChecked, isTableWareChecked, isBabyChairChecked,
                    isNursingRoomChecked, isPlayRoomChecked, isAutomaticDoorChecked, rating);

            // Reviews 컬렉션에 데이터 추가
            reviewsCollectionRef.add(newReview)
                    .addOnSuccessListener(documentReference -> {
                        // 저장 성공 시 동작
                        String reviewId = documentReference.getId();
                        Log.d("ReviewActivity", "Review added with ID: " + reviewId);
                        // 예: 저장 완료 메시지 표시, 이전 액티비티로 돌아가기 등
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        // 저장 실패 시 동작
                        Log.e("ReviewActivity", "Error adding review", e);
                        // 예: 오류 메시지 표시 등
                    });
        });

    }

    public static class Review {
        private String reviewText;
        private boolean hasRamp;
        private boolean hasTableWare;
        private boolean hasBabyChair;
        private boolean hasNursingRoom;
        private boolean hasPlayRoom;
        private boolean hasAutomaticDoor;
        private float rating;
        private String storeName;

        public Review(String storeName, String reviewText, boolean hasRamp, boolean hasTableWare, boolean hasBabyChair,
                      boolean hasNursingRoom, boolean hasPlayRoom, boolean hasAutomaticDoor, float rating) {
            this.storeName = storeName;
            this.reviewText = reviewText;
            this.hasRamp = hasRamp;
            this.hasTableWare = hasTableWare;
            this.hasBabyChair = hasBabyChair;
            this.hasNursingRoom = hasNursingRoom;
            this.hasPlayRoom = hasPlayRoom;
            this.hasAutomaticDoor = hasAutomaticDoor;
            this.rating = rating;
        }

        // Getter and Setter methods
        @PropertyName("storeName")
        public String getStoreName() {
            return storeName;
        }
        @PropertyName("reviewText")
        public String getReviewText() {
            return reviewText;
        }
        @PropertyName("hasRamp")
        public boolean getHasRamp() {
            return hasRamp;
        }
        @PropertyName("hasTableWare")
        public boolean getHasTableWare() {
            return hasTableWare;
        }
        @PropertyName("hasBabyChair")
        public boolean getHasBabyChair() {
            return hasBabyChair;
        }
        @PropertyName("hasNursingRoom")
        public boolean getHasNursingRoom() {
            return hasNursingRoom;
        }
        @PropertyName("hasPlayRoom")
        public boolean getHasPlayRoom() {
            return hasPlayRoom;
        }
        @PropertyName("hasAutomaticDoor")
        public boolean getHasAutomaticDoor() {
            return hasAutomaticDoor;
        }
        @PropertyName("rating")
        public float getRating() {
            return rating;
        }

        @PropertyName("storeName")
        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }
        @PropertyName("reviewText")
        public void setReviewText(String reviewText) {
            this.reviewText = reviewText;
        }
        @PropertyName("hasRamp")
        public void setHasRamp() {
            this.hasRamp = hasRamp;
        }
        @PropertyName("hasTableWare")
        public void setHasTableWare() {
            this.hasTableWare = hasTableWare;
        }
        @PropertyName("hasBabyChair")
        public void setHasBabyChair() {
            this.hasBabyChair = hasBabyChair;
        }
        @PropertyName("hasNursingRoom")
        public void setHasNursingRoom() {
            this.hasNursingRoom = hasNursingRoom;
        }
        @PropertyName("hasPlayRoom")
        public void setHasPlayRoom() {
            this.hasPlayRoom = hasPlayRoom;
        }
        @PropertyName("hasAutomaticDoor")
        public void setHasAutomaticDoor() {
            this.hasAutomaticDoor = hasAutomaticDoor;
        }
        @PropertyName("rating")
        public void setRating() {
            this.rating = rating;
        }
    }

}