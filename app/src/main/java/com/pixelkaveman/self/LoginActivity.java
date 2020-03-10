package com.pixelkaveman.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import model.Journal;
import util.JournalApi;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "loginActivity";
    private Button loginButton;
    private Button createAccountButton;
    private AutoCompleteTextView emailAddress;
    private EditText password;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private ProgressBar progressBar;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);
        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress);
        loginButton = findViewById(R.id.email_login_button);
        createAccountButton = findViewById(R.id.create_acc_button_login);

        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.email_login_button:
                loginEmailPasswordUser(emailAddress.getText().toString().trim()
                        , password.getText().toString().trim());
                break;

            case R.id.create_acc_button_login:
                loadCreateAccountActivity();
                break;
        }
    }


    private void loginEmailPasswordUser(String email, String password) {


        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                final String currentUserId = user.getUid();
                                collectionReference
                                        .whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                                                @Nullable FirebaseFirestoreException e) {

                                                if (e != null) {
                                                }
                                                assert queryDocumentSnapshots != null;
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        JournalApi journalApi = JournalApi.getInstance();
                                                        journalApi.setUsername(snapshot.getString("username"));
                                                        journalApi.setUserId(snapshot.getString("userId"));

                                                        //Go to ListActivity
                                                        startActivity(new Intent(LoginActivity.this,
                                                                PostJournalActivity.class));


                                                    }


                                                }

                                            }
                                        });

                            } else {
                                Toast.makeText(LoginActivity.this, "Please enter correct email and password.", Toast.LENGTH_LONG).show();

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);

                        }
                    });


        } else {

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,
                    "Please enter email and password",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void loadCreateAccountActivity() {
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        currentUser = firebaseAuth.getCurrentUser();
//        firebaseAuth.addAuthStateListener(authStateListener);
//    }
}
