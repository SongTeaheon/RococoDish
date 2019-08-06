package com.example.front_ui.Search;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.front_ui.R;

public class FragmentTag2 extends Fragment {
    // 가게 페이지랑 똑같이 만들면 됩니당. 배껴보려다 StorePostInfo가 코틀린이라 일단 뒀습니당.

    private String TAG = "TAGFragmentTag2";
    GridView tagGridview;
    TextView tagName;
    ImageView backBtn;
    ImageView sortBtn;
    FragmentTag2Adapter fragmentTag2Adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.store_page, container, false);

        tagGridview = view.findViewById(R.id.gridView0);
//        tagName = view.findViewById(R.id.tvStore);
        backBtn = view.findViewById(R.id.backButton);
//        sortBtn = view.findViewById(R.id.sort_button);
//
//        tagName.setText();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
//        sortBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //todo : 분류는 아직
//            }
//        });

        fragmentTag2Adapter = new FragmentTag2Adapter(getActivity(), tagName.getText().toString());
        tagGridview.setAdapter(fragmentTag2Adapter);

        return view;
    }
}
