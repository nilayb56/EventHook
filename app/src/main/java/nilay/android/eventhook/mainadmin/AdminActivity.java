package nilay.android.eventhook.mainadmin;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;

import nilay.android.eventhook.fragment.log.LogoutFragment;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.admin.AddCollegeFragment;
import nilay.android.eventhook.fragment.admin.AddRoleFragment;
import nilay.android.eventhook.fragment.admin.AdminUpdateRolesFragment;
import nilay.android.eventhook.fragment.admin.ApproveClgAdminFragment;

public class AdminActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_admin);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_admin);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        Class fragmentClass = null;
        int id = item.getItemId();
        if(id==R.id.admin_logout){
            Log.e("","");
            fragmentClass = LogoutFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fragment!=null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flAdmin, fragment).commit();
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

        if (id == R.id.nav_adminAddClg) {
            fragmentClass = AddCollegeFragment.class;
        } else if (id == R.id.nav_adminAddRole) {
            fragmentClass = AddRoleFragment.class;
        } else if (id == R.id.nav_adminUpdtRole) {
            fragmentClass = AdminUpdateRolesFragment.class;
        }  else if (id == R.id.nav_adminCnfClgAdminReq) {
            fragmentClass = ApproveClgAdminFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.flAdmin, fragment).commit();
        item.setChecked(true);

        DrawerLayout drawer = findViewById(R.id.drawer_admin);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
