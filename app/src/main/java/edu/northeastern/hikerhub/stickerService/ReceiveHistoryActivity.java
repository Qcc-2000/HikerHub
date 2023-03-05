package edu.northeastern.hikerhub.stickerService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.stickerService.models.Event;

public class ReceiveHistoryActivity extends AppCompatActivity {

    private static final String EVENT_TABLE = "Events";
    private static final String EVENT_RECEIVER = "receiver";

    private DatabaseReference mDatabase;

    private List<Event> historyEvents = new ArrayList<>();

    private RecyclerView recyclerView;
    private ReceiveHistoryReviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_receive_history);

        // Connects firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String loginUsername = getIntent().getStringExtra("login_username");
        getHistoryOfReceivedStickers(loginUsername);
    }


    private void createRecyclerView() {

        rLayoutManger = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.receive_history_recycler_view);
        recyclerView.setHasFixedSize(true);

        rviewAdapter = new ReceiveHistoryReviewAdapter(historyEvents);
        ItemClickListener itemClickListener = new ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String eventId = historyEvents.get(position).eventId;
                mDatabase.child(EVENT_TABLE).child(eventId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Event event = snapshot.getValue(Event.class);
                        if (event == null) {
                            return;
                        }
                        event.notifyStatus = true;
                        mDatabase.child(EVENT_TABLE).child(eventId).setValue(event);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        };
        rviewAdapter.setOnItemClickListener(itemClickListener);

        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);


    }

    private void getHistoryOfReceivedStickers(String userName) {

        mDatabase.child(EVENT_TABLE)
                .orderByChild(EVENT_RECEIVER)
                .equalTo(userName)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Event> eventHistory = new ArrayList<>();
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Event event = dataSnapshot.getValue(Event.class);
                        eventHistory.add(event);
                    }
                }
                updateHistoryView(eventHistory);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ...
            }
        });
    }

    private void updateHistoryView(List<Event> events) {
        // Sort the event by timestamp desc.
        events.sort((o1, o2) -> Long.compare(o2.timestampInMillis, o1.timestampInMillis));
        historyEvents = events;
        createRecyclerView();
    }

    // notification


    //notification end
}