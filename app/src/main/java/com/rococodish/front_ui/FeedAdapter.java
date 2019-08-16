package com.rococodish.front_ui;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
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

import org.apache.http.annotation.Immutable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_feed_item, viewGroup,  false);
        ItemRowHolder mh = new ItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int i) {
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

        //TODO : 좋아요 총 기능,  모듈처럼 사용할 수 있도록 함수로 제작

        final CollectionReference likeCollRef = FirebaseFirestore.getInstance()
                .collection("포스팅")
                .document(postingInfo.postingId)
                .collection("좋아요");
        final DocumentReference likeDocRef = likeCollRef.document(FirebaseAuth.getInstance().getUid());

        //좋아요 모듈~~
        setLikeSystem(likeCollRef, likeDocRef, itemRowHolder.tvLikeNum, itemRowHolder.ivLike);


        //댓글 개수 띄우기
        FirebaseFirestore.getInstance()
                .collection("포스팅")
                .document(postingInfo.postingId)
                .collection("댓글")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        assert queryDocumentSnapshots != null;

                        itemRowHolder.tvComment.setText(String.format("댓글 %d개 보기", queryDocumentSnapshots.getDocuments().size()));
                    }
                });

    }
    //좋아요 총괄 함수
    public void setLikeSystem(CollectionReference likeCollRef,
                              final DocumentReference likeDocRef,
                              final TextView tv_numLike,
                              final ImageView iv_Like){
        //todo : 좋아요 개수 띄우기
        likeCollRef
                .whereEqualTo("isLiked", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        assert queryDocumentSnapshots != null;

                        tv_numLike.setText(String.format("좋아하는 사람 %d 명", queryDocumentSnapshots.getDocuments().size()));
                    }
                });

        //todo : 좋아요 상태 띄우기
        likeDocRef
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.d(TAG, e.getMessage());
                        }
                        if(documentSnapshot.exists()){
                            Boolean isLiked = documentSnapshot.getBoolean("isLiked");

                            iv_Like.setImageResource(isLiked? R.drawable.ic_heart : R.drawable.ic_grey_heart);

                            likeClick(isLiked, iv_Like, likeDocRef);

                        }
                        else{

                            boolean isLiked = false;

                            iv_Like.setImageResource(R.drawable.ic_grey_heart);

                            likeClick(isLiked, iv_Like, likeDocRef);
                        }
                    }
                });
    }
    //좋아요 클릭함수
    public void likeClick(final boolean isLiked,
                   final ImageView iv_like,
                   final DocumentReference likeDocRef){

        //좋아요 애니메이션 적용부분
        final ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean _isLiked = !isLiked;

                iv_like.setImageResource(_isLiked? R.drawable.ic_heart : R.drawable.ic_grey_heart);

                if(_isLiked == true){
                    likeDocRef.set(ImmutableMap.of("isLiked", _isLiked));//false -> true일 경우가 아예 없는 경우가 있기 때문
                    iv_like.startAnimation(scaleAnimation);
                }
                else{
                    likeDocRef.update("isLiked", _isLiked);
                }
            }
        });

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
