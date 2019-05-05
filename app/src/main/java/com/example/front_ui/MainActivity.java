package com.example.front_ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonToLogin;
    Button buttonToSingIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonToLogin = findViewById(R.id.button_login);
        buttonToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToLoginActivity();
            }
        });

        buttonToSingIn = findViewById(R.id.button_signIn);
        buttonToSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToSignInActivity();
            }
        });
    }

    public void moveToLoginActivity() {
        Intent intent = new Intent(this, SubActivity.class);
        startActivity(intent);
    }

    public void moveToSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
