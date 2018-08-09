package com.rpm.realmexamples.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.example.tag.realofflineexample.R;
import com.rpm.realmexamples.adapters.PersonDetailsAdapter;
import com.rpm.realmexamples.models.Person;
import com.rpm.realmexamples.realm.RealmController;
import com.rpm.realmexamples.utils.AppUtil;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    //    private static int id = 1;
    private FloatingActionButton fabAddPerson;
    private Realm myRealm;
    private ListView lvPersonNameList;
    private static ArrayList<Person> personArrayList = new ArrayList<>();
    private PersonDetailsAdapter personDetailsAdapter;
    private static MainActivity instance;
    private AlertDialog.Builder subDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RealmConfiguration realmConfiguration = RealmController.getInstance(this).getRealmConfiguration();
        myRealm = Realm.getInstance(realmConfiguration);
//        myRealm = Realm.getInstance(this);
        instance = this;

//        myRealm.writeCopyTo(new File(Environment.getExternalStorageDirectory().toString()));
        writeRealmDatabaseToFile();

        getAllWidgets();
        bindWidgetsWithEvents();
        setPersonDetailsAdapter();
        getAllUsers();
    }

    private void writeRealmDatabaseToFile() {
        try {
            final File file = new File(Environment.getExternalStorageDirectory().getPath().concat("/default.realm"));
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            myRealm.writeCopyTo(file);
            Toast.makeText(MainActivity.this, "Success export realm file", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            myRealm.close();
            e.printStackTrace();
        }
    }

    public static MainActivity getInstance() {
        return instance;
    }

    private void getAllWidgets() {
        fabAddPerson = (FloatingActionButton) findViewById(R.id.fabAddPerson);
        lvPersonNameList = (ListView) findViewById(R.id.lvPersonNameList);
    }

    private void bindWidgetsWithEvents() {
        fabAddPerson.setOnClickListener(this);
        lvPersonNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PersonDetailsActivity.class);
                intent.putExtra("PersonID", personArrayList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void setPersonDetailsAdapter() {
        personDetailsAdapter = new PersonDetailsAdapter(MainActivity.this, personArrayList);
        lvPersonNameList.setAdapter(personDetailsAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddPerson:
                addOrUpdatePersonDetailsDialog(null, -1);
                break;
        }
    }

    public void addOrUpdatePersonDetailsDialog(final Person model, final int position) {

        //subdialog
        subDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("Please enter all the details!!!")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dlg2, int which) {
                        dlg2.cancel();
                    }
                });

        //maindialog
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.prompt_dialog, null);
        AlertDialog.Builder mainDialog = new AlertDialog.Builder(MainActivity.this);
        mainDialog.setView(promptsView);

        final EditText etAddPersonName = (EditText) promptsView.findViewById(R.id.etAddPersonName);
        final EditText etAddPersonEmail = (EditText) promptsView.findViewById(R.id.etAddPersonEmail);
        final EditText etAddPersonAddress = (EditText) promptsView.findViewById(R.id.etAddPersonAddress);
        final EditText etAddPersonAge = (EditText) promptsView.findViewById(R.id.etAddPersonAge);

        if (model != null) {
            etAddPersonName.setText(model.getName());
            etAddPersonEmail.setText(model.getEmail());
            etAddPersonAddress.setText(model.getAddress());
            etAddPersonAge.setText(String.valueOf(model.getAge()));
        }

        mainDialog.setCancelable(false)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = mainDialog.create();
        dialog.show();

        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etAddPersonName.getText().toString()) && !TextUtils.isEmpty(etAddPersonEmail.getText().toString()) && !TextUtils.isEmpty(etAddPersonAddress.getText().toString()) && !TextUtils.isEmpty(etAddPersonAge.getText().toString())) {
                    Person person = new Person();
                    person.setName(etAddPersonName.getText().toString());
                    person.setEmail(etAddPersonEmail.getText().toString());
                    person.setAddress(etAddPersonAddress.getText().toString());
                    person.setAge(Integer.parseInt(etAddPersonAge.getText().toString()));

                    if (model == null)
                        addDataToRealm(person);
                    else
                        updatePersonDetails(person, position, model.getId());

                    dialog.cancel();
                } else {
                    subDialog.show();
                }
            }
        });
    }

    private void addDataToRealm(Person model) {
        myRealm.beginTransaction();

        Person person = myRealm.createObject(Person.class, AppUtil.randomString(5));
//        person.setId(id);
        person.setName(model.getName());
        person.setEmail(model.getEmail());
        person.setAddress(model.getAddress());
        person.setAge(model.getAge());
        personArrayList.add(person);

        myRealm.commitTransaction();
        personDetailsAdapter.notifyDataSetChanged();
//        id++;
    }

    public void deletePerson(String personId, int position) {
        final RealmResults<Person> results = myRealm.where(Person.class).equalTo("id", personId).findAll();
        Log.d(TAG, "deletePerson: results.size : " + results.size());
        /*myRealm.beginTransaction();
        results.remove(0);
        myRealm.commitTransaction();*/

        myRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteFirstFromRealm();
            }
        });

        personArrayList.clear();
        getAllUsers();
//        personArrayList.remove(results);
//        personDetailsAdapter.notifyDataSetChanged();
    }

    public Person searchPerson(String personId) {
        RealmResults<Person> results = myRealm.where(Person.class).equalTo("id", personId).findAll();

        myRealm.beginTransaction();
        myRealm.commitTransaction();

        return results.get(0);
    }

    public void updatePersonDetails(Person model, int position, String personID) {
        Person editPersonDetails = myRealm.where(Person.class).equalTo("id", personID).findFirst();
        myRealm.beginTransaction();
        editPersonDetails.setName(model.getName());
        editPersonDetails.setEmail(model.getEmail());
        editPersonDetails.setAddress(model.getAddress());
        editPersonDetails.setAge(model.getAge());
        myRealm.commitTransaction();

        personArrayList.set(position, editPersonDetails);
        personDetailsAdapter.notifyDataSetChanged();
    }

    private void getAllUsers() {
//        RealmResults<Person> results = myRealm.where(Person.class).findAll();
//        RealmResults<Person> results = myRealm.where(Person.class).findAllSorted("age", Sort.ASCENDING); //ORDER BY, For older version
        RealmResults<Person> results = myRealm.where(Person.class).sort("age", Sort.ASCENDING).findAll(); //ORDER BY, For newer version
//        RealmResults<Person> results = myRealm.where(Person.class).distinct("age").findAll(); // DISTINCT query
//        RealmResults<Person> results = myRealm.where(Person.class).contains("name", "r", Case.INSENSITIVE).findAll(); //LIKE query
//        RealmResults<Person> results = myRealm.where(Person.class).equalTo("name", "r").or().equalTo("email", "r").findAll(); //OR query
//        RealmResults<Person> results = myRealm.where(Person.class).equalTo("name", "r").and().equalTo("email", "r").findAll(); //AND query


        myRealm.beginTransaction();
        for (int i = 0; i < results.size(); i++) {
            personArrayList.add(results.get(i));
        }

        if (results.size() > 0) {
//            id = myRealm.where(Person.class).max("id").intValue() + 1;
        }
        myRealm.commitTransaction();
        personDetailsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        personArrayList.clear();
        myRealm.close();
    }
}
