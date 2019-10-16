package nilay.android.eventhook.students;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nilay.android.eventhook.fragment.student.StudentVotesFragment;
import nilay.android.eventhook.fragment.student.UploadWorkFragment;
import nilay.android.eventhook.home.HomeTwoActivity;
import nilay.android.eventhook.fragment.log.LogoutFragment;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.student.StAddMemberFragment;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;
import nilay.android.eventhook.viewmodels.StudentViewModel;

public class StudentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String userid = "";
    private String username = "";
    private String clgid = "";
    private String roleid = "";

    private StudentViewModel studentViewModel;
    private RegistrationViewModel registrationViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();

        TextView lblNavUserName = headerView.findViewById(R.id.lblNavUserName);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userid = sharedPref.getString("userid", "");
        username = sharedPref.getString("username", "");
        clgid = sharedPref.getString("collegeid", "");
        roleid = sharedPref.getString("roleid", "");
        lblNavUserName.setText(username);
        if (sharedPref.getString("logFlag", "").toString().equals("0")) {
            Intent it = new Intent(getApplicationContext(), HomeTwoActivity.class);
            startActivity(it);
        }

        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
        studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        studentViewModel.setUserid(userid);
        studentViewModel.setClgid(clgid);
        registrationViewModel.userType(roleid, "Student");

        dbRef = database.getReference("College").child(studentViewModel.getClgid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                College college = dataSnapshot.getValue(College.class);
                if (college != null) {
                    studentViewModel.setClgname(college.getCollege_name());
                    registrationViewModel.selectedCollege(studentViewModel.getClgid(), studentViewModel.getClgname());

                    DatabaseReference dbref = database.getReference("GroupMaster");
                    String userid = studentViewModel.getUserid();
                    Query query = dbref.orderByChild("group_leader").equalTo(userid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean exists = dataSnapshot.exists();
                            if (exists) {
                                studentViewModel.setLeader(true);
                                getParticipatedEvents(menu, studentViewModel.getUserid());
                            } else {
                                menu.findItem(R.id.nav_StAddMember).setVisible(false);
                                getParticipatedEvents(menu, studentViewModel.getUserid());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    if (!clgid.equals("")) {
                        studentViewModel.setClgname(clgid);
                        registrationViewModel.selectedCollege(studentViewModel.getClgid(), studentViewModel.getClgname());

                        DatabaseReference dbref = database.getReference("GroupMaster");
                        String userid = studentViewModel.getUserid();
                        Query query = dbref.orderByChild("group_leader").equalTo(userid);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                boolean exists = dataSnapshot.exists();
                                if (exists) {
                                    studentViewModel.setLeader(true);
                                } else {
                                    menu.findItem(R.id.nav_StAddMember).setVisible(false);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getParticipatedEvents(Menu menu, String userId) {
        dbRef = database.getReference("UserParticipation");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<UserParticipation> userParticipations = new ArrayList<>();
                for (DataSnapshot eventDataSnapShot : dataSnapshot.getChildren()) {

                    for (DataSnapshot partDataSnapShot : eventDataSnapShot.getChildren()) {

                        UserParticipation participation = partDataSnapShot.getValue(UserParticipation.class);
                        if (participation != null && participation.getUser_id().equals(userId) && participation.getContent_submission() == 0) {
                            userParticipations.add(participation);
                        }

                    }

                }
                studentViewModel.setParticipations(userParticipations);
                checkUploadWorkEvents(menu, userParticipations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkUploadWorkEvents(Menu menu, List<UserParticipation> userParticipations) {
        dbRef = database.getReference("Event");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Event> events = new ArrayList<>();
                for (UserParticipation participation : userParticipations) {
                    Event event = dataSnapshot.child(participation.getEvent_id()).getValue(Event.class);
                    if (Objects.requireNonNull(event).getUpload_work() == 1)
                        events.add(event);
                }
                if (events.size() == 0)
                    menu.findItem(R.id.nav_StUploadWork).setVisible(false);
                else
                    studentViewModel.setParticipatedEvents(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.studentLogout) {
            fragmentClass = LogoutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flStudent, fragment).commit();
            item.setChecked(true);
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.nav_StAddMember) {
            if (studentViewModel.isLeader()) {
                fragmentClass = StAddMemberFragment.class;
            } else {
                item.setVisible(false);
            }
        } else if (id == R.id.nav_StUploadWork) {
            fragmentClass = UploadWorkFragment.class;
        } else if (id == R.id.nav_StRatings) {
            fragmentClass = StudentVotesFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flStudent, fragment).commit();
            item.setChecked(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
