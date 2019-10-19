package nilay.android.eventhook.fragment.student;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import nilay.android.eventhook.R;

public class LocationFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap map;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, container, false);

        //SupportMapFragment mapFragment = (SupportMapFragment) this;

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
