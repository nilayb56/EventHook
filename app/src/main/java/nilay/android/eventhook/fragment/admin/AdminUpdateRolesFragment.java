package nilay.android.eventhook.fragment.admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.mainadmin.UsersAdapter;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.viewmodels.AppAdminViewModel;

public class AdminUpdateRolesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerUsers;
    private UsersAdapter usersAdapter;
    private AppAdminViewModel adminViewModel;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    public AdminUpdateRolesFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static AdminUpdateRolesFragment newInstance(String param1, String param2) {
        AdminUpdateRolesFragment fragment = new AdminUpdateRolesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_update_roles, container, false);

        recyclerUsers = view.findViewById(R.id.recyclerUsers);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUsers.setItemAnimator(new DefaultItemAnimator());

        if (getActivity() != null) {
            adminViewModel = new ViewModelProvider(getActivity()).get(AppAdminViewModel.class);
        }

        adminViewModel.getStateChange().observe(this, state -> {
            if(adminViewModel.getClgAdminRoleId().equals(""))
                getClgAdminRoleId();
            else
                loadRecyclerView(adminViewModel.getClgAdminRoleId());
        });

        return view;
    }

    private void getClgAdminRoleId() {
        dbRef = database.getReference("UserRole");
        Query query = dbRef.orderByChild("role_name").equalTo("College Admin");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    UserRole role = childDataSnapShot.getValue(UserRole.class);
                    if (role != null) {
                        adminViewModel.setClgAdminRoleId(role.getRole_id());
                        loadRecyclerView(role.getRole_id());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadRecyclerView(String roleId) {
        List<Users> usersList = new ArrayList<>();
        dbRef = database.getReference("Users");
        Query query = dbRef.orderByChild("role_id").equalTo(roleId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    Users user = childDataSnapShot.getValue(Users.class);
                    usersList.add(user);
                }
                usersAdapter = new UsersAdapter(getActivity(), usersList);
                recyclerUsers.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void loadSpinnerUserRole(Spinner spinner, Context context) {
        List<UserRole> roles = new ArrayList<>();
        roles.add(new UserRole("0", "Select User Role"));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("UserRole");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    UserRole role = childDataSnapShot.getValue(UserRole.class);
                    if (role != null && (role.getRole_name().equals("Admin") || role.getRole_name().equals("College Admin"))) {
                            roles.add(role);
                    }
                }
                spinner.setAdapter(new ArrayAdapter<UserRole>(Objects.requireNonNull(context), android.R.layout.simple_spinner_dropdown_item, roles));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
