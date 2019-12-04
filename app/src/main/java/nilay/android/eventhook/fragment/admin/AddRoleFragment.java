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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.UserRole;

public class AddRoleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String rolename="";
    private TextView txtAdminAddRole;
    private Button btnAddRole;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public AddRoleFragment() {    }

    // TODO: Rename and change types and number of parameters
    public static AddRoleFragment newInstance(String param1, String param2) {
        AddRoleFragment fragment = new AddRoleFragment();
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
        final View view = inflater.inflate(R.layout.fragment_add_role, container, false);
        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);
        txtAdminAddRole = (TextView) view.findViewById(R.id.txtAdminAddRole);
        btnAddRole = (Button) view.findViewById(R.id.btnAddRole);
        btnAddRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rolename = txtAdminAddRole.getText().toString();
                if (rolename.equals("")) {
                    txtAdminAddRole.requestFocus();
                    txtAdminAddRole.setError("FIELD CANNOT BE EMPTY");
                } else if (!rolename.matches("^[\\p{L} .'-]+$")) {
                    txtAdminAddRole.requestFocus();
                    txtAdminAddRole.setError("ENTER ONLY ALPHABETS");
                } else {
                    dbRef = database.getReference("UserRole");
                    String id = dbRef.push().getKey();
                    UserRole userRole = new UserRole(id, rolename);
                    dbRef.child(id).setValue(userRole);
                    txtAdminAddRole.setText("");
                    rolename = "";
                    Toast.makeText(view.getContext(), "New User Role Added", Toast.LENGTH_SHORT).show();
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
