package com.example.front_ui.PostingProcess;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.front_ui.DataModel.KakaoStoreInfo;
import com.example.front_ui.DataModel.PostingInfo;
import com.example.front_ui.DataModel.StoreInfo;
import com.example.front_ui.R;
import com.example.front_ui.Utils.AlgoliaUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
* oncreate : bundle에서 데이터를 꺼낸다.
* onCreateView : 버튼 및 평점 세팅, postingOnFirebaseDatabase()
* postingOnFirebaseDatabase() : 사진 업로드 + setAndSendPosting
* setAndSendPosting() : 데이터 세팅(store, posting) + checkStoreData
* checkStoreData() : 해당 가게가 있는지 확인. 없으면 putNewStoreInfo, 있으면 changeStarInfo
* putNewStoreInfo() : store데이터를 db에 보낸다. + putPostingInfo
* updatesFirestore() : store데이터의 별점 변경. +  putPostingInfo
* putPostingInfo() : 포스팅 정보를 올린다.
 */



public class LastShareFragment extends Fragment {

    private final String TAG = "TAGLastShareFragment";
    RatingBar mRatingBar;
    TextView mStarText;
    EditText text_description;
    EditText tags;
    TextView text_title;
    List<Double> detail_aver_star;
    GeoPoint geoPoint;


    KakaoStoreInfo kakaoStoreInfo;
    byte[] byteImage;

    FirebaseStorage storage;
    FirebaseFirestore db;
    Spannable mspanable;
    int hashTagIsComing = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate!");
        //Store Search Fragment에서 받은 bundle데이터(네이터 api 검색 결과 선택 항목)을 받는다.
        if (getArguments() != null) {
            kakaoStoreInfo = getArguments().getParcelable("storeData");
            byteImage = getArguments().getByteArray("byteArray");
            Log.d(TAG, "kakaoStoreInfo : " + kakaoStoreInfo.place_name);
            if(byteImage == null)
                Log.e(TAG, "no byteImage");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_last_share, container, false);
        Log.d(TAG, "onCreateView!");

//        text_description = view.findViewById(R.id.search_btn);
        mRatingBar = view.findViewById(R.id.ratingBar);
        mStarText = view.findViewById(R.id.starText);
        detail_aver_star = new ArrayList<Double>() {};
//        String text = text_description.getText().toString();
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();

        //back 버튼 - storesearch로 이동.... 그 바꿔야하나...
        ImageView backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick : closing the LastShare fragment, back to restaurant search fragment");
                //back button 기능
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStackImmediate();

            }
        });
        //해쉬태그 처리
        tags = (EditText)view.findViewById(R.id.hashTag);
        mspanable = tags.getText();

        tags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String startChar = null;

                try{
                    startChar = Character.toString(s.charAt(start));
                    Log.i(TAG, "새로운 태그의 첫글자 : "+startChar);
                }catch (Exception e){
                    startChar = "";
                }
                if (startChar.equals("#")){
                    changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }
                if(startChar.equals(" ")){
                    hashTagIsComing = 0;
                }
                if(hashTagIsComing != 0){
                    changeTheColor(s.toString().substring(start), start, start + count);
                    hashTagIsComing++;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            private void changeTheColor(String s, int start, int end) {
                mspanable.setSpan(
                        new ForegroundColorSpan(
                                getResources().getColor(R.color.colorAccent)),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });

        //share 버튼
        ImageView completeButton = view.findViewById(R.id.imageViewComplete);
        completeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onclick : share!!!!!");
                //이미지, 가게정보, 태그 등을 파이어베이스로 올리는 함수 실행
                postingOnFirebaseDatabase();
                updateUserPostingNum();
                //다끝나면 액티비티 종료
                getActivity().finish();
            }
        });

        //Star Rating Bar setup
        setupStarRatingBar();
        return view;
    }



    //ratingBar Listener
    private void setupStarRatingBar(){
        //맛
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                setStarText(mRatingBar, mStarText);
            }
        });
    }


    private void setStarText(RatingBar b, TextView v){ //변경
        v.setText(String.valueOf(v));
        float i = (float) b.getRating();
        if (i == 1) {
            v.setText("1.0");
        } else if (i == 2) {
            v.setText("2.0");
        } else if (i == 3) {
            v.setText("3.0");
        } else if (i == 4) {
            v.setText("4.0");
        } else if (i == 5) {
            v.setText("5.0");
        } else {
            v.setText("");
        }
    }


    //사진 upload 후, 사진 업로드가 완료되면 업로드된 url을 가져온 후, db에 다른 정보들과 함께 세팅한다.
    private void postingOnFirebaseDatabase() {
        Log.d(TAG, "start postingOnFirebaseDatabase");
        String uploadDir = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        StorageReference storageRef = storage.getReference();
        String filename = UUID.nameUUIDFromBytes(byteImage).toString();
        final StorageReference imagesRef = storageRef.child("images/"+uploadDir+"/"+filename+".jpg");

        UploadTask uploadTask = imagesRef.putBytes(byteImage);
        //get upload Url
        Task<Uri> urlTask = uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG, "sending image to firebase is failed");
                // Handle unsuccessful uploads
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String downloadUriStr = downloadUri.toString();
                    Log.d(TAG, "Getting upload url is completed. download url : " + downloadUriStr);

                    //전달해야하는 데이터 세팅 및 데이터 전달
                    setAndSendPosting(downloadUriStr);

                } else {
                    // Handle failures
                    Log.w(TAG, "Getting upload url is failed.");
                }
            }
        });
    }

    private void setAndSendPosting(String imagePathInStorage) {
        /*
        * 함수 구조
        * 1. set the Posting data into postingInfo
        * 2. set the Store Data into storeInfo
        * 3. check the data in log cat
        * 4. add store info and posting info in dataBase!!!!
         * */
        //1. set the Posting data!!
        final PostingInfo postingInfo = new PostingInfo();
        postingInfo.writerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postingInfo.writerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        postingInfo.postingTime = Timestamp.now();
        postingInfo.hashTags = tags.getText().toString();
        detail_aver_star.add((double)mRatingBar.getRating());//맛
        postingInfo.detail_aver_star = detail_aver_star;
        postingInfo.storeName = kakaoStoreInfo.place_name;
        postingInfo.address = kakaoStoreInfo.road_address_name;

        //평균값 설정.
        float sum =0 ;
        for(int i=0 ; i<detail_aver_star.size(); i++ ) {
            sum += detail_aver_star.get(i);
        }

        float aver_star = sum/detail_aver_star.size();
        postingInfo.aver_star = aver_star;
        postingInfo.imagePathInStorage = imagePathInStorage;

        //2. 해당 가게 정보가 이미 올라와있으면 받아온다. 없으면 새로 넣는다.
        //2. set the Store data!!
        float x = Float.parseFloat(kakaoStoreInfo.x);
        float y = Float.parseFloat(kakaoStoreInfo.y);
        geoPoint = new GeoPoint(y, x);
        StoreInfo storeInfo = new StoreInfo(kakaoStoreInfo.place_name, aver_star, kakaoStoreInfo.address_name, detail_aver_star, new GeoPoint(y, x));
        storeInfo.setKakaoId(kakaoStoreInfo.id);
        checkStoreData(storeInfo, postingInfo);


        //3. log cat에서 확인하기 위한 코드
        Log.d(TAG, "postingInfo - \n " +
                "imagePathInStorage : " + postingInfo.imagePathInStorage +"\n"+
                "postingTime : " + postingInfo.postingTime +"\n" +
                "description : "  + postingInfo.description +"\n"+
                "writerId : "  + postingInfo.writerId +"\n"+
                "aver_star : "  + postingInfo.aver_star +"\n");
        for(int i=0 ; i<detail_aver_star.size(); i++ ) {
            Log.d(TAG, "detail_aver_star ["+ i +"] : " + detail_aver_star.get(i));
        }

    }

    private void checkStoreData(final StoreInfo storeInfo, final PostingInfo postingInfo) {

        Log.d(TAG, "getStoreDataFromFirestore. storeName : " + storeInfo.name +", address :  " + storeInfo.address);


        //이미 해당 가게 정보가 있는지 확인
        db.collection("가게")
                .whereEqualTo("name", postingInfo.storeName)
                .whereEqualTo("address", storeInfo.address)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            //조건에 해당하는게 없는지 확인. 없으면 새로 넣어준다.

                            if(task.getResult().isEmpty()) {
                                Log.d(TAG, "task.getResult : " + task.getResult().isEmpty());
                                storeInfo.setPostingNum(1);
                                putNewStoreInfo(storeInfo, postingInfo);
                            }else {
                                //있으면 업데이트 해준다.
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "get document!! : " + document.getId() + " => " + document.getData());
                                    //postingInfo에 따라 별점 데이터를 바꾸어 준다.
                                    changeStarAndUpdateStoreData(postingInfo, document.getId());

                                    //해당 도큐먼트의 내용을 바뀐 별점 내용으로 바꾸어주고, collection에 postingInfo를 넣어준다.
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



    }

    //postingInfo의 별점을 storeInfo에 넣어준다.
    //평점 바꾸어주는 코드 필요. 데이터 write까지
    private void changeStarAndUpdateStoreData(final PostingInfo postingInfo, final String storeId) {
        Log.d(TAG, "changeStarAndUpdateStoreData.");
        //트랜잭션으로 원자적으로 사용. 데이터 가져와서 세팅!
        final DocumentReference storeRef = db.collection("가게").document(storeId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Map<String,Object> storeInfo = transaction.get(storeRef).getData();


                long postingNum = (long)storeInfo.get("postingNum");
                double aver_star = (double)storeInfo.get("aver_star");
                // Compute new number of ratings
                long newNumRatings = postingNum + 1;

                // Compute new average rating
                double oldRatingTotal = aver_star * postingNum;
                double newAvgRating = (oldRatingTotal + postingInfo.getAver_star()) / newNumRatings;

                // Set new restaurant info
                storeInfo.put("postingNum", newNumRatings);
                storeInfo.put("aver_star", newAvgRating);


                // Update restaurant
                transaction.set(storeRef, storeInfo);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });

        putPostingInfo(storeId, postingInfo);

    }

    //add new store info and posting info in dataBase!!!!
    private void putNewStoreInfo(final StoreInfo storeInfo, final PostingInfo postingInfo) {
        final String docUUID = UUID.randomUUID().toString();
        storeInfo.setStoreId(docUUID);

        db.collection("가게")
                .document(docUUID)
                .set(storeInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "store컬렉션 내부 post컬렉션 내부 포스팅 ID: " + docUUID);
                        //만든 storeInfo에 geoPoint를 세팅한다.
                        CollectionReference geoFirestoreRef = FirebaseFirestore.getInstance().collection("가게");
                        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
                        geoFirestore.setLocation(docUUID, geoPoint);
                        geoFirestore.setLocation(docUUID, geoPoint, new GeoFirestore.CompletionListener() {
                            @Override
                            public void onComplete(Exception exception) {//setLocation이 성공했는지 확인

                                if (exception == null){
                                    Log.d(TAG, "Location saved on server successfully!");
                                }
                            }
                        });

                        //algoia에 데이터 추가
                        AlgoliaUtils.addObject(storeInfo);

                        //store collection document내부에 post컬렉션에 데이터를 넣는다.
                        putPostingInfo(docUUID, postingInfo);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "가게 내부 포스팅 채널. Error writing document on putPosting", e);
                    }
                });

    }

    private void putPostingInfo(String storeId, PostingInfo postingInfo){
        //db에 데이터를 넣는 코드 필요
        final String docUUID = UUID.randomUUID().toString();
        postingInfo.setPostingId(docUUID);
        //posting document uuid
        postingInfo.setStoreId(storeId);
        db.collection("가게").document(storeId).collection("포스팅채널").document(docUUID)
                .set(postingInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "store컬렉션 내부 post컬렉션 내부 포스팅 ID: " + docUUID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "가게 내부 포스팅 채널. Error writing document on putPosting", e);
                    }
                });

        db.collection("포스팅").document(docUUID)
                .set(postingInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "포스팅 컬렉션: " + docUUID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "포스팅 collection setting. Error adding document", e);
                    }
                });
    }

    /*
    * 사용자 데이터 게시글 수 업데이트
    * */
    void updateUserPostingNum(){

        final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "updateUserPosting num. user uid : " + userUid);
        db.collection("사용자")
                .document(userUid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        long postingNum = (long)documentSnapshot.get("postingNum") + 1;
                        Log.d(TAG, "updateUserPostingNum. new num : " + postingNum);

                        Map<String, Long> data = new HashMap<>();
                        data.put("postingNum", postingNum);

                        db.collection("사용자").document(userUid)
                                .set(data, SetOptions.merge());
                    }
                });




    }
}
