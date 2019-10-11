package nilay.android.eventhook.fragment.coordinator;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.VolunteerDuty;
import nilay.android.eventhook.viewmodels.CoordinatorViewModel;

public class CodAddDutyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText txtCodAddDuty;
    private CheckBox checkDutyRepeat;
    private Button btnAddDuty;

    CoordinatorViewModel codViewModel;

    String dutyrepeat = "0";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    public CodAddDutyFragment() {    }

    // TODO: Rename and change types and number of parameters
    public static CodAddDutyFragment newInstance(String param1, String param2) {
        CodAddDutyFragment fragment = new CodAddDutyFragment();
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
        View view = inflater.inflate(R.layout.fragment_cod_add_duty, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        txtCodAddDuty = view.findViewById(R.id.txtCodAddDuty);
        btnAddDuty = view.findViewById(R.id.btnAddDuty);
        checkDutyRepeat = view.findViewById(R.id.checkDutyRepeat);

        if(getActivity()!=null)
            codViewModel = new ViewModelProvider(getActivity()).get(CoordinatorViewModel.class);

        btnAddDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dutyname = txtCodAddDuty.getText().toString();
                if(dutyname.equals("")){
                    txtCodAddDuty.requestFocus();
                    txtCodAddDuty.setError("FIELD CANNOT BE EMPTY");
                } else {
                    dbRef = database.getReference("VolunteerDuty");
                    String id = dbRef.push().getKey();
                    VolunteerDuty volDuty = new VolunteerDuty(codViewModel.getEventid(), id, dutyname, "0", dutyrepeat);
                    if(id!=null)
                        dbRef.child(id).setValue(volDuty);
                    Toast.makeText(view.getContext(), "New Volunteer Duty Added!", Toast.LENGTH_SHORT).show();
                    txtCodAddDuty.setText("");
                    checkDutyRepeat.setChecked(false);
                }
            }
        });

        checkDutyRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dutyrepeat = "1";
                } else {
                    dutyrepeat = "0";
                }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
