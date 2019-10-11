package nilay.android.eventhook.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.LoginActivity;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.getter.GetCollegeFragment;
import nilay.android.eventhook.interfaces.CollegeData;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.EventImageList;
import nilay.android.eventhook.registration.RegistrationActivity;
import nilay.android.eventhook.viewmodels.CollegeViewModel;

public class HomeTwoActivity extends AppCompatActivity {

    private CardView cardSelectClg;
    private RecyclerView recyclerView;
    private LinearLayout linearHomeTwo;
    private EventCardRecyclerViewAdapter adapter;

    private CollegeViewModel collegeViewModel;

    private String clgname = "";
    private String clgid = "";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_two);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        cardSelectClg = findViewById(R.id.cardSelectClg);
        linearHomeTwo = findViewById(R.id.linearHomeTwo);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.event_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.event_grid_spacing_small);
        recyclerView.addItemDecoration(new EventGridItemDecoration(largePadding, smallPadding));

        collegeViewModel = new ViewModelProvider(HomeTwoActivity.this).get(CollegeViewModel.class);
        collegeViewModel.getCollegeId().observe(HomeTwoActivity.this, collegeId -> {
            clgid = collegeId;
            fillEvent();
        });
        collegeViewModel.getCollegeName().observe(HomeTwoActivity.this, collegeName -> {
            clgname = collegeName;
        });

        Class fragmentClass = GetCollegeFragment.class;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        assert fragment != null;
        fragmentManager.beginTransaction().replace(R.id.flGetClg, fragment).commit();

    }

    private void fillEvent() {
        if(!clgid.equals("0")) {
            cardSelectClg.setVisibility(View.GONE);
            dbRef = database.getReference("Event");
            Query query = dbRef.orderByChild("college_id").equalTo(clgid);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<EventImageList> eventList = new ArrayList<>();
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        Event event = childDataSnapshot.getValue(Event.class);
                        if (event != null)
                            if (!collegeViewModel.getCollegeId().equals("0")) {
                                if (childDataSnapshot.child("img_url").exists() && event.getCollege_id().equals(clgid)) {
                                    eventList.add(new EventImageList(clgname, event));
                                }
                            }
                    }
                    adapter = new EventCardRecyclerViewAdapter(eventList, HomeTwoActivity.this);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        int orientation = getScreenOrientation();
        if (orientation == 1) {
            linearHomeTwo.setBackground(getResources().getDrawable(R.drawable.bgfinal));
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false));
        } else if (orientation == 2) {
            linearHomeTwo.setBackground(getResources().getDrawable(R.drawable.bglandscape));
            recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false));
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
}
