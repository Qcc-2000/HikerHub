package edu.northeastern.hikerhub.stickerService;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.stickerService.models.Event;

public class SendHistoryActivity extends AppCompatActivity {

    private static final String EVENT_TABLE = "Events";
    private static final String EVENT_SENDER = "sender";

    private DatabaseReference mDatabase;

    private List<Event> historyEvents = new ArrayList<>();

    private RecyclerView recyclerView;
    private SendHistoryReviewAdapter rviewAdapter;
    private RecyclerView.LayoutManager rLayoutManger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_history);

        // Connects firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String loginUsername = getIntent().getStringExtra("login_username");
        getHistoryOfSentStickers(loginUsername);
    }


    private void createRecyclerView(Map<String, Integer> countByStickerId) {

        rLayoutManger = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.send_history_recycler_view);
        recyclerView.setHasFixedSize(true);

        rviewAdapter = new SendHistoryReviewAdapter(countByStickerId);
        recyclerView.setAdapter(rviewAdapter);
        recyclerView.setLayoutManager(rLayoutManger);


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
        createRecyclerView(countByStickerId);
        // TODO:
    }

}
