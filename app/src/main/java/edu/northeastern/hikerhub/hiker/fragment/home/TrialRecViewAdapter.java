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
import java.util.List;
import java.util.Random;

import edu.northeastern.hikerhub.R;


public class TrialRecViewAdapter extends RecyclerView.Adapter<TrialRecViewAdapter.ViewHolder> {
    private static  final String TAG = "TrialRecViewAdapter";
    private List<Trail> trails = new ArrayList<>();
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
        holder.txtTrailDifficulty.setText(trails.get(position).getDifficulty().toString());
        holder.txtTrailName.setText(trails.get(position).getName());
        holder.txtTrailLenTime.setText(trails.get(position).getLenAndTime());

        Glide.with(mContext)
                .asBitmap()
                .load(trails.get(position).getImgUrl())
                .into(holder.imgTrail);
        holder.parentTail.setOnClickListener(new View.OnClickListener() {
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

    public void setTrails(List<Trail> trails) {
        this.trails = trails;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView parentTail;
        private ImageView imgTrail;
        private TextView txtTrailDifficulty;
        private TextView txtTrailName;
        private TextView txtTrailLenTime;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentTail = itemView.findViewById(R.id.parentTail);
            imgTrail = itemView.findViewById(R.id.imgTrail);
            txtTrailDifficulty = itemView.findViewById(R.id.txtTrailDifficulty);
            txtTrailName = itemView.findViewById(R.id.txtTrailName);
            txtTrailLenTime = itemView.findViewById(R.id.txtTrailLenTime);
        }
    }
}
