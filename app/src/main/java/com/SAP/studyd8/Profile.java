package com.SAP.studyd8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {
    EditText university,username, firstName, lastName, major, studyHabits;
    Button finishButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    //private DatabaseReference myRef;
    //private FirebaseDatabase mFirebaseDatabase;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username = findViewById(R.id.profileUsername);
        university = findViewById(R.id.profileUniversity);
        firstName = findViewById(R.id.profileFirstName);
        lastName = findViewById(R.id.profileLastName);
        major = findViewById(R.id.profileMajor);
        studyHabits = findViewById(R.id.profileStudyHabits);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser()==null){
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
        }
        userId=fAuth.getCurrentUser().getUid();
        finishButton = findViewById(R.id.finishButton);
        try {
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String user_username = username.getText().toString().trim();
                    String user_firstName = firstName.getText().toString().trim();
                    String user_lastName = lastName.getText().toString().trim();
                    String user_major = major.getText().toString().trim();
                    String user_university = university.getText().toString().trim();
                    String user_studyHabits = studyHabits.getText().toString().trim();

                    // unique username stuff
                    /*
                    Query usernameQuery;
                    usernameQuery = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("username").equalTo(user_username);

                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.getChildrenCount() > 0)
                                Toast.makeText(getApplicationContext(), "Choose a different username", Toast.LENGTH_SHORT).show();
                            else
                            {

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
*/
                    DocumentReference documentReference = fStore.collection("users").document(userId);

                    Map<String, Object> user_data = new HashMap<>();
                    user_data.put("username", user_username);
                    user_data.put("firstName", user_firstName);
                    user_data.put("lastName", user_lastName);
                    user_data.put("major", user_major);
                    user_data.put("university", user_university);
                    user_data.put("studyHabits", user_studyHabits);


                    documentReference.set(user_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });



                }
            });
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}