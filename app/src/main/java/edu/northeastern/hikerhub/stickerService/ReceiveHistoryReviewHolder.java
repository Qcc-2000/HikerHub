package edu.northeastern.hikerhub.stickerService;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.northeastern.hikerhub.R;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiveHistoryReviewHolder extends RecyclerView.ViewHolder {
    public ImageView stickerIcon;
    public TextView sender;
    public TextView time;
    public TextView status;

    public ReceiveHistoryReviewHolder(View itemView, final ItemClickListener listener) {
        super(itemView);
        stickerIcon = itemView.findViewById(R.id.item_icon);
        sender = itemView.findViewById(R.id.event_sender);
        time = itemView.findViewById(R.id.event_time);
        status = itemView.findViewById(R.id.status);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }
}
