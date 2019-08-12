package com.rococodish.front_ui.PostingProcess;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rococodish.front_ui.DataModel.KakaoStoreInfo;
import com.rococodish.front_ui.R;

import java.util.ArrayList;

class StoreSearchAdapterInViewPager extends RecyclerView.Adapter<StoreSearchAdapterInViewPager.ViewHolder>{
    final private String TAG = "TAGStoreSearchAdptVP";
    Context context;
    ArrayList<KakaoStoreInfo> dataList;

    public StoreSearchAdapterInViewPager(Context context, ArrayList<KakaoStoreInfo> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_store_search_row, viewGroup, false);
        ViewHolder mh = new ViewHolder(view);
        return mh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final KakaoStoreInfo kakaoStoreInfo = dataList.get(i);
        viewHolder.title.setText(kakaoStoreInfo.getPlace_name());
        viewHolder.address.setText(kakaoStoreInfo.getAddress_name());

        viewHolder.constraint.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //선택한 아이템뷰 확인 및 데이터 전달
                Log.d(TAG, "item clicked : " + kakaoStoreInfo.getPlace_name());
                //activity로 store정보를 보내준다.
                MainShareActivity activity = (MainShareActivity)context;
                activity.setKakaoStoreInfo(kakaoStoreInfo);
                //선택한 가게 정보 데이터를 bundle에 넣고 다음 프래그먼트로 이동
                setFragmentAndMove();

            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView address;
        public ConstraintLayout constraint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            address = itemView.findViewById(R.id.tv_address);
            constraint = itemView.findViewById(R.id.constraint_kakaostore);

        }

    }

    private void setFragmentAndMove() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((MainShareActivity)context).startActivityForResult(intent, 1001);
    }


}