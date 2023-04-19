package edu.northeastern.hikerhub;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import edu.northeastern.hikerhub.hiker.TrailActivity;
import edu.northeastern.hikerhub.hiker.fragment.auth.AuthActivity;
import edu.northeastern.hikerhub.hiker.fragment.auth.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startA6Activity(View view){
        startActivity(new Intent(this,A6.class));
    }

    public void startFirebaseActivity(View view){
        startActivity(new Intent(this, HikeActivity.class));
    }

    public void startGroupProjectActivity(View view) {
        startActivity(new Intent(this, GroupActivity.class));
    }
    public void startAboutActivity(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void startHikerActivity(View view) {
        startActivity(new Intent(this, TrailActivity.class));
    }


}
