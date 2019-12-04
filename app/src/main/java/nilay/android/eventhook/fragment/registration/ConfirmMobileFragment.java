package nilay.android.eventhook.fragment.registration;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import nilay.android.eventhook.AddFocusChangedListener;
import nilay.android.eventhook.R;
import nilay.android.eventhook.home.HomeTwoActivity;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;

public class ConfirmMobileFragment extends Fragment {

    private RegistrationViewModel registrationViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_mobile, container, false);

        TextInputLayout txtMobileLayout;
        TextInputEditText txtMobile;
        MaterialButton btnGetOTP;

        txtMobileLayout = view.findViewById(R.id.txtMobileLayout);
        txtMobile = view.findViewById(R.id.txtMobile);
        btnGetOTP = view.findViewById(R.id.btnGetOTP);

        registrationViewModel = new ViewModelProvider(Objects.requireNonNull(getActivity())).get(RegistrationViewModel.class);

        txtMobile.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtMobile, txtMobileLayout, "[6-9][0-9]{9}", "MOBILE NUMBER NOT IN CORRECT FORMAT"));
        btnGetOTP.setOnClickListener((View v) -> {
            String mobNo = txtMobile.getText().toString();
            if (mobNo.equals("")) {
                txtMobile.requestFocus();
                txtMobileLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!mobNo.matches("[6-9][0-9]{9}")) {
                txtMobile.requestFocus();
                txtMobileLayout.setError("MOBILE NUMBER NOT IN CORRECT FORMAT");
            } else {

                Toast.makeText(getContext(), "Mobile Number Added", Toast.LENGTH_SHORT).show();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference dbRef = database.getReference("Volunteer").child(registrationViewModel.getEventid()).child(registrationViewModel.getUserid());
                dbRef.child("mobile_number").setValue(mobNo);
                dbRef.child("user_name").setValue(registrationViewModel.getUsername());
                Fragment fragment = null;
                Class fragmentClass = CommonRegistrationFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(fragment!=null) {
                    if(registrationViewModel.getEventid() == null || registrationViewModel.getEventid().equals("")){
                        Intent i = new Intent(getActivity(), HomeTwoActivity.class);
                        startActivity(i);
                    } else {
                        FragmentManager fragmentManager = getChildFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment).commit();
                    }
                }
            }
        });

        return  view;
    }
}
