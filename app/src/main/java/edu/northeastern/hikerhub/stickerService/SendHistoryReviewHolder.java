package edu.northeastern.hikerhub.stickerService;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import edu.northeastern.hikerhub.R;

public class SendHistoryReviewHolder extends RecyclerView.ViewHolder {
    public ImageView stickerIcon;
    public TextView count;

    public SendHistoryReviewHolder(View itemView) {
        super(itemView);
        stickerIcon = itemView.findViewById(R.id.send_history_item_icon);
        count = itemView.findViewById(R.id.send_history_count);
    }
}
