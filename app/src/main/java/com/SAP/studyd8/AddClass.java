package com.SAP.studyd8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddClass extends AppCompatActivity {
    private static final String TAG = "AddClass";

    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "Name";

    private EditText editTextName;
    private EditText editTextID;
    private Button button;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String currentUniversity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        editTextName = findViewById(R.id.getClassName);
        editTextID  = findViewById(R.id.getClassID);
        button = findViewById(R.id.finishButton);
    }

    public void saveNote(View v) {
        String name = editTextName.getText().toString();
        String id = editTextID.getText().toString();

        Map<String, Object> note = new HashMap<>();
        note.put(KEY_ID, id);
        note.put(KEY_NAME, name);

        //Get name of university selected
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            currentUniversity = bundle.getString("university");
        }

        assert currentUniversity != null;
        db.collection(currentUniversity).document(name).set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddClass.this, "Class added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddClass.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

        //if button clicked go to add class screen
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start class search when a college is clicked
                Intent intent = new Intent(AddClass.this, ClassSearch.class);
                intent.putExtra("university", currentUniversity);
                startActivity(intent);
            }
        });
    }
}