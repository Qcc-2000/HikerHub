package edu.northeastern.hikerhub.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import com.google.firebase.database.DatabaseReference;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.stickerService.ReceiveHistoryActivity;
import edu.northeastern.hikerhub.stickerService.SendHistoryActivity;
import edu.northeastern.hikerhub.stickerService.SendStickerActivity;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getSimpleName();
    private String loginUsername = "";
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        loginUsername = getIntent().getStringExtra("login_username");
        System.out.println("Login user name: " + loginUsername);
    }

    public void goToSendSticker(View view) {
        Intent intent = new Intent(getBaseContext(), SendStickerActivity.class);
        intent.putExtra("login_username", loginUsername);
        startActivity(intent);
    }

    public void goToSendHistory(View view) {
        Intent intent = new Intent(getBaseContext(), SendHistoryActivity.class);
        intent.putExtra("login_username", loginUsername);
        startActivity(intent);
    }

    public void goToHistory(View view) {
        Intent intent = new Intent(getBaseContext(), ReceiveHistoryActivity.class);
        intent.putExtra("login_username", loginUsername);
        startActivity(intent);
    }
}