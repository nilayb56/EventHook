package nilay.android.eventhook.fragment.coordinator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
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
import nilay.android.eventhook.model.Volunteer;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;

public class CodAssignDutyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private CardView cardVolIfPresent;
    private LinearLayout linearVolAssign;
    private TextView lblVolList;

    private CoordinatorViewModel codViewModel;

    private List<Users> usersList = new ArrayList<>();

    private boolean isVol = false;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    public CodAssignDutyFragment() {    }

    // TODO: Rename and change types and number of parameters
    public static CodAssignDutyFragment newInstance(String param1, String param2) {
        CodAssignDutyFragment fragment = new CodAssignDutyFragment();
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
        View view = inflater.inflate(R.layout.fragment_cod_assign_duty, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        cardVolIfPresent = view.findViewById(R.id.cardVolIfPresent);
        linearVolAssign = view.findViewById(R.id.linearVolAssign);
        lblVolList = view.findViewById(R.id.lblVolList);

        if(getActivity()!=null) {
            codViewModel = new ViewModelProvider(getActivity()).get(CoordinatorViewModel.class);
        }

        codViewModel.getAssignState().observe(this, state -> {
            linearVolAssign.removeAllViews();
            dbRef = database.getReference("Volunteer").child(codViewModel.getEventid());
            dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    isVol = false;
                    linearVolAssign.removeAllViews();
                    Volunteer volunteer = new Volunteer();
                    for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                        String eventid = childDataSnapShot.child("event_id").getValue().toString();
                        String dutyid = childDataSnapShot.child("duty_id").getValue().toString();
                        String valid_user = childDataSnapShot.child("valid_user").getValue().toString();
                        if(!codViewModel.getEventid().equals("") && eventid.equals(codViewModel.getEventid()) && dutyid.equals("0") && valid_user.equals("1")){
                            isVol = true;
                            volunteer = childDataSnapShot.getValue(Volunteer.class);
                            DatabaseReference dbReff = database.getReference("Users").child(volunteer.getUser_id());
                            dbReff.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Users users = dataSnapshot.getValue(Users.class);
                                    if(users!=null) {
                                        usersList.add(users);
                                        codViewModel.setUsers(usersList);
                                        fillVolList(users);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                        if(isVol){
                            cardVolIfPresent.setVisibility(View.GONE);
                        } else {
                            cardVolIfPresent.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });

        return view;
    }

    private void fillVolList(Users user) {
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = DutyAssignFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment!=null) {
            FragmentManager fragmentManager = getFragmentManager();
            if(fragmentManager!=null)
                fragmentManager.beginTransaction().add(R.id.linearVolAssign, fragment, user.getUser_id()).commit();
        }
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
