package com.my.kellen.dailychecklist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity {
    private SQLiteDatabase mDb;
    private EditText inputName;
    private Spinner spinner;
    private CheckBox box;
    private boolean editMode;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        DbHelper dbHelper = new DbHelper(getApplicationContext());
        mDb = dbHelper.getWritableDatabase();
        inputName = findViewById(R.id.editText);
        spinner = findViewById(R.id.spinner);
        box = findViewById(R.id.checkBox);
        Intent i = getIntent();
        editMode = false;
        if (i.hasExtra("Name")){
            editMode = true;
            inputName.setText(i.getStringExtra("Name"));
            id = i.getIntExtra("Id", 1);
            String[] daylist = getResources().getStringArray(R.array.dayArray);
            int day = 0;
            for (String item:daylist) {
                if(i.getStringExtra("Date").equals(item)){
                    day = java.util.Arrays.asList(daylist).indexOf(item);
                }
            }
            spinner.setSelection(day);
            box.setChecked(i.getIntExtra("Repeat", 0) == 1);
        }
        else{
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("u", Locale.US);
        int today = Integer.parseInt(sdf.format(now.getTime()));
        spinner.setSelection(today - 1);}
    }

    public void onClick(View view) {
        if (inputName.getText().length() == 0) {
            return;
        }
        else if(editMode){
            ContentValues cv = new ContentValues();
            cv.put(Contract.Entry.COLUMN_NAME, String.valueOf(inputName.getText()));
            cv.put(Contract.Entry.COLUMN_DATE, String.valueOf(spinner.getSelectedItem()));
            cv.put(Contract.Entry.COLUMN_COMPLETE, 0);
            int check = 0; if (box.isChecked()) {check = 1;}
            cv.put(Contract.Entry.COLUMN_REPEAT, check);
            mDb.update(Contract.Entry.TABLE_NAME, cv, Contract.Entry._ID + "=" + String.valueOf(id), null);
        }
        else {
        addNewEntry(String.valueOf(inputName.getText()), String.valueOf(spinner.getSelectedItem()), box.isChecked());
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void addNewEntry(String name, String day, Boolean checked) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.Entry.COLUMN_NAME, name);
        cv.put(Contract.Entry.COLUMN_DATE, day);
        cv.put(Contract.Entry.COLUMN_COMPLETE, 0);
        int check = 0;
        if (checked) {
            check = 1;
        }
        cv.put(Contract.Entry.COLUMN_REPEAT, check);
        mDb.insert(Contract.Entry.TABLE_NAME, null, cv);
    }

}
