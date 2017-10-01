package com.ttdn.memonote.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.ttdn.memonote.R;
import com.ttdn.memonote.data.DBHelper;
import com.ttdn.memonote.utils.AppSettings;

import java.util.Calendar;

public class AddActivity extends AppCompatActivity {
    private EditText title, content;
    private DBHelper db;
    private int id;
    private String time_now, pre_color;
    private Toolbar toolbar;
    private static View add_screen;
    private int default_color;
    private Bundle bundle;
    private static AppSettings appSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        db = new DBHelper(this);

        appSettings = new AppSettings(this);

        title = (EditText) findViewById(R.id.add_activity_title);
        content = (EditText) findViewById(R.id.add_activity_content);
        add_screen = findViewById(R.id.add_activity);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.add_activity_toolbar);
        setTitle("Add Note");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_done_black_24dp);
        bundle = getIntent().getExtras();

        if (bundle != null) {
            //call to edit
            id = bundle.getInt("id");
            Cursor cursor = db.getData(id);
            cursor.moveToFirst();
            String pre_title = cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE));
            String pre_content = cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENT));
            pre_color = cursor.getString(cursor.getColumnIndex(DBHelper.COL_COLOR));

            title.setText(pre_title);
            content.setText(pre_content);
            add_screen.setBackgroundColor(Integer.parseInt(pre_color));

            cursor.close();
        } else {
            //call to add
            default_color = appSettings.getNoteBgColor();
            add_screen.setBackgroundColor(default_color);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.pick_color:
                showColorDialog();
                break;
            case R.id.voices:
                break;
            case android.R.id.home:
                if (bundle == null) {
                    Add_data(-1);//new data
                } else
                    Add_data(id);//update existing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void Add_data(int i) {
        //close keyboard
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        //time
        Calendar calendar = Calendar.getInstance();
        time_now = calendar.getTime().toString().substring(0, 16);
        String titleEntered = title.getText().toString();
        String contentEntered = content.getText().toString();
        //if title empty get from substring
        if (titleEntered.matches("") && !contentEntered.matches("")) {
            //get 2 words from content
            int length = contentEntered.length();
            if (length <= 15)
                titleEntered = contentEntered.substring(0, length);
            else
                titleEntered = contentEntered.substring(0, 15);
        }
        if (titleEntered.matches("") && contentEntered.matches("")) {
            //nothing entered
            Toast.makeText(this, "Can't save empty note", Toast.LENGTH_SHORT).show();
        } else {
            if (i == -1) {//new data
                String colorEntered = String.valueOf(appSettings.getNoteBgColor());
                boolean success_insert = db.insertData(titleEntered, contentEntered, time_now, colorEntered);
                if (success_insert)
                    Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Add failed", Toast.LENGTH_SHORT).show();
            } else {//update existing
                boolean success_update = db.updateData(id, titleEntered, contentEntered, time_now, pre_color);
                if (success_update)
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
            }

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportFinishAfterTransition();
        } else {
            finish();
        }

    }

    private void showColorDialog() {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.MyAlertDialogAppCompatStyle);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        View view = LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.color_picker_layout, null);

        final ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
        OpacityBar opacityBar = (OpacityBar) view.findViewById(R.id.opacitybar);
        SaturationBar saturationBar = (SaturationBar) view.findViewById(R.id.saturationbar);
        ValueBar valueBar = (ValueBar) view.findViewById(R.id.valuebar);

        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);

        //color
        int oldColor = appSettings.getNoteBgColor();
        picker.setOldCenterColor(oldColor);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                // int color=picker.getColor();
                appSettings.setNoteBgColor(color);
                Log.d("AddActivity: ", "-DiaLog color change :" + color);
            }

        });
        builder.setTitle("Select a color")
                .setView(view)
                .setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                add_screen.setBackgroundColor(appSettings.getNoteBgColor());

                            }
                        })
                .setNegativeButton(getResources().getString(R.string.cancel), null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (bundle != null)//called to edit
        {
         /* //  AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, R.style.MyAlertDialogAppCompatStyle);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setMessage(R.string.discard_changes)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //save
                            Add_data(id);//update existing
                        }
                    })
                    .setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //discard
                            supportFinishAfterTransition();
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Do nothing
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();*/
            Toast.makeText(this, "Discard save note", Toast.LENGTH_SHORT).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                supportFinishAfterTransition();
            else
                finish();
        }
    }


}
