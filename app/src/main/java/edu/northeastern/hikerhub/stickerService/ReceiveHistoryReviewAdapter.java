package edu.northeastern.hikerhub.stickerService;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.util.List;

import edu.northeastern.hikerhub.R;
import edu.northeastern.hikerhub.stickerService.models.Event;

public class ReceiveHistoryReviewAdapter extends RecyclerView.Adapter<ReceiveHistoryReviewHolder> {

    private final List<Event> events;
    private ItemClickListener listener;

    //Constructor
    public ReceiveHistoryReviewAdapter(List<Event> itemList) {
        this.events = itemList;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ReceiveHistoryReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ReceiveHistoryReviewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ReceiveHistoryReviewHolder holder, int position) {
        Event currentItem = events.get(position);

        holder.stickerIcon.setImageResource(Integer.parseInt(currentItem.stickerId));
        holder.sender.setText(currentItem.sender);
        holder.time.setText(Instant.ofEpochMilli(currentItem.timestampInMillis).toString());
        if (!currentItem.notifyStatus) {
            holder.status.setText("Unread");
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
