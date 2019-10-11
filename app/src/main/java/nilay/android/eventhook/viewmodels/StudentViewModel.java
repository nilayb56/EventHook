package nilay.android.eventhook.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nilay.android.eventhook.model.GroupMaster;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.registration.RegistrationAdapter;

public class StudentViewModel extends ViewModel {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    private Integer groupmembers = 0;
    private boolean leader = false;
    private String userid = "";
    private String groupid = "0";
    private String groupname = "0";
    private String clgid = "";
    private String clgname = "";
    private String eventid = "";
    private RegistrationAdapter registrationAdapter;
    private Integer viewPagerPos;

    /*public StudentViewModel() {
        dbRef = database.getReference("UserParticipation").child(eventid).child(userid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserParticipation userPart = new UserParticipation();
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    String user = childDataSnapShot.child("user_id").getValue().toString();
                    if(user.equals(getUserid())){
                        userPart = childDataSnapShot.getValue(UserParticipation.class);
                        if(userPart!=null) {
                            setEventid(userPart.getEvent_id());
                            setGroupid(userPart.getGroup_id());
                            break;
                        }
                    }
                }
                if(!getGroupid().equals("0")) {

                    DatabaseReference dbReff = database.getReference("GroupMaster").child(getGroupid());
                    dbReff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot gdataSnapshot) {
                            GroupMaster groupMaster = new GroupMaster();
                            groupMaster = gdataSnapshot.getValue(GroupMaster.class);
                            if (groupMaster != null) {
                                if(groupMaster.getGroup_leader().equals(getUserid())){
                                    setLeader(true);
                                }
                                setGroupmembers(groupMaster.getUnregistered_count());
                                setGroupid(groupMaster.getGroup_id());
                                setGroupname(groupMaster.getGroup_name());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getClgid() {
        return clgid;
    }

    public void setClgid(String clgid) {
        this.clgid = clgid;
    }

    public String getClgname() {
        return clgname;
    }

    public void setClgname(String clgname) {
        this.clgname = clgname;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public void setGroupmembers(Integer groupmembers) {
        this.groupmembers = groupmembers;
    }

    public Integer getGroupmembers() {
        return groupmembers;
    }

    public RegistrationAdapter getRegistrationAdapter() {
        return registrationAdapter;
    }

    public void setRegistrationAdapter(RegistrationAdapter registrationAdapter) {
        this.registrationAdapter = registrationAdapter;
    }

    public Integer getViewPagerPos() {
        return viewPagerPos;
    }

    public void setViewPagerPos(Integer viewPagerPos) {
        this.viewPagerPos = viewPagerPos;
    }
}
