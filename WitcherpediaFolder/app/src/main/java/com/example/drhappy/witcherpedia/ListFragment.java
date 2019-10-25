package com.example.drhappy.witcherpedia;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String ARG_LIST_ADAPTER_TYPE = "adapter_type";


    // TODO: Rename and change types of parameters
    private String adapter_type;

    private DBHelper witcherDB;
    private ListView listView;

    public ListView getListView() {
        return listView;
    }

    public ListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type Parameter 1.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance(String type) {
        ListFragment fragment = new ListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_LIST_ADAPTER_TYPE, type);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && savedInstanceState == null) {
            adapter_type = getArguments().getString(ARG_LIST_ADAPTER_TYPE);
        } else if (savedInstanceState != null) {
            adapter_type = ((Witcherpedia)getActivity().getApplicationContext()).getCurrent_view();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        listView = view.findViewById(R.id.list_view);
        listView.setOnItemClickListener( (parentView, childView, position, id) -> onListItemSelectedListener(parentView, position) );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        witcherDB = DBHelper.getInstance(getActivity());

        setListAdapter(adapter_type);
    }

    private void onListItemSelectedListener(AdapterView<?> parentView, int position) {
        String item = (String) parentView.getItemAtPosition(position);

        switch (((Witcherpedia)getActivity().getApplicationContext()).getCurrent_view()) {
            case "Factions":
                ((Witcherpedia)getActivity().getApplicationContext()).setFaction_selected(item);

                setListAdapter("Contents");
                break;
            case "Contents":
                switch (item) {
                    case "Units":
                        setListAdapter("Units");

                        break;
                    case "Territories":
                        setListAdapter("Territories");

                        break;
                    case "Overview":
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

                        ft.replace(R.id.frame_container, OverviewFragment.newInstance());
                        ft.commit();

                        break;
                }

                break;
            case "Units":
                setDescription("Unit Description", item);

                break;
            case "Territories":
                setDescription("Territory Description", item);

                break;
        }

    }

    void setListAdapter(String type) {
        String faction_selected = ((Witcherpedia)getActivity().getApplicationContext()).getFaction_selected();
        CustomArrayAdapter list_adapter = (CustomArrayAdapter) listView.getAdapter();

        if (list_adapter != null) {
            list_adapter.clear();
        }

        ArrayList<String> label_alist = new ArrayList<>();
        ArrayList<String> icon_alist = new ArrayList<>();

        boolean subtitleOn = true;
        switch (type) {
            case "Factions": {
                try (Cursor resultSet = witcherDB.getFactions()) {
                    for (resultSet.moveToFirst(); !resultSet.isAfterLast(); resultSet.moveToNext()) {
                        String factionn = resultSet.getString(resultSet.getColumnIndex("factionn"));
                        label_alist.add(factionn);
                        String drawablen = resultSet.getString(resultSet.getColumnIndex("drawablen"));
                        icon_alist.add(drawablen);
                    }
                }

                subtitleOn = false;
                break;
            }
            case "Contents": {
                label_alist.add("Overview");
                label_alist.add("Units");
                label_alist.add("Heroes");
                label_alist.add("Territories");

                icon_alist.add("ic_overview");
                icon_alist.add("ic_units");
                icon_alist.add("ic_heroes");
                icon_alist.add("ic_territories");
                break;
            }
            case "Units": {
                try (Cursor resultSet = witcherDB.getUnits(faction_selected)) {
                    for (resultSet.moveToFirst(); !resultSet.isAfterLast(); resultSet.moveToNext()) {
                        String unitn = resultSet.getString(resultSet.getColumnIndex("unitn"));
                        label_alist.add(unitn);
                        String drawablen = resultSet.getString(resultSet.getColumnIndex("drawablen"));
                        icon_alist.add(drawablen);
                    }
                }
                break;
            }
            case "Territories": {
                try (Cursor resultSet = witcherDB.getTerritories(faction_selected)) {
                    for (resultSet.moveToFirst(); !resultSet.isAfterLast(); resultSet.moveToNext()) {
                        int num = resultSet.getInt(resultSet.getColumnIndex("num"));
                        String territoryn = resultSet.getString(resultSet.getColumnIndex("territoryn"));
                        String fullname = "<font color='#3A3A3A'>" + num + " </font>" + territoryn;
                        label_alist.add(fullname);

                        String category = resultSet.getString(resultSet.getColumnIndex("category"));
                        if (category.equals("Fortified")) {
                            icon_alist.add("ic_fortified_territory");
                        } else {
                            icon_alist.add("ic_open_territory");
                        }
                    }

                }
                break;
            }
        }

        if (subtitleOn && faction_selected != null && !faction_selected.equals("")) {
            Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setSubtitle(faction_selected);
        }
        else {
            Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setSubtitle("");
        }
        ((Witcherpedia)getActivity().getApplicationContext()).setCurrent_view(type);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(((Witcherpedia)getActivity().getApplicationContext()).getCurrent_view());

        list_adapter = new CustomArrayAdapter(getActivity(), label_alist, icon_alist);
        listView.setAdapter(list_adapter);
    }

    void setDescription(String type, String name) {
        Intent intent = new Intent(getActivity(), DescriptionActivity.class);

        intent.putExtra("Type", type);
        intent.putExtra("Item_Selected", name);
        intent.putStringArrayListExtra("Adapter", ((CustomArrayAdapter)listView.getAdapter()).labels);

        startActivity(intent);

        /*
        // when we return
        if (type.equals("Unit Description")) {
            setListAdapter("Units");
        }
        else if (type.equals("Territory Description")) {
            setListAdapter("Territories");
        }

        //Objects.requireNonNull(((AppCompatActivity)getActivity()).getSupportActionBar()).setSubtitle("");

        //((Witcherpedia)getActivity().getApplicationContext()).setCurrent_view(type);
        ((Witcherpedia)getActivity().getApplicationContext()).setDesc_selected(name);

        //CustomArrayAdapter adapter = (CustomArrayAdapter) listView.getAdapter();
        //listView.setSelection(adapter.labels.indexOf(name) - 2);
        */
    }
}
