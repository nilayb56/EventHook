package nilay.android.eventhook.fragment.registration;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import nilay.android.eventhook.AddFocusChangedListener;
import nilay.android.eventhook.AddListenerOnTextChange;
import nilay.android.eventhook.R;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;

import static nilay.android.eventhook.fragment.registration.CommonRegistrationFragment.generateOTP;
import static nilay.android.eventhook.fragment.registration.CommonRegistrationFragment.sendMail;
import static nilay.android.eventhook.fragment.registration.GroupRegistrationFragment.getDate;

public class GroupMemberFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    TextView lblGroupCollege;
    TextInputLayout txtEmailLayout, txtUserNameLayout;
    TextInputEditText txtMemberName, txtMemberEmailid;
    MaterialButton btnMemberReg;

    private String collegeid = "";
    private String collegename = "";
    private String groupid = "";
    private String membername = "";
    private String memberemail = "";
    private String grouppassword = "";
    private String groupname = "";
    private String roleid = "";
    private String eventid = "";
    private String regdate = "";
    private boolean regFlag;
    private RegistrationViewModel registrationViewModel = new RegistrationViewModel();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public GroupMemberFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static GroupMemberFragment newInstance(String param1, String param2) {
        GroupMemberFragment fragment = new GroupMemberFragment();
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
        View view = inflater.inflate(R.layout.fragment_group_member, container, false);
        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);
        lblGroupCollege = view.findViewById(R.id.lblGroupCollege);

        txtEmailLayout = view.findViewById(R.id.txtEmailLayout);
        txtUserNameLayout = view.findViewById(R.id.txtUserNameLayout);

        txtMemberName = view.findViewById(R.id.txtMemberName);
        txtMemberEmailid = view.findViewById(R.id.txtMemberEmailid);
        btnMemberReg = view.findViewById(R.id.btnMemberReg);

        if (getActivity() != null)
            registrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);

        collegeid = registrationViewModel.getCollegeid();
        collegename = registrationViewModel.getCollegename();
        groupid = registrationViewModel.getGroupid();
        groupname = registrationViewModel.getGroupname();
        eventid = registrationViewModel.getEventid();
        roleid = registrationViewModel.getRoleid();

        if(!collegename.equals(""))
            lblGroupCollege.setText(collegename);

        if (!groupid.equals("")) {
            btnMemberReg.setEnabled(true);
        }

        txtMemberEmailid.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtMemberEmailid, txtEmailLayout, "^\\w+([\\.-]?\\w+)*@[A-Za-z\\-]+([\\.-]?[A-Za-z\\-]+)*(\\.[A-Za-z\\-]{2,3})+$", "EMAIL ADDRESS NOT IN CORRECT FORMAT"));
        txtMemberName.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtMemberName, txtUserNameLayout, "^[\\p{L} .'-]+$", "ENTER ONLY ALPHABETS"));

        btnMemberReg.setOnClickListener((View v) -> {
            membername = txtMemberName.getText().toString();
            memberemail = txtMemberEmailid.getText().toString();
            if (memberemail.equals("")) {
                txtMemberEmailid.requestFocus();
                txtEmailLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!memberemail.matches("^\\w+([\\.-]?\\w+)*@[A-Za-z\\-]+([\\.-]?[A-Za-z\\-]+)*(\\.[A-Za-z\\-]{2,3})+$")) {
                txtMemberEmailid.requestFocus();
                txtEmailLayout.setError("EMAIL ADDRESS NOT IN CORRECT FORMAT");
            } else if (membername.equals("")) {
                txtMemberName.requestFocus();
                txtUserNameLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!membername.matches("^[\\p{L} .'-]+$")) {
                txtMemberName.requestFocus();
                txtUserNameLayout.setError("ENTER ONLY ALPHABETS");
            } else {
                registrationViewModel.setValidEmail(true);
                dbRef = database.getReference("Users");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            if (childDataSnapshot.child("email_id").exists() && childDataSnapshot.child("email_id").getValue().toString().equals(memberemail)) {
                                if (getContext() != null) {
                                    registrationViewModel.setValidEmail(false);
                                    Users user = childDataSnapshot.getValue(Users.class);
                                    if (user != null) {
                                        DatabaseReference roleRef = database.getReference("UserRole").child(user.getRole_id());
                                        roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                UserRole userRole = dataSnapshot.getValue(UserRole.class);
                                                if (userRole != null && (userRole.getRole_name().equals("Student") || userRole.getRole_name().equals("Volunteer"))) {

                                                    DatabaseReference volRef = database.getReference("Volunteer").child(eventid).child(user.getUser_id());
                                                    volRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                            if (!dataSnapshot.exists()) {

                                                                DatabaseReference eventRef = database.getReference("UserParticipation").child(eventid).child(user.getUser_id());
                                                                eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                                        if (dataSnapshot.exists()) {

                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                            builder.setMessage("Already a Registered User in this Event ")
                                                                                    .setCancelable(false)
                                                                                    .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                                                                        txtMemberEmailid.setText("");
                                                                                        txtMemberName.setText("");
                                                                                    });
                                                                            AlertDialog alert = builder.create();
                                                                            alert.show();

                                                                        } else {

                                                                            registrationViewModel.setUserExists(true);
                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                            builder.setTitle("Already a REGISTERED User")
                                                                                    .setMessage("Do you want to Proceed with Previous Registration Details?")
                                                                                    .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                                                                        txtMemberName.setText(user.getUser_name());
                                                                                        registrationViewModel.setValidEmail(true);
                                                                                    })
                                                                                    .setNegativeButton("No", (DialogInterface dialog, int which) -> {
                                                                                        txtMemberEmailid.setText("");
                                                                                        txtMemberName.setText("");
                                                                                    }).show();

                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                            } else {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                builder.setMessage("Cannot Register as a Participant\nYou are Registered as a Volunteer for this Event")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                                                            txtMemberEmailid.setText("");
                                                                            txtMemberName.setText("");
                                                                        });
                                                                AlertDialog alert = builder.create();
                                                                alert.show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                                } else {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setMessage("Only Student and Student Volunteer can Participate in Events")
                                                            .setCancelable(false)
                                                            .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                                                txtMemberEmailid.setText("");
                                                                txtMemberName.setText("");
                                                            });
                                                    AlertDialog alert = builder.create();
                                                    alert.show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }
                        }
                        if (registrationViewModel.isValidEmail()) {
                            membername = StringUtils.capitalize(membername);
                            regdate = getDate();
                            registrationViewModel.setRegFlag(true);
                            txtMemberName.setEnabled(false);
                            txtMemberEmailid.setEnabled(false);
                            btnMemberReg.setEnabled(false);

                            insertUser();

                            Toast.makeText(view.getContext(), "Member Registration Successful", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

    private void insertUser() {
        grouppassword = generateOTP();
        dbRef = database.getReference("Users");
        String id = dbRef.push().getKey();
        Users users = new Users(id, membername, memberemail, grouppassword, roleid, collegeid, regdate, "0");
        if (id != null)
            dbRef.child(id).setValue(users);

        insertParticipation(id);
    }

    private void insertParticipation(String userId) {
        dbRef = database.getReference("UserParticipation");
        UserParticipation userParticipation = new UserParticipation(userId, membername, eventid, groupid, groupname);
        if (userId != null)
            dbRef.child(eventid).child(userId).setValue(userParticipation);

        addAsGroupMember();

        sendMail(getContext(),"Successful Registration in EventHook", "Congratulations " + membername + "!!!\nYou are Registered as a Group Member of team " + groupname + ".\nIf you want to use the Perks of EVENTHOOK to its full capacity you can log in EVENTHOOK and your Password is " + grouppassword + ".\nYou can CHANGE your password in your Edit Profile Section.", memberemail, "");

    }

    private void addAsGroupMember() {
        dbRef = database.getReference("GroupMaster").child(groupid);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer members = Integer.parseInt(dataSnapshot.child("unregistered_count").getValue().toString());
                members -= 1;
                if (members >= 0)
                    dbRef.child("unregistered_count").setValue(members);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            registrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);
            collegeid = registrationViewModel.getCollegeid();
            collegename = registrationViewModel.getCollegename();
            groupid = registrationViewModel.getGroupid();
            groupname = registrationViewModel.getGroupname();
            eventid = registrationViewModel.getEventid();
            regFlag = registrationViewModel.isRegFlag();
            roleid = registrationViewModel.getRoleid();
            if(!collegename.equals(""))
                lblGroupCollege.setText(collegename);
        }
        if (!groupid.equals("")) {
            btnMemberReg.setEnabled(true);
        }
        if (regFlag && (!txtMemberEmailid.getText().toString().equals("")) && (!txtMemberName.getText().toString().equals(""))) {
            txtMemberName.setEnabled(false);
            txtMemberEmailid.setEnabled(false);
            btnMemberReg.setEnabled(false);
        }
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
