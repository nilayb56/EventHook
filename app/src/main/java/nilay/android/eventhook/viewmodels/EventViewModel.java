package nilay.android.eventhook.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.model.Event;

public class EventViewModel extends ViewModel {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("Event");
    private MutableLiveData<List<Event>> event;
    private String collegeid = "";

    public String getCollegeid() {
        return collegeid;
    }

    public void setCollegeid(String collegeid) {
        this.collegeid = collegeid;
    }

    public MutableLiveData<List<Event>> getEvent() {
        if (event == null) {
            event = new MutableLiveData<List<Event>>();
            loadEvent();
        }
        return event;
    }

    private void loadEvent() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Event> eventList = new ArrayList<>();
                eventList.add(0, new Event("0", "Select Event"));
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    if (childDataSnapShot.child("college_id").exists() && childDataSnapShot.child("college_id").getValue().toString().equals(collegeid)) {
                        eventList.add(childDataSnapShot.getValue(Event.class));
                    }
                }
                event.setValue(eventList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
