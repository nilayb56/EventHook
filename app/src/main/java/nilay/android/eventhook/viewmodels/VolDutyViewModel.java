package nilay.android.eventhook.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.model.VolunteerDuty;

public class VolDutyViewModel extends ViewModel {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("VolunteerDuty");
    private List<String> staticDuties;
    private MutableLiveData<List<VolunteerDuty>> volduties;
    private String eventid = "";

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public List<String> getStaticDuties() {
        staticDuties = new ArrayList<>();
        staticDuties.add(0,"Quick Registrations");
        staticDuties.add(1,"Attendance");
        staticDuties.add(2,"Submit Result");
        staticDuties.add(3,"Finance");
        return staticDuties;
    }

    public MutableLiveData<List<VolunteerDuty>> getVolduties() {
        if (volduties == null) {
            volduties = new MutableLiveData<List<VolunteerDuty>>();
            loadDuties();
        }
        return volduties;
    }

    private void loadDuties() {
        Query query = dbRef.orderByChild("event_id").equalTo(getEventid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<VolunteerDuty> dutyList = new ArrayList<>();
                dutyList.add(0, new VolunteerDuty("0", "0", "Select Duty", "0", "0"));
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    dutyList.add(childDataSnapShot.getValue(VolunteerDuty.class));
                }
                volduties.setValue(dutyList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
