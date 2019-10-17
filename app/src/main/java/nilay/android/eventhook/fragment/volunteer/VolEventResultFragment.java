package nilay.android.eventhook.fragment.volunteer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.EventResult;
import nilay.android.eventhook.model.EventStudentSubmission;
import nilay.android.eventhook.model.GroupMaster;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.Volunteer;
import nilay.android.eventhook.viewmodels.EventViewModel;
import nilay.android.eventhook.viewmodels.VolDutyViewModel;
import nilay.android.eventhook.viewmodels.VolunteerViewModel;
import nilay.android.eventhook.volunteer.VolunteerActivity;

public class VolEventResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView lblContEvent, lblRatings;
    private Spinner spinnerContestants, spinnerRank;
    private Button btnContResult;

    private VolunteerViewModel volViewModel;
    private EventViewModel eventViewModel;
    private VolDutyViewModel volDutyViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public VolEventResultFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static VolEventResultFragment newInstance(String param1, String param2) {
        VolEventResultFragment fragment = new VolEventResultFragment();
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
        View view = inflater.inflate(R.layout.fragment_vol_event_result, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        lblContEvent = view.findViewById(R.id.lblContEvent);
        lblRatings = view.findViewById(R.id.lblRatings);
        spinnerRank = view.findViewById(R.id.spinnerRank);
        spinnerContestants = view.findViewById(R.id.spinnerContestants);
        btnContResult = view.findViewById(R.id.btnContResult);
        lblRatings.setVisibility(View.GONE);

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
                    String grpevent = String.valueOf(events.get(i).getGroup_event());
                    volViewModel.setIfgroup(grpevent);
                    lblContEvent.setText(volViewModel.getEventname());
                    if(volViewModel.getIfgroup().equals("1")){
                        loadGroupList(volViewModel.getEventid());
                    } else {
                        loadParticipantList(volViewModel.getEventid());
                    }
                    break;
                }
            }
        });

        if (getContext() != null)
            spinnerRank.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, getRankList()));

        spinnerRank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rank = spinnerRank.getItemAtPosition(position).toString();
                switch (rank) {
                    case "First":
                        volViewModel.setRank("1");
                        break;
                    case "Second":
                        volViewModel.setRank("2");
                        break;
                    case "Third":
                        volViewModel.setRank("3");
                        break;
                    default:
                        volViewModel.setRank("0");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerContestants.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(volViewModel.getIfgroup().equals("1")){
                    GroupMaster group = (GroupMaster) parent.getSelectedItem();
                    volViewModel.setWinnerId(group.getGroup_id());
                    volViewModel.setWinnerName(group.getGroup_name());
                } else {
                    UserParticipation participant = (UserParticipation) parent.getSelectedItem();
                    checkIfRatingsPresent(participant);
                    volViewModel.setWinnerId(participant.getUser_id());
                    volViewModel.setWinnerName(participant.getUser_name());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnContResult.setOnClickListener((View v) -> {
                if (volViewModel.getRank().equals("0")) {
                    spinnerRank.requestFocus();
                    TextView errorText = (TextView) spinnerRank.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("SELECT RANK");
                } else if (volViewModel.getWinnerId().equals("0")){
                    spinnerContestants.requestFocus();
                    TextView errorText = (TextView) spinnerContestants.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    if(volViewModel.getIfgroup().equals("1"))
                        errorText.setText("SELECT WINNER GROUP");
                    else
                        errorText.setText("SELECT WINNER CONTESTANT");
                } else {
                    checkifWinnerExists(volViewModel.getWinnerId());
                }
        });

        return view;
    }

    public static List<String> getRankList() {
        List<String> rank = new ArrayList<>();
        rank.add("Select Rank");
        rank.add("First");
        rank.add("Second");
        rank.add("Third");
        return rank;
    }

    private void checkifWinnerExists(String winnerId) {
        dbRef = database.getReference("EventResult").child(volViewModel.getEventid()).child(winnerId);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DatabaseReference dbref = database.getReference("EventResult");
                EventResult eventResult = new EventResult(volViewModel.getWinnerId(), volViewModel.getWinnerName(), volViewModel.getRank());
                if(dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "This Participant is Already given a Rank, to Update Result go to Update Section", Toast.LENGTH_LONG).show();
                    setDefaultSpinners();
                }
                else {
                    dbref.child(volViewModel.getEventid()).child("result_confirmed").setValue("0");
                    dbref.child(volViewModel.getEventid()).child(volViewModel.getWinnerId()).setValue(eventResult);
                    Toast.makeText(getContext(), "Result Submitted", Toast.LENGTH_SHORT).show();
                    setDefaultSpinners();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDefaultSpinners(){
        spinnerRank.setSelection(0);
        spinnerContestants.setSelection(0);
    }

    private void loadParticipantList(String eventid) {
        List<UserParticipation> contestants = new ArrayList<>();
        contestants.add(new UserParticipation("0","Select Winner Contestant"));
        dbRef = database.getReference("UserParticipation").child(volViewModel.getEventid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    UserParticipation userParticipation = childDataSnapShot.getValue(UserParticipation.class);
                    if(userParticipation!=null && userParticipation.getEvent_attendance().equals("1"))
                        contestants.add(childDataSnapShot.getValue(UserParticipation.class));
                }
                volViewModel.setParticipantList(contestants);
                if(getContext()!=null) {
                    spinnerContestants.setAdapter(new ArrayAdapter<UserParticipation>(getContext(), android.R.layout.simple_spinner_dropdown_item, volViewModel.getParticipantList()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkIfRatingsPresent(UserParticipation participant) {
        dbRef = database.getReference("EventStudentSubmission");
        Query query = dbRef.orderByChild("event_id").equalTo(participant.getEvent_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot childDataSnapShot : dataSnapshot.getChildren()) {
                        EventStudentSubmission submission = childDataSnapShot.getValue(EventStudentSubmission.class);
                        assert submission != null;
                        lblRatings.setVisibility(View.VISIBLE);
                        lblRatings.setText("Ratings: "+submission.getRatings());
                    }
                } else
                    lblRatings.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGroupList(String eventid) {
        List<GroupMaster> groups = new ArrayList<>();
        groups.add(new GroupMaster("0","Select Winner Group"));
        dbRef = database.getReference("GroupMaster");
        Query query = dbRef.orderByChild("event_id").equalTo(eventid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    groups.add(childDataSnapShot.getValue(GroupMaster.class));
                }
                volViewModel.setGroupList(groups);
                if(getContext()!=null)
                    spinnerContestants.setAdapter(new ArrayAdapter<GroupMaster>(getContext(), android.R.layout.simple_spinner_dropdown_item,volViewModel.getGroupList()));
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
