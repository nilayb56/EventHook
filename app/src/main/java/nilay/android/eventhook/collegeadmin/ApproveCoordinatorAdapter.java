package nilay.android.eventhook.collegeadmin;

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
import nilay.android.eventhook.viewmodels.ClgAdminViewModel;

public class ApproveCoordinatorAdapter extends ArrayAdapter<ApproveUser> {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    List<ApproveUser> coordList;
    FragmentActivity activity;
    int resource;
    private ClgAdminViewModel clgAdminViewModel;

    public ApproveCoordinatorAdapter(@NonNull FragmentActivity activity, int resource, @NonNull List<ApproveUser> coordList) {
        super(activity, resource, coordList);
        this.activity = activity;
        this.resource = resource;
        this.coordList = coordList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(resource, null, false);

        if(coordList.size()==0){
            Toast.makeText(activity, "No Coordinator Registered", Toast.LENGTH_SHORT).show();
        }

        TextView lblCoordName = view.findViewById(R.id.lblAprvUserName);
        ImageView imgApproveCoord = view.findViewById(R.id.imgApproveUser);
        ImageView imgRejectCoord = view.findViewById(R.id.imgRejectUser);

        if(activity!=null){
            clgAdminViewModel = new ViewModelProvider(activity).get(ClgAdminViewModel.class);
        }

        ApproveUser coodinator = coordList.get(position);

        lblCoordName.setText("");
        lblCoordName.append(coodinator.getUser_name()+"\n("+coodinator.getEmail_id()+")");

        imgApproveCoord.setOnClickListener( (View v) -> {
            dbRef = database.getReference("Coordinator").child(coodinator.getUser_id());
            dbRef.child("valid_user").setValue("1");
            clgAdminViewModel.getStateChange().setValue("1");
            Toast.makeText(activity, "Approved", Toast.LENGTH_SHORT).show();
        });

        imgRejectCoord.setOnClickListener( (View v) -> {
            dbRef = database.getReference("Coordinator");
            dbRef.child(coodinator.getUser_id()).removeValue();
            DatabaseReference dbref = database.getReference("Users");
            dbref.child(coodinator.getUser_id()).removeValue();
            coordList.remove(position);

            clgAdminViewModel.getStateChange().setValue("0");
            Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
