package edu.northeastern.hikerhub.hiker.fragment.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import edu.northeastern.hikerhub.R;


public class TrialRecViewAdapter extends RecyclerView.Adapter<TrialRecViewAdapter.ViewHolder> {
    private static  final String TAG = "TrialRecViewAdapter";
    private ArrayList<Trail> trails = new ArrayList<>();
    private Context mContext;

    public TrialRecViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_trail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Called");
        holder.txtTrailName.setText(trails.get(position).getName());
        Glide.with(mContext)
                .asBitmap()
                .load(trails.get(position).getImageUrl())
                .into(holder.imgTrail);
        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, trails.get(position).getName() + " Selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return trails.size();
    }

    public void setTrails(ArrayList<Trail> trails) {
        this.trails = trails;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView parent;
        private ImageView imgTrail;
        private TextView txtTrailName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            imgTrail = itemView.findViewById(R.id.imgTrail);
            txtTrailName = itemView.findViewById(R.id.txtTrailName);
        }
    }
}
