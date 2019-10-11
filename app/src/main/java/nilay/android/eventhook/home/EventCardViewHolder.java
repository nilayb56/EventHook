package nilay.android.eventhook.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

import nilay.android.eventhook.R;

public class EventCardViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout linearEvent;
    public NetworkImageView event_image;
    public TextView lblEventName;
    public TextView lblEventDesc;

    public EventCardViewHolder(@NonNull View itemView) {
        super(itemView);
        linearEvent = itemView.findViewById(R.id.linearEvent);
        event_image = itemView.findViewById(R.id.event_image);
        lblEventName = itemView.findViewById(R.id.lblEventName);
        lblEventDesc = itemView.findViewById(R.id.lblEventDesc);
    }
}
