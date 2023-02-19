package edu.northeastern.hikerhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class A6 extends AppCompatActivity {
    public static String URL = "https://api.nationalize.io/";
    A6Service service;
    private EditText nameEditText;
    private Context context;
    private A6Adapter adapter;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private CountryList list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a6);
        init();
        if (savedInstanceState != null) {
            ArrayList<String> id;
            double[] pro;
            id = savedInstanceState.getStringArrayList("id");
            pro = savedInstanceState.getDoubleArray("pro");
            List<Country> countries = new ArrayList<>();
            for (int i = 0; i < id.toArray().length; i++) {
                countries.add(new Country(id.get(i), pro[i]));
            }
            list = new CountryList();
            list.setCountry(countries);
            adapter.setCountries(countries);
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        ArrayList<String> id = new ArrayList<>();
        double[] pro = new double[list.getCountry().toArray().length];
        for (int i = 0; i < list.getCountry().toArray().length; i++) {
            id.add(list.getCountry().get(i).getCountry_id());
            pro[i] = list.getCountry().get(i).getProbability();
        }

        savedInstanceState.putStringArrayList("id", id);
        savedInstanceState.putDoubleArray("pro", pro);
    }

    public void init()
    {
        nameEditText = findViewById(R.id.edit_text_name_input);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView = findViewById(R.id.recyclerview);
        adapter = new A6Adapter();
        context = this;
        initRecyclerView(recyclerView, new LinearLayoutManager(this), adapter);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(A6Service.class);
    }
    public void initRecyclerView(RecyclerView recyclerView, RecyclerView.LayoutManager manager, RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }

    public void search(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String name = nameEditText.getText().toString();
        service.listCountry(name).enqueue(new Callback<CountryList>() {
            @Override
            public void onResponse(Call<CountryList> call, Response<CountryList> response) {
                list = response.body();
                adapter.setCountries(list.getCountry());
                adapter.notifyDataSetChanged();
//                for(Country country: list.getCountry()){
//                    System.out.println(country.getCountry_id());
//                }

                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<CountryList> call, Throwable t) {
                Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();


            }
        });

    }
}