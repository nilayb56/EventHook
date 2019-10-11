package nilay.android.eventhook.fragment.student;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.registration.GroupMemberFragment;
import nilay.android.eventhook.model.GroupMaster;
import nilay.android.eventhook.registration.RegistrationAdapter;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;
import nilay.android.eventhook.viewmodels.StudentViewModel;

public class StAddMemberFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TabLayout tabsMember;
    private ViewPager viewPagerMember;
    private Spinner spinnerContGroup;
    private CardView cardSelGrp;
    private RegistrationAdapter adapter;

    private Integer groupmembers;

    private StudentViewModel studentViewModel = new StudentViewModel();
    private RegistrationViewModel registrationViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public StAddMemberFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static StAddMemberFragment newInstance(String param1, String param2) {
        StAddMemberFragment fragment = new StAddMemberFragment();
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
        View view = inflater.inflate(R.layout.fragment_st_add_member, container, false);
        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);
        viewPagerMember = view.findViewById(R.id.viewPagerMember);
        tabsMember = view.findViewById(R.id.tabsMember);
        spinnerContGroup = view.findViewById(R.id.spinnerContGroup);
        cardSelGrp = view.findViewById(R.id.cardSelGrp);
        if(getActivity()!=null) {
            registrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);
            studentViewModel = new ViewModelProvider(getActivity()).get(StudentViewModel.class);
        }
        groupmembers = studentViewModel.getGroupmembers();
        loadSpinner();
        viewPagerMember.setCurrentItem(0);
        tabsMember.setupWithViewPager(viewPagerMember);
        tabsMember.setTabMode(TabLayout.MODE_SCROLLABLE);

        spinnerContGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                if(position!=0){
                    cardSelGrp.setVisibility(View.GONE);
                    viewPagerMember.setVisibility(View.VISIBLE);
                    tabsMember.setVisibility(View.VISIBLE);
                    GroupMaster group = (GroupMaster) parent.getSelectedItem();
                    registrationViewModel.setEventid(group.getEvent_id());
                    registrationViewModel.setGroupInfo(group.getGroup_id(),group.getGroup_name());
                    setUpViewPager(group.getUnregistered_count(),viewPagerMember);
                } else {
                    cardSelGrp.setVisibility(View.VISIBLE);
                    viewPagerMember.setVisibility(View.GONE);
                    tabsMember.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        viewPagerMember.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                registrationViewModel.setViewPagerPos(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    private void loadSpinner() {
        dbRef = database.getReference("GroupMaster");
        String userid = studentViewModel.getUserid();
        Query query = dbRef.orderByChild("group_leader").equalTo(userid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<GroupMaster> groups = new ArrayList<>();
                groups.add(0,new GroupMaster("0","Select Group"));
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){
                    GroupMaster group = childDataSnapShot.getValue(GroupMaster.class);
                    groups.add(group);
                }
                if(getContext()!=null)
                    spinnerContGroup.setAdapter(new ArrayAdapter<GroupMaster>(getContext(),android.R.layout.simple_spinner_dropdown_item,groups));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpViewPager(Integer members, ViewPager viewPager) {
        int count;
        adapter = new RegistrationAdapter(getChildFragmentManager(), viewPager);
        adapter.setOnlyMembers(true);
        for (int i = 0; i < members; i++) {
            count = i;
            adapter.addFragment(new GroupMemberFragment(), "Member " + ++count, i);
        }
        viewPagerMember.setAdapter(adapter);
        registrationViewModel.setRegistrationAdapter(adapter);
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
