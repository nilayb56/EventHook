package nilay.android.eventhook.fragment.volunteer;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.Attendance;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.viewmodels.EventViewModel;
import nilay.android.eventhook.viewmodels.VolDutyViewModel;
import nilay.android.eventhook.viewmodels.VolunteerViewModel;
import nilay.android.eventhook.volunteer.AttndAdapter;

public class VolAttendanceFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView lblVtrEvent;
    private ListView listParticipant;
    private Button btnPartAttend;
    private ViewGroup.LayoutParams list;

    private VolunteerViewModel volViewModel;
    private EventViewModel eventViewModel;
    private VolDutyViewModel volDutyViewModel;
    private AttndAdapter attendanceAdapter;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public VolAttendanceFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static VolAttendanceFragment newInstance(String param1, String param2) {
        VolAttendanceFragment fragment = new VolAttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vol_attendance, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        lblVtrEvent = view.findViewById(R.id.lblVtrEvent);
        listParticipant = view.findViewById(R.id.listParticipant);
        btnPartAttend = view.findViewById(R.id.btnPartAttend);
        list = listParticipant.getLayoutParams();

        if (getActivity() != null) {
            volViewModel = new ViewModelProvider(getActivity()).get(VolunteerViewModel.class);
            volDutyViewModel = new ViewModelProvider(this).get(VolDutyViewModel.class);
            eventViewModel = new ViewModelProvider(getActivity()).get(EventViewModel.class);
            eventViewModel.setCollegeid(volViewModel.getCollegeid());
            volDutyViewModel.setEventid(volViewModel.getEventid());
        }

        eventViewModel.getEvent().observe(this, events -> {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getEvent_id().equals(volViewModel.getEventid())) {
                    volViewModel.setEventname(events.get(i).getEvent_name());
                    volViewModel.setIfgroup(String.valueOf(events.get(i).getGroup_event()));
                    lblVtrEvent.setText(volViewModel.getEventname());
                    getParticipantList();
                    break;
                }
            }
        });

        btnPartAttend.setOnClickListener((View v) -> {
            AttndAdapter attndAdapter = volViewModel.getAttndAdapter();
            List<Attendance> attendances = attndAdapter.getUserList();
            boolean flag = false;
            for (int i = 0; i < attendances.size(); i++) {
                if (attendances.get(i).getSelected().equals("1")) {
                    dbRef = database.getReference("UserParticipation").child(volViewModel.getEventid()).child(attendances.get(i).getUserid());
                    dbRef.child("event_attendance").setValue("1");
                    flag = true;
                }
            }
            if (flag) {
                getParticipantList();
                Toast.makeText(getContext(), "Attendance Submitted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No Participant Selected", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getParticipantList() {
        dbRef = database.getReference("UserParticipation").child(volViewModel.getEventid());
        Query query = dbRef.orderByChild("event_attendance").equalTo("0");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserParticipation> userParticipations = new ArrayList<>();
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    userParticipations.add(childSnapShot.getValue(UserParticipation.class));
                }
                if (userParticipations.size() == 0) {
                    btnPartAttend.setText("No Participant Registered!");
                    btnPartAttend.setPadding(10, 5, 10, 5);
                    btnPartAttend.setEnabled(false);
                    //Toast.makeText(getContext(), "No Participant Registered", Toast.LENGTH_SHORT).show();
                } else {
                    btnPartAttend.setText("OK");
                    btnPartAttend.setEnabled(true);
                }
                fillListView(userParticipations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fillListView(List<UserParticipation> userParticipations) {
        dbRef = database.getReference("Users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Attendance> attendances = new ArrayList<>();
                for (int i = 0; i < userParticipations.size(); i++) {
                    String userid = userParticipations.get(i).getUser_id();
                    String username = dataSnapshot.child(userParticipations.get(i).getUser_id()).getValue(Users.class).getUser_name();
                    String emailid = dataSnapshot.child(userParticipations.get(i).getUser_id()).getValue(Users.class).getEmail_id();
                    String groupname = userParticipations.get(i).getGroup_name();
                    String groupid = userParticipations.get(i).getGroup_id();
                    attendances.add(new Attendance(groupid, groupname, userid, username, emailid));
                }
                if (getContext() != null) {
                    attendanceAdapter = new AttndAdapter(getContext(), R.layout.fvol_contestants_list, attendances);
                    listParticipant.setAdapter(attendanceAdapter);
                    volViewModel.setAttndAdapter(attendanceAdapter);
                }
                if (userParticipations.size() != 0) {
                    Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    listParticipant.startAnimation(animfadein);
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
    public void onAttach(@NonNull Context context) {
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