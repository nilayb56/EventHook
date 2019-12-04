package nilay.android.eventhook.fragment.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import nilay.android.eventhook.R;
import nilay.android.eventhook.home.HomeTwoActivity;
import nilay.android.eventhook.registration.RegistrationActivity;
import nilay.android.eventhook.viewmodels.HomeViewModel;

public class CommonHomeMenuFragment extends Fragment {

    private RelativeLayout rlCommonHomeMenu;
    private MaterialTextView lblTitle, lblDescription;
    private MaterialButton btnRegister;
    private ImageView imgLinkedIn;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common_home_menu, container, false);

        rlCommonHomeMenu = view.findViewById(R.id.rlCommonHomeMenu);
        lblTitle = view.findViewById(R.id.lblTitle);
        lblDescription = view.findViewById(R.id.lblDescription);
        imgLinkedIn = view.findViewById(R.id.imgLinkedIn);
        btnRegister = view.findViewById(R.id.btnRegister);

        if(getActivity()!=null)
            homeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);

        imgLinkedIn.setOnClickListener((View v) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/in/nilay-bhatt-a65a71153"));
            startActivity(browserIntent);
        });

        btnRegister.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), HomeTwoActivity.class);
            startActivity(intent);
        });

        switch (homeViewModel.getMenuItem()){
            case "About":
                lblTitle.setText("");
                lblTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info,0,0,0);
                lblDescription.setText("EventHook Developed by Nilay Bhatt\nB. Tech A.I.T\nAndroid Developer\nLinkedIn Profile");
                btnRegister.setVisibility(View.GONE);
                imgLinkedIn.setVisibility(View.VISIBLE);
                break;
            case "Help":
                lblTitle.setText("");
                lblTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_help,0,0,0);
                lblDescription.setText("This tab displays the Work-Flow of this APP\n\n\u2022College Admin : Authenticated Registration to add College Events and View the Overall Event\n\n\u2022Coordinator : Authenticated Registration to act as a main handler of ONE Event further Authenticates Registered Volunteers and Assigns Duties to them and a Final Submission of Event Result\n\n\u2022Volunteers : Authenticated Registrations to perform Duties such as, \n1)Quick Registrations\n2)Attendance of Participants\n3)Submission of Results\n4)Finance\n\n\u2022Student : A Registered User of any College can Participate in any Event and can Enjoy the perks of Viewing Real-Time Results and Give their VOTES for Public Events such as Photography, Collage, etc.");
                lblDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                btnRegister.setVisibility(View.GONE);
                imgLinkedIn.setVisibility(View.GONE);
                break;
            case "Contact Us":
                lblTitle.setText("");
                lblTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_contact_us,0,0,0);
                lblDescription.setText("Email Id: nilayb56@student.aau.in\nMobile No.: 9876543210");
                btnRegister.setVisibility(View.GONE);
                imgLinkedIn.setVisibility(View.GONE);
                break;
            case "View Events/Register":
                lblTitle.setText("");
                lblTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_registration_form_menu,0,0,0);
                lblDescription.setText("Registrations are Open for Events in Listed Colleges\n\nSelect a college and get the Event List\n\nRegistrations are of different Types namely,\n\u2022College Admin\n\u2022Event Coordinator\n\u2022Volunteer\n\u2022Student\n\nFor Registrations as a College Admin does not Require Selection of Event but for Other types, Event Selection is MUST\n\nFor Change of Registration Types, Select from the DropDown above Registration after Selection of any Event\n\nClick to GET REGISTERED!");
                lblDescription.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                btnRegister.setVisibility(View.VISIBLE);
                imgLinkedIn.setVisibility(View.GONE);
                break;
        }

        return view;
    }

}
