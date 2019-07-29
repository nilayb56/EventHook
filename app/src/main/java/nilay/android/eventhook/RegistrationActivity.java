package nilay.android.eventhook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nilay.android.eventhook.model.Event;

public class RegistrationActivity extends AppCompatActivity {
    private String eventname = "";
    private String eventid = "";
    private String collegename = "";
    String modeprivate,groupevent="";
    private EditText txtSelectedEvent;
    private TextView lblEventData;
    Event event = new Event();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbref = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        lblEventData = (TextView) findViewById(R.id.lblEventData);
        txtSelectedEvent = (EditText) findViewById(R.id.txtSelectedEvent);

        Intent i = getIntent();
        eventname = i.getStringExtra("Event_Name");
        eventid = i.getStringExtra("Event_Id");
        collegename = i.getStringExtra("College_Name");
        txtSelectedEvent.setText(eventname);
        getEventData();
    }

    private void getEventData() {
        dbref = database.getReference("Event").child(eventid);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
                if(event.getMode_private()==0){
                    modeprivate = "It is not a Private Event";
                }else {
                    modeprivate = "It is a Private Event";
                }
                if(event.getGroup_event()==0){
                    groupevent = "It is not a Group Event";
                }else {
                    groupevent = "It is a Group Event\n\u2022Event allows maximum "+event.getGroup_members().toString()+" Members";
                }
                lblEventData.setText("\u2022Event is Organized by "+collegename+"\n\u2022"+groupevent+"\n\u2022"+modeprivate+"\n\u2022Registrations are started from Date "+event.getReg_start_date()+"\n\u2022Last Date of Registration is "+event.getReg_end_date()+"\n\u2022Last Date of Cancelling Registration is "+event.getCancel_date()+"\n\u2022Entry Fees for the Event is \u20B9"+event.getEvent_fees()+"\n\u2022THE BIG EVENT DATE IS "+event.getEvent_date());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
