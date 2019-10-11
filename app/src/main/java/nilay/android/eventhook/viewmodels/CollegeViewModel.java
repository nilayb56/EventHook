package nilay.android.eventhook.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.model.College;

public class CollegeViewModel extends ViewModel {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("College");
    private MutableLiveData<List<College>> college;
    //private String collegeId = "";
    //private String collegeName = "";
    private MutableLiveData<String> collegeId;
    private MutableLiveData<String> collegeName;

    public MutableLiveData<String> getCollegeId() {
        if(collegeId==null){
            MutableLiveData<String> id = new MutableLiveData<>();
            id.setValue("0");
            this.collegeId = id;
        }
        return collegeId;
    }

    public MutableLiveData<String> getCollegeName() {
        if(collegeName==null){
            MutableLiveData<String> name = new MutableLiveData<>();
            name.setValue("0");
            this.collegeName = name;
        }
        return collegeName;
    }

    public LiveData<List<College>> getCollegeList() {
        if (college == null) {
            college = new MutableLiveData<List<College>>();
            loadCollege();
        }
        return college;
    }

    private void loadCollege() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<College> clg = new ArrayList<>();
                clg.add(0,new College("0","Select College"));
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    clg.add(childDataSnapshot.getValue(College.class));
                }
                college.setValue(clg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }*/
}
