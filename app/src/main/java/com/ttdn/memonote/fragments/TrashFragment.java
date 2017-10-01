package com.ttdn.memonote.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ttdn.memonote.R;
import com.ttdn.memonote.activity.AddActivity;
import com.ttdn.memonote.adapters.NoteAdapter;
import com.ttdn.memonote.data.DBHelper;
import com.ttdn.memonote.data.Note;
import com.ttdn.memonote.data.RemoveDBHelper;
import com.ttdn.memonote.utils.AppSettings;
import com.ttdn.memonote.utils.MyRecyclerItemListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ttdn1 on 9/30/2017.
 */

public class TrashFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = "TrashFragment";

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.empty)
    TextView txtEmpty;

    String id;
    Note noteItem;

    private boolean isGrid;
    private Menu mMenu;
    private List<Note> listNotesData;
    private AppSettings appSettings;

    RemoveDBHelper removeDBHelper;
    DBHelper db;

    DrawerLayout drawerLayout;
    NavigationView nview;
    Toolbar toolbar;

    @Nullable
    @OnClick(R.id.fab)
    public void onClick(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("CLEAR ALL")
                .setMessage(R.string.clear_prompt)
                .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeDBHelper.deleteAll();
                        onResume();
                    }
                })
                .setNegativeButton("No, Wait", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public TrashFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note, container, false);
        appSettings = new AppSettings(getActivity());
        setHasOptionsMenu(true);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        configRecyclerView();
        setRecyclerView();
    }

    private void configRecyclerView() {
        if (isGrid) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setRecyclerView() {
        //show list note
        if (listNotesData == null) {
            listNotesData = new ArrayList<>();
        } else {
            listNotesData.clear();
        }


        removeDBHelper = new RemoveDBHelper(getActivity().getApplication());
        db = new DBHelper(getActivity().getApplication());

        if (appSettings.getOldestSort()) {
            listNotesData = removeDBHelper.getAllDataAsc();
        } else {
            listNotesData = removeDBHelper.getAllDataDesc();
        }

        if (listNotesData.size() == 0) {
            txtEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            NoteAdapter adapter = new NoteAdapter(getActivity(), listNotesData);
            recyclerView.setAdapter(adapter);

            recyclerView.scrollToPosition(0);
            recyclerView.addOnItemTouchListener(new MyRecyclerItemListener(getActivity(),
                    recyclerView,
                    new MyRecyclerItemListener.OnClickItemInterface() {
                        @Override
                        public void onClick(View view, int position) {
                            noteItem = listNotesData.get(position);
                            id = noteItem.getId();
                            showRestoreDialog(id);

                        }

                        @Override
                        public void onLongClick(View view, int position) {
                            //do nothing
                        }
                    }));
        }
    }

    private void showRestoreDialog(String id) {
        final int int_id = Integer.parseInt(id);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.restore)
                .setMessage(R.string.restoreMessage)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeDBHelper.deleteData(int_id);
                        onResume();
                        Toast.makeText(getActivity(), R.string.deleted, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Restore", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.insertData(noteItem.getTitle(), noteItem.getContent(), noteItem.getDate(), noteItem.getColor());
                        removeDBHelper.deleteData(int_id);
                        onResume();
                        Toast.makeText(getActivity(), R.string.restored, Toast.LENGTH_SHORT).show();

                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isGrid = appSettings.getIsGrid();
        configRecyclerView();
        setRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.note_fragment_menu, menu);
        this.mMenu = menu;
        if (isGrid) {
            mMenu.findItem(R.id.changeListGrid).setIcon(R.drawable.ic_view_stream_black_24dp);
        } else {
            mMenu.findItem(R.id.changeListGrid).setIcon(R.drawable.ic_dashboard_black_24dp);
        }
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeListGrid:
                isGrid = !isGrid;
                if (isGrid) {
                    mMenu.findItem(R.id.changeListGrid).setIcon(R.drawable.ic_view_stream_black_24dp);
                } else {
                    mMenu.findItem(R.id.changeListGrid).setIcon(R.drawable.ic_dashboard_black_24dp);
                }
                appSettings.setIsGrid(isGrid);
                configRecyclerView();
                setRecyclerView();
                break;

        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //  ((NoteAdapter)recyclerView.getAdapter()).resetView(false);
        if (newText.isEmpty()) {
            ((NoteAdapter) recyclerView.getAdapter()).getFilter().filter("");
        } else {
            ((NoteAdapter) recyclerView.getAdapter()).getFilter().filter(newText.toLowerCase());

        }
        return true;
    }
}
