package com.drpleaserespect.nyaamii.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.drpleaserespect.nyaamii.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DebuggingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item is selected. You can retrieve the selected item using
        Log.d("DebuggingPage", "onItemSelected: " + parent.getItemAtPosition(pos));
        Log.d("DebuggingPage", "ID: " + parent.getId());
        Log.d("SpinnerID", "ID: " + R.id.DebugSpinner);
        if (parent.getId() == R.id.DebugSpinner){
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.ProfileState), MODE_PRIVATE);
            sharedPref.edit().putString("User", (String) parent.getItemAtPosition(pos)).apply();
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debugging_page);


        Spinner spinner = findViewById(R.id.DebugSpinner);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection("UserData");

        colRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> usernames = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    usernames.add((String) document.get("Username"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usernames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.ProfileState), MODE_PRIVATE);
                spinner.setSelection(adapter.getPosition(sharedPref.getString("User", "DrPleaseRespect")));
            }
        });
        spinner.setOnItemSelectedListener(this);
    }
}