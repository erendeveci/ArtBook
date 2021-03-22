package com.eren.artbookfragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.eren.artbookfragment.R;
import com.eren.artbookfragment.fragment.First_DataList_FragmentDirections;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RowHolder>  {

    ArrayList<String> artName;
    ArrayList<Integer> artId;



    public RecyclerViewAdapter(ArrayList<String> artName, ArrayList<Integer> artId) {
        this.artName = artName;
        this.artId = artId;
    }

    @NonNull
    @Override
    public RowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.art_row, parent, false);

        return new RowHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RowHolder holder, int position) {
        holder.textViewArtName.setText(artName.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                First_DataList_FragmentDirections.ActionDataListMainToDataAdd action = First_DataList_FragmentDirections.actionDataListMainToDataAdd("old");
                action.setAboutSecond("old");
                action.setNumberSecond(artId.get(position));

                Navigation.findNavController(v).navigate(action);




            }
        });

    }

    @Override
    public int getItemCount() {
        return artName.size();
    }

    public class RowHolder extends RecyclerView.ViewHolder {
        TextView textViewArtName;

        public RowHolder(@NonNull View itemView) {
            super(itemView);
            textViewArtName = itemView.findViewById(R.id.textViewArtName);


        }
    }
}
