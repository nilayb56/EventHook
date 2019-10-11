package nilay.android.eventhook.fragment.volunteer;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.registration.AddMoreElementFragment;
import nilay.android.eventhook.fragment.registration.CommonRegistrationFragment;
import nilay.android.eventhook.fragment.registration.GroupMemberFragment;
import nilay.android.eventhook.fragment.registration.GroupRegistrationFragment;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.registration.RegistrationAdapter;
import nilay.android.eventhook.viewmodels.EventViewModel;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;
import nilay.android.eventhook.viewmodels.VolunteerViewModel;

public class QuickRegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView lblVolClgName, lblFormTitle;
    private Spinner spinnerVolEvent;
    private View titleLine;
    private TabLayout tabs;
    private ViewPager viewPagerRegistration;
    private FrameLayout flGetReg;

    private VolunteerViewModel volViewModel;
    private EventViewModel eventViewModel;
    private RegistrationViewModel regViewModel;

    private int count;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public QuickRegistrationFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static QuickRegistrationFragment newInstance(String param1, String param2) {
        QuickRegistrationFragment fragment = new QuickRegistrationFragment();
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
        View view = inflater.inflate(R.layout.fragment_quick_registration, container, false);

        lblVolClgName = view.findViewById(R.id.lblVolClgName);
        lblFormTitle = view.findViewById(R.id.lblFormTitle);
        spinnerVolEvent = view.findViewById(R.id.spinnerVolEvent);
        titleLine = view.findViewById(R.id.titleLine);
        tabs = view.findViewById(R.id.tabs);
        viewPagerRegistration = view.findViewById(R.id.viewPagerRegistration);
        flGetReg = view.findViewById(R.id.flGetReg);

        tabs.setupWithViewPager(viewPagerRegistration);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        if (getActivity() != null) {
            volViewModel = new ViewModelProvider(getActivity()).get(VolunteerViewModel.class);
            eventViewModel = new ViewModelProvider(getActivity()).get(EventViewModel.class);
            regViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);
            eventViewModel.setCollegeid(volViewModel.getCollegeid());
            lblVolClgName.setText(volViewModel.getCollegename());
            volViewModel.setQuickReg(true);
            getUserType();
        }

        eventViewModel.getEvent().observe(this, events -> {
            spinnerVolEvent.setAdapter(new ArrayAdapter<Event>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, events));
        });

        spinnerVolEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position != 0) {
                    Event event = (Event) adapterView.getSelectedItem();
                    regViewModel.setEventid(event.getEvent_id());
                    regViewModel.setEventFees(event.getEvent_fees());
                    regViewModel.setIsGroupEvent(String.valueOf(event.getGroup_event()));
                    regViewModel.setMinmembers(event.getMin_members());
                    regViewModel.setMaxmembers(event.getGroup_members());
                    if (regViewModel.getIsGroupEvent().equals("1")) {
                        setUpGroupViewPager(viewPagerRegistration, regViewModel.getMinmembers());
                    } else {
                        setUpViewPager(viewPagerRegistration, view);
                    }
                } else {
                    Toast.makeText(getContext(), "Please Select a Valid Event!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        viewPagerRegistration.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                regViewModel.setViewPagerPos(position);
                int addMember = regViewModel.getMinmembers();
                regViewModel.setVisibleMemberCount(addMember);
                volViewModel.setPrevRegisteredEventId(regViewModel.getEventid());
                volViewModel.setGeneralConfirmation(true);
                if (regViewModel.getVisibleMemberCount() == position) {
                    if (getActivity() != null) {
                        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                                .setTitle("Done With Registration?\n")
                                .setMessage("\n\u2022Press No if You want to do More Member Registrations...")
                                .setCancelable(false)
                                .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                    regViewModel.setNoListedClg(false);
                                    regViewModel.setRegFlag(false);
                                    regViewModel.setLeaderRegFlag(false);
                                    regViewModel.setValidEmail(false);
                                    regViewModel.setUserExists(false);
                                    flGetReg.setVisibility(View.VISIBLE);
                                    tabs.setVisibility(View.GONE);
                                    viewPagerRegistration.setVisibility(View.GONE);
                                    Fragment fragment = null;
                                    Class fragmentClass = VolFeeConfirmationFragment.class;
                                    try {
                                        fragment = (Fragment) fragmentClass.newInstance();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    assert fragment != null;
                                    fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment, "FeeFragment").commit();
                                })
                                .setNegativeButton("No", (DialogInterface dialog, int which) -> {

                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;
    }

    private void setUpGroupViewPager(ViewPager viewPagerRegistration, Integer minMembers) {
        flGetReg.setVisibility(View.GONE);
        titleLine.setVisibility(View.GONE);
        lblFormTitle.setVisibility(View.GONE);
        tabs.setVisibility(View.VISIBLE);
        viewPagerRegistration.setVisibility(View.VISIBLE);
        RegistrationAdapter adapter = new RegistrationAdapter(getChildFragmentManager(), viewPagerRegistration);
        if (minMembers == 1) {
            adapter.addFragment(new CommonRegistrationFragment(), "Registration Form", 0);
            viewPagerRegistration.setAdapter(adapter);
        } else {
            for (int i = 0; i < minMembers; i++) {
                count = i;
                count++;
                if (i == 0)
                    adapter.addFragment(new GroupRegistrationFragment(), "Group Leader", i);
                else
                    adapter.addFragment(new GroupMemberFragment(), "Member " + count, i);

            }
            adapter.setAddMemberIndex(count);
            adapter.addFragment(new AddMoreElementFragment(), "Add Member", count);
            viewPagerRegistration.setAdapter(adapter);
            regViewModel.setRegistrationAdapter(adapter);
        }
    }

    private void setUpViewPager(ViewPager viewPagerRegistration, View view) {

        flGetReg.removeAllViewsInLayout();

        volViewModel.setGeneralConfirmation(true);
        viewPagerRegistration.setVisibility(View.GONE);
        flGetReg.setVisibility(View.VISIBLE);
        lblFormTitle.setVisibility(View.VISIBLE);
        tabs.setVisibility(View.GONE);
        lblFormTitle.setText("Registration Form");
        titleLine.setVisibility(View.VISIBLE);
        Class fragmentClass = CommonRegistrationFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getChildFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment, "CommonReg").commit();
    }

    private void getUserType() {
        DatabaseReference dbRef = database.getReference("UserRole");
        Query query = dbRef.orderByChild("role_name").equalTo("Student");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot roleSnapShot : dataSnapshot.getChildren()) {
                    UserRole role = roleSnapShot.getValue(UserRole.class);
                    if (role != null && role.getRole_name().equals("Student"))
                        regViewModel.userType(role.getRole_id(), role.getRole_name());
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
    public void onAttach(@NotNull Context context) {
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
