package com.example.front_ui.PostingProcess;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.front_ui.R;

public class StoreSearchFragCafe extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_search_frag_store, container, false);


        return view;
    }
}

class StoreSearchFragCafeAdapter extends RecyclerView.Adapter<StoreSearchFragStoreAdapter.ViewHolder>{

    Context context;

    public StoreSearchFragCafeAdapter(Context context){
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public StoreSearchFragStoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_store_search_row, viewGroup, false);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull StoreSearchFragStoreAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
