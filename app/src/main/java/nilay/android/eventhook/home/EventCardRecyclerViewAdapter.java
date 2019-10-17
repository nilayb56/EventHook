package nilay.android.eventhook.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.EventImageList;
import nilay.android.eventhook.model.EventTheme;
import nilay.android.eventhook.registration.RegistrationActivity;

public class EventCardRecyclerViewAdapter extends RecyclerView.Adapter<EventCardViewHolder> {

    private List<EventImageList> eventImageLists;
    private ImageRequester imageRequester;
    private Activity activity;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("EventTheme");

    public EventCardRecyclerViewAdapter(List<EventImageList> eventImageLists, Activity activity) {
        this.eventImageLists = eventImageLists;
        imageRequester = ImageRequester.getInstance();
        this.activity = activity;
    }

    @NonNull
    @Override
    public EventCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_images, parent, false);
        return new EventCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventCardViewHolder holder, int position) {
        if (eventImageLists != null && position < eventImageLists.size()) {
            EventImageList eventImage = eventImageLists.get(position);

            holder.lblEventName.setText(eventImage.getEvent().getEvent_name());
            holder.lblEventDesc.setText("");
            holder.lblEventDesc.append("Event Date: "+eventImage.getEvent().getEvent_date());

            imageRequester.setImageFromUrl(holder.event_image, eventImage.getEvent().getImg_url());

            holder.linearEvent.setOnClickListener((View v) -> {
                View eventDetails = LayoutInflater.from(activity).inflate(R.layout.event_details, null);
                TextView eventclg = eventDetails.findViewById(R.id.eventclg);
                eventclg.setText(eventImage.getCollegeName());
                TextView eventdate = eventDetails.findViewById(R.id.eventdate);
                eventdate.setText(eventImage.getEvent().getEvent_date());
                TextView group = eventDetails.findViewById(R.id.group);
                TextView max = eventDetails.findViewById(R.id.max);
                TextView min = eventDetails.findViewById(R.id.min);
                TextView maxmem = eventDetails.findViewById(R.id.maxmem);
                TextView minmem = eventDetails.findViewById(R.id.minmem);
                TextView lblThemes = eventDetails.findViewById(R.id.lblThemes);
                TextView themes = eventDetails.findViewById(R.id.themes);
                TextView themeList = eventDetails.findViewById(R.id.themeList);

                dbRef.child(eventImage.getEvent().getEvent_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    lblThemes.setText("Event Themes");
                                    themes.setVisibility(View.VISIBLE);
                                    themeList.setVisibility(View.VISIBLE);
                                    for (DataSnapshot themeSnapShot : dataSnapshot.getChildren()) {
                                        EventTheme theme = themeSnapShot.getValue(EventTheme.class);
                                        assert theme != null;
                                        themeList.append("\u2022"+theme.getTheme_name()+"\n");
                                    }
                                } else {
                                    lblThemes.setText("No Event Themes");
                                    themes.setVisibility(View.GONE);
                                    themeList.setVisibility(View.GONE);
                                }
                                if(eventImage.getEvent().getGroup_event()==0){
                                    group.setText("Event is Not a Group Event");
                                    max.setVisibility(View.GONE);
                                    min.setVisibility(View.GONE);
                                    maxmem.setVisibility(View.GONE);
                                    minmem.setVisibility(View.GONE);
                                } else {
                                    max.setVisibility(View.VISIBLE);
                                    min.setVisibility(View.VISIBLE);
                                    maxmem.setVisibility(View.VISIBLE);
                                    minmem.setVisibility(View.VISIBLE);
                                    maxmem.setText(String.valueOf(eventImage.getEvent().getGroup_members()));
                                    minmem.setText(String.valueOf(eventImage.getEvent().getMin_members()));
                                }
                                TextView regstart = eventDetails.findViewById(R.id.regstart);
                                regstart.setText(eventImage.getEvent().getReg_start_date());
                                TextView regend = eventDetails.findViewById(R.id.regend);
                                regend.setText(eventImage.getEvent().getReg_end_date());
                                TextView regcancel = eventDetails.findViewById(R.id.regcancel);
                                regcancel.setText(eventImage.getEvent().getCancel_date());
                                TextView regfees = eventDetails.findViewById(R.id.regfees);
                                regfees.setText("");
                                regfees.append("\u20B9"+eventImage.getEvent().getEvent_fees());
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setTitle("Event Details\n")
                                        .setCancelable(false)
                                        .setView(eventDetails)
                                        .setPositiveButton("Register", (DialogInterface dialog, int which) -> {
                                            Intent i = new Intent(activity, RegistrationActivity.class);
                                            i.putExtra("eventid",eventImage.getEvent().getEvent_id());
                                            i.putExtra("eventname",eventImage.getEvent().getEvent_name());
                                            i.putExtra("collegename",eventImage.getCollegeName());
                                            i.putExtra("memmax",String.valueOf(eventImage.getEvent().getGroup_members()));
                                            i.putExtra("memmin",String.valueOf(eventImage.getEvent().getMin_members()));
                                            i.putExtra("fees",eventImage.getEvent().getEvent_fees());
                                            activity.startActivity(i);
                                        })
                                        .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> {
                                        })
                                        .setIcon(android.R.drawable.ic_media_play)
                                        .show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            });
        }
    }

    @Override
    public int getItemCount() {
        return eventImageLists.size();
    }
}
