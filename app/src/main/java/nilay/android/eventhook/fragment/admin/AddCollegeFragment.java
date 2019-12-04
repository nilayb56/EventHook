package nilay.android.eventhook.fragment.admin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.College;

public class AddCollegeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String clgname,clgdept="";
    private EditText txtAdminAddClg, txtAdminAddClgDept;
    private Button btnAddClg;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public AddCollegeFragment() {  }
    // TODO: Rename and change types and number of parameters
    public static AddCollegeFragment newInstance(String param1, String param2) {
        AddCollegeFragment fragment = new AddCollegeFragment();
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

        View view = inflater.inflate(R.layout.fragment_add_college, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        txtAdminAddClg = view.findViewById(R.id.txtAdminAddClg);
        txtAdminAddClgDept = view.findViewById(R.id.txtAdminAddClgDept);
        btnAddClg = (Button) view.findViewById(R.id.btnAddClg);
        btnAddClg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clgname = txtAdminAddClg.getText().toString();
                clgdept = txtAdminAddClgDept.getText().toString();
                if(clgname.equals("")){
                    txtAdminAddClg.requestFocus();
                    txtAdminAddClg.setError("FIELD CANNOT BE EMPTY");
                } else if (!clgname.matches("^[\\p{L} .'-]+$")) {
                    txtAdminAddClg.requestFocus();
                    txtAdminAddClg.setError("ENTER ONLY ALPHABETS");
                } if(clgdept.equals("")){
                    txtAdminAddClgDept.requestFocus();
                    txtAdminAddClgDept.setError("FIELD CANNOT BE EMPTY");
                } else if (!clgdept.matches("^[\\p{L} .'-]+$")) {
                    txtAdminAddClgDept.requestFocus();
                    txtAdminAddClgDept.setError("ENTER ONLY ALPHABETS");
                } else {
                    dbRef = database.getReference("College");
                    String id = dbRef.push().getKey();
                    College college = new College(id, clgname, clgdept);
                    dbRef.child(id).setValue(college);
                    txtAdminAddClg.setText("");
                    txtAdminAddClgDept.setText("");
                    clgname = "";
                    clgdept = "";
                    Toast.makeText(view.getContext(), "New College Added", Toast.LENGTH_SHORT).show();
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