package com.rococodish.front_ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.rococodish.front_ui.DataModel.PostingInfo;
import com.rococodish.front_ui.DataModel.StoreInfo;
import com.rococodish.front_ui.Utils.DataPassUtils;
import com.rococodish.front_ui.Utils.GlideApp;
import com.rococodish.front_ui.Utils.KakaoUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ItemRowHolder> {

    private final String TAG = "TAGFeedAdapter";
    private ArrayList<PostingInfo> mListPosting;
    private Context mContext;
    FrameLayout loadingFrame;
    FirebaseStorage storage;


    public FeedAdapter(Context context,
                       ArrayList<PostingInfo> list,
                       FrameLayout loadingFrame){
        this.mListPosting = list;
        this.mContext = context;
        this.loadingFrame = loadingFrame;
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder ");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_feed_item, null);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ItemRowHolder itemRowHolder, final int i) {
        final PostingInfo postingInfo = mListPosting.get(i);
        getStoreData(postingInfo, itemRowHolder);

        StorageReference fileReference = storage.getReferenceFromUrl(postingInfo.imagePathInStorage);
        GlideApp.with(mContext).load(fileReference).into(itemRowHolder.ivMain);

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        itemRowHolder.tvDay.setText(transFormat.format(postingInfo.getPostingTime()));
        itemRowHolder.tvStoreName.setText(postingInfo.getStoreName());
        itemRowHolder.tvAddress.setText(postingInfo.getAddress());
        itemRowHolder.tvScore.setText(Double.toString(postingInfo.getAver_star()));
        itemRowHolder.tvDescription.setText(postingInfo.getHashTags());

        itemRowHolder.tvProfileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyPage.class);
                intent.putExtra("userUUID", postingInfo.writerId);
                Log.d(TAG, "들어왔씁니다.");
                mContext.startActivity(intent);
            }
        });
        itemRowHolder.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyPage.class);
                intent.putExtra("userUUID", postingInfo.writerId);
                Log.d(TAG, "들어왔씁니다.");
                mContext.startActivity(intent);
            }
        });



        //TODO:태완님 좋아요 기능좀...
//        itemRowHolder.ivLike;
//        likeFunc();


//        itemRowHolder.tvLikeNum;
//        itemRowHolder.tvComment

    }

    @Override
    public int getItemCount() {
        return (null != mListPosting ? mListPosting.size() : 0);
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView ivLike;
        ImageView ivShare;
        ImageView ivProfile;
        ImageView ivMain;
        TextView tvProfileName;
        TextView tvDay;
        TextView tvDescription;

        TextView tvStoreName;
        TextView tvDistance;
        TextView tvAddress;
        TextView tvScore;
        TextView tvLikeNum;
        TextView tvComment;




        //세로부분 리사이클러 뷰 적용!
        public ItemRowHolder(View view) {
            super(view);
            this.ivMain = view.findViewById(R.id.iv_main);
            this.ivProfile = view.findViewById(R.id.iv_profile);
            this.ivLike = view.findViewById(R.id.iv_Like);
            this.ivShare = view.findViewById(R.id.iv_share);

            this.tvProfileName = view.findViewById(R.id.tv_profileName);
            this.tvDay = view.findViewById(R.id.tv_Day);
            this.tvStoreName = view.findViewById(R.id.tv_storeName);
            this.tvDistance = view.findViewById(R.id.tv_Distance);
            this.tvAddress = view.findViewById(R.id.tv_address);
            this.tvScore = view.findViewById(R.id.tv_Score);
            this.tvLikeNum = view.findViewById(R.id.tv_likeNum);
            this.tvComment = view.findViewById(R.id.tv_comment);
            this.tvDescription = view.findViewById(R.id.tv_description);



        }
    }


    private void getStoreData(final PostingInfo postingInfo, final ItemRowHolder itemRowHolder) {
        FirebaseFirestore.getInstance()
                .collection("가게")
                .document(postingInfo.getStoreId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            // Document found in the offline cache
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, "document data: " + document.getData().toString());
                            //가게 데이터를 가져오면, distance, share, storeName, 이미지 이동 등 세팅


                            final StoreInfo storeInfo = document.toObject(StoreInfo.class);
                            itemRowHolder.tvDistance.setText(SubActivity.getDistanceStr(storeInfo.getGeoPoint().getLatitude(), storeInfo.getGeoPoint().getLongitude()) + "!");
                            itemRowHolder.ivShare.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    shareFunc(postingInfo, storeInfo);
                                }
                            });
                            itemRowHolder.tvStoreName.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(mContext, StorePageActivity.class);

                                    Log.d(TAG, "name : " + storeInfo.getName() + " averStar : " + storeInfo.getAver_star() + " docId : " + storeInfo.getStoreId());
                                    intent.putExtra("storeName", storeInfo.getName());
                                    intent.putExtra("averStar", storeInfo.getAver_star());
                                    intent.putExtra("documentId", storeInfo.getStoreId());

                                    mContext.startActivity(intent);
                                }
                            });
                            itemRowHolder.ivMain.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    //DishView로 이동
                                    //데이터 전달
                                    Intent intent = new Intent(mContext, DishView.class);
                                    DataPassUtils.makeIntentForData(intent, postingInfo, storeInfo);

                                    mContext.startActivity(intent);
                                }
                            });
                            itemRowHolder.tvAddress.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    KakaoUtils.OpenKakaoMap(mContext, storeInfo.getKakaoId(), storeInfo.getGeoPoint().getLatitude(), storeInfo.getGeoPoint().getLongitude());
                                }
                            });
                            loadingFrame.setVisibility(View.GONE);

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }


                });
        FirebaseFirestore.getInstance()
                .collection("사용자")
                .document(postingInfo.getWriterId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if (documentSnapshot.exists()){

                            itemRowHolder.tvProfileName.setText((String)documentSnapshot.get("nickname"));

                            //프로필 이미지 업로드
                            String userImage = (String) documentSnapshot.get("profileImage");
                            if(userImage != null){
                                GlideApp.with(mContext)
                                        .load(userImage)
                                        .into(itemRowHolder.ivProfile);
                            }
                            else{
                                GlideApp.with(mContext)
                                        .load(R.drawable.basic_user_image)
                                        .into(itemRowHolder.ivProfile);
                            }


                        }
                    }
                });
    }

    private void shareFunc(PostingInfo postingInfo, StoreInfo storeInfo){
        String disStr = SubActivity.getDistanceStr(storeInfo.getGeoPoint().getLatitude(), storeInfo.getGeoPoint().getLongitude());
        Log.d(TAG, "num like " + postingInfo.getNumLike());
        LocationTemplate params = LocationTemplate.newBuilder(storeInfo.getAddress(),
                ContentObject.newBuilder(storeInfo.getName(),
                        postingInfo.imagePathInStorage,
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption(postingInfo.getHashTags())
                        .build())
                .setSocial(SocialObject.newBuilder().setLikeCount(postingInfo.getNumLike()).setCommentCount(0)
                        .setSharedCount(0).setViewCount(0).build())
                .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                        .setAndroidExecutionParams("distance="+disStr+"&postingId="+postingInfo.getPostingId()+"&storeId="+storeInfo.getStoreId())
                        .build()))
                .setAddressTitle(storeInfo.getName())
                .build();

        Map<String, String> serverCallbackArgs = new HashMap<String, String>();
        serverCallbackArgs.put("user_id", "sth534@naver.com");
        serverCallbackArgs.put("product_id", "292744");

        KakaoLinkService.getInstance().sendDefault(mContext, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
                // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
            }
        });

    }
}
