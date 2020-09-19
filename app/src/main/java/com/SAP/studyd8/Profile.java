package com.SAP.studyd8;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {
    EditText university_text, username_text, firstName_text, lastName_text, major_text, studyHabits_text;
    Button finishButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String username, userId, firstName, lastName, major, studyHabits;
    String university = "";
    List<String> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username_text = findViewById(R.id.profileUsername);
        firstName_text = findViewById(R.id.profileFirstName);
        lastName_text = findViewById(R.id.profileLastName);
        major_text = findViewById(R.id.profileMajor);
        studyHabits_text = findViewById(R.id.profileStudyHabits);

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }

        finishButton = findViewById(R.id.finishButton);
        try {
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    username = username_text.getText().toString().trim();
                    firstName = firstName_text.getText().toString().trim();
                    lastName = lastName_text.getText().toString().trim();
                    major = major_text.getText().toString().trim();
                    studyHabits = studyHabits_text.getText().toString().trim();

                    // username contains only letters and numbers
                    username = username.replaceAll("[^a-zA-Z0-9]", "");
                    username_text.setText(username);


                    // unique username stuff - identifies if the username is unique
                    // doesn't move on if username is not unique
                    CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
                    Query query = usersRef.whereEqualTo("username", username);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String user = documentSnapshot.getString("username");

                                    if(user.equalsIgnoreCase(username)){
                                        Log.d("TAG", "User Exists");
                                        Toast.makeText(getApplicationContext(), "Please choose a unique username", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            if(task.getResult().size() == 0 ){
                                Log.d("TAG", "User DNE");
                                addUserData();
                            }
                        }
                    });
                }




            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void addUserData()
    {
        //get user id
        userId = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(userId);

        //create a user model containing all aspects of a users profile, then upload to firestore
        UserModel userModel = new UserModel(userId, username, firstName, lastName, major, university, studyHabits, courses);
        documentReference.set(userModel);

        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), UniversitySearch.class));
    }
}