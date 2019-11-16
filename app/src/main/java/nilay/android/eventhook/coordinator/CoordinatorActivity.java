package nilay.android.eventhook.coordinator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;

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
import android.view.View;
import android.widget.TextView;

import nilay.android.eventhook.home.HomeTwoActivity;
import nilay.android.eventhook.fragment.log.LogoutFragment;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.coordinator.ApproveVolunteerFragment;
import nilay.android.eventhook.fragment.coordinator.CodAssignDutyFragment;
import nilay.android.eventhook.fragment.coordinator.CodAddDutyFragment;
import nilay.android.eventhook.fragment.coordinator.ConfirmOrUpdtResultFragment;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.Coordinator;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;

public class CoordinatorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private CoordinatorViewModel cordViewModel;

    TextView lblNavUserName;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coordinator);
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

        lblNavUserName = headerView.findViewById(R.id.lblNavUserName);

        cordViewModel = new ViewModelProvider(this).get(CoordinatorViewModel.class);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(cordViewModel.getRoleid().equals("")) {
            cordViewModel.setUserid(sharedPref.getString("userid", ""));
            //Toast.makeText(this, cordViewModel.getUserid(), Toast.LENGTH_SHORT).show();
            cordViewModel.setUsername(sharedPref.getString("username", ""));
            cordViewModel.setCollegeid(sharedPref.getString("collegeid", ""));
            cordViewModel.setRoleid(sharedPref.getString("roleid", ""));
            lblNavUserName.setText(cordViewModel.getUsername());
        }
        if(sharedPref.getString("logFlag", "").equals("0")){
            Intent it = new Intent(getApplicationContext(), HomeTwoActivity.class);
            startActivity(it);
        }

        dbRef = database.getReference("College").child(cordViewModel.getCollegeid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                College college = dataSnapshot.getValue(College.class);
                if(college!=null)
                    cordViewModel.setCollegename(college.getCollege_name());

                getCoordEventId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getCoordEventId() {
        dbRef = database.getReference("Coordinator").child(cordViewModel.getUserid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Coordinator coordinator = dataSnapshot.getValue(Coordinator.class);
                if(coordinator!=null)
                    cordViewModel.setEventid(coordinator.getEvent_id());

                getEventDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getEventDetails() {
        dbRef = database.getReference("Event").child(cordViewModel.getEventid());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                if(event!=null) {
                    cordViewModel.setEventname(event.getEvent_name());
                    if(event.getGroup_event()==1)
                        cordViewModel.setEventIsGroup(true);
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
        getMenuInflater().inflate(R.menu.coordinator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.logout_Coordinator) {
            fragmentClass = LogoutFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flCoordinator, fragment).commit();
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
        if (id == R.id.nav_CrdReqVol) {
            fragmentClass = ApproveVolunteerFragment.class;
        } else if (id == R.id.nav_CrdAddDuty) {
            fragmentClass = CodAddDutyFragment.class;
        } else if (id == R.id.nav_CrdAssignDuty) {
            fragmentClass = CodAssignDutyFragment.class;
        } else if (id == R.id.nav_CrdCnfResult) {
            fragmentClass = ConfirmOrUpdtResultFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flCoordinator, fragment).commit();
            item.setChecked(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
