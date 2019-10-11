package nilay.android.eventhook.collegeadmin;

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

import nilay.android.eventhook.fragment.log.LogoutFragment;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.collegeadmin.AddEventFragment;
import nilay.android.eventhook.fragment.collegeadmin.ApproveCoordinatorFragment;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.viewmodels.ClgAdminViewModel;

public class ClgAdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String userid = "";
    private String username = "";
    private String clgid = "";
    private String roleid = "";

    private ClgAdminViewModel clgAdminViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clg_admin);
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

        TextView lblNavUserName = headerView.findViewById(R.id.lblNavUserName);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userid = sharedPref.getString("userid", "");
        username = sharedPref.getString("username", "");
        clgid = sharedPref.getString("collegeid", "");
        roleid = sharedPref.getString("roleid", "");
        lblNavUserName.setText(username);
        //Toast.makeText(this, clgid, Toast.LENGTH_SHORT).show();

        clgAdminViewModel = new ViewModelProvider(this).get(ClgAdminViewModel.class);
        clgAdminViewModel.setRoleid(roleid);

        dbRef = database.getReference("College").child(clgid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                College college = dataSnapshot.getValue(College.class);
                if(college!=null)
                    clgAdminViewModel.clgdata(college.getCollege_id(),college.getCollege_name());
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
        getMenuInflater().inflate(R.menu.clg_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();

        if (id == R.id.clgAdminLogout) {
            fragmentClass = LogoutFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flClgAdmin, fragment).commit();
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

        if (id == R.id.nav_ClA_AddEvent) {
            fragmentClass = AddEventFragment.class;
        } else if (id == R.id.nav_ClA_Coordinator) {
            fragmentClass = ApproveCoordinatorFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flClgAdmin, fragment).commit();
        item.setChecked(true);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
