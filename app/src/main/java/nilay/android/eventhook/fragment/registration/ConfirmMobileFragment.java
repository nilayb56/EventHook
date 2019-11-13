package nilay.android.eventhook.fragment.registration;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import nilay.android.eventhook.AddFocusChangedListener;
import nilay.android.eventhook.R;

public class ConfirmMobileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_mobile, container, false);

        TextInputLayout txtMobileLayout, txtMobileOTPLayout;
        TextInputEditText txtMobile, txtMobileOTP;
        MaterialButton btnGetOTP, btnResendOTP;

        txtMobileLayout = view.findViewById(R.id.txtMobileLayout);
        txtMobileOTPLayout = view.findViewById(R.id.txtMobileOTPLayout);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtMobileOTP = view.findViewById(R.id.txtMobileOTP);
        btnGetOTP = view.findViewById(R.id.btnGetOTP);
        btnResendOTP = view.findViewById(R.id.btnResendOTP);

        txtMobile.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtMobile, txtMobileLayout, "[6-9][0-9]{9}", "MOBILE NUMBER NOT IN CORRECT FORMAT"));
        btnGetOTP.setOnClickListener((View v) -> {

        });

        btnResendOTP.setOnClickListener((View v) -> {

        });

        return view;
    }
}
