package nilay.android.eventhook.fragment.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.SampleActivity;
import nilay.android.eventhook.fragment.getter.GetCollegeFragment;
import nilay.android.eventhook.interfaces.CollegeData;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.viewmodels.CollegeViewModel;
import nilay.android.eventhook.viewmodels.EventViewModel;
import nilay.android.eventhook.viewmodels.HomeViewModel;

public class ResultDashboardFragment extends Fragment {

    private Spinner spinnerCollege, spinnerEvents;
    private EventViewModel eventViewModel;
    private HomeViewModel homeViewModel;
    private CollegeViewModel collegeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_dashboard, container, false);

        //spinnerCollege = view.findViewById(R.id.spinnerCollege);
        spinnerEvents = view.findViewById(R.id.spinnerEvents);

        if(getActivity()!=null) {
            eventViewModel = new ViewModelProvider(getActivity()).get(EventViewModel.class);
            homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
            collegeViewModel = new ViewModelProvider(getActivity()).get(CollegeViewModel.class);
            Class fragmentClass = GetCollegeFragment.class;
            Fragment fragment = null;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            FragmentManager fragmentManager =  getActivity().getSupportFragmentManager();
            assert fragment != null;
            fragmentManager.beginTransaction().replace(R.id.flGetClg, fragment).commit();

            collegeViewModel.getCollegeId().observe(getActivity(), collegeId -> {
                if(!collegeId.equals("0")) {
                    homeViewModel.setCollegeId(collegeId);
                    eventViewModel.setCollegeid(collegeId);
                    eventViewModel.getEvent().observe(Objects.requireNonNull(getActivity()), events -> {
                        spinnerEvents.setAdapter(new ArrayAdapter<Event>(Objects.requireNonNull(getContext()),android.R.layout.simple_spinner_dropdown_item,events));
                    });
                }
            });
            collegeViewModel.getCollegeName().observe(Objects.requireNonNull(getActivity()), collegeName -> {
                if(!collegeName.equals("0"))
                    homeViewModel.setCollegeName(collegeName);
            });
        }

        return view;
    }
}
