package com.example.front_ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.front_ui.DataModel.PostingInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

public class Dialog_Answer extends Dialog {

    Context context;
    private PostingInfo postingInfo;
    private String commentDocId;

    private EditText input;
    private Button submit;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "TAGDialog_Answer";

    public Dialog_Answer(Context context,
                         PostingInfo postingInfo,
                         String commentDocId) {
        super(context);
        this.context = context;
        this.postingInfo = postingInfo;
        this.commentDocId = commentDocId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_answer);

        input = findViewById(R.id.answer_edittext_dialog);
        submit = findViewById(R.id.answer_button_dialog);

        //답변을 디비에 전송
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("답변 등록중입니다.");
                progressDialog.show();

                String inputText = input.getText().toString();

                db.collection("포스팅")
                        .document(postingInfo.postingId)
                        .collection("댓글")
                        .document(commentDocId)
                        .update("answer", inputText)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "성공적으로 답변이 업로드 되었습니다.");

                                dismiss();
                                progressDialog.dismiss();
                            }
                        });
            }
        });

    }
}
