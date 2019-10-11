package nilay.android.eventhook.fragment.coordinator;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.model.VolunteerDuty;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;
import nilay.android.eventhook.viewmodels.DutyAssignViewModel;
import nilay.android.eventhook.viewmodels.VolDutyViewModel;

public class DutyAssignFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView lblVolDet;
    private Spinner spinnerVolDuty;
    private ImageView btnAssign;

    private CoordinatorViewModel codViewModel;
    private DutyAssignViewModel dutyAssignViewModel;
    private VolDutyViewModel volDutyViewModel;

    private Integer idx=0;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    public DutyAssignFragment() {    }

    // TODO: Rename and change types and number of parameters
    public static DutyAssignFragment newInstance(String param1, String param2) {
        DutyAssignFragment fragment = new DutyAssignFragment();
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
        View view = inflater.inflate(R.layout.fragment_duty_assign, container, false);

        lblVolDet = view.findViewById(R.id.lblVolDet);
        spinnerVolDuty = view.findViewById(R.id.spinnerDutyList);
        btnAssign = view.findViewById(R.id.btnAssign);

        if(getActivity()!=null) {
            codViewModel = new ViewModelProvider(getActivity()).get(CoordinatorViewModel.class);
            dutyAssignViewModel = new ViewModelProvider(this).get(DutyAssignViewModel.class);
            volDutyViewModel = new ViewModelProvider(this).get(VolDutyViewModel.class);
        }

        String volId = this.getTag();
        for(int i=0; i<codViewModel.getUsers().size(); i++){
            Users users = codViewModel.getUsers().get(i);
            if(volId.equals(users.getUser_id())){
                lblVolDet.append(users.getUser_name()+"\n"+users.getEmail_id());
                dutyAssignViewModel.setVol_id(users.getUser_id());
                //Toast.makeText(getContext(), "Index: "+volId, Toast.LENGTH_SHORT).show();
                break;
            }
        }

        loadSpinnerVolDuty();

        spinnerVolDuty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    idx = position;
                    VolunteerDuty volunteerDuty = (VolunteerDuty) parent.getSelectedItem();
                    dutyAssignViewModel.setDuty_id(volunteerDuty.getDuty_id());
                    dutyAssignViewModel.setDuty_name(volunteerDuty.getDuty_name());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnAssign.setOnClickListener((View v) -> {
                if(idx==0){
                    spinnerVolDuty.requestFocus();
                    TextView errorText = (TextView) spinnerVolDuty.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);
                    errorText.setText("SELECT DUTY");
                } else {
                    dbRef = database.getReference("Volunteer").child(codViewModel.getEventid()).child(dutyAssignViewModel.getVol_id());
                    dbRef.child("duty_id").setValue(dutyAssignViewModel.getDuty_id());

                    dbRef = database.getReference("VolunteerDuty").child(dutyAssignViewModel.getDuty_id());
                    dbRef.child("duty_assign").setValue("1");

                    removeFragment();
                }
        });

        return view;
    }

    private void removeFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(this.getTag());
        if(fragment != null)
            getChildFragmentManager().beginTransaction().remove(fragment).commit();

        //codViewModel.getAssignState().setValue("1");
    }

    private void loadSpinnerVolDuty() {
        List<VolunteerDuty> volunteerDuties = new ArrayList<>();
        dbRef = database.getReference("VolunteerDuty");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                volunteerDuties.clear();
                volunteerDuties.add(0,new VolunteerDuty("0","0","Select Duty","0","0"));
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    VolunteerDuty volDuty = childDataSnapShot.getValue(VolunteerDuty.class);
                    if(volDuty!=null) {
                        String eventid = volDuty.getEvent_id();
                        String dutyAssign = volDuty.getDuty_assign();
                        String dutyRepeat = volDuty.getDuty_repeat();
                        if(eventid.equals(codViewModel.getEventid())){
                            if(dutyAssign.equals("0")) {
                                volunteerDuties.add(childDataSnapShot.getValue(VolunteerDuty.class));
                            } else {
                                if(dutyRepeat.equals("1")){
                                    volunteerDuties.add(childDataSnapShot.getValue(VolunteerDuty.class));
                                }
                            }
                        }
                    }
                }
                if(getContext()!=null)
                    spinnerVolDuty.setAdapter(new ArrayAdapter<VolunteerDuty>(getContext(), android.R.layout.simple_spinner_dropdown_item, volunteerDuties));
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
