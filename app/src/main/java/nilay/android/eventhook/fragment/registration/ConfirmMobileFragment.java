package nilay.android.eventhook.fragment.registration;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import nilay.android.eventhook.AddFocusChangedListener;
import nilay.android.eventhook.R;

import static nilay.android.eventhook.fragment.registration.CommonRegistrationFragment.generateOTP;

public class ConfirmMobileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private String codeSent = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_mobile, container, false);

        TextInputLayout txtMobileLayout, txtMobileOTPLayout;
        TextInputEditText txtMobile, txtMobileOTP;
        MaterialButton btnGetOTP, btnResendOTP;

        mAuth = FirebaseAuth.getInstance();

        txtMobileLayout = view.findViewById(R.id.txtMobileLayout);
        txtMobileOTPLayout = view.findViewById(R.id.txtMobileOTPLayout);
        txtMobile = view.findViewById(R.id.txtMobile);
        txtMobileOTP = view.findViewById(R.id.txtMobileOTP);
        btnGetOTP = view.findViewById(R.id.btnGetOTP);
        btnResendOTP = view.findViewById(R.id.btnResendOTP);

        txtMobile.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtMobile, txtMobileLayout, "[6-9][0-9]{9}", "MOBILE NUMBER NOT IN CORRECT FORMAT"));
        btnGetOTP.setOnClickListener((View v) -> {
            String mobNo = txtMobile.getText().toString();
            if (mobNo.equals("")) {
                txtMobile.requestFocus();
                txtMobileLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!mobNo.matches("[6-9][0-9]{9}")) {
                txtMobile.requestFocus();
                txtMobileLayout.setError("EMAIL ADDRESS NOT IN CORRECT FORMAT");
            } else {
                sendOtp(mobNo);
            }
        });

        txtMobileOTP.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtMobileOTP, txtMobileOTPLayout));
        btnResendOTP.setOnClickListener((View v) -> {
            String mobOTP = txtMobileOTP.getText().toString();
            if (mobOTP.equals("")) {
                txtMobileOTP.requestFocus();
                txtMobileOTPLayout.setError("FIELD CANNOT BE EMPTY");
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, mobOTP);
                signInWithPhoneAuthCredentials(credential);
            }
        });

        return view;
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Objects.requireNonNull(getActivity()), (@NonNull Task<AuthResult> task) -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Mobile Number Authenticated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Problem in Mobile Number Authentication, PLEASE TRY AGAIN!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendOtp(String mobNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                Objects.requireNonNull(getActivity()),               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            codeSent = s;
        }
    };
}
