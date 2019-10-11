package nilay.android.eventhook.fragment.registration;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import nilay.android.eventhook.R;
import nilay.android.eventhook.registration.RegistrationAdapter;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;

public class AddMoreElementFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ImageView imgStAddElement;

    private Integer maxmembers = 0;
    private static Integer members = 0;

    private RegistrationAdapter regAdapter;
    private RegistrationViewModel registrationViewModel = new RegistrationViewModel();

    public AddMoreElementFragment() {    }

    // TODO: Rename and change types and number of parameters
    public static AddMoreElementFragment newInstance(String param1, String param2) {
        AddMoreElementFragment fragment = new AddMoreElementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_more_element, container, false);

        imgStAddElement = view.findViewById(R.id.imgStAddElement);

        if (getActivity() != null) {
            registrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);
            maxmembers = registrationViewModel.getMaxmembers();
            if(members==0) {
                members = registrationViewModel.getMinmembers();
                //members++;
                registrationViewModel.setVisibleMemberCount(members);
            }
            regAdapter = registrationViewModel.getRegistrationAdapter();
        }



        imgStAddElement.setOnClickListener( (View v) -> {
            members = registrationViewModel.getVisibleMemberCount();
            int addBtnPosition = registrationViewModel.getViewPagerPos();
            if(members<=maxmembers){
                regAdapter.setAddMemberIndex(members);
                regAdapter.addFragment(new GroupMemberFragment(),"Member "+members, addBtnPosition);
                regAdapter.addFragment(new AddMoreElementFragment(),"Add Member", members);
                regAdapter.notifyDataSetChanged();

                View feesView = getLayoutInflater().inflate(R.layout.layout_event_fees,null);
                TextView lblRegPartNo = feesView.findViewById(R.id.lblRegPartNo);
                TextView lblRegPartFees = feesView.findViewById(R.id.lblRegPartFees);

                lblRegPartNo.setText(String.valueOf(members));
                int fees = Integer.valueOf(registrationViewModel.getEventFees());
                fees = fees*members;
                lblRegPartFees.setText("\u20B9"+fees);
                members++;

                new AlertDialog.Builder(getContext())
                        .setTitle("Member Added\n")
                        .setCancelable(false)
                        .setView(feesView)
                        .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {
                                // Continue with delete operation
                        })
                        .setIcon(android.R.drawable.ic_input_add)
                        .show();

                int viewIndex = addBtnPosition;
                viewIndex = viewIndex-2;
                regAdapter.setViewPagerPosition(viewIndex);
                //Toast.makeText(getContext(), "Member Added", Toast.LENGTH_SHORT).show();
            } else {
                int index = maxmembers;
                index++;
                if(regAdapter.getAddMemberIndex()!=index)
                    regAdapter.setAddMemberIndex(index);
                Toast.makeText(getContext(), "Limit of Maximum Members Reached!", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onPause() {
        super.onPause();
        registrationViewModel.setVisibleMemberCount(members);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
