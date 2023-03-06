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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.hikerhub.GroupActivity;
import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.stickerService.ReceiveHistoryActivity;
import edu.northeastern.hikerhub.stickerService.SendHistoryActivity;
import edu.northeastern.hikerhub.stickerService.SendStickerActivity;
import edu.northeastern.hikerhub.stickerService.models.Event;

public class UserActivity extends AppCompatActivity {

    private static final String TAG = UserActivity.class.getSimpleName();
    private String loginUsername = "";

    private static final String EVENT_TABLE = "Events";
    private static final String EVENT_RECEIVER = "receiver";

    private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
        loginUsername = getIntent().getStringExtra("login_username");
        System.out.println("Login user name: " + loginUsername);

        // notification
        createNotificationChannel();
        getNewNotification(loginUsername);

        // notification end

        // Connects firebase
//        GroupActivity.mDatabase = FirebaseDatabase.getInstance().getReference();
        String loginUsername = getIntent().getStringExtra("login_username");


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

    // notification
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification() {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent intent = new Intent(this, ReceiveHistoryActivity.class);
        PendingIntent checkIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Build notification
        // Actions are just fake
        String channelId = getString(R.string.channel_id);

//        Notification noti = new Notification.Builder(this)   DEPRECATED
        Notification noti = new NotificationCompat.Builder(this,channelId)
                .setContentTitle("You got a new Sticker!")
//                .setContentText("Subject")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_action_check, "CHECK", checkIntent)
                .setContentIntent(checkIntent)
                .build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL ;

        notificationManager.notify(0, noti);
    }

    private void getNewNotification(String userName) {


        GroupActivity.mDatabase.child(EVENT_TABLE)
                .orderByChild(EVENT_RECEIVER)
                .equalTo(userName)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        List<Event> eventHistory = new ArrayList<>();
                        if (first == true) {
                            first = false;
                        } else {
                            sendNotification();
                        }

                        System.out.println(snapshot.toString() + "?????dayinlema");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // ...
                    }
                });
    }
    // notification end

}