package nilay.android.eventhook.fragment.getter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import nilay.android.eventhook.interfaces.CollegeData;
import nilay.android.eventhook.R;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.viewmodels.CollegeViewModel;
import nilay.android.eventhook.viewmodels.HomeViewModel;

public class GetCollegeFragment extends Fragment implements LifecycleOwner {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_college, container, false);

        Spinner spinnerGetClg = view.findViewById(R.id.spinnerGetClg);

        HomeViewModel homeViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(HomeViewModel.class);

        CollegeViewModel clgModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(CollegeViewModel.class);
        clgModel.getCollegeList().observe(Objects.requireNonNull(getActivity()), college -> {
            spinnerGetClg.setAdapter(new ArrayAdapter<College>(view.getContext(), android.R.layout.simple_spinner_dropdown_item,college));
        });

        spinnerGetClg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){
                    College clg = (College) adapterView.getSelectedItem();
                    Log.e("Clg: ",clg.getCollege_id());
                    clgModel.getCollegeId().setValue(clg.getCollege_id());
                    clgModel.getCollegeName().setValue(clg.getCollege_name());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

}
