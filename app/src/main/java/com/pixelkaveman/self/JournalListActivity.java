package com.pixelkaveman.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import adapter.JournalRecyclerAdapter;
import model.Journal;
import util.JournalApi;

public class JournalListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private com.google.firebase.auth.FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    List<Journal> journalList ;
    RecyclerView recyclerView;
    JournalRecyclerAdapter journalRecyclerAdapter;
    RecyclerView.LayoutManager linearLayoutManager;

    CollectionReference collectionReference = db.collection("Journal");
    TextView noJournalEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noJournalEntry = findViewById(R.id.list_no_thoughts);

        journalList = new ArrayList<Journal>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_add:
                //Take users to add journal
                if(user != null && firebaseAuth != null){

                    startActivity(new Intent(JournalListActivity.this , PostJournalActivity.class));
                    //finish();
                }
                break;

            case R.id.action_signout:
                //sign user out
                if (user != null && firebaseAuth != null){
                    firebaseAuth.signOut();

                    startActivity(new Intent(JournalListActivity.this ,
                            MainActivity.class));
                    //finish();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //here we will get all our journals

        collectionReference.whereEqualTo("userId" , JournalApi.getInstance()
                .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (!queryDocumentSnapshots.isEmpty()){

                            for (QueryDocumentSnapshot journals : queryDocumentSnapshots){
                                //add journal inside the journalList
                                Journal journal = journals.toObject(Journal.class); // converting into Journal object
                                journalList.add(journal);
                            }

                            //Invoke recyclerview
                            JournalRecyclerAdapter journalRecyclerAdapter = new JournalRecyclerAdapter(JournalListActivity.this
                                    , journalList);
                            recyclerView.setAdapter(journalRecyclerAdapter);
                        }
                        else{
                            noJournalEntry.setVisibility(View.VISIBLE);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}
