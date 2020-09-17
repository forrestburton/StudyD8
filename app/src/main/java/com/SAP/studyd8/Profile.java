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
    EditText university, username, firstName, lastName, major, studyHabits;
    Button finishButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId, user_username, user_firstName, user_lastName, user_major, user_university, user_studyHabits;
    List<String> courses;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userRef = db.collection("users");

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

        if (fAuth.getCurrentUser() == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
        userId = fAuth.getCurrentUser().getUid();
        finishButton = findViewById(R.id.finishButton);
        try {
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    user_username = username.getText().toString().trim();
                    user_firstName = firstName.getText().toString().trim();
                    user_lastName = lastName.getText().toString().trim();
                    user_major = major.getText().toString().trim();
                    user_university = university.getText().toString().trim();
                    user_studyHabits = studyHabits.getText().toString().trim();

                    // username contains only letters and numbers
                    user_username = user_username.replaceAll("[^a-zA-Z0-9]", "");
                    username.setText(user_username);


                    // unique username stuff - identifies if the username is unique
                    // doesn't move on if username is not unique
                    CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
                    Query query = usersRef.whereEqualTo("username", user_username);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                    String user = documentSnapshot.getString("username");

                                    if(user.equalsIgnoreCase(user_username)){
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
        DocumentReference documentReference = fStore.collection("users").document(userId);


        UserModel userModel = new UserModel(userId, user_username, user_firstName, user_lastName, user_major, user_university, user_studyHabits, courses);

        userRef.add(userModel);
        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}