package com.pixelkaveman.self;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG ="loginActivity" ;
    private Button loginButton;
    private Button createAccountButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.email_login_button);
        createAccountButton =findViewById(R.id.create_acc_button_login);

        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.email_login_button:
                loadAddJournalsActivity();
                break;

            case R.id.create_acc_button_login:
                loadCreateAccountActivity();
                break;
        }
    }

    private void loadAddJournalsActivity(){
        Log.d(TAG , "Load create journal activity");
    }

    private void loadCreateAccountActivity(){
        Intent intent = new Intent(this , CreateAccountActivity.class);
        startActivity(intent);
    }
}
