package nilay.android.eventhook.coordinator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.ApproveUser;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;

public class ApproveVolunteerAdapter extends ArrayAdapter<ApproveUser> {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    List<ApproveUser> userList;
    FragmentActivity activity;
    int resource;
    private CoordinatorViewModel coordinatorViewModel;

    public ApproveVolunteerAdapter(@NonNull FragmentActivity activity, int resource, @NonNull List<ApproveUser> userList) {
        super(activity, resource, userList);
        this.activity = activity;
        this.resource = resource;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(resource, null, false);

        if(userList.size()==0){
            Toast.makeText(activity, "No Coordinator Registered", Toast.LENGTH_SHORT).show();
        }

        TextView lblCoordName = view.findViewById(R.id.lblAprvUserName);
        ImageView imgApproveCoord = view.findViewById(R.id.imgApproveUser);
        ImageView imgRejectCoord = view.findViewById(R.id.imgRejectUser);

        if(activity!=null){
            coordinatorViewModel = new ViewModelProvider(activity).get(CoordinatorViewModel.class);
        }

        ApproveUser user = userList.get(position);

        lblCoordName.setText("");
        lblCoordName.append(user.getUser_name()+"\n("+user.getEmail_id()+")");

        imgApproveCoord.setOnClickListener( (View v) -> {
            dbRef = database.getReference("Volunteer").child(user.getEvent_id()).child(user.getUser_id());
            dbRef.child("valid_user").setValue("1");
            coordinatorViewModel.getStateChange().setValue("1");
            Toast.makeText(activity, "Approved", Toast.LENGTH_SHORT).show();
        });

        imgRejectCoord.setOnClickListener( (View v) -> {
            dbRef = database.getReference("Volunteer").child(user.getEvent_id());
            dbRef.child(user.getUser_id()).removeValue();
            DatabaseReference dbref = database.getReference("Users");
            dbref.child(user.getUser_id()).removeValue();
            userList.remove(position);
            coordinatorViewModel.getStateChange().setValue("0");
            Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
