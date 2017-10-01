package com.ttdn.memonote.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ttdn.memonote.R;
import com.ttdn.memonote.data.DBHelper;
import com.ttdn.memonote.data.RemoveDBHelper;

import java.util.Calendar;

public class ViewActivity extends AppCompatActivity {

    private DBHelper db;
    private RemoveDBHelper trash_db;
    private TextView txt_title, txt_content, txt_date;
    private Integer id;
    private View view_screen;
    private String title_string, content_string, date_string, color_string, time;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private Cursor cursor;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        initView();
    }

    private void initView() {

        txt_title = (TextView) findViewById(R.id.view_screen_title);
        txt_content = (TextView) findViewById(R.id.view_screen_content);
        txt_date = (TextView) findViewById(R.id.view_screen_date);

        view_screen = findViewById(R.id.view_screen);
        toolbar = (Toolbar) findViewById(R.id.view_screen_toolbar);
        setSupportActionBar(toolbar);
        setTitle("View Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp);

        db = new DBHelper(this);
        trash_db = new RemoveDBHelper(this);
        Bundle extras = getIntent().getExtras();
        id = extras.getInt("id");
        cursor = db.getData(id);
        cursor.moveToFirst();
        title_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE));
        content_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_CONTENT));
        date_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE));
        time = cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE));
        color_string = cursor.getString(cursor.getColumnIndex(DBHelper.COL_COLOR));
        cursor.close();

        txt_title.setText(title_string);
        txt_content.setText(content_string);
        txt_date.setText(date_string);

        view_screen.setBackgroundColor(Integer.parseInt(color_string));
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Integer.parseInt(color_string));
        }
        fab = (FloatingActionButton) findViewById(R.id.view_fab);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        fab.setImageResource(R.drawable.ic_edit_black_24dp);
                        edit_pressed();

                    }
                }
        );
    }

    private void edit_pressed() {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        Intent i = new Intent(this, AddActivity.class);
        i.putExtras(bundle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> p1 = Pair.create((View) fab, "trans_add_fab");
            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this);
            finish();
            startActivity(i, options.toBundle());
        } else
            startActivity(i);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor = db.getData(id);
        cursor.moveToFirst();
        cursor.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.activity_view_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            supportFinishAfterTransition();
        else
            finish();

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.viewscreen_delete:
                showDeleteDialog();
                break;
            case R.id.viewscreen_share:
                shareData(title_string, content_string);
                break;
            case R.id.viewscreen_clipboard:
                copyToClipboard(title_string, content_string);
                Toast.makeText(getApplicationContext(), R.string.clipboard, Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }

        return true;

    }

    private void copyToClipboard(String title_string, String content_string) {
        ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied text", "["+title_string + "] " + content_string);
        manager.setPrimaryClip(clipData);
    }

    private void shareData(String title, String content) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "[" + title + "]\n" + content);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Choose app to share"));
    }

    private void showDeleteDialog() {
      //  AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.MyAlertDialogAppCompatStyle);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setMessage(R.string.delete_prompt)
                .setTitle("Delete Note")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addToTrash();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void addToTrash() {
        Calendar calendar = Calendar.getInstance();
        time = calendar.getTime().toString().substring(0, 16);
        trash_db.insertData(title_string, content_string, time, color_string);
        db.deleteData(id);
        Toast.makeText(this, R.string.move_to_trash, Toast.LENGTH_SHORT).show();

        supportFinishAfterTransition();
    }


}
