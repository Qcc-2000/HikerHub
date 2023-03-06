package edu.northeastern.hikerhub.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.stickerService.ReceiveHistoryActivity;
import edu.northeastern.hikerhub.stickerService.SendHistoryActivity;
import edu.northeastern.hikerhub.stickerService.SendStickerActivity;
import edu.northeastern.hikerhub.stickerService.models.Event;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getSimpleName();
    private String loginUsername = "";
    private DatabaseReference mDatabase;

    private static final String EVENT_TABLE = "Events";
    private static final String EVENT_RECEIVER = "receiver";


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