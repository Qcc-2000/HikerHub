package edu.northeastern.hikerhub;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class A6Adapter extends RecyclerView.Adapter<A6Adapter.ViewHolder> {
    private List<Country> countries;

    public A6Adapter() {
        countries = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_a6, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Country country = countries.get(position);
        holder.getCountryIdTextView().setText("Country_id:  " + country.getCountry_id());
        holder.getPossibilityTextView().setText("Probability:  " + country.getProbability().toString());

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView countryIdTextView;
        private TextView possibilityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            countryIdTextView = itemView.findViewById(R.id.textview_country_id);
            possibilityTextView = itemView.findViewById(R.id.textview_possibility);
        }

        public TextView getCountryIdTextView() {
            return countryIdTextView;
        }

        public void setCountryIdTextView(TextView countryIdTextView) {
            this.countryIdTextView = countryIdTextView;
        }

        public TextView getPossibilityTextView() {
            return possibilityTextView;
        }

        public void setPossibilityTextView(TextView possibilityTextView) {
            this.possibilityTextView = possibilityTextView;
        }
    }

}
