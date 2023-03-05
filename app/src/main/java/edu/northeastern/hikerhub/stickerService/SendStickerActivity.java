package edu.northeastern.hikerhub.stickerService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.stickerService.models.Event;
import edu.northeastern.hikerhub.stickerService.models.User;
import edu.northeastern.hikerhub.utils.Utils;

public class SendStickerActivity extends AppCompatActivity {

    private static final String TAG = SendStickerActivity.class.getSimpleName();
    private String serverKey = "";
    private DatabaseReference mDatabase;
    private static final String EVENT_TABLE = "Events";
    private static final String EVENT_SENDER = "sender";
    private static final String USER_TABLE = "Users";
    private String stickerId;
    private String senderUserName;
    private String receiverUserName;
    private EditText et_receiver;
    private Button btn_send;
    private ImageView selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sticker);
        serverKey = "key=" + Utils.getProperties(getApplicationContext()).getProperty("SERVER_KEY");
        // Connects firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        senderUserName = getIntent().getStringExtra("login_username");
        et_receiver = findViewById(R.id.receiver);
        btn_send = findViewById(R.id.send);

        //image
        ImageView ivCat1 = (ImageView) findViewById(R.id.cat1);
        ImageView ivCat2 = (ImageView) findViewById(R.id.cat2);
        ivCat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerId = String.valueOf(R.drawable.cat1);
                ivCat1.getDrawable().setColorFilter(0x77000000,PorterDuff.Mode.SRC_ATOP);

                selectedImage = ivCat1;
                Log.d("logInfo=:",stickerId);
            }
        });
        ivCat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerId = String.valueOf(R.drawable.cat2);
                ivCat2.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                selectedImage = ivCat2;
                Log.d("logInfo=:",stickerId);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiverUserName = et_receiver.getText().toString();
                selectedImage.getDrawable().clearColorFilter();
                Log.d("logInfo=:",receiverUserName);
                sendSticker(senderUserName, receiverUserName, stickerId);
            }
        });

    }

    // Sends sticker to another user
    private void sendSticker(String senderUserName, String receiverUserName, String stickerId) {

        mDatabase.child(USER_TABLE).child(receiverUserName).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User receiver = snapshot.getValue(User.class);
                        if (receiver == null) {
                            Utils.postToastMessage(String.format("Receiver %s doesn't exist", receiverUserName),
                                    getApplicationContext());
                            return;
                        }
                        if (receiver.fcmToken.isEmpty()) {
                            Utils.postToastMessage(
                                    String.format("Receiver %s doesn't have a registered device", receiverUserName),
                                    getApplicationContext());
                            return;
                        }

                        String eventId = UUID.randomUUID().toString();
                        Event event = new Event(eventId, stickerId, senderUserName, receiverUserName,
                                Instant.now().toEpochMilli(), false);
                        mDatabase.child(EVENT_TABLE).child(eventId).setValue(event);
                        sendMessageToDevice(senderUserName, stickerId, eventId, receiverUserName,
                                receiver.fcmToken);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // ...
                    }
                });

    }

    /**
     * Pushes a notification to a given device-- in particular, this device,
     * because that's what the instanceID token is defined to be.
     */
    private void sendMessageToDevice(
            String sender, String stickerId, String eventId, String receiver, String targetToken) {
        // Prepare data
        JSONObject jPayload = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {

            jdata.put("title", String.format("%s sends you a new message", sender));
            jdata.put("stickerId", stickerId);
            jdata.put("eventId", eventId);
            jdata.put("receiver", receiver);

            // If sending to a single client
            jPayload.put("to", targetToken);
            jPayload.put("priority", "high");
            jPayload.put("data", jdata);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final String resp =
                        edu.northeastern.hikerhub.utils.Utils.fcmHttpConnection(serverKey, jPayload);
                Log.i(TAG, String.format("FCM Server response: %s", resp));
                try {
                    JSONObject responseJson = new JSONObject(resp);
                    if (responseJson.has("success") && responseJson.getInt("success") == 1) {
                        Utils.postToastMessage("Sticker sent successfully!", getApplicationContext());
                    } else {
                        Utils.postToastMessage("Sticker sent failed! " +
                                        responseJson.getJSONArray("results").getJSONObject(0).get("error"),
                                getApplicationContext());
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "error: " + e.toString());
                    Utils.postToastMessage("Sticker sent failed!", getApplicationContext());
                }
            }
        });
        t.start();
    }


    public void getHistoryOfSentStickers(String userName) {
        mDatabase.child(EVENT_TABLE).orderByChild(EVENT_SENDER).equalTo(userName).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Event> eventHistory = new ArrayList<>();
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Event event = dataSnapshot.getValue(Event.class);
                                eventHistory.add(event);
                            }
                            updateStatisticsView(eventHistory);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //
                    }
                });

    }

    private void updateStatisticsView(List<Event> eventHistory) {
        Map<String, Integer> countByStickerId = new HashMap<>();
        for (Event event : eventHistory) {
            countByStickerId.put(event.stickerId, countByStickerId.getOrDefault(event.stickerId, 0) + 1);
        }
        // TODO:
    }
}