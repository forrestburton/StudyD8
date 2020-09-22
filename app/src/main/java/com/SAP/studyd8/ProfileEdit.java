package com.SAP.studyd8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ProfileEdit extends AppCompatActivity {

    private final String changeUniversity = "PROFILE";
    EditText firstName, lastName, major, studyHabits;
    TextView username;
    Button finishButton, switchUniversity;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, user_username, user_firstName, user_lastName, user_major, user_university, user_studyHabits;
    List<String> courses;
    Bundle bundle;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
       // username = findViewById(R.id.profileUsername);
        switchUniversity = findViewById(R.id.switchUniversityButton);
        firstName = findViewById(R.id.profileFirstName);
        lastName = findViewById(R.id.profileLastName);
        major = findViewById(R.id.profileMajor);
        studyHabits = findViewById(R.id.profileStudyHabits);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        //see if university was changed
        Intent intent = getIntent();
        bundle = intent.getExtras();

        ref = db.collection("users").document(userId);

        ref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
            //    username.setText(value.getString("username"));
                firstName.setText(value.getString("firstName"));
                lastName.setText(value.getString("lastName"));
                major.setText(value.getString("major"));
                studyHabits.setText(value.getString("studyHabits"));

                //if university was changed, then get new university. Otherwise, keep current university
                if(bundle != null) {
                    user_university = bundle.getString("currentUniversity");
                }
                else {
                    user_university = value.getString("university");
                }
            }
        });

        finishButton = findViewById(R.id.finishButton);
        try {
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  user_username = username.getText().toString().trim();
                    user_firstName = firstName.getText().toString().trim();
                    user_lastName = lastName.getText().toString().trim();
                    user_major = major.getText().toString().trim();
                    user_studyHabits = studyHabits.getText().toString().trim();

                    addUserData();

                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        try {
            switchUniversity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProfileEdit.this, UniversitySearch.class);
                    intent.putExtra("changeUniversity", changeUniversity);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void addUserData()
    {
        DocumentReference documentReference = fStore.collection("users").document(userId);

        //get courses and username from firestore
        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user_username = documentSnapshot.getString("username");
                        courses = (List<String>) documentSnapshot.get("courses");

                        //create new userModel with updated course list
                        UserModel userModel = new UserModel(userId, user_username, user_firstName, user_lastName, user_major, user_university, user_studyHabits, courses);

                        //update user profile with added course
                        ref.set(userModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "An error has occured", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }
}