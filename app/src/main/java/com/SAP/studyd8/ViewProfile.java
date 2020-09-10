package com.SAP.studyd8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class ViewProfile extends AppCompatActivity {

    TextView topText, username, name, university, major, studyHabits;
    Button addCourses, editProfile;
    private String uid;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        topText = (TextView) findViewById(R.id.profileTopText);
        username = (TextView) findViewById(R.id.username);
        name = (TextView) findViewById(R.id.Name);
        university = (TextView) findViewById(R.id.university);
        major = (TextView) findViewById(R.id.major);
        addCourses = (Button) findViewById(R.id.addCourses);
        editProfile = (Button) findViewById(R.id.editButton);
        studyHabits = (TextView)findViewById(R.id.studyHabitsText);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ref = db.collection("users").document(uid);

        ref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText("Username: " + value.getString("username"));
                name.setText("Name: " + value.getString("firstName") + " " + value.getString("lastName"));
                university.setText("University: " + value.getString("university"));
                major.setText("Major: " + value.getString("major"));
                studyHabits.setText("Study Habits: "+ value.getString("studyHabits"));
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileEdit.class));
                finish();
            }
        });


    }
}
