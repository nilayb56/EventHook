package nilay.android.eventhook.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import nilay.android.eventhook.home.HomeTwoActivity;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.registration.AddMoreElementFragment;
import nilay.android.eventhook.fragment.registration.CommonRegistrationFragment;
import nilay.android.eventhook.fragment.registration.GroupMemberFragment;
import nilay.android.eventhook.fragment.registration.GroupRegistrationFragment;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;

public class RegistrationActivity extends AppCompatActivity {
    private String eventname = "";
    private String eventid = "";
    private String eventFees = "";
    private String collegename = "";
    private Integer groupmembersmax = 0;
    private Integer groupmembersmin = 0;
    private String roleid = "";
    private String rolename = "";
    private String role = "0";
    private Integer cnt = 0;
    private Integer count = 0;
    private LinearLayout linearRegActivity;
    private TextView lblEventData, lblFormTitle, lblUserRole;
    private FrameLayout flGetReg;
    private Spinner spinnerUserRole;
    private ArrayList<String> roleName = new ArrayList<>();
    private ArrayList<String> roleId = new ArrayList<>();
    private ViewPager viewPagerRegistration;
    private TabLayout tabLayout;
    private View formLine;
    private RegistrationAdapter adapter;
    private RegistrationViewModel regViewModel = new RegistrationViewModel();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        linearRegActivity = findViewById(R.id.linearRegActivity);
        lblUserRole = findViewById(R.id.lblUserRole);
        lblEventData = findViewById(R.id.lblEventData);
        lblFormTitle = findViewById(R.id.lblFormTitle);
        spinnerUserRole = findViewById(R.id.spinnerUserRole);
        viewPagerRegistration = findViewById(R.id.viewPagerRegistration);
        tabLayout = findViewById(R.id.tabs);
        flGetReg = findViewById(R.id.flGetReg);
        formLine = findViewById(R.id.titleLine);

        Activity activity = this;

        loadSpinnerUserRole();

        Intent i = getIntent();
        if (i.getStringExtra("role") != null) {
            role = i.getStringExtra("role");
        } else {
            role = "0";
        }
        eventname = i.getStringExtra("eventname");
        eventid = i.getStringExtra("eventid");
        eventFees = i.getStringExtra("fees");
        collegename = i.getStringExtra("collegename");
        if (i.getStringExtra("memmax") != null) {
            groupmembersmax = Integer.parseInt(i.getStringExtra("memmax"));
            groupmembersmin = Integer.parseInt(i.getStringExtra("memmin"));
        }
        if (groupmembersmax == 0) {
            groupmembersmax = 1;
            groupmembersmin = 1;
        }

        regViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        regViewModel.setEventid(eventid);
        regViewModel.setEventFees(eventFees);
        regViewModel.setMaxmembers(groupmembersmax);
        regViewModel.setMinmembers(groupmembersmin);

        viewPagerRegistration.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPagerRegistration);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        spinnerUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    regViewModel.setAdapterView(adapterView);
                    //((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                    roleid = "";
                    rolename = "";
                    rolename = spinnerUserRole.getItemAtPosition(spinnerUserRole.getSelectedItemPosition()).toString();
                    roleid = roleId.get(i);
                    regViewModel.userType(roleid, rolename);
                    if (rolename.equals("College Admin")) {
                        lblEventData.setTextColor(getResources().getColor(R.color.colorText));
                        lblEventData.setText("");
                        lblEventData.append("Proceed with Registration");
                        setupViewPagerCommon(viewPagerRegistration);
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                        builder.setTitle("No Event Selected")
                                .setMessage("\u2022Registration is of College Admin Type\n\n\u2022For Student Event Participation please Select an Event first")
                                .setCancelable(false)
                                .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert);
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        if (eventid == null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                            builder.setTitle("No Event Selected")
                                    .setMessage("\u2022For further Registration, Selection of any Event is Must!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                        Intent intent = new Intent(RegistrationActivity.this, HomeTwoActivity.class);
                                        startActivity(intent);
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert);;
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            if (rolename.equals("Student") && groupmembersmax != 1) {

                                setupViewPagerStudent(viewPagerRegistration, groupmembersmin);

                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setTitle("Disclaimer")
                                        .setMessage("\u2022Minimum Registrations of " + groupmembersmin + " Participants are Compulsory,\n\n\u2022Further Regs. can be done after Group Leader Log in or Adding new Memebers on Clicking '+' Icon.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert);;
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                setupViewPagerCommon(viewPagerRegistration);
                            }
                            //lblEventData.setTextColor(Color.WHITE);
                            lblEventData.setText("");
                            lblEventData.append("\u2022Event Name: " + eventname);
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        viewPagerRegistration.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                regViewModel.setViewPagerPos(position);
                if (regViewModel.getVisibleMemberCount() == position) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("Done With Registration?\n")
                            .setMessage("\n\u2022Press 'NO' if You want to do More Member Registrations...")
                            .setCancelable(false)
                            .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                Intent i = new Intent(RegistrationActivity.this,HomeTwoActivity.class);
                                startActivity(i);
                            })
                            .setNegativeButton("No", (DialogInterface dialog, int which) -> {

                            })
                            .setIcon(android.R.drawable.ic_dialog_alert);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = getScreenOrientation();
        if (orientation == 1) {
            linearRegActivity.setBackground(getResources().getDrawable(R.drawable.bgfinal));
        } else if (orientation == 2) {
            linearRegActivity.setBackground(getResources().getDrawable(R.drawable.bglandscape));
        }
    }

    protected int getScreenOrientation() {
        Display getOrient = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        getOrient.getSize(size);

        int orientation;
        if (size.x < size.y) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        return orientation;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent i = new Intent(RegistrationActivity.this, HomeTwoActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setupViewPagerStudent(ViewPager viewPager, Integer cnt) {
        Log.e("Status", "setupViewPagerStudent() Method Executed");
        flGetReg.setVisibility(View.GONE);
        formLine.setVisibility(View.GONE);
        lblFormTitle.setVisibility(View.GONE);
        tabLayout.setVisibility(View.VISIBLE);
        viewPagerRegistration.setVisibility(View.VISIBLE);
        adapter = new RegistrationAdapter(getSupportFragmentManager(), viewPager);
        if (cnt == 1) {
            adapter.addFragment(new CommonRegistrationFragment(), "Registration Form", 0);
            viewPager.setAdapter(adapter);
        } else {
            for (int i = 0; i < cnt; i++) {
                count = i;
                count++;
                if (i == 0)
                    adapter.addFragment(new GroupRegistrationFragment(), "Group Leader", i);
                else
                    adapter.addFragment(new GroupMemberFragment(), "Member " + count, i);

            }
            adapter.setAddMemberIndex(count);
            adapter.addFragment(new AddMoreElementFragment(), "Add Member", count);
            viewPager.setAdapter(adapter);
            regViewModel.setRegistrationAdapter(adapter);
        }

    }

    private void setupViewPagerCommon(ViewPager viewPager) {
        Log.e("Status", "setupViewPagerCommon() Method Executed");
        viewPagerRegistration.setVisibility(View.GONE);
        flGetReg.setVisibility(View.VISIBLE);
        lblFormTitle.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        lblFormTitle.setText("Registration Form");
        formLine.setVisibility(View.VISIBLE);
        Class fragmentClass = CommonRegistrationFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment).commit();
    }

    private void loadSpinnerUserRole() {
        roleId.clear();
        roleName.clear();
        roleId.add("0");
        roleName.add("Select Role");
        dbRef = database.getReference("UserRole");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roleId.clear();
                roleName.clear();
                roleId.add("0");
                roleName.add("Select Role");
                int position = 0;
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if (!Objects.requireNonNull(childDataSnapshot.child("role_name").getValue()).toString().equals("Admin")) {
                        cnt++;
                        roleId.add(childDataSnapshot.getKey());
                        roleName.add(Objects.requireNonNull(childDataSnapshot.child("role_name").getValue()).toString());
                        if (role.equals("1") && Objects.requireNonNull(childDataSnapshot.child("role_name").getValue()).toString().equals("College Admin")) {
                            position = cnt;
                        } else if (role.equals("0") && Objects.requireNonNull(childDataSnapshot.child("role_name").getValue()).toString().equals("Student")) {
                            position = cnt;
                        }
                    }
                    if (cnt == (dataSnapshot.getChildrenCount() - 1)) {
                        spinnerUserRole.setAdapter(new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, roleName));
                        spinnerUserRole.setSelection(position);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        cnt = 0;
    }

}
