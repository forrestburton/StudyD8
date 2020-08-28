package com.SAP.studyd8;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ViewProfile extends AppCompatActivity {

    TextView topText, username, name, university, major;
    EditText studyHabits;
    Button addCourses, submitStudy;
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
        studyHabits = (EditText) findViewById(R.id.studyHabitsBox);
        submitStudy = (Button) findViewById(R.id.submitStudy);
        addCourses = (Button) findViewById(R.id.addCourses);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ref = db.collection("users").document(uid);

        ref.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText("Username: " + value.getString("username"));
                name.setText("Name: " + value.getString("firstName") + " " + value.getString("lastName"));
                university.setText("University: " + value.getString("university"));
                major.setText("Major: " + value.getString("major"));
                studyHabits.setHint(value.getString("studyHabits"));
            }
        });

        submitStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = studyHabits.getText().toString().trim();
                DocumentReference noteRef = db.collection("users").document(uid);
                noteRef.update("studyHabits", input);
            }
        });
    }
}
