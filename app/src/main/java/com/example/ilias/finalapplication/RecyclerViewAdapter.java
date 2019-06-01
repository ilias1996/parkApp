package com.example.ilias.finalapplication;

/*
 bron: https://www.youtube.com/watch?v=Vyqz_-sJGFk
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();

    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mLicense = new ArrayList<>();
    private ArrayList<String> mKind = new ArrayList<>();
    private Context mContext ;

    public RecyclerViewAdapter(ArrayList<String> mIds, ArrayList<String> mLicense, ArrayList<String> mKind, Context mContext) {
        this.mIds = mIds;
        this.mLicense = mLicense;
        this.mKind = mKind;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Log.d(TAG, "onBindViewHolder is called");
        viewHolder.licensePlate.setText(mLicense.get(position));
        viewHolder.Id.setText((mIds.get(position)));
        viewHolder.Kind.setText(mKind.get(position));
    }

    @Override
    public int getItemCount() {
        return mIds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView Id;
        TextView licensePlate;
        TextView Kind;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView){
            super(itemView);

            Id = itemView.findViewById(R.id.txtID);
            licensePlate   = itemView.findViewById(R.id.txtLicensePlate);
            Kind  = itemView.findViewById(R.id.txtKind);
            parentLayout  = itemView.findViewById(R.id.parent_layout);

        }
    }

}
