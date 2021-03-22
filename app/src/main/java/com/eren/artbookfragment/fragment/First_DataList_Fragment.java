package com.eren.artbookfragment.fragment;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eren.artbookfragment.R;
import com.eren.artbookfragment.adapter.RecyclerViewAdapter;

import java.util.ArrayList;


public class First_DataList_Fragment extends Fragment  {
    RecyclerView recyclerView;
    SQLiteDatabase database;
    ArrayList<String> artNameList;
    ArrayList<Integer> artIdList;
    RecyclerViewAdapter recyclerViewAdapter;
    View myView;

    public First_DataList_Fragment() {
        // Required empty public constructor
    }

    public void onBackPressed() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        /* */


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_data_list_main, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        artNameList = new ArrayList<>();
        artIdList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        myView = view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewAdapter = new RecyclerViewAdapter(artNameList,artIdList);

        recyclerView.setAdapter(recyclerViewAdapter);

        getData();

    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_art_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_art) {
            // NavDirections action = First_DataList_FragmentDirections.actionDataListMainToDataAdd();

            First_DataList_FragmentDirections.ActionDataListMainToDataAdd action = First_DataList_FragmentDirections.actionDataListMainToDataAdd("new");
            action.setAboutSecond("new");
            Navigation.findNavController(myView).navigate(action);

        }


        return super.onOptionsItemSelected(item);

    }

    public void getData() {

        try{


            database = getActivity().openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR , paintername VARCHAR , year VARCHAR,image BLOB)");

            Cursor cursor = database.rawQuery("SELECT * FROM arts", null);

            int idX = cursor.getColumnIndex("id");
            int nameX = cursor.getColumnIndex("artname");

            while (cursor.moveToNext()) {

                artNameList.add(cursor.getString(nameX));
                artIdList.add(cursor.getInt(idX));


            }
            recyclerViewAdapter.notifyDataSetChanged();
            cursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}