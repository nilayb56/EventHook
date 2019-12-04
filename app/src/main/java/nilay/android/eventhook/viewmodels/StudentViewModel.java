package nilay.android.eventhook.viewmodels;

import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.GroupMaster;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.registration.RegistrationAdapter;

public class StudentViewModel extends ViewModel {
    private Integer groupmembers = 0;
    private boolean leader = false;
    private String userid = "";
    private String groupid = "0";
    private String groupname = "0";
    private String clgid = "";
    private String clgname = "";
    private String eventid = "";
    private List<UserParticipation> participations;
    private List<Event> participatedEvents;
    private ImageView imageview;
    private Integer holderPosition;
    private ArrayList<Uri> filePathList;

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

    public List<UserParticipation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<UserParticipation> participations) {
        this.participations = participations;
    }

    public List<Event> getParticipatedEvents() {
        return participatedEvents;
    }

    public void setParticipatedEvents(List<Event> participatedEvents) {
        this.participatedEvents = participatedEvents;
    }

    public ImageView getImageview() {
        return imageview;
    }

    public void setImageview(ImageView imageview) {
        this.imageview = imageview;
    }

    public Integer getHolderPosition() {
        return holderPosition;
    }

    public void setHolderPosition(Integer holderPosition) {
        this.holderPosition = holderPosition;
    }

    public ArrayList<Uri> getFilePathList() {
        return filePathList;
    }

    public void setFilePathList(ArrayList<Uri> filePathList) {
        this.filePathList = filePathList;
    }
}
