package com.rococodish.front_ui.Notice;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rococodish.front_ui.R;
import com.rococodish.front_ui.SubActivity;

public class NoticeActivity extends AppCompatActivity {

    NoticeAdapter noticeAdapter;
    RecyclerView rv_notice;
    ImageView backBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_page);

        if(isTaskRoot()){
            Intent intent = new Intent(NoticeActivity.this, SubActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("goNotice", "true");
            startActivity(intent);
            finish();
        }
        else{
            rv_notice = findViewById(R.id.rv_notice);
            noticeAdapter = new NoticeAdapter(this);
            rv_notice.setLayoutManager(new LinearLayoutManager(NoticeActivity.this, RecyclerView.VERTICAL, false));
            rv_notice.setAdapter(noticeAdapter);

            backBtn = findViewById(R.id.backButton);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }




    }
}
