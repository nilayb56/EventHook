package nilay.android.eventhook.volunteer;

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
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import nilay.android.eventhook.home.HomeTwoActivity;
import nilay.android.eventhook.fragment.log.LogoutFragment;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.volunteer.QuickRegistrationFragment;
import nilay.android.eventhook.fragment.volunteer.VolAttendanceFragment;
import nilay.android.eventhook.fragment.volunteer.VolEventResultFragment;
import nilay.android.eventhook.fragment.volunteer.VolFeeConfirmationFragment;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.Volunteer;
import nilay.android.eventhook.model.VolunteerDuty;
import nilay.android.eventhook.viewmodels.VolunteerViewModel;

public class VolunteerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private VolunteerViewModel volViewModel;

    private Menu menu;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);
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

        menu = navigationView.getMenu();

        TextView lblNavUserName = headerView.findViewById(R.id.lblNavUserName);

        volViewModel = new ViewModelProvider(this).get(VolunteerViewModel.class);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (volViewModel.getUserid().equals("")) {
            volViewModel.setUserid(sharedPref.getString("userid", ""));
            volViewModel.setUsername(sharedPref.getString("username", ""));
            volViewModel.setCollegeid(sharedPref.getString("collegeid", ""));
            volViewModel.setEventid(sharedPref.getString("roleid", ""));
            lblNavUserName.setText(volViewModel.getUsername());
        }
        if (sharedPref.getString("logFlag", "").equals("0")) {
            Intent it = new Intent(getApplicationContext(), HomeTwoActivity.class);
            startActivity(it);
        }

        dbRef = database.getReference("College").child(volViewModel.getCollegeid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                College college = dataSnapshot.getValue(College.class);
                if (college != null)
                    volViewModel.setCollegename(college.getCollege_name());

                getVolEventId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getVolEventId() {
        dbRef = database.getReference("Volunteer").child(volViewModel.getEventid()).child(volViewModel.getUserid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                /*for(DataSnapshot eventDataSnapShot : dataSnapshot.getChildren()){

                    for(DataSnapshot userDataSnapShot : eventDataSnapShot.getChildren()){*/

                        Volunteer volunteer = dataSnapshot.getValue(Volunteer.class);
                        if (volunteer != null && volunteer.getUser_id().equals(volViewModel.getUserid())) {
                            //volViewModel.setEventid(volunteer.getEvent_id());
                            if(volunteer.getDuty_id().equals("0")){
                                Toast.makeText(VolunteerActivity.this, "No Duty has been Assigned to You!", Toast.LENGTH_SHORT).show();
                                menu.findItem(R.id.nav_VolDutyResults).setVisible(false);
                                menu.findItem(R.id.nav_volAttendance).setVisible(false);
                                menu.findItem(R.id.nav_volFinance).setVisible(false);
                                menu.findItem(R.id.nav_volQuickReg).setVisible(false);
                            } else {
                                volViewModel.setDutyid(volunteer.getDuty_id());

                                getDutyName(volViewModel.getDutyid());
                            }
                        }

                    /*}

                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getDutyName(String dutyid) {
        dbRef = database.getReference("VolunteerDuty").child(dutyid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VolunteerDuty duty = dataSnapshot.getValue(VolunteerDuty.class);
                if (duty != null) {
                    volViewModel.setDutyname(duty.getDuty_name());

                    switch (volViewModel.getDutyname()) {
                        case "Quick Registrations":
                            menu.findItem(R.id.nav_volAttendance).setVisible(false);
                            menu.findItem(R.id.nav_VolDutyResults).setVisible(false);
                            menu.findItem(R.id.nav_volFinance).setVisible(false);
                            break;
                        case "Attendance":
                            menu.findItem(R.id.nav_VolDutyResults).setVisible(false);
                            menu.findItem(R.id.nav_volFinance).setVisible(false);
                            menu.findItem(R.id.nav_volQuickReg).setVisible(false);
                            break;
                        case "Submit Result":
                            menu.findItem(R.id.nav_volAttendance).setVisible(false);
                            menu.findItem(R.id.nav_volFinance).setVisible(false);
                            menu.findItem(R.id.nav_volQuickReg).setVisible(false);
                            break;
                        case "Finance":
                            menu.findItem(R.id.nav_volAttendance).setVisible(false);
                            menu.findItem(R.id.nav_VolDutyResults).setVisible(false);
                            menu.findItem(R.id.nav_volQuickReg).setVisible(false);
                            break;
                    }
                }
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
        getMenuInflater().inflate(R.menu.volunteer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.logout_Volunteer) {
            fragmentClass = LogoutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flVolunteer, fragment).commit();
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

        if (id == R.id.nav_volAttendance) {
            fragmentClass = VolAttendanceFragment.class;
        } else if (id == R.id.nav_VolSubmitRes) {
            fragmentClass = VolEventResultFragment.class;
        } else if (id == R.id.nav_volQuickReg) {
            fragmentClass = QuickRegistrationFragment.class;
        } else if (id == R.id.nav_VolUpdtRes) {

        } else if (id == R.id.nav_volFinance){
            fragmentClass = VolFeeConfirmationFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flVolunteer, fragment).commit();
            item.setChecked(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
