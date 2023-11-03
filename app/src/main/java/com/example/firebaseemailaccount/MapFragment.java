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
    private Marker currentMarker;
    private InfoWindow currentInfoWindow;
    private Button reviewButton;
    private Button searchButton;
    private EditText searchEditText;

    private final ArrayList<Marker> markers = new ArrayList<>();

    public MapFragment() {

    }

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
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });

        searchButton = rootView.findViewById(R.id.searchButton);
        searchEditText = rootView.findViewById(R.id.searchEditText);
        searchButton.setOnClickListener(v -> {
            String searchQuery = searchEditText.getText().toString();
            searchLocationByName(searchQuery);
        });

        Button babyTablewareButton = rootView.findViewById(R.id.btnFilter1);
        babyTablewareButton.setOnClickListener(v -> {
            generateMarker(true); // 아기식기가 있는 장소만 표시
        });
        return rootView;
    }

    private void searchLocationByName(String name) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location");

        // 기존에 표시된 모든 마커 숨기기
        for (Marker marker : markers) {
            marker.setMap(null);
        }
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    String placeName = placeSnapshot.child("name").getValue(String.class); // placeName은 한글이름, name은 영어이름이여야 모든 데이터를 불러올 수 있다.

                    if (placeName != null && placeName.equals(name)) {
                        double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                        double longitude = placeSnapshot.child("longitude").getValue(Double.class);

                        LatLng newLatLng = new LatLng(latitude, longitude);
                        Marker newMarker = new Marker();
                        newMarker.setPosition(newLatLng);
                        newMarker.setTag(placeName);
                        newMarker.setMap(naverMap);
                        markers.add(newMarker);


                        // 기존에 표시된 마커 중에서 검색한 마커만 다시 표시
                        for (Marker marker : markers) {
                            if (marker.getTag() != null && marker.getTag().toString().equals(name)) {
                                marker.setMap(naverMap);
                            }
                        }

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
                                   /* // 정보 창 열기
                                    currentInfoWindow = infoWindow;
                                    infoWindow.open(newMarker);
                                    currentMarker = newMarker;*/


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



    public void generateMarker(boolean hasBabyTableware) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location");

        // 이전에 생성한 마커들 모두 삭제
        for (Marker marker : markers) {
            marker.setMap(null);
        }
        markers.clear(); // 리스트도 초기화
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot placeSnapshot : dataSnapshot.getChildren()) {
                    // 장소의 'hasBabyTableware' 필드 값 확인
                    Boolean placeHasBabyTableware = placeSnapshot.child("hasBabyTableware").getValue(Boolean.class);

                    if (placeHasBabyTableware != null && placeHasBabyTableware == hasBabyTableware) {
                        // '아기식기'의 유무에 따라 필터링된 장소만 마커 생성
                        String placeName = placeSnapshot.getKey();
                        double latitude = placeSnapshot.child("latitude").getValue(Double.class);
                        double longitude = placeSnapshot.child("longitude").getValue(Double.class);

                        // 마커 생성 및 표시
                        LatLng newLatLng = new LatLng(latitude, longitude);
                        Marker newMarker = new Marker();
                        newMarker.setPosition(newLatLng);
                        newMarker.setTag(placeName);
                        newMarker.setMap(naverMap);

                        markers.add(newMarker); // 리스트에 마커 추가

                        // 아기식기 유무에 따라 필터링된 마커만 지도에 표시
                        if (placeHasBabyTableware == hasBabyTableware) {
                            newMarker.setMap(naverMap);
                        }
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
                            StorageReference storageRef = storage.getReference().child(placeName + ".png");
                            // 이미지 다운로드 URL 가져오기
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // 다운로드 URL을 사용하여 이미지 로드 등의 작업 수행
                                String imageUrl = uri.toString();
                                // 이미지를 imageView에 설정하거나 처리하는 등의 작업 수행
                                // Glide 등의 라이브러리를 사용하여 이미지 로드를 쉽게 처리할 수 있습니다.
                                Glide.with(requireContext()).load(imageUrl).into(imageView);
                            }).addOnFailureListener(exception -> {
                                // 이미지 다운로드 실패 시 처리할 작업 수행
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
                                   /* // 정보 창 열기
                                    currentInfoWindow = infoWindow;
                                    infoWindow.open(newMarker);
                                    currentMarker = newMarker;*/


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
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });

    }



    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;

        Button babyTablewareButton = rootView.findViewById(R.id.btnFilter1);

        babyTablewareButton.setOnClickListener(v -> {
            generateMarker(true); // 아기식기가 있는 장소만 표시
        });

        Button searchButton = rootView.findViewById(R.id.searchButton);
        EditText searchEditText = rootView.findViewById(R.id.searchEditText); // searchEditText 초기화

        searchButton.setOnClickListener(v -> {
            String searchQuery = searchEditText.getText().toString();
            searchLocationByName(searchQuery);
        });

        // 초기 위치 설정
        double initialLatitude = 37.5828483; // 초기 위도(한성대)
        double initialLongitude = 127.0105811; // 초기 경도(한성대)

        // 지도의 초기 위치로 이동
        naverMap.setCameraPosition(new CameraPosition(
                new LatLng(initialLatitude, initialLongitude), // 위도와 경도 설정
                15 // 줌 레벨 설정
        ));



        // 마커 생성, 후에 배열로 추가할 수 있도록 변경할 수 있을 것!
        /*
        generateMarker("AtwosomePlace");
        generateMarker("CGV");
        generateMarker("CityHall");
        generateMarker("HanaBank");
        generateMarker("HansungUniversity");
        generateMarker("HansungUniversityStation");
        generateMarker("Lotteria");
        generateMarker("Napoleon");
        generateMarker("SFCMall");
        generateMarker("Sioldon");
        generateMarker("Starbucks");
        generateMarker("Sungnyemun");
        generateMarker("ThePlaza");
        generateMarker("BurgerPark");
        generateMarker("CafeTravel");
        generateMarker("Cheongkimyeonga");
        generateMarker("PantanoDessert");
        generateMarker("SeongkuakMuseum");
        generateMarker("TeenteenHall");
        generateMarker("JacksonPizza");
        */

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