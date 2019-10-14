package nilay.android.eventhook.fragment.registration;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import java.util.Objects;

import nilay.android.eventhook.AddFocusChangedListener;
import nilay.android.eventhook.AddListenerOnTextChange;
import nilay.android.eventhook.GMailSender;
import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.volunteer.VolFeeConfirmationFragment;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.Coordinator;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.model.Volunteer;
import nilay.android.eventhook.viewmodels.CollegeViewModel;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;
import nilay.android.eventhook.viewmodels.VolunteerViewModel;

import static nilay.android.eventhook.fragment.registration.GroupRegistrationFragment.getDate;

public class CommonRegistrationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private LinearLayout linearCRegData;
    private RelativeLayout rlUserData;
    private CardView cardCnfEmail;
    private TextView lblNoListedClg, lblBackListClg;
    private TextInputLayout txtOTPLayout, txtTypeClgLayout, txtEmailLayout, txtUserNameLayout, txtUserPwdLayout, txtUserCnfPwdLayout;
    private TextInputEditText txtEmailid, txtEmailOTP, txtUserName, txtUserPwd, txtUserCnfPwd, txtTypeClg;
    private MaterialButton btnGetOTP, btnUserReg, btnResendOTP;
    private AppCompatSpinner spinnerRegClg;
    private String emailid = "";
    private String username = "";
    private String collegeid = "";
    private String collegename = "";
    private String enteredClgname = "";
    private String password = "";
    private String confirmpwd = "";
    private String eventid = "";
    private String eventname = "";
    private String generatedOTP = "";
    private String userOTP = "";
    private String roleid = "";
    private String rolename = "";
    private Integer clgidseq = 0;

    private static boolean flag;

    private RegistrationViewModel registrationViewModel = new RegistrationViewModel();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    public CommonRegistrationFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static CommonRegistrationFragment newInstance(String param1, String param2) {
        CommonRegistrationFragment fragment = new CommonRegistrationFragment();
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
        final View view = inflater.inflate(R.layout.fragment_common_registration, container, false);
        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);
        /*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
        if (getActivity() != null) {
            registrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);
            eventid = registrationViewModel.getEventid();
            roleid = registrationViewModel.getRoleid();
            rolename = registrationViewModel.getRolename();
            username = registrationViewModel.getUsername();
        }

        linearCRegData = view.findViewById(R.id.rlCRegData);
        rlUserData = view.findViewById(R.id.rlCUserData);
        cardCnfEmail = view.findViewById(R.id.cardCnfEmail);
        lblNoListedClg = view.findViewById(R.id.lblNoListedClg);
        lblBackListClg = view.findViewById(R.id.lblBackListClg);

        txtOTPLayout = view.findViewById(R.id.txtOTPLayout);
        txtTypeClgLayout = view.findViewById(R.id.txtTypeClgLayout);
        txtEmailLayout = view.findViewById(R.id.txtEmailLayout);
        txtUserNameLayout = view.findViewById(R.id.txtUserNameLayout);
        txtUserPwdLayout = view.findViewById(R.id.txtUserPwdLayout);
        txtUserCnfPwdLayout = view.findViewById(R.id.txtUserCnfPwdLayout);

        txtTypeClg = view.findViewById(R.id.txtTypeClg);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtEmailid = view.findViewById(R.id.txtEmailid);
        txtUserPwd = view.findViewById(R.id.txtUserPwd);
        txtUserCnfPwd = view.findViewById(R.id.txtUserCnfPwd);
        txtEmailOTP = view.findViewById(R.id.txtEmailOTP);
        btnUserReg = view.findViewById(R.id.btnUserReg);
        btnGetOTP = view.findViewById(R.id.btnGetOTP);
        btnResendOTP = view.findViewById(R.id.btnResendOTP);
        spinnerRegClg = view.findViewById(R.id.spinnerRegClg);
        txtUserName.setText("");

        CollegeViewModel clgModel = new ViewModelProvider(this).get(CollegeViewModel.class);
        clgModel.getCollegeList().observe(this, college -> {
            spinnerRegClg.setAdapter(new ArrayAdapter<College>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, college));
        });

        if (!username.equals("") || clgidseq != 0) {
            txtUserName.setText(username);
        }

        lblNoListedClg.setOnClickListener((View v) -> {
            registrationViewModel.setNoListedClg(true);
            clgidseq = -1;
            spinnerRegClg.setVisibility(View.GONE);
            txtTypeClgLayout.setVisibility(View.VISIBLE);
            txtTypeClg.requestFocus();
        });

        lblBackListClg.setOnClickListener((View v) -> {
            registrationViewModel.setNoListedClg(false);
            clgidseq = 0;
            spinnerRegClg.setVisibility(View.VISIBLE);
            txtTypeClgLayout.setVisibility(View.GONE);
        });

        spinnerRegClg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                College clg = (College) adapterView.getSelectedItem();
                collegeid = clg.getCollege_id();
                collegename = clg.getCollege_name();
                registrationViewModel.selectedCollege(collegeid, collegename);
                clgidseq = spinnerRegClg.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        txtEmailid.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtEmailid, txtEmailLayout, "^\\w+([\\.-]?\\w+)*@[A-Za-z\\-]+([\\.-]?[A-Za-z\\-]+)*(\\.[A-Za-z\\-]{2,3})+$", "EMAIL ADDRESS NOT IN CORRECT FORMAT"));
        txtEmailOTP.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtEmailOTP, txtOTPLayout));

        btnGetOTP.setOnClickListener((View v) -> {
            if (generatedOTP.equals("")) {
                emailid = txtEmailid.getText().toString();
                if (emailid.equals("")) {
                    txtEmailid.requestFocus();
                    txtEmailLayout.setError("FIELD CANNOT BE EMPTY");
                } else if (!emailid.matches("^\\w+([\\.-]?\\w+)*@[A-Za-z\\-]+([\\.-]?[A-Za-z\\-]+)*(\\.[A-Za-z\\-]{2,3})+$")) {
                    txtEmailid.requestFocus();
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
                                                        if (registrationViewModel.getRolename().equals("College Admin")) {
                                                            txtEmailid.setText("");
                                                            Toast.makeText(getContext(), "Already Registered as a Volunteer", Toast.LENGTH_SHORT).show();
                                                        } else {
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
                                                                                                txtEmailid.setText("");
                                                                                            });
                                                                                    AlertDialog alert = builder.create();
                                                                                    alert.show();

                                                                                } else {

                                                                                    registrationViewModel.setUserExists(true);
                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                                    builder.setTitle("Already a REGISTERED User")
                                                                                            .setMessage("Do you want to Proceed with Previous Registration Details?")
                                                                                            .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                                                                                registrationViewModel.setUserid(user.getUser_id());
                                                                                                rlUserData.setVisibility(View.VISIBLE);
                                                                                                btnUserReg.setVisibility(View.VISIBLE);
                                                                                                txtTypeClgLayout.setVisibility(View.VISIBLE);
                                                                                                txtOTPLayout.setVisibility(View.GONE);
                                                                                                cardCnfEmail.setVisibility(View.GONE);
                                                                                                btnGetOTP.setVisibility(View.GONE);
                                                                                                spinnerRegClg.setVisibility(View.GONE);
                                                                                                lblNoListedClg.setVisibility(View.GONE);
                                                                                                lblBackListClg.setVisibility(View.GONE);
                                                                                                txtEmailid.setText(user.getEmail_id());
                                                                                                txtUserName.setText(user.getUser_name());
                                                                                                txtUserPwd.setText(user.getPassword());
                                                                                                txtUserPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                                                                txtEmailid.setEnabled(false);
                                                                                                txtUserName.setEnabled(false);
                                                                                                txtUserPwd.setEnabled(false);
                                                                                                txtTypeClg.setEnabled(false);
                                                                                                DatabaseReference dbref = database.getReference("College").child(user.getCollege_id());
                                                                                                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                        College college = dataSnapshot.getValue(College.class);
                                                                                                        if (college != null) {
                                                                                                            txtTypeClg.setText(college.getCollege_name());
                                                                                                        } else {
                                                                                                            txtTypeClg.setText(user.getCollege_id());
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                    }
                                                                                                });
                                                                                            })
                                                                                            .setNegativeButton("No", (DialogInterface dialog, int which) -> {
                                                                                                txtEmailid.setText("");
                                                                                                txtOTPLayout.setVisibility(View.GONE);
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
                                                                                    txtEmailid.setText("");
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

                                                    } else {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                        builder.setMessage("Only Student and Student Volunteer can Participate in Events")
                                                                .setCancelable(false)
                                                                .setPositiveButton("OK", (DialogInterface dialog, int id) -> {
                                                                    txtEmailid.setText("");
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
                                txtOTPLayout.setVisibility(View.VISIBLE);
                                generatedOTP = generateOTP();
                                sendMail(getContext(), "One Time Key for EventHook Email Confirmation", "Your Key is ", emailid, generatedOTP);
                                btnGetOTP.setText("Confirm");
                                btnResendOTP.setVisibility(View.VISIBLE);
                                btnResendOTP.setEnabled(true);
                                txtOTPLayout.setVisibility(View.VISIBLE);
                                txtEmailOTP.setEnabled(true);
                                txtEmailOTP.requestFocus();
                                Toast.makeText(view.getContext(), "OTP Sent to " + emailid, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            } else {
                txtOTPLayout.setVisibility(View.VISIBLE);
                userOTP = txtEmailOTP.getText().toString();
                if (!emailid.equals(txtEmailid.getText().toString())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Email Address Changed")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Yes", (DialogInterface dialog, int which) -> {
                                emailid = txtEmailid.getText().toString();
                                generatedOTP = "";
                                btnGetOTP.setText("Get OTP");
                                txtOTPLayout.setVisibility(View.GONE);
                                btnResendOTP.setVisibility(View.GONE);
                                txtEmailOTP.setText("");
                                txtEmailOTP.setEnabled(false);
                                txtEmailid.requestFocus();
                            })
                            .setNegativeButton("No", (DialogInterface dialog, int which) -> {
                                txtEmailid.setText(emailid);
                            }).show();
                } else if (userOTP.equals("")) {
                    txtEmailOTP.requestFocus();
                    txtOTPLayout.setError("FIELD CANNOT BE EMPTY");
                } else {
                    if (userOTP.equals(generatedOTP)) {
                        Toast.makeText(view.getContext(), "Email Address Confirmed", Toast.LENGTH_SHORT).show();
                        btnUserReg.setVisibility(View.VISIBLE);
                        btnGetOTP.setVisibility(View.GONE);
                        btnResendOTP.setVisibility(View.GONE);
                        txtEmailid.setEnabled(false);
                        txtOTPLayout.setVisibility(View.GONE);
                        cardCnfEmail.setVisibility(View.GONE);
                        rlUserData.setVisibility(View.VISIBLE);
                    } else {
                        txtEmailOTP.requestFocus();
                        txtOTPLayout.setError("OTP DOES NOT MATCH!");
                        txtEmailOTP.setText("");
                    }
                }
            }
        });

        btnResendOTP.setOnClickListener((View v) -> {
            if (!generatedOTP.equals("")) {
                if (emailid.equals("")) {
                    txtEmailid.requestFocus();
                    txtEmailLayout.setError("FIELD CANNOT BE EMPTY");
                } else {
                    sendMail(getContext(), "One Time Key for EventHook Email Confirmation", "Your Key is ", emailid, generatedOTP);
                    Toast.makeText(getContext(), "OTP sent again to " + emailid, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(view.getContext(), "Please Press GET OTP Button First!", Toast.LENGTH_SHORT).show();
            }
        });

        txtTypeClg.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtTypeClg, txtTypeClgLayout));
        txtUserName.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtUserName, txtUserNameLayout, "^[\\p{L} .'-]+$", "ENTER ONLY ALPHABETS"));
        txtUserPwd.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtUserPwd, txtUserPwdLayout));
        txtUserCnfPwd.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtUserCnfPwd, txtUserCnfPwdLayout));

        btnUserReg.setOnClickListener((View v) -> {
            username = txtUserName.getText().toString();
            password = txtUserPwd.getText().toString();
            confirmpwd = txtUserCnfPwd.getText().toString();
            enteredClgname = txtTypeClg.getText().toString();
            if (registrationViewModel.isNoListedClg() && enteredClgname.equals("")) {
                txtTypeClg.requestFocus();
                txtTypeClgLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (clgidseq == 0 && !registrationViewModel.isUserExists()) {
                spinnerRegClg.requestFocus();
                TextView errorText = (TextView) spinnerRegClg.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);
                errorText.setText("SELECT YOUR COLLEGE");
            } else if (username.equals("")) {
                txtUserName.requestFocus();
                txtUserNameLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!username.matches("^[\\p{L} .'-]+$")) {
                txtUserName.requestFocus();
                txtUserNameLayout.setError("ENTER ONLY ALPHABETS");
            } else if (password.equals("")) {
                txtUserPwd.requestFocus();
                txtUserPwdLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (confirmpwd.equals("")) {
                txtUserCnfPwd.requestFocus();
                txtUserCnfPwdLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!confirmpwd.equals(password)) {
                txtUserCnfPwd.requestFocus();
                txtUserCnfPwd.setError("PASSWORD DOES NOT MATCH");
                txtUserCnfPwd.setText("");
            } else {
                if (registrationViewModel.isNoListedClg())
                    registrationViewModel.selectedCollege(enteredClgname, enteredClgname);

                String role = registrationViewModel.getRolename();
                if (registrationViewModel.isUserExists()) {

                    switch (role) {
                        case "Student":
                            insertParticipation(registrationViewModel.getUserid());
                            showDialog();
                            break;

                        case "Volunteer":
                            insertVolunteer(registrationViewModel.getUserid());
                            break;

                        default:
                            Toast.makeText(getContext(), "Invalid Request!", Toast.LENGTH_SHORT).show();
                            break;
                    }

                } else {
                    if (registrationViewModel.isNoListedClg()) {
                        if (role.equals("Student")) {
                            insertUser(username, emailid, password, registrationViewModel.getRoleid(), enteredClgname);
                            showDialog();
                        } else {
                            Toast.makeText(getContext(), "Only Student Participant can Type for College Name which is NOT Listed.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        insertUser(username, emailid, password, registrationViewModel.getRoleid(), collegeid);
                        if (role.equals("Student")) {
                            showDialog();
                        }
                    }
                }

                clearForm();
                btnUserReg.setVisibility(View.GONE);
                btnGetOTP.setVisibility(View.VISIBLE);
                btnResendOTP.setVisibility(View.GONE);
            }
        });

        return view;
    }

    private void showDialog() {
        View feesView = getLayoutInflater().inflate(R.layout.layout_event_fees, null);
        TextView lblRegPartNo = feesView.findViewById(R.id.lblRegPartNo);
        TextView lblRegPartFees = feesView.findViewById(R.id.lblRegPartFees);
        lblRegPartNo.setText("1");
        int fees = Integer.valueOf(registrationViewModel.getEventFees());
        lblRegPartFees.setText("\u20B9" + fees);
        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle("Registration Successful\n")
                .setCancelable(false)
                .setView(feesView)
                .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {
                    dialogReg();
                })
                .setIcon(android.R.drawable.ic_input_add)
                .show();
    }

    private void dialogReg() {
        if (getActivity() != null) {
            VolunteerViewModel volViewModel = new ViewModelProvider(getActivity()).get(VolunteerViewModel.class);
            volViewModel.setPrevRegisteredEventId(eventid);

            if (volViewModel.isGeneralConfirmation()) {
                new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        .setTitle("Done With Registration?\n")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {
                            Fragment fragment = null;
                            Class fragmentClass = VolFeeConfirmationFragment.class;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            assert fragment != null;
                            fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment).commit();
                        })
                        .setNegativeButton(android.R.string.no, (DialogInterface dialog, int which) -> {

                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    private void clearForm() {
        for (int i = 0, count = rlUserData.getChildCount(); i < count; ++i) {
            View view = rlUserData.getChildAt(i);
            if (view instanceof EditText) {
                ((TextInputEditText) view).setText("");
            }
            if (view instanceof Spinner) {
                ((AppCompatSpinner) view).setSelection(0);
            }
        }
        registrationViewModel.setNoListedClg(false);
        clgidseq = 0;
        btnGetOTP.setText("Get OTP");
        rlUserData.setVisibility(View.GONE);
        cardCnfEmail.setVisibility(View.VISIBLE);
        spinnerRegClg.setVisibility(View.VISIBLE);
        spinnerRegClg.setSelection(0);
        txtTypeClgLayout.setVisibility(View.GONE);
        txtTypeClg.setText("");
        txtEmailid.setText("");
        txtEmailid.setEnabled(true);
        txtOTPLayout.setVisibility(View.VISIBLE);
        txtEmailOTP.setText("");
        txtEmailOTP.setEnabled(false);
        generatedOTP = "";
    }

    private void insertUser(String name, String email, String pwd, String role, String college) {

        String date = getDate();
        dbRef = database.getReference("Users");
        String id = dbRef.push().getKey();
        Users users = new Users(id, name, email, pwd, role, college, date, "0");
        if (id != null)
            dbRef.child(id).setValue(users);
        String roleName = registrationViewModel.getRolename();
        switch (roleName) {

            case "College Admin":
                registrationViewModel.setUserid(id);
                registrationViewModel.setUsername(name);
                registrationViewModel.setEmail_id(emailid);
                Fragment fragment = null;
                Class fragmentClass = ClgAdminRegFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (fragment != null) {
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager != null)
                        fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment).commit();
                }
                break;

            case "Student":
                insertParticipation(id);
                break;

            case "Coordinator":
                insertCoordinator(id);
                break;

            case "Volunteer":
                insertVolunteer(id);
                break;

            default:
                sendMail(getContext(), "Successful Registration in EventHook", "Congratulations " + this.username + "!!!\nYou are Registered as a College Admin of " + collegename + ".\nPlease Login to enjoy the Perks of EVENTHOOK.", this.emailid, "");
                break;
        }
    }

    private void insertVolunteer(String uid) {
        dbRef = database.getReference("Volunteer");
        Volunteer volunteer = new Volunteer(uid, eventid, "0", "0");
        dbRef.child(eventid).child(uid).setValue(volunteer);
        sendMail(getContext(), "Successful Registration in EventHook", "Congratulations " + username + "!!!\nYou are Registered as a Volunteer in Event " + eventname + ".\nPlease Login to enjoy the Perks of EVENTHOOK.", emailid, "");
    }

    private void insertCoordinator(String uid) {
        dbRef = database.getReference("Coordinator");
        Coordinator coordinator = new Coordinator(uid, eventid, "0");
        dbRef.child(uid).setValue(coordinator);
        sendMail(getContext(), "Successful Registration in EventHook", "Congratulations " + username + "!!!\nYou are Registered as a Coordinator in Event " + eventname + ".\nPlease Login to enjoy the Perks of EVENTHOOK.", emailid, "");
    }

    private void insertParticipation(String uid) {
        dbRef = database.getReference("UserParticipation");
        UserParticipation userParticipation = new UserParticipation(uid, username, eventid);
        dbRef.child(eventid).child(uid).setValue(userParticipation);

        sendMail(getContext(), "Successful Registration in EventHook", "Congratulations " + username + "!!!\nYou are Registered as a participant in Event " + eventname + ".\nPlease Login to enjoy the Perks of EVENTHOOK.", emailid, "");

    }

    public static String generateOTP() {
        String AlphaNumericString = "ABCDEFGHJKMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijkmnopqrstuvxyz";
        int n = 8;

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public static void sendMail(Context context, String subject, String body, String emailid, String otp) {
        final GMailSender sender = new GMailSender("nilayb77@gmail.com", "nb843710");
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground(Void... arg) {
                try {
                    sender.sendMail(subject,
                            body + otp,
                            "nilayb77@gmail.com",
                            emailid);
                } catch (Exception e) {
                    if (e.toString().equals("Invalid Addresses")) {
                        Toast.makeText(context, "Email Address Doesn't Exists!!!", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("SendMail", e.getMessage(), e);
                }
                return null;
            }
        }.execute();

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