package com.SAP.studyd8;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RemoveClasses extends AppCompatActivity {

    ListView lvClasses;
    Button removeAll;
    CustomAdapter removeClassAdapter;
    String userId, user_username, user_firstName, user_lastName, user_major, user_university, user_studyHabits, currentClassID, currentClassName;
    private FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private DocumentReference ref;
    List<String> courses;

    List<ClassModel> classList = new ArrayList<>();    //vector holding universities. Change to firebase for classes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_classes);

        lvClasses = findViewById(R.id.lvClasses);
        removeAll = findViewById(R.id.removeButton);

        //get courses from firebase
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ref = mFirestore.collection("users").document(userId);

        ref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        List<String> tempList = (List<String>) documentSnapshot.get("courses");
                        int courseSize = tempList.size();

                        for (int i = 0; i < courseSize; i += 2) {
                            String tempId = tempList.get(i);
                            String tempName = tempList.get(i + 1);
                            ClassModel tempClass = new ClassModel(tempId, tempName);

                            classList.add(tempClass);
                        }
                        removeClassAdapter = new CustomAdapter(classList, getApplicationContext());
                        lvClasses.setAdapter(removeClassAdapter);
                    }
                });

        //if button clicked go to add class screen
        removeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start class search when a college is clicked
                Toast.makeText(getApplicationContext(), "All classes removed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RemoveClasses.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    //Custom Adapter for ClassModel Class
    public class CustomAdapter extends BaseAdapter {

        private List<ClassModel> classes;
        private List<ClassModel> classesFiltered;
        private Context context;

        public CustomAdapter(List<ClassModel> classes, Context context) {
            this.classes = classes;
            this.classesFiltered = classes;
            this.context = context;
        }

        @Override
        public int getCount() {
            return classesFiltered.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.class_row_items, null);

            TextView classCode = view.findViewById(R.id.classCode);
            TextView className = view.findViewById(R.id.className);

            classCode.setText(classesFiltered.get(position).getClassCode());
            className.setText(classesFiltered.get(position).getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //get class clicked on and store class ID     !!RETRIEVE OLD DATA FIRST??
                    currentClassID = classesFiltered.get(position).getClassCode();
                    currentClassName = classesFiltered.get(position).getName();

                    //get users ID, then get document of specific user
                    userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    ref = mFirestore.collection("users").document(userId);

                    //get user data from firestore
                    ref.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    user_username = documentSnapshot.getString("username");
                                    user_firstName = documentSnapshot.getString("firstName");
                                    user_lastName = documentSnapshot.getString("lastName");
                                    user_major = documentSnapshot.getString("major");
                                    user_university = documentSnapshot.getString("university");
                                    user_studyHabits = documentSnapshot.getString("studyHabits");

                                    //update courses array
                                    List<String> tempList = (List<String>) documentSnapshot.get("courses");
                                    tempList.remove(currentClassID);
                                    tempList.remove(currentClassName);
                                    courses = tempList;

                                    //create new userModel with updated course list
                                    UserModel userModel = new UserModel(userId, user_username, user_firstName, user_lastName, user_major, user_university, user_studyHabits, courses);

                                    //update user profile with added course
                                    ref.set(userModel)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getApplicationContext(), "Course removed", Toast.LENGTH_SHORT).show();
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
            });

            return view;
        }
    }
}