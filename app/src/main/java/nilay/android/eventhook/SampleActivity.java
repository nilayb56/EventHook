package nilay.android.eventhook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import nilay.android.eventhook.fragment.home.HomeRecyclerFragment;
import nilay.android.eventhook.fragment.volunteer.VolAttendanceFragment;

public class SampleActivity extends AppCompatActivity {

    private FrameLayout flRecyclerHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        flRecyclerHome = findViewById(R.id.flRecyclerHome);
        Fragment fragment = null;
        Class fragmentClass =  HomeRecyclerFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flRecyclerHome, fragment).commit();
        }

    }
}
