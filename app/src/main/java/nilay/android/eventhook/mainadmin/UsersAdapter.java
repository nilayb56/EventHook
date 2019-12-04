package nilay.android.eventhook.mainadmin;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.viewmodels.AppAdminViewModel;

import static nilay.android.eventhook.fragment.admin.AdminUpdateRolesFragment.loadSpinnerUserRole;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private FragmentActivity activity;
    private List<Users> users;
    private UserRole newRole;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public UsersAdapter(FragmentActivity activity, List<Users> users) {
        this.activity = activity;
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.user_list,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        AppAdminViewModel adminViewModel = new ViewModelProvider(activity).get(AppAdminViewModel.class);
        Users user = users.get(position);
        loadSpinnerUserRole(holder.spinnerRole,activity);
        holder.lblUserName.setText(user.getUser_name());
        holder.lblUserEmail.setText(user.getEmail_id());
        holder.spinnerRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newRole = (UserRole) parent.getSelectedItem();
                if(newRole.getRole_id().equals(user.getRole_id())){
                    Toast.makeText(activity, "Please Select other than the Previous Role", Toast.LENGTH_SHORT).show();
                    holder.spinnerRole.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holder.btnDone.setOnClickListener((View v) -> {
            if(newRole.getRole_id().equals("0")){
                Toast.makeText(activity, "Please Select a Valid User Role", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle("Changing User Privileges\n")
                        .setMessage("User will now have App Privileges of an Admin\nAre you Sure?")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {
                            dbRef = database.getReference("Users").child(user.getUser_id());
                            dbRef.child("role_id").setValue(newRole.getRole_id());
                            dbRef = database.getReference("CollegeAdmin");
                            dbRef.child(user.getUser_id()).removeValue();
                            adminViewModel.getStateChange().setValue("1");
                        })
                        .setNegativeButton(android.R.string.no, (DialogInterface dialog, int which) -> {
                            holder.spinnerRole.setSelection(0);
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView lblUserName, lblUserEmail;
        private Spinner spinnerRole;
        private Button btnDone;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            lblUserName = itemView.findViewById(R.id.lblUserName);
            lblUserEmail = itemView.findViewById(R.id.lblUserEmail);
            spinnerRole = itemView.findViewById(R.id.spinnerRole);
            btnDone = itemView.findViewById(R.id.btnDone);
        }
    }
}
