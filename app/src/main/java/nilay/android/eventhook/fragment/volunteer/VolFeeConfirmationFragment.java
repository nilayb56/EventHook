package nilay.android.eventhook.fragment.volunteer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
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
import nilay.android.eventhook.fragment.coordinator.UIUtils;
import nilay.android.eventhook.model.Attendance;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.viewmodels.EventViewModel;
import nilay.android.eventhook.viewmodels.VolunteerViewModel;
import nilay.android.eventhook.volunteer.ConfirmPaymentAdapter;

public class VolFeeConfirmationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView lblVtrEvent;
    private ListView listParticipant;
    private Button btnPaymentCnf;
    private CardView cardCnfFee;
    private LinearLayout linearCnfFee;

    private VolunteerViewModel volViewModel;
    private EventViewModel eventViewModel;
    private ConfirmPaymentAdapter paymentAdapter;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public VolFeeConfirmationFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static VolFeeConfirmationFragment newInstance(String param1, String param2) {
        VolFeeConfirmationFragment fragment = new VolFeeConfirmationFragment();
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
        View view = inflater.inflate(R.layout.fragment_vol_fee_confirmation, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        lblVtrEvent = view.findViewById(R.id.lblVtrEvent);
        listParticipant = view.findViewById(R.id.listParticipant);
        btnPaymentCnf = view.findViewById(R.id.btnPaymentCnf);
        cardCnfFee = view.findViewById(R.id.cardCnfFee);
        linearCnfFee = view.findViewById(R.id.linearCnfFee);

        if (getActivity() != null) {
            volViewModel = new ViewModelProvider(getActivity()).get(VolunteerViewModel.class);
            eventViewModel = new ViewModelProvider(getActivity()).get(EventViewModel.class);
            eventViewModel.setCollegeid(volViewModel.getCollegeid());
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

        btnPaymentCnf.setOnClickListener((View v) -> {
            ConfirmPaymentAdapter paymentAdapter = volViewModel.getPaymentAdapter();
            List<Attendance> payers = paymentAdapter.getUserList();
            boolean flag = false;
            String groupid = "";
            int count = 0;
            for (int i = 0; i < payers.size(); i++) {
                if (payers.get(i).getSelected().equals("1")) {
                    flag = true;

                    if (groupid.equals(payers.get(i).getGroupid())) {
                        approveUserPayment(payers.get(i));
                        count++;
                        if(i==(payers.size()-1)){
                            dbRef = database.getReference("GroupMaster").child(groupid);
                            dbRef.child("payments_confirmed").setValue(count);
                        }
                    } else {
                        if (!groupid.equals("")) {
                            dbRef = database.getReference("GroupMaster").child(groupid);
                            dbRef.child("payments_confirmed").setValue(count);
                        }
                        groupid = payers.get(i).getGroupid();
                        approveUserPayment(payers.get(i));
                        count = 0;
                        count++;
                    }

                }
            }
            if (flag) {
                getParticipantList();
                Toast.makeText(getContext(), "Payments Confirmed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "No Option Selected", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void approveUserPayment(Attendance user) {
        if (volViewModel.isQuickReg())
            dbRef = database.getReference("UserParticipation").child(volViewModel.getPrevRegisteredEventId()).child(user.getUserid());
        else
            dbRef = database.getReference("UserParticipation").child(volViewModel.getEventid()).child(user.getUserid());

        dbRef.child("payment_status").setValue("2");
    }

    private void getParticipantList() {

        if (volViewModel.isGeneralConfirmation()) {
            linearCnfFee.setBackground(getResources().getDrawable(R.drawable.bg));
            cardCnfFee.setVisibility(View.GONE);
            lblVtrEvent.setText("Confirm Event Registration Fee");
            dbRef = database.getReference("UserParticipation").child(volViewModel.getPrevRegisteredEventId());
        } else {
            linearCnfFee.setBackground(getResources().getDrawable(R.drawable.bgapps));
            cardCnfFee.setVisibility(View.VISIBLE);
            lblVtrEvent.setText(volViewModel.getEventname());
            dbRef = database.getReference("UserParticipation").child(volViewModel.getEventid());
        }
        Query query = dbRef.orderByChild("payment_status").equalTo("0");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserParticipation> userParticipations = new ArrayList<>();
                for (DataSnapshot childSnapShot : dataSnapshot.getChildren()) {
                    userParticipations.add(childSnapShot.getValue(UserParticipation.class));
                }
                if (userParticipations.size() == 0) {
                    btnPaymentCnf.setText("No Payments Done!");
                    btnPaymentCnf.setPadding(10, 5, 10, 5);
                    btnPaymentCnf.setEnabled(false);
                } else {
                    btnPaymentCnf.setText("Confirm");
                    btnPaymentCnf.setEnabled(true);
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
                    paymentAdapter = new ConfirmPaymentAdapter(getContext(), R.layout.fvol_contestants_list, attendances);
                    listParticipant.setAdapter(paymentAdapter);
                    UIUtils.setListViewHeightBasedOnItems(listParticipant);
                    volViewModel.setPaymentAdapter(paymentAdapter);
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
