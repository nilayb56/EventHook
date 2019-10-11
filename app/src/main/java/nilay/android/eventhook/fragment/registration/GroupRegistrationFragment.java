package nilay.android.eventhook.fragment.registration;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.StrictMode;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import org.apache.commons.lang3.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nilay.android.eventhook.AddListenerOnTextChange;
import nilay.android.eventhook.R;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.GroupMaster;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.viewmodels.CollegeViewModel;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;

import static nilay.android.eventhook.fragment.registration.CommonRegistrationFragment.generateOTP;
import static nilay.android.eventhook.fragment.registration.CommonRegistrationFragment.sendMail;

public class GroupRegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout linearRegData;
    private CardView cardCnfEmail;
    private RelativeLayout rlUserData;
    private TextView lblNoGListedClg, lblGBackListClg;
    private TextInputLayout txtOTPLayout, txtTypeClgLayout, txtEmailLayout, txtUserNameLayout, txtUserPwdLayout, txtUserCnfPwdLayout, txtGroupNameLayout;
    private TextInputEditText txtGTypeClg, txtGLeaderEmailid, txtGLeaderEmailOTP, txtGLeaderName, txtGroupName, txtGroupPwd, txtCnfGroupPwd;
    private MaterialButton btnGLeaderGetOTP, btnGLeaderResend, btnGLeaderReg;
    private AppCompatSpinner spinnerGLeaderRegClg;
    private String emailid = "";
    private String username = "";
    private String collegeid = "";
    private String collegename = "";
    private String enteredClgname = "";
    private String password = "";
    private String confirmpwd = "";
    private String groupname = "";
    private String eventid = "";
    private String eventname = "";
    private String roleid = "";
    private String generatedOTP = "";
    private String userOTP = "";
    private boolean flag;
    private RegistrationViewModel registrationViewModel = new RegistrationViewModel();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public GroupRegistrationFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static GroupRegistrationFragment newInstance(String param1, String param2) {
        GroupRegistrationFragment fragment = new GroupRegistrationFragment();
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
        View view = inflater.inflate(R.layout.fragment_group_registration, container, false);
        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (getActivity() != null) {
            registrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);
            eventid = registrationViewModel.getEventid();
            roleid = registrationViewModel.getRoleid();
        }

        rlUserData = view.findViewById(R.id.relUserData);
        linearRegData = view.findViewById(R.id.rlRegData);
        cardCnfEmail = view.findViewById(R.id.cardCnfEmail);
        lblNoGListedClg = view.findViewById(R.id.lblNoGListedClg);
        lblGBackListClg = view.findViewById(R.id.lblGBackListClg);

        txtOTPLayout = view.findViewById(R.id.txtOTPLayout);
        txtTypeClgLayout = view.findViewById(R.id.txtTypeClgLayout);
        txtEmailLayout = view.findViewById(R.id.txtEmailLayout);
        txtUserNameLayout = view.findViewById(R.id.txtUserNameLayout);
        txtUserPwdLayout = view.findViewById(R.id.txtUserPwdLayout);
        txtUserCnfPwdLayout = view.findViewById(R.id.txtUserCnfPwdLayout);
        txtGroupNameLayout = view.findViewById(R.id.txtGroupNameLayout);

        txtGTypeClg = view.findViewById(R.id.txtGTypeClg);
        txtGLeaderName = view.findViewById(R.id.txtGLeaderName);
        txtGLeaderEmailid = view.findViewById(R.id.txtGLeaderEmailid);
        txtGroupPwd = view.findViewById(R.id.txtGroupPwd);
        txtCnfGroupPwd = view.findViewById(R.id.txtCnfGroupPwd);
        txtGroupName = view.findViewById(R.id.txtGroupName);
        txtGLeaderEmailOTP = view.findViewById(R.id.txtGLeaderEmailOTP);
        btnGLeaderGetOTP = view.findViewById(R.id.btnGLeaderGetOTP);
        btnGLeaderResend = view.findViewById(R.id.btnGLeaderResend);
        btnGLeaderReg = view.findViewById(R.id.btnGLeaderReg);
        spinnerGLeaderRegClg = view.findViewById(R.id.spinnerGLeaderRegClg);

        if (registrationViewModel.isLeaderRegFlag())
            disableForm(linearRegData, false);


        CollegeViewModel clgModel = new ViewModelProvider(this).get(CollegeViewModel.class);
        clgModel.getCollegeList().observe(this, college -> {
            spinnerGLeaderRegClg.setAdapter(new ArrayAdapter<College>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, college));
        });

        lblNoGListedClg.setOnClickListener((View v) -> {
            registrationViewModel.setNoListedClg(true);
            registrationViewModel.setSpinnerPos(-1);
            spinnerGLeaderRegClg.setVisibility(View.GONE);
            txtTypeClgLayout.setVisibility(View.VISIBLE);
            txtGTypeClg.requestFocus();
        });

        lblGBackListClg.setOnClickListener((View v) -> {
            registrationViewModel.setNoListedClg(false);
            registrationViewModel.setSpinnerPos(0);
            spinnerGLeaderRegClg.setVisibility(View.VISIBLE);
            txtTypeClgLayout.setVisibility(View.GONE);
        });

        spinnerGLeaderRegClg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    College clg = (College) adapterView.getSelectedItem();
                    collegeid = clg.getCollege_id();
                    collegename = clg.getCollege_name();
                    registrationViewModel.setSpinnerPos(spinnerGLeaderRegClg.getSelectedItemPosition());
                    registrationViewModel.selectedCollege(collegeid, collegename);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //txtGLeaderEmailid.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtGLeaderEmailid, txtEmailLayout, "^\\w+([\\.-]?\\w+)*@[A-Za-z\\-]+([\\.-]?[A-Za-z\\-]+)*(\\.[A-Za-z\\-]{2,3})+$", "EMAIL ADDRESS NOT IN CORRECT FORMAT"));
        txtGLeaderEmailOTP.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtGLeaderEmailOTP, txtOTPLayout));

        btnGLeaderGetOTP.setOnClickListener((View v) -> {
            if (generatedOTP.equals("")) {
                emailid = txtGLeaderEmailid.getText().toString();
                if (emailid.equals("")) {
                    txtGLeaderEmailid.requestFocus();
                    txtEmailLayout.setError("FIELD CANNOT BE EMPTY");
                } else if (!emailid.matches("^\\w+([\\.-]?\\w+)*@[A-Za-z\\-]+([\\.-]?[A-Za-z\\-]+)*(\\.[A-Za-z\\-]{2,3})+$")) {
                    txtGLeaderEmailid.requestFocus();
                    txtEmailLayout.setError("EMAIL ADDRESS NOT IN CORRECT FORMAT");
                } else {
                    flag = true;
                    dbRef = database.getReference("Users");
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                if (childDataSnapshot.child("email_id").exists() && childDataSnapshot.child("email_id").getValue().toString().equals(emailid)) {
                                    if (getContext() != null) {
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
                                                                                builder.setMessage("Already a Registered User in this Event " + eventname)
                                                                                        .setCancelable(false)
                                                                                        .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                                                                            txtGLeaderEmailid.setText("");
                                                                                        });
                                                                                AlertDialog alert = builder.create();
                                                                                alert.show();

                                                                            } else {

                                                                                registrationViewModel.setUserExists(true);
                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                                builder.setTitle("Already a REGISTERED User")
                                                                                        .setMessage("Do you want to Proceed with Previous Registration Details?")
                                                                                        .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                                                                            registrationViewModel.setEmailConfirmed(true);
                                                                                            registrationViewModel.setUserid(user.getUser_id());
                                                                                            rlUserData.setVisibility(View.VISIBLE);
                                                                                            btnGLeaderReg.setVisibility(View.VISIBLE);
                                                                                            txtTypeClgLayout.setVisibility(View.VISIBLE);
                                                                                            txtOTPLayout.setVisibility(View.GONE);
                                                                                            cardCnfEmail.setVisibility(View.GONE);
                                                                                            btnGLeaderGetOTP.setVisibility(View.GONE);
                                                                                            spinnerGLeaderRegClg.setVisibility(View.GONE);
                                                                                            lblNoGListedClg.setVisibility(View.GONE);
                                                                                            lblGBackListClg.setVisibility(View.GONE);
                                                                                            txtGLeaderEmailid.setText(user.getEmail_id());
                                                                                            txtGLeaderName.setText(user.getUser_name());
                                                                                            txtGroupPwd.setText(user.getPassword());
                                                                                            txtGroupPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                                                            txtGLeaderEmailid.setEnabled(false);
                                                                                            txtGLeaderName.setEnabled(false);
                                                                                            txtGroupPwd.setEnabled(false);
                                                                                            txtGTypeClg.setEnabled(false);
                                                                                            DatabaseReference dbref = database.getReference("College").child(user.getCollege_id());
                                                                                            dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                    College college = dataSnapshot.getValue(College.class);
                                                                                                    if (college != null) {
                                                                                                        registrationViewModel.setNoListedClg(true);
                                                                                                        txtGTypeClg.setText(college.getCollege_name());
                                                                                                        registrationViewModel.selectedCollege(college.getCollege_id(), college.getCollege_name());
                                                                                                    } else {
                                                                                                        registrationViewModel.setNoListedClg(true);
                                                                                                        txtGTypeClg.setText(user.getCollege_id());
                                                                                                        registrationViewModel.selectedCollege(user.getCollege_id(), user.getCollege_id());
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                }
                                                                                            });
                                                                                        })
                                                                                        .setNegativeButton("No", (DialogInterface dialog, int which) -> {
                                                                                            txtGLeaderEmailid.setText("");
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
                                                                                txtGLeaderEmailid.setText("");
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
                                                                    txtGLeaderEmailid.setText("");
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
                                    flag = false;
                                    break;
                                }
                            }
                            if (flag) {
                                generatedOTP = generateOTP();
                                sendMail(getContext(),"One Time Key for EventHook Email Confirmation", "Your Key is ", emailid, generatedOTP);
                                btnGLeaderGetOTP.setText("Confirm");
                                btnGLeaderResend.setVisibility(View.VISIBLE);
                                btnGLeaderResend.setEnabled(true);
                                txtGLeaderEmailOTP.setEnabled(true);
                                Toast.makeText(view.getContext(), "OTP Sent to " + emailid, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                userOTP = txtGLeaderEmailOTP.getText().toString();
                if (!emailid.equals(txtGLeaderEmailid.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Email Address Changed")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                emailid = txtGLeaderEmailid.getText().toString();
                                generatedOTP = "";
                                btnGLeaderGetOTP.setText("Get OTP");
                                btnGLeaderResend.setVisibility(View.GONE);
                                txtGLeaderEmailOTP.setText("");
                                txtGLeaderEmailOTP.setEnabled(false);
                            })
                            .setNegativeButton("No", (DialogInterface dialog, int which) -> {
                                txtGLeaderEmailid.setText(emailid);
                            }).show();
                } else if (userOTP.equals("")) {
                    txtGLeaderEmailOTP.requestFocus();
                    txtOTPLayout.setError("FIELD CANNOT BE EMPTY");
                } else {
                    if (userOTP.equals(generatedOTP)) {
                        Toast.makeText(view.getContext(), "Email Address Confirmed", Toast.LENGTH_SHORT).show();
                        btnGLeaderReg.setVisibility(View.VISIBLE);
                        btnGLeaderGetOTP.setVisibility(View.GONE);
                        btnGLeaderResend.setVisibility(View.GONE);
                        txtGLeaderEmailid.setEnabled(false);
                        txtOTPLayout.setVisibility(View.GONE);
                        cardCnfEmail.setVisibility(View.GONE);
                        rlUserData.setVisibility(View.VISIBLE);
                        registrationViewModel.setEmailConfirmed(true);
                    } else {
                        txtGLeaderEmailOTP.requestFocus();
                        txtOTPLayout.setError("OTP DOES NOT MATCH!");
                        txtGLeaderEmailOTP.setText("");
                    }
                }
            }
        });

        btnGLeaderResend.setOnClickListener((View v) -> {
            if (!generatedOTP.equals("")) {
                if (emailid.equals("")) {
                    txtGLeaderEmailid.requestFocus();
                    txtGLeaderEmailid.setError("FIELD CANNOT BE EMPTY");
                } else {
                    sendMail(getContext(),"One Time Key for EventHook Email Confirmation", "Your Key is ", emailid, generatedOTP);
                    Toast.makeText(getContext(), "OTP sent again to " + emailid, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(view.getContext(), "Technical Problem, Please Refresh the Page", Toast.LENGTH_SHORT).show();
            }
        });

        txtGTypeClg.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtGTypeClg, txtTypeClgLayout));
        txtGLeaderName.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtGLeaderName, txtUserNameLayout, "^[\\p{L} .'-]+$", "ENTER ONLY ALPHABETS"));
        txtGroupPwd.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtGroupPwd, txtUserPwdLayout));
        txtCnfGroupPwd.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtCnfGroupPwd, txtUserCnfPwdLayout));
        txtGroupName.addTextChangedListener(new AddListenerOnTextChange(getContext(), txtGroupName, txtGroupNameLayout));

        btnGLeaderReg.setOnClickListener((View v) -> {
            emailid = txtGLeaderEmailid.getText().toString();
            username = txtGLeaderName.getText().toString();
            password = txtGroupPwd.getText().toString();
            confirmpwd = txtCnfGroupPwd.getText().toString();
            groupname = txtGroupName.getText().toString();
            enteredClgname = txtGTypeClg.getText().toString();
            if (registrationViewModel.isNoListedClg() && enteredClgname.equals("")) {
                txtGTypeClg.requestFocus();
                txtTypeClgLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (registrationViewModel.getSpinnerPos() == 0 && !registrationViewModel.isUserExists()) {
                spinnerGLeaderRegClg.requestFocus();
                TextView errorText = (TextView) spinnerGLeaderRegClg.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("SELECT YOUR COLLEGE");
            } else if (username.equals("")) {
                txtGLeaderName.requestFocus();
                txtUserNameLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!username.matches("^[\\p{L} .'-]+$")) {
                txtGLeaderName.requestFocus();
                txtUserNameLayout.setError("ENTER ONLY ALPHABETS");
            } else if (password.equals("")) {
                txtGroupPwd.requestFocus();
                txtUserPwdLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (confirmpwd.equals("")) {
                txtCnfGroupPwd.requestFocus();
                txtUserCnfPwdLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!confirmpwd.equals(password)) {
                txtCnfGroupPwd.requestFocus();
                txtCnfGroupPwd.setError("PASSWORD DOES NOT MATCH");
                txtCnfGroupPwd.setText("");
            } else if (groupname.equals("")) {
                txtGroupName.requestFocus();
                txtGroupNameLayout.setError("FIELD CANNOT BE EMPTY");
            } else {
                username = StringUtils.capitalize(username);
                registrationViewModel.setLeaderRegFlag(true);

                if (!registrationViewModel.isUserExists()) {
                    if (registrationViewModel.isNoListedClg()) {
                        registrationViewModel.selectedCollege(enteredClgname,enteredClgname);
                        insertUser(username, emailid, password, roleid, enteredClgname);
                    } else {
                        insertUser(username, emailid, password, roleid, collegeid);
                    }
                } else {
                    insertGroupMaster(registrationViewModel.getUserid());
                }

                disableForm(linearRegData, false);

                View feesView = getLayoutInflater().inflate(R.layout.layout_event_fees,null);
                TextView lblRegPartNo = feesView.findViewById(R.id.lblRegPartNo);
                TextView lblRegPartFees = feesView.findViewById(R.id.lblRegPartFees);
                int members = registrationViewModel.getMinmembers();
                lblRegPartNo.setText(String.valueOf(members));
                int fees = Integer.valueOf(registrationViewModel.getEventFees());
                fees = fees*members;
                lblRegPartFees.setText("\u20B9"+fees);
                new AlertDialog.Builder(getContext())
                        .setTitle("Group Leader registration Successful\n")
                        .setCancelable(false)
                        .setView(feesView)
                        .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {

                        })
                        .setIcon(android.R.drawable.ic_input_add)
                        .show();

                //Toast.makeText(view.getContext(), "GROUP LEADER REGISTRATION SUCCESSFUL", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void insertUser(String name, String email, String pwd, String role, String college) {
        String date = getDate();
        dbRef = database.getReference("Users");
        String id = dbRef.push().getKey();
        Users users = new Users(id, name, email, pwd, role, college, date, "0");
        if (id != null)
            dbRef.child(id).setValue(users);

        insertGroupMaster(id);
    }

    private void insertGroupMaster(String leaderId) {
        Integer member = registrationViewModel.getMaxmembers() - 1;
        dbRef = database.getReference("GroupMaster");
        String gid = dbRef.push().getKey();
        GroupMaster groupMaster = new GroupMaster(gid, eventid, groupname, leaderId, 0, member);
        if (gid != null)
            dbRef.child(gid).setValue(groupMaster);

        registrationViewModel.setGroupInfo(gid, groupname);

        insertParticipation(leaderId, gid);
    }

    private void insertParticipation(String userId, String groupId) {
        dbRef = database.getReference("UserParticipation");
        UserParticipation userParticipation = new UserParticipation(userId, username, eventid, groupId, registrationViewModel.getGroupname());
        if (userId != null)
            dbRef.child(eventid).child(userId).setValue(userParticipation);

        sendMail(getContext(),"Successful Registration in EventHook", "Congratulations " + username + "!!!\nYou are Registered as a Group Leader of team " + groupname + ".\nIf you have not registered your members or to its full capacity you can log in EVENTHOOK and Add them LATER.", emailid, "");
    }

    private static void disableForm(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                disableForm(child, enabled);
            }
        }
    }

    public static String getDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        return df.format(c);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (registrationViewModel.isLeaderRegFlag()) {
            disableForm(linearRegData, false);
            rlUserData.setVisibility(View.VISIBLE);
            cardCnfEmail.setVisibility(View.GONE);
            btnGLeaderGetOTP.setVisibility(View.GONE);
            btnGLeaderResend.setVisibility(View.GONE);
        }
        if (registrationViewModel.getSpinnerPos() != 0)
            spinnerGLeaderRegClg.setSelection(registrationViewModel.getSpinnerPos());
        if (registrationViewModel.isEmailConfirmed()) {

            if(registrationViewModel.isUserExists()){

                rlUserData.setVisibility(View.VISIBLE);
                btnGLeaderReg.setVisibility(View.VISIBLE);
                txtOTPLayout.setVisibility(View.GONE);
                cardCnfEmail.setVisibility(View.GONE);
                btnGLeaderGetOTP.setVisibility(View.GONE);
                lblNoGListedClg.setVisibility(View.GONE);
                lblGBackListClg.setVisibility(View.GONE);
                txtGLeaderEmailid.setEnabled(false);
                txtGLeaderName.setEnabled(false);
                txtGroupPwd.setEnabled(false);

                if(registrationViewModel.isNoListedClg()){
                    spinnerGLeaderRegClg.setVisibility(View.GONE);
                    txtTypeClgLayout.setVisibility(View.VISIBLE);
                    txtGTypeClg.setEnabled(false);
                } else {
                    spinnerGLeaderRegClg.setVisibility(View.VISIBLE);
                    txtTypeClgLayout.setVisibility(View.GONE);
                }

            } else {

                rlUserData.setVisibility(View.VISIBLE);
                btnGLeaderReg.setVisibility(View.VISIBLE);
                txtOTPLayout.setVisibility(View.GONE);
                cardCnfEmail.setVisibility(View.GONE);
                btnGLeaderGetOTP.setVisibility(View.GONE);
                txtGLeaderEmailid.setEnabled(false);

                if(registrationViewModel.isNoListedClg()){
                    spinnerGLeaderRegClg.setVisibility(View.GONE);
                    txtTypeClgLayout.setVisibility(View.VISIBLE);
                    txtGTypeClg.setEnabled(false);
                } else {
                    spinnerGLeaderRegClg.setVisibility(View.VISIBLE);
                    txtTypeClgLayout.setVisibility(View.GONE);
                }

            }
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
