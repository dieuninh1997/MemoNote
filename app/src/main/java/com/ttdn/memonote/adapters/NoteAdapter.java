package com.ttdn.memonote.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ttdn.memonote.R;
import com.ttdn.memonote.activity.AddActivity;
import com.ttdn.memonote.data.Note;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by ttdn1 on 9/29/2017.
 */

public class NoteAdapter
        extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
        implements Filterable {
    private Context context;
    private List<Note> listNotesData;
    private String id;
    private Note note;


    public NoteAdapter(Context context, List<Note> listNotesData) {
        this.context = context;
        this.listNotesData = listNotesData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    //involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder != null) {
            note = listNotesData.get(position);
            id = note.getId();
            holder.txt_content.setText(note.getContent());
            holder.txt_title.setText(note.getTitle());
            holder.txt_time.setText(note.getDate());
            holder.card_view_item.setCardBackgroundColor(Integer.parseInt(note.getColor()));


            if(note.alarm.equals(""))
            {
                holder.img_alarm.setVisibility(View.GONE);
            }else
            {
                holder.img_alarm.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public int getItemCount() {
        try {
            return listNotesData.size();
        } catch (NullPointerException e) {

        }
        return 0;
    }


    //search
    List<Note> noteListSearch;

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults oReturn = new FilterResults();
                List<Note> results = new ArrayList<>();
                if (noteListSearch == null) {
                    noteListSearch = listNotesData;
                }
                if (charSequence != null) {
                    if (noteListSearch != null && noteListSearch.size() > 0) {
                        for (Note noteInfo : noteListSearch) {
                            if (noteInfo.getTitle().toLowerCase().contains(charSequence) || noteInfo.getContent().toLowerCase().contains(charSequence) || noteInfo.getDate().toLowerCase().contains(charSequence)) {
                                results.add(noteInfo);
                            }
                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();
                }

                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence,
                                          FilterResults filterResults) {

                listNotesData = (ArrayList<Note>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_note;
        private ImageView img_alarm;
        private TextView txt_title;
        private TextView txt_content;
        private TextView txt_time;
        private CardView card_view_item;


        public ViewHolder(View itemView) {
            super(itemView);
            txt_title = (TextView) itemView.findViewById(R.id.txt_title);
            txt_time = (TextView) itemView.findViewById(R.id.txt_time);
            txt_content = (TextView) itemView.findViewById(R.id.txt_content);
            img_note = (ImageView) itemView.findViewById(R.id.img_note);
            img_alarm = (ImageView) itemView.findViewById(R.id.img_alrm);
            card_view_item = (CardView) itemView.findViewById(R.id.card_view_item);

        }
    }
}
