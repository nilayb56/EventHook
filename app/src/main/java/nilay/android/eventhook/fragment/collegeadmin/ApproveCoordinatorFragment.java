package nilay.android.eventhook.fragment.collegeadmin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import nilay.android.eventhook.R;
import nilay.android.eventhook.collegeadmin.ApproveCoordinatorAdapter;
import nilay.android.eventhook.model.ApproveUser;
import nilay.android.eventhook.model.Coordinator;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.model.VolunteerDuty;
import nilay.android.eventhook.viewmodels.ClgAdminViewModel;
import nilay.android.eventhook.viewmodels.EventViewModel;
import nilay.android.eventhook.viewmodels.VolDutyViewModel;

public class ApproveCoordinatorFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Spinner spinnerClgAdmEvent;
    private ListView listCoordinators;
    private CardView cardMsgCoord;

    private String collegename = "";
    private String collegeid = "";

    private ClgAdminViewModel clgAdminViewModel;
    private EventViewModel eventViewModel;
    private VolDutyViewModel volDutyViewModel;
    private ApproveCoordinatorAdapter coordinatorAdapter;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public ApproveCoordinatorFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ApproveCoordinatorFragment newInstance(String param1, String param2) {
        ApproveCoordinatorFragment fragment = new ApproveCoordinatorFragment();
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
        View view = inflater.inflate(R.layout.fragment_approve_coordinator, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        spinnerClgAdmEvent = view.findViewById(R.id.spinnerClgAdmEvent);
        listCoordinators = view.findViewById(R.id.listCoordinators);
        listCoordinators.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        cardMsgCoord = view.findViewById(R.id.cardMsgCoord);

        if (getActivity() != null) {
            eventViewModel = new ViewModelProvider(getActivity()).get(EventViewModel.class);
            clgAdminViewModel = new ViewModelProvider(getActivity()).get(ClgAdminViewModel.class);
            volDutyViewModel = new ViewModelProvider(getActivity()).get(VolDutyViewModel.class);
            collegeid = clgAdminViewModel.getCollegeid();
            collegename = clgAdminViewModel.getCollegename();
            eventViewModel.setCollegeid(collegeid);
        }

        eventViewModel.getEvent().observe(this, events -> {
            spinnerClgAdmEvent.setAdapter(new ArrayAdapter<Event>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, events));
        });

        spinnerClgAdmEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    Event event = (Event) adapterView.getSelectedItem();
                    clgAdminViewModel.setEventid(event.getEvent_id());
                    clgAdminViewModel.setEventname(event.getEvent_name());
                    volDutyViewModel.setEventid(event.getEvent_id());
                    fillListView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        clgAdminViewModel.getStateChange().observe(this, stateChanged -> {
            if(!clgAdminViewModel.getEventid().equals("")) {
                if (stateChanged.equals("1")) {
                    insertVolDuties(volDutyViewModel.getEventid());
                    fillListView();
                } else if (stateChanged.equals("0")) {
                    fillListView();
                }
            }
        });

        return view;
    }

    private void insertVolDuties(String eventid) {
        List<String> duties = volDutyViewModel.getStaticDuties();
        for(int i=0; i<duties.size(); i++){
            dbRef = database.getReference("VolunteerDuty");
            String id = dbRef.push().getKey();
            VolunteerDuty volDuty = new VolunteerDuty(eventid, id, duties.get(i), "0", "0");
            if(id!=null)
                dbRef.child(id).setValue(volDuty);
        }
    }

    private void fillListView() {
        Log.e("Called: ", "Coordinator");
        dbRef = database.getReference("Coordinator");
        Query query = dbRef.orderByChild("event_id").equalTo(clgAdminViewModel.getEventid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Coordinator> coordinator = new ArrayList<>();
                for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                    if (childDataSnapShot.child("valid_user").getValue().toString().equals("0"))
                        coordinator.add(childDataSnapShot.getValue(Coordinator.class));
                }
                if (coordinator.size() == 0) {
                    listCoordinators.setVisibility(View.GONE);
                    cardMsgCoord.setVisibility(View.VISIBLE);
                    if(getContext()!=null)
                        Toast.makeText(getContext(), "No Coordinator Registered", Toast.LENGTH_SHORT).show();
                } else {
                    listCoordinators.setVisibility(View.VISIBLE);
                    cardMsgCoord.setVisibility(View.GONE);
                }
                loadListView(coordinator);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadListView(List<Coordinator> coordinator) {
        dbRef = database.getReference("Users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ApproveUser> coodinatorList = new ArrayList<>();
                for (int i = 0; i < coordinator.size(); i++) {
                    Users user = dataSnapshot.child(coordinator.get(i).getUser_id()).getValue(Users.class);
                    if(user!=null) {
                        String userid = user.getUser_id();
                        String username = user.getUser_name();
                        String emailid = user.getEmail_id();
                        String eventid = coordinator.get(i).getEvent_id();
                        coodinatorList.add(new ApproveUser(userid, username, emailid, eventid));
                    }
                }
                if(getActivity()!=null) {
                    coordinatorAdapter = new ApproveCoordinatorAdapter(getActivity(), R.layout.approve_user_list, coodinatorList);
                    listCoordinators.setAdapter(coordinatorAdapter);
                }
                if(coordinator.size()!=0) {
                    Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    listCoordinators.startAnimation(animfadein);
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
