package com.rpm.realmexamples.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.tag.realofflineexample.R;
import com.rpm.realmexamples.models.Person;


public class PersonDetailsActivity extends AppCompatActivity {

    private TextView tvPersonDetailId, tvPersonDetailName, tvPersonDetailEmail, tvPersonDetailAddress, tvPersonDetailAge;
    private Person person = new Person();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getAllWidgets();
        getDataFromPreviousActivity();
        setDataInWidgets();
    }

    private void getAllWidgets() {
        tvPersonDetailId = (TextView) findViewById(R.id.tvPersonDetailID);
        tvPersonDetailName = (TextView) findViewById(R.id.tvPersonDetailName);
        tvPersonDetailEmail = (TextView) findViewById(R.id.tvPersonDetailEmail);
        tvPersonDetailAddress = (TextView) findViewById(R.id.tvPersonDetailAddress);
        tvPersonDetailAge = (TextView) findViewById(R.id.tvPersonDetailAge);
    }

    private void getDataFromPreviousActivity() {
        String personID = getIntent().getStringExtra("PersonID");
        person = MainActivity.getInstance().searchPerson(personID);
    }

    private void setDataInWidgets() {
        tvPersonDetailId.setText(getString(R.string.person_id, String.valueOf(person.getId())));
        tvPersonDetailName.setText(getString(R.string.person_name, person.getName()));
        tvPersonDetailEmail.setText(getString(R.string.person_email, person.getEmail()));
        tvPersonDetailAddress.setText(getString(R.string.person_address, person.getAddress()));
        tvPersonDetailAge.setText(getString(R.string.person_age, String.valueOf(person.getAge())));
    }
}
