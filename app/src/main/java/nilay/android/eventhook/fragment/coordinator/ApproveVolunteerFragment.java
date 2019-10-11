package nilay.android.eventhook.fragment.coordinator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.coordinator.ApproveVolunteerAdapter;
import nilay.android.eventhook.model.ApproveUser;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.model.Volunteer;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;
import nilay.android.eventhook.viewmodels.EventViewModel;

public class ApproveVolunteerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView lblVtrSelEvent;
    private ListView listVolunteer;

    private String collegename = "";
    private String collegeid = "";

    private CoordinatorViewModel cordViewModel;
    private EventViewModel eventViewModel;
    private ApproveVolunteerAdapter volunteerAdapter;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public ApproveVolunteerFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ApproveVolunteerFragment newInstance(String param1, String param2) {
        ApproveVolunteerFragment fragment = new ApproveVolunteerFragment();
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
        View view = inflater.inflate(R.layout.fragment_approve_volunteer, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        lblVtrSelEvent = view.findViewById(R.id.lblVtrSelEvent);
        listVolunteer = view.findViewById(R.id.listVolunteer);
        listVolunteer.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        if (getActivity() != null) {
            eventViewModel = new ViewModelProvider(getActivity()).get(EventViewModel.class);
            cordViewModel = new ViewModelProvider(getActivity()).get(CoordinatorViewModel.class);
            collegeid = cordViewModel.getCollegeid();
            collegename = cordViewModel.getCollegename();
            eventViewModel.setCollegeid(collegeid);
        }

        eventViewModel.getEvent().observe(this, events -> {
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getEvent_id().equals(cordViewModel.getEventid())) {
                    cordViewModel.setEventname(events.get(i).getEvent_name());
                    lblVtrSelEvent.setText(cordViewModel.getEventname());
                    fillListView();
                    break;
                }
            }
        });

        cordViewModel.getStateChange().observe(this, stateChanged -> {
            if(!cordViewModel.getEventid().equals("")) {
                    fillListView();
            }
        });

        return view;
    }

    private void fillListView() {
        if (getActivity() != null) {
            Log.e("Called: ", "Volunteer");
            dbRef = database.getReference("Volunteer").child(cordViewModel.getEventid());
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Volunteer> volunteer = new ArrayList<>();
                    for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                        if (childDataSnapShot.child("valid_user").getValue().toString().equals("0"))
                            volunteer.add(childDataSnapShot.getValue(Volunteer.class));
                    }
                    if (volunteer.size() == 0) {
                        if (getContext() != null)
                            Toast.makeText(getContext(), "No Volunteer Registered", Toast.LENGTH_SHORT).show();
                    }
                    loadListView(volunteer);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void loadListView(List<Volunteer> volunteer) {
        dbRef = database.getReference("Users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //List<Users> users = new ArrayList<>();
                List<ApproveUser> users = new ArrayList<>();
                for (int i = 0; i < volunteer.size(); i++) {
                    Users user = dataSnapshot.child(volunteer.get(i).getUser_id()).getValue(Users.class);
                    if (user != null) {
                        String userid = user.getUser_id();
                        String username = user.getUser_name();
                        String emailid = user.getEmail_id();
                        String eventid = volunteer.get(i).getEvent_id();
                        users.add(new ApproveUser(userid, username, emailid, eventid));
                    }
                    //users.add(dataSnapshot.child(volunteer.get(i).getUser_id()).getValue(Users.class));
                }
                //listVolunteer.setAdapter(new ArrayAdapter<Users>(getContext(), android.R.layout.simple_list_item_multiple_choice, users));
//                UIUtils.setListViewHeightBasedOnItems(listVolunteer);
                if (getActivity() != null) {
                    volunteerAdapter = new ApproveVolunteerAdapter(getActivity(), R.layout.approve_user_list, users);
                    listVolunteer.setAdapter(volunteerAdapter);
                }
                if (volunteer.size() != 0) {
                    Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                    listVolunteer.startAnimation(animfadein);
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
