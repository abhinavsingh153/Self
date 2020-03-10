package com.pixelkaveman.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG =  "createAccountActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

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

        firebaseAuth = FirebaseAuth.getInstance();
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

                if (!TextUtils.isEmpty(usernameEditText.getText().toString())
                && !TextUtils.isEmpty(emailEditText.getText().toString())
                && !TextUtils.isEmpty(passwordEditText.getText().toString())) {

                    String username = usernameEditText.getText().toString().trim();
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();

                    createUserEmailAccount(email, password, username);
                }
                else{

                    Toast.makeText(CreateAccountActivity.this ,
                            "Empty fields not allowed." ,
                            Toast.LENGTH_LONG);
                }
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
                                final String currentUserId = currentUser.getUid();

                                //create a map so we can create a user in the user ciollection in the firestore.
                                Map<String , String> userObj = new HashMap<>();
                                userObj.put("userId" , currentUserId);
                                userObj.put("username" , username);

                                //save to our firestore database.

                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                                if (Objects.requireNonNull(task.getResult().exists())){

                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                    String name = task.getResult()
                                                                            .getString("username");

                                                                    JournalApi journalApi = JournalApi.getInstance(); // Global API
                                                                    journalApi.setUsername(name);
                                                                    journalApi.setUserId(currentUserId);

                                                                    Intent intent = new Intent(CreateAccountActivity.this ,
                                                                            PostJournalActivity.class);
                                                                    intent.putExtra("username" , name);
                                                                    intent.putExtra("userId" , currentUserId);
                                                                    startActivity(intent);

                                                                }
                                                                else{

                                                                    progressBar.setVisibility(View.INVISIBLE);

                                                                }
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                            }else{

                                //something went wrong
                                Toast.makeText(CreateAccountActivity.this , "Something went wrong" , Toast.LENGTH_LONG).show();
                                Log.d(TAG , task.toString());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG , e.toString());

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
