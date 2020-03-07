package com.pixelkaveman.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button createAccountButton;

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        progressBar = findViewById(R.id.create_acct_progress);
        createAccountButton = findViewById(R.id.create_account_button);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null){
                    // user is alreday logged in
                }
                else{
                    //no user yet.

                }
            }
        };

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                createUserEmailAccount(email , password , username);
            }
        });
    }

    private void createUserEmailAccount(String email , String password , final String username){

        if (!TextUtils.isEmpty(email)
        && !TextUtils.isEmpty(password)
        && !TextUtils.isEmpty(username)){

            progressBar.setVisibility(View.VISIBLE);

            //TODO: Create account

            firebaseAuth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                //we take user to the AddJournalActivity.

                                currentUser = firebaseAuth.getCurrentUser();
                                String currentUserId = currentUser.getUid();

                                //create a map so we can create a user in the user ciollection in the firestore.
                                Map<String , String> userObj = new HashMap<>();
                                userObj.put("userId" , currentUserId);
                                userObj.put("username" , username);

                                //save to our firestore database.

                            }else{

                                //something went wrong
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

        }else{

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
