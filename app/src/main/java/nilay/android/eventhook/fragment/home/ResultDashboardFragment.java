package nilay.android.eventhook.fragment.home;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.SampleActivity;
import nilay.android.eventhook.fragment.getter.GetCollegeFragment;
import nilay.android.eventhook.interfaces.CollegeData;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.EventResult;
import nilay.android.eventhook.viewmodels.CollegeViewModel;
import nilay.android.eventhook.viewmodels.EventViewModel;
import nilay.android.eventhook.viewmodels.HomeViewModel;

public class ResultDashboardFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_dashboard, container, false);

        TextView result, first, second, third;
        TableLayout tableRank;
        Spinner spinnerEvents;
        HomeViewModel homeViewModel;
        CollegeViewModel collegeViewModel;

        spinnerEvents = view.findViewById(R.id.spinnerEvents);
        tableRank = view.findViewById(R.id.tableRank);
        result = view.findViewById(R.id.result);
        first = view.findViewById(R.id.first);
        second = view.findViewById(R.id.second);
        third = view.findViewById(R.id.third);

        if (getActivity() != null) {
            homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
            collegeViewModel = new ViewModelProvider(getActivity()).get(CollegeViewModel.class);
            homeViewModel.setMenu(true);
            Class fragmentClass = GetCollegeFragment.class;
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            assert fragment != null;
            fragmentManager.beginTransaction().replace(R.id.flGetClg, fragment).commit();

            collegeViewModel.getCollegeId().observe(getActivity(), collegeId -> {
                if (!collegeId.equals("0")) {
                    homeViewModel.setCollegeId(collegeId);
                    loadSpinnerEvents(collegeId, spinnerEvents);
                }
            });
            collegeViewModel.getCollegeName().observe(Objects.requireNonNull(getActivity()), collegeName -> {
                if (!collegeName.equals("0"))
                    homeViewModel.setCollegeName(collegeName);
                tableRank.setVisibility(View.GONE);
                result.setVisibility(View.GONE);
            });
        }

        spinnerEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view;
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(20);
                if (position != 0) {
                    Event event = (Event) parent.getSelectedItem();
                    Log.e("Event: ", event.getEvent_id());
                    dbRef = database.getReference("EventResult").child(event.getEvent_id());
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.child("result_confirmed").getValue().toString().equals("1")) {
                                tableRank.setVisibility(View.VISIBLE);
                                result.setVisibility(View.GONE);
                                first.setText("");
                                second.setText("");
                                third.setText("");
                                for (DataSnapshot resultDataSnapShot : dataSnapshot.getChildren()) {
                                    if (!resultDataSnapShot.getKey().equals("result_confirmed")) {
                                        EventResult result = resultDataSnapShot.getValue(EventResult.class);
                                        if (result != null) {
                                            if (result.getRank().equals("1")) {
                                                first.append(result.getWinner_name() + "\n");
                                            } else if (result.getRank().equals("2")) {
                                                second.append(result.getWinner_name() + "\n");
                                            } else if (result.getRank().equals("3")) {
                                                third.append(result.getWinner_name() + "\n");
                                            }
                                        }
                                    }
                                }

                            } else {
                                tableRank.setVisibility(View.GONE);
                                result.setText("Result Not Yet Confirmed!");
                                result.setTextColor(getResources().getColor(R.color.colorError));
                                result.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void loadSpinnerEvents(String collegeId, Spinner spinnerEvents) {
        dbRef = database.getReference("Event");
        Query query = dbRef.orderByChild("college_id").equalTo(collegeId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Event> events = new ArrayList<>();
                events.add(new Event("0","Select Event"));
                for(DataSnapshot eventDataSnapShot : dataSnapshot.getChildren()){
                    events.add(eventDataSnapShot.getValue(Event.class));
                }
                spinnerEvents.setAdapter(new ArrayAdapter<Event>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_dropdown_item, events));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
