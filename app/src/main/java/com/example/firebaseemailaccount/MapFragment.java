package com.example.firebaseemailaccount;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.naver.maps.map.overlay.Marker;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private NaverMap naverMap;
    private ImageView imageView;
    private TextView titleTextView;
    private TextView addressTextView;
    private TextView phoneTextView;
    private RatingBar averageRatingBar;


    public MapFragment() { }

    @NonNull
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_map, container, false);

        mapView = rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        SlidingUpPanelLayout slidingUpPanelLayout = rootView.findViewById(R.id.slidingPanelLayout);
        imageView = rootView.findViewById(R.id.iconImageView);
        titleTextView = rootView.findViewById(R.id.titleTextView);
        phoneTextView = rootView.findViewById(R.id.PhoneNumberTextView);
        addressTextView = rootView.findViewById(R.id.addressTextView);
        Button myButton = rootView.findViewById(R.id.myButton);
        myButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ReviewActivity.class);
            System.out.println("MapFragment: " + titleTextView.getText());
            intent.putExtra("key", titleTextView.getText()); // 여기서 "key"는 데이터를 식별하기 위한 키 값이고, value는 전달하려는 데이터입니다.
            startActivity(intent);
        });
        averageRatingBar = rootView.findViewById(R.id.ratingBarResult);

        //초기화
        titleTextView.setText(""); // 제목 초기화
        phoneTextView.setText(""); // 전화번호 초기화
        addressTextView.setText(""); // 주소 초기화
        imageView.setImageResource(R.drawable.white); // 이미지 초기화


        // ListView에 데이터 추가
        ListView listView = rootView.findViewById(R.id.listView);
        ArrayList<String> dataList = new ArrayList<>();
        dataList.add("항목 1");
        dataList.add("항목 2");
        dataList.add("항목 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                // 슬라이딩 패널의 슬라이드 상태 변경 이벤트 처리
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                // 슬라이딩 패널의 상태 변경 이벤트 처리
            }
        });

        return rootView;
    }

    public void generateMarker(String name) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("/GoingBaby/Location/" + name);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println(dataSnapshot.child("name").getValue());
                    double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    LatLng newLatLng = new LatLng(latitude, longitude);

                    // 새로운 LatLng 객체를 사용하여 원하는 작업 수행
                    Marker newMarker = new Marker();
                    newMarker.setPosition(newLatLng);
                    newMarker.setTag(name); // 마커에 태그 설정
                    newMarker.setMap(naverMap);

                    // 마커 클릭 리스너 등록
                    newMarker.setOnClickListener(marker -> {
                        // 마커 클릭 시 동작할 내용 작성
                        titleTextView.setText(String.valueOf(dataSnapshot.child("name").getValue()));
                        phoneTextView.setText(String.valueOf(dataSnapshot.child("PhoneNumber").getValue()));
                        addressTextView.setText(String.valueOf(dataSnapshot.child(name + "Address").getValue()));
                        averageRatingBar.setRating(0.0f);

                        // FirebaseStorage 접근
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        // StorageReference 생성
                        StorageReference storageRef = storage.getReference().child(name + ".png");
                        // 이미지 다운로드 URL 가져오기
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // 다운로드 URL을 사용하여 이미지 로드 등의 작업 수행
                                String imageUrl = uri.toString();
                                // 이미지를 imageView에 설정하거나 처리하는 등의 작업 수행
                                // Glide 등의 라이브러리를 사용하여 이미지 로드를 쉽게 처리할 수 있습니다.
                                Glide.with(getContext()).load(imageUrl).into(imageView);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // 이미지 다운로드 실패 시 처리할 작업 수행
                            }
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

                                    // 모든 문서의 rating 값을 합산하고 문서 개수를 세기
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                        double ratingDouble = documentSnapshot.getDouble("rating");
                                        totalRating += ratingDouble;
                                        count++;
                                    }

                                    // 평균 계산
                                    if (count > 0) {
                                        double averageRating = totalRating / count;
                                        float averageRatingFloat = (float) averageRating;
                                        System.out.println("averageRatingFloat: " + averageRatingFloat);

                                        // averageRatingBar에 평균값 설정
                                        averageRatingBar.setRating(averageRatingFloat);

                                        // 평균값을 사용하여 원하는 동작 수행
                                        // 예: TextView에 평균값 설정 등
                                        // ...
                                    }
                                }

                            } else {
                                // 쿼리 실패 시 동작
                                Log.e("ReviewActivity", "Error getting reviews", task.getException());
                                // 예: 오류 메시지 표시 등
                            }
                        });

                        return true;
                    });
                } else {
                    // 데이터가 없는 경우 처리
                    System.out.println("Error");
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

        // 초기 위치 설정
        double initialLatitude = 37.5828483; // 초기 위도(한성대)
        double initialLongitude = 127.0105811; // 초기 경도(한성대)

        // 지도의 초기 위치로 이동
        naverMap.setCameraPosition(new CameraPosition(
                new LatLng(initialLatitude, initialLongitude), // 위도와 경도 설정
                15 // 줌 레벨 설정
        ));

        // 마커 생성
        generateMarker("AtwosomePlace");
        generateMarker("CGV");
        generateMarker("CityHall");
        generateMarker("HanaBank");
        generateMarker("HansungUniversity");
        generateMarker("HansungUniversityStation");
        generateMarker("Lotteria");
        generateMarker("Napoleon");
        generateMarker("SFCMall");
        generateMarker("Sioldon");;
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