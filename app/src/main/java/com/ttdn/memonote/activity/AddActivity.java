package com.ttdn.memonote.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.ttdn.memonote.R;
import com.ttdn.memonote.adapters.CameraAdapter;
import com.ttdn.memonote.adapters.PictureAdapter;
import com.ttdn.memonote.data.Camera;
import com.ttdn.memonote.data.DBHelper;
import com.ttdn.memonote.data.Note;
import com.ttdn.memonote.utils.AppSettings;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


    private static final int PICK_IMAGE=100;
    private static final int REQUEST_CODE_CAMERA=1;
    private static final int PERMISSION_MULTIPLE_REQUEST=123;
    private Dialog mDialogCamera;
    protected ListView mLvCamera;
    protected ArrayList<Camera> mArrayCamera;
    protected PictureAdapter mPictureAdapter;
    protected ArrayList<String> mArrayPicture =new ArrayList<>();
    protected String mPicturePath="";
    protected String mDate = "", mTime = "";
    protected ListView mGrvPicture;


    public void cameraDialog(Activity activity) {
        mDialogCamera = new Dialog(activity, R.style.Dialog);
        mDialogCamera.setContentView(R.layout.dialog_camera);
        mDialogCamera.setTitle("Insert Picture");
        mLvCamera = (ListView) mDialogCamera.findViewById(R.id.lv_camera);
        mArrayCamera = new ArrayList<>();
        mArrayCamera.add(new Camera(" Take Photo", R.drawable.ic_photo_camera_black_24dp));
        mArrayCamera.add(new Camera(" Choose Photo", R.drawable.ic_image_black_24dp));
        CameraAdapter adapterCamera = new CameraAdapter(activity, R.layout.list_item_camera, mArrayCamera);
        mLvCamera.setAdapter(adapterCamera);
        mDialogCamera.show();
        mLvCamera.setOnItemClickListener(new choosePicture());
    }

    public class choosePicture implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getItemIdAtPosition(position) == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermission();
                }else {
                    takePhoto();
                }
            }
            if (parent.getItemIdAtPosition(position) == 1) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
                mDialogCamera.dismiss();
            }
        }
    }

    private void takePhoto() {
        Intent takePictureIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null)
        {
            File photoFile=null;
            photoFile=createImageFile();
            if(photoFile!=null)
            {
                Uri photoURI = FileProvider.getUriForFile(getApplication(), "com.example.tam.appnotes.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getApplicationContext().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
            }
        }
        mDialogCamera.dismiss();
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPicturePath = image.getAbsolutePath();
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)+ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {
                Snackbar.make(this.findViewById(android.R.id.content),"Please grant permission to upload profile photo",Snackbar.LENGTH_INDEFINITE)
                        .setAction("ENABLE", new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA},PERMISSION_MULTIPLE_REQUEST);
                            }
                        }).show();
            }else
            {
                requestPermissions(
                        new String[]{android.Manifest.permission
                                .READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                        PERMISSION_MULTIPLE_REQUEST);
            }
        }else
        {
            takePhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGrvPicture = (ListView) findViewById(R.id.grv_picture);
        mGrvPicture.setVisibility(View.VISIBLE);

        if(resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri uriImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uriImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mPicturePath = cursor.getString(columnIndex);
                cursor.close();
                mArrayPicture.add(mPicturePath);
            }
            if (requestCode == REQUEST_CODE_CAMERA && data != null) {
                mArrayPicture.add(mPicturePath);
            }
        }
        mPictureAdapter = new PictureAdapter(this, R.layout.list_item_picture, mArrayPicture);
        mGrvPicture.setAdapter(mPictureAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_MULTIPLE_REQUEST:
                if(grantResults.length>0)
                {
                    boolean cameraPermission=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    boolean readExtenalFile=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraPermission && readExtenalFile)
                    {
                        takePhoto();
                    }else
                    {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please grant permission to upload profile photo",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{android.Manifest.permission
                                                        .READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                                                PERMISSION_MULTIPLE_REQUEST);
                                    }
                                }).show();
                    }


                }
                break;
        }



    }

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
            case R.id.photos:
                cameraDialog(this);
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

        String listPicturePath = mArrayPicture.toString();
        String pictuePath = listPicturePath.substring(1, listPicturePath.length() - 1);
       // String alarm = mDate + mTime;

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
               // boolean success_insert = db.insertData(titleEntered, contentEntered, time_now, colorEntered);


                boolean success_insert = db.insertNote(new Note(titleEntered,contentEntered,time_now,colorEntered," ",pictuePath));


                if (success_insert)
                    Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Add failed", Toast.LENGTH_SHORT).show();
            } else {//update existing
              //  boolean success_update = db.updateData(id, titleEntered, contentEntered, time_now, pre_color);
                boolean success_update = db.updateNote(id, new Note(titleEntered,contentEntered,time_now,pre_color," ",pictuePath));
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
