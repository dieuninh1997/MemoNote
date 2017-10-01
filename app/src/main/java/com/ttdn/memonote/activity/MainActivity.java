package com.ttdn.memonote.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ttdn.memonote.R;
import com.ttdn.memonote.fragments.AboutFragment;
import com.ttdn.memonote.fragments.NoteFragment;
import com.ttdn.memonote.fragments.SettingsFragment;
import com.ttdn.memonote.fragments.TrashFragment;
import com.ttdn.memonote.utils.AppSettings;
import com.ttdn.memonote.utils.Constant;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG="MainActivity";
    private FragmentManager fm=getSupportFragmentManager();
    private boolean isOpenSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/*

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if(fm.getBackStackEntryCount()==0)
                {
                    setFirstItem(navigationView);
                }
            }
        });
        fm.beginTransaction().replace(R.id.frame, new NoteFragment()).commit();
        if(!isOpenSettings)
        {
            setFirstItem(navigationView);
        }else
        {
            openSettings(navigationView);
        }

    }

    private void openSettings(NavigationView navigationView) {
        MenuItem menuItem = navigationView.getMenu().getItem(2);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(menuItem.getTitle());
        }
        menuItem.setChecked(true);
        onNavigationItemSelected(menuItem);
    }

    private void setFirstItem(NavigationView navigationView) {
        MenuItem menuItem = navigationView.getMenu().getItem(0);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(menuItem.getTitle());
        }
        menuItem.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() > 0) {
            cleanBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void cleanBackStack() {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setTitle(item.getTitle());
        }

        int id = item.getItemId();
        FragmentTransaction transaction=fm.beginTransaction();
        if (id == R.id.nav_all_notes) {

            if(fm.getBackStackEntryCount()>0)
            {
                cleanBackStack();
            }
            transaction.replace(R.id.frame,new NoteFragment())
                    .commit();
        } else if (id == R.id.nav_trash) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, new TrashFragment())
                    .commit();
        } else if (id == R.id.nav_settings) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, new SettingsFragment())
                    .commit();
        } else if (id == R.id.nav_about) {
            transaction.addToBackStack(null);
            transaction.replace(R.id.frame, new AboutFragment())
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
