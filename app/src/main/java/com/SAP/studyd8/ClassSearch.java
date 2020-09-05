package com.SAP.studyd8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassSearch extends AppCompatActivity {

    private static final String TAG = "" ;
    String currentUniversity = "";
    private FirebaseFirestore mFirestore;
    ListView lvClasses;
    CustomAdapter classAdapter;
    Button button;

    String codes[] = {"1111", "2222", "7777"};
    String names[] = {"CS 31", "CS 32", "PHIL 7"};
    List<ClassModel> classList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_search);

        lvClasses = findViewById(R.id.lvClasses);
        button = findViewById(R.id.addButton);

        //Get name of university selected
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            currentUniversity = bundle.getString("university");
        }

        //Get classes from firebase
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection(currentUniversity)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String className = document.getString("Name");
                                String id = document.getString("ID");

                                ClassModel classModel = new ClassModel(id, className);
                                classList.add(classModel);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        classAdapter = new CustomAdapter(classList, getApplicationContext());
                        lvClasses.setAdapter(classAdapter);
                    }
                });

        //if button clicked go to add class screen
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddClass.class));
            }
        });
    }


    //IMPLEMENTING SEARCH VIEW
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search by class code");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                classAdapter.getFilter().filter(newText);

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_view) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Custom Adapter for ClassModel Class
    public class CustomAdapter extends BaseAdapter implements Filterable {

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
                    // String currentUniversity = classesFiltered.get(position).getName();
                }
            });

            return view;
        }

        //Filtering search results
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();

                    if (constraint == null || constraint.length() == 0) {
                        filterResults.count = classes.size();
                        filterResults.values = classes;
                    } else {
                        String searchStr = constraint.toString().toLowerCase();
                        List<ClassModel> resultData = new ArrayList<>();

                        for (ClassModel classModel : classes) {
                            if (classModel.getClassCode().toLowerCase().contains(searchStr)) {
                                resultData.add(classModel);
                            }
                        }

                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    classesFiltered = (List<ClassModel>) results.values;

                    notifyDataSetChanged();
                }
            };

            return filter;
        }
    }
}