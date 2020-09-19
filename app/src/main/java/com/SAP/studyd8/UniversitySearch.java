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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UniversitySearch extends AppCompatActivity {

    ListView lvUniversities;
    CustomAdapter universityAdapter;
    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    DocumentReference ref;
    String userId, user_username, user_firstName, user_lastName, user_major, user_studyHabits;
    String changeUniversity = "";
    private final String profile = "PROFILE";
    List<String> courses;

    int images[] = {R.drawable.ucla_logo, R.drawable.ucsb_logo, R.drawable.yale_logo, R.drawable.udub_logo, R.drawable.stanford_logo,
            R.drawable.westwash_logo, R.drawable.daffodil_logo, R.drawable.harvard_logo, R.drawable.alberta_logo, R.drawable.hopkins_logo,
            R.drawable.unc_charlotte_logo, R.drawable.virgtech_logo, R.drawable.uiuc_logo, R.drawable.os_logo, R.drawable.texasam_logo,
            R.drawable.northcarolina_logo, R.drawable.boston_logo, R.drawable.princeton_logo, R.drawable.berkeley_logo, R.drawable.chapman_logo,
            R.drawable.columbia_logo, R.drawable.usc_logo};

    String names[] = {"University of California, Los Angeles", "University of California, Santa Barbara", "Yale", "University of Washington", "Stanford",
            "Western Washington University", "Daffodil International University", "Harvard University", "University of Alberta", "Johns Hopkins University",
            "UNC Charlotte", "Virginia Tech", "University of Illinois Urbana-Champaign", "Oregon State University", "Texas A&M University",
            "North Carolina State University", "Boston University", "Princeton University", "University of California, Berkeley", "Chapman University",
            "Columbia University", "University of Southern California"};
    List<UniversityModel> universityList = new ArrayList<>();    //vector holding universities. Change to firebase for classes


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_search);

        lvUniversities = findViewById(R.id.lvUniversities);

        for (int i = 0; i < names.length; i++)   //Add each university to the vector
        {
            UniversityModel universityModel = new UniversityModel(names[i], images[i]);
            universityList.add(universityModel);
        }

        //Check if this is setting or changing university
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            changeUniversity = bundle.getString("changeUniversity");
        }

        universityAdapter = new CustomAdapter(universityList, this);
        lvUniversities.setAdapter(universityAdapter);
    }


    //IMPLEMENTING SEARCH VIEW
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                universityAdapter.getFilter().filter(newText);

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



    //Custom Adapter for UniversityModel Class instead of just String
    public class CustomAdapter extends BaseAdapter implements Filterable {

        private List<UniversityModel> universities;
        private List<UniversityModel> universitiesFiltered;
        private Context context;

        public CustomAdapter(List<UniversityModel> universities, Context context) {
            this.universities = universities;
            this.universitiesFiltered = universities;
            this.context = context;
        }

        @Override
        public int getCount() {
            return universitiesFiltered.size();
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
            View view = getLayoutInflater().inflate(R.layout.row_items, null);

            ImageView universityView = view.findViewById(R.id.universityView);
            TextView universityName = view.findViewById(R.id.universityName);

            universityView.setImageResource(universitiesFiltered.get(position).getImage());
            universityName.setText(universitiesFiltered.get(position).getName());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String currentUniversity = universitiesFiltered.get(position).getName();

                    //get user id
                    userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    ref = mFireStore.collection("users").document(userId);

                    //get user data from firestore
                    ref.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    user_username = documentSnapshot.getString("username");
                                    user_firstName = documentSnapshot.getString("firstName");
                                    user_lastName = documentSnapshot.getString("lastName");
                                    user_major = documentSnapshot.getString("major");
                                    user_studyHabits = documentSnapshot.getString("studyHabits");
                                    courses = (List<String>) documentSnapshot.get("courses");

                                    //create new userModel with updated course list
                                    UserModel userModel = new UserModel(userId, user_username, user_firstName, user_lastName, user_major, currentUniversity, user_studyHabits, courses);

                                        //update user profile with added course
                                        ref.set(userModel)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        //check if need to return to ProfileEdit.java
                                                        if (changeUniversity.equals(profile)) {
                                                            Toast.makeText(getApplicationContext(), "University Selected", Toast.LENGTH_SHORT).show();

                                                            Intent intent = new Intent(UniversitySearch.this, ProfileEdit.class);
                                                            intent.putExtra("currentUniversity", currentUniversity);
                                                            startActivity(intent);
                                                        }
                                                        else {
                                                            Toast.makeText(getApplicationContext(), "University Selected", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                        }
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

        //Filtering search results
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();

                    if(constraint == null || constraint.length() == 0) {
                        filterResults.count = universities.size();
                        filterResults.values = universities;
                    }
                    else {
                        String searchStr = constraint.toString().toLowerCase();
                        List<UniversityModel> resultData = new ArrayList<>();

                        for (UniversityModel universityModel:universities) {
                            if(universityModel.getName().toLowerCase().contains(searchStr)) {
                                resultData.add(universityModel);
                            }
                        }

                        filterResults.count = resultData.size();
                        filterResults.values = resultData;
                    }

                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    universitiesFiltered = (List<UniversityModel>) results.values;

                    notifyDataSetChanged();
                }
            };

            return filter;
        }
    }
}