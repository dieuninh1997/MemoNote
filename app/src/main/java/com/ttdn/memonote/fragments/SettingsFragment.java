package com.ttdn.memonote.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.ttdn.memonote.R;
import com.ttdn.memonote.utils.AppSettings;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {

    @Bind(R.id.SortSwitch)
    Switch sortSwitch;

   /* @Bind(R.id.ThemeSwitch)
   Switch themeSwitch;*/

    AppSettings appSettings;

    @Bind(R.id.card_sort)
    CardView cardSort;

//    CardView cardTheme;

    Boolean isOldestSort, isNightTheme;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickSwitch();
    }

    private void setOnClickSwitch() {
        sortSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortSwitchClick();
            }
        });

       /* themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThemeSwitchClick();
            }
        });*/
    }

   /* private void ThemeSwitchClick() {
        isNightTheme=!isNightTheme;
        appSettings.setDarkTheme(isNightTheme);
        themeSwitch.setChecked(appSettings.getDarkTheme());
    }*/

    private void SortSwitchClick() {
        isOldestSort=!isOldestSort;
        appSettings.setOldestSort(isOldestSort);
        sortSwitch.setChecked(appSettings.getOldestSort());
    }

    @Override
    public void onResume() {
        super.onResume();
        //sort initial
        if(appSettings.getOldestSort())
        {
            sortSwitch.setChecked(true);
        }else
            sortSwitch.setChecked(false);

      /*  if(appSettings.getDarkTheme())
        {
            themeSwitch.setChecked(true);
        }else
            themeSwitch.setChecked(false);
*/
        setOnClickSwitch();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this,view);
        appSettings=new AppSettings(getActivity().getApplication());
        isOldestSort=appSettings.getOldestSort();
        isNightTheme=appSettings.getDarkTheme();

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
