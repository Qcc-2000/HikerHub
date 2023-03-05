package edu.northeastern.hikerhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        startActivity(new Intent(this, FirebaseActivity.class));
    }

    public void startGroupProjectActivity(View view) {
        startActivity(new Intent(this, GroupActivity.class));
    }
    public void startAboutActivity(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }
}