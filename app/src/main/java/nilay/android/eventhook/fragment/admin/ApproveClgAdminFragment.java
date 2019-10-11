package nilay.android.eventhook.fragment.admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
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
import nilay.android.eventhook.mainadmin.ApproveClgAdminAdapter;
import nilay.android.eventhook.model.ApproveUser;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.CollegeAdmin;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.viewmodels.AppAdminViewModel;

public class ApproveClgAdminFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView listClgAdmin;
    private ApproveClgAdminAdapter clgAdminAdapter;

    private AppAdminViewModel adminViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public ApproveClgAdminFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ApproveClgAdminFragment newInstance(String param1, String param2) {
        ApproveClgAdminFragment fragment = new ApproveClgAdminFragment();
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
        View view = inflater.inflate(R.layout.fragment_approve_clg_admin_req, container, false);

        listClgAdmin = view.findViewById(R.id.listClgAdmin);
        listClgAdmin.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (getActivity() != null) {
            adminViewModel = new ViewModelProvider(getActivity()).get(AppAdminViewModel.class);
            adminViewModel.getStateChange().observe(Objects.requireNonNull(getActivity()), state -> {
                fillListView();
            });
        }

        return view;
    }

    private void fillListView() {
        dbRef = database.getReference("CollegeAdmin");
        Query query = dbRef.orderByChild("valid_user").equalTo("0");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ApproveUser> clgAdminList = new ArrayList<>();
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    CollegeAdmin collegeAdmin = childDataSnapShot.getValue(CollegeAdmin.class);
                    if (collegeAdmin != null) {
                        String clgname = collegeAdmin.getCollege_name();
                        String userid = collegeAdmin.getUser_id();
                        String username = collegeAdmin.getUser_name();
                        String emailid = collegeAdmin.getEmail_id();
                        String url = collegeAdmin.getIdCard_URL();
                        clgAdminList.add(new ApproveUser(clgname, userid, username, emailid, url));
                    }
                }
                if (getActivity() != null) {
                    clgAdminAdapter = new ApproveClgAdminAdapter(getActivity(), R.layout.approve_user_list, clgAdminList);
                    listClgAdmin.setAdapter(clgAdminAdapter);
                }
                if (clgAdminList.size() != 0) {
                    Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    listClgAdmin.startAnimation(animfadein);
                } else {
                    Toast.makeText(getActivity(), "No College Admins Registered!", Toast.LENGTH_SHORT).show();
                }
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
