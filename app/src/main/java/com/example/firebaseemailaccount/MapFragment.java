package com.example.firebaseemailaccount;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private NaverMap naverMap;
    private View rootView;  // 클래스 수준에 rootView 변수 선언


    private ImageView imageView; // 매장 이미지 변수
    private TextView titleTextView; // 매장 이름 변수
    private TextView addressTextView; // 매장 주소 변수
    private TextView phoneTextView; // 매장 번호 변수
    private RatingBar averageRatingBar; // 매장 평점 변수
    private ListView listView; // 매장 리뷰
    private TextView optionTextView1; // 매장 옵션1
    private TextView optionTextView2; // 매장 옵션2
    private Button reviewButton;
    private Button searchButton; // 검색버튼
    private EditText searchEditText; // 검색할 매장 내용을 입력하는 editText
    private Button babyTablewareButton; // 아기식기 필터
    private Button playRoomButton; // 놀이방 필터
    private Button rampButton; // 경사로 필터
    private final ArrayList<Marker> markers = new ArrayList<>();

    public MapFragment() { }

    @NonNull
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_map, container, false); // rootView 변수에 할당
        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        SlidingUpPanelLayout slidingUpPanelLayout = rootView.findViewById(R.id.slidingPanelLayout);
        imageView = rootView.findViewById(R.id.iconImageView);
        titleTextView = rootView.findViewById(R.id.titleTextView);
        addressTextView = rootView.findViewById(R.id.addressTextView);
        phoneTextView = rootView.findViewById(R.id.phoneNumberTextView);
        optionTextView1 = rootView.findViewById(R.id.OptionTextView1);
        optionTextView2 = rootView.findViewById(R.id.OptionTextView2);
        averageRatingBar = rootView.findViewById(R.id.ratingBarResult);
        listView = rootView.findViewById(R.id.listView);
        reviewButton = rootView.findViewById(R.id.myButton); // 리뷰하기 버튼
        reviewButton.setOnClickListener(v -> { // 리뷰하기 버튼을 누르면 화면 전환
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            intent.putExtra("key", titleTextView.getText()); // 여기서 "key"는 데이터를 식별하기 위한 키 값이고, value는 전달하려는 데이터입니다.
            startActivity(intent);
        });

        //초기화
        titleTextView.setText("오늘의 PICK"); // 제목 초기화
        // 글자 크기와 bold 설정
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // 크기를 원하는 값으로 변경
        titleTextView.setTypeface(null, Typeface.BOLD);

        // 글자 색상 변경
        titleTextView.setTextColor(Color.BLUE); // 색상을 원하는 값으로 변경
        phoneTextView.setText("서울특별시 종로구 율곡로23길 3");
        addressTextView.setText("★ 판타노디저트 ★");
        optionTextView1.setText("수유실X, 아기의자O, 아기식기O");
        optionTextView2.setText("자동문X, 놀이방O, 경사로O");
        imageView.setImageResource(R.drawable.ic_empty_like); // 이미지 초기화

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) { }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) { }
        });

        return rootView;
    }

    private void generateMarker() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    String placeName = placeSnapshot.child("name").getValue(String.class);

                    if (placeSnapshot.child("name").exists() && placeSnapshot.child("name").getValue() != null) {
                        double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                        double longitude = placeSnapshot.child("longitude").getValue(Double.class);

                        LatLng newLatLng = new LatLng(latitude, longitude);
                        Marker newMarker = new Marker();
                        newMarker.setPosition(newLatLng);
                        newMarker.setTag(placeName);
                        newMarker.setMap(naverMap);
                        markers.add(newMarker);

                        // 마커 클릭 리스너 등록
                        newMarker.setOnClickListener(marker -> {
                            // 마커 클릭 시 동작할 내용 작성
                            titleTextView.setText(String.valueOf(placeSnapshot.child("name").getValue()));
                            phoneTextView.setText(String.valueOf(placeSnapshot.child("PhoneNumber").getValue()));
                            addressTextView.setText(String.valueOf(placeSnapshot.child("Address").getValue()));

                            // 경사로, 아기식기, 아기용품 등등 관련 정보 표시
                            String optionText1 = placeSnapshot.child("NursingRoom").getValue() + " "
                                    + placeSnapshot.child("BabyChair").getValue() + " "
                                    + placeSnapshot.child("BabyTableware").getValue();
                            optionTextView1.setText(optionText1);
                            String optionText2 = placeSnapshot.child("AutomaticDoors").getValue() + " "
                                    + placeSnapshot.child("PlayRoom").getValue() + " "
                                    + placeSnapshot.child("Ramp").getValue();
                            optionTextView2.setText(optionText2);
                            averageRatingBar.setRating(0.0f);

                            // FirebaseStorage 접근
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            // StorageReference 생성
                            StorageReference storageRef = storage.getReference().child(placeSnapshot.getKey() + ".png");
                            // 이미지 다운로드 URL 가져오기
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // 다운로드 URL을 사용하여 이미지 로드 등의 작업 수행
                                String imageUrl = uri.toString();
                                // 이미지를 imageView에 설정하거나 처리하는 등의 작업 수행
                                // Glide 등의 라이브러리를 사용하여 이미지 로드를 쉽게 처리할 수 있습니다.
                                Glide.with(requireContext()).load(imageUrl).into(imageView);
                            }).addOnFailureListener(exception -> {
                                // 이미지 다운로드 실패 시 처리할 작업 수행
                                System.out.println("Image download Error");
                            });


                            // Firestore 인스턴스 가져오기
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // "Reviews" 컬렉션 참조
                            CollectionReference reviewsCollectionRef = db.collection("Reviews");

                            // storeName이 "cgv"인 데이터 필터링
                            Query query = reviewsCollectionRef.whereEqualTo("storeName", titleTextView.getText().toString());

                            // 쿼리 실행
                            query.get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // 쿼리 결과 가져오기
                                    QuerySnapshot querySnapshot = task.getResult();

                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        double totalRating = 0.0;
                                        int count = 0;
                                        ArrayList<String> reviewData = new ArrayList<>();
                                        String Info[] = new String[6]; // 옵션 표시 배열
                                        for (int i = 0; i < Info.length; i++) {
                                            Info[i] = "";
                                        }

                                        // 모든 문서의 rating 값을 합산하고 문서 개수를 세기
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            // 평균 평점 계산
                                            double ratingDouble = documentSnapshot.getDouble("rating");
                                            totalRating += ratingDouble;
                                            count++;
                                            // 경사로, 아기식기등 옵션 사항 표시하기
                                            int info_count = 0; // 옵션 표시 배열 길이
                                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("hasNursingRoom"))) {
                                                Info[info_count++] = "수유실O, ";
                                            } else {
                                                Info[info_count++] = "수유실X, ";
                                            }
                                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("hasBabyChair"))) {
                                                Info[info_count++] = "아기의자O, ";
                                            } else {
                                                Info[info_count++] = "아기의자X, ";
                                            }
                                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("hasTableWare"))) {
                                                Info[info_count++] = "아기식기O";
                                            } else {
                                                Info[info_count++] = "아기식기X";
                                            }
                                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("hasAutomaticDoor"))) {
                                                Info[info_count++] = "자동문O, ";
                                            } else {
                                                Info[info_count++] = "자동문X, ";
                                            }
                                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("hasPlayRoom"))) {
                                                Info[info_count++] = "놀이방O, ";
                                            } else {
                                                Info[info_count++] = "놀이방X, ";
                                            }
                                            if (Boolean.TRUE.equals(documentSnapshot.getBoolean("hasRamp"))) {
                                                Info[info_count++] = "경사로O";
                                            } else {
                                                Info[info_count++] = "경사로X";
                                            }

                                            StringBuilder InfoOption = new StringBuilder();
                                            for (int j = 0; j < info_count; j++) {
                                                InfoOption.append(Info[j]);
                                                if (j == 2) {
                                                    InfoOption.append("\n");
                                                }
                                            }
                                            InfoOption.append("\n\n");
                                            InfoOption.append("<리뷰내용>\n");
                                            InfoOption.append(documentSnapshot.getString("reviewText"));
                                            InfoOption.append("\n");
                                            reviewData.add(InfoOption.toString());
                                        }

                                        // 평균 계산
                                        if (count > 0) {
                                            double averageRating = totalRating / count;
                                            float averageRatingFloat = (float) averageRating;

                                            // averageRatingBar에 평균값 설정
                                            averageRatingBar.setRating(averageRatingFloat);
                                        }
                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, reviewData);
                                        listView.setAdapter(adapter); // listView를 adater랑 연결해서 데이터 넣어주기
                                    }


                                } else {
                                    // 쿼리 실패 시 동작
                                    Log.e("ReviewActivity", "Error getting reviews", task.getException());
                                }
                            });

                            return true;
                        });
                    } else {
                        // 데이터가 없는 경우 처리
                        System.out.println("Error");
                    }

                    // ... Continue with the rest of your marker setup, such as setting listeners ...
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }

    private void filterMarkers(String searchQuery) {
        for (Marker marker : markers) {
            if (marker.getTag() != null && marker.getTag().toString().contains(searchQuery)) {
                // 검색어와 일치하는 마커만 표시
                marker.setMap(naverMap);
            } else {
                // 검색어와 일치하지 않는 마커는 숨김
                marker.setMap(null);
            }
        }
    }

    private void showAllMarkers() {
        for (Marker marker : markers) {
            // 모든 마커를 표시
            marker.setMap(naverMap);
        }
    }

    private void filterMarkersByOption(String optionKey, boolean optionValue) {
        for (Marker marker : markers) {
            String placeName = (String) marker.getTag();

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location");
            databaseRef.orderByChild("name").equalTo(placeName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                        boolean option = placeSnapshot.child(optionKey).getValue(Boolean.class);

                        // Option 값이 true이면서 해당 옵션 필터가 true일 때 해당 마커를 표시
                        if (option == optionValue) {
                            marker.setMap(naverMap);
                        } else {
                            marker.setMap(null);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 에러 처리
                }
            });
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        // 초기 위치 설정
        double initialLatitude = 37.5828483; // 초기 위도(한성대)
        double initialLongitude = 127.0105811; // 초기 경도(한성대)

        // 지도의 초기 위치로 이동
        naverMap.setCameraPosition(new CameraPosition(
                new LatLng(initialLatitude, initialLongitude), // 위도와 경도 설정
                15 // 줌 레벨 설정
        ));

        generateMarker();

        //필터 기능 추가
        searchButton = rootView.findViewById(R.id.searchButton);
        searchEditText = rootView.findViewById(R.id.searchEditText); // searchEditText 초기화
        babyTablewareButton = rootView.findViewById(R.id.btnFilter1);
        playRoomButton = rootView.findViewById(R.id.btnFilter2);
        rampButton = rootView.findViewById(R.id.btnFilter3);

        searchButton.setOnClickListener(v -> {
            String searchQuery = searchEditText.getText().toString().trim();

            if (!searchQuery.isEmpty()) {
                // 검색어가 비어 있지 않은 경우
                filterMarkers(searchQuery);
            } else {
                // 검색어가 비어 있는 경우
                // 모든 마커를 표시
                showAllMarkers();
            }
        });

        babyTablewareButton.setOnClickListener(v -> {
            filterMarkersByOption("hasBabyTableware", true);
        });

        playRoomButton.setOnClickListener(v -> {
            filterMarkersByOption("hasPlayRoom", true);
        });

        rampButton.setOnClickListener(v -> {
            filterMarkersByOption("hasRamp", true);
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}