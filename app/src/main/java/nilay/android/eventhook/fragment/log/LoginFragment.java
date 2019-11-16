package nilay.android.eventhook.fragment.log;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import nilay.android.eventhook.AddFocusChangedListener;
import nilay.android.eventhook.AddListenerOnTextChange;
import nilay.android.eventhook.LoginActivity;
import nilay.android.eventhook.R;
import nilay.android.eventhook.collegeadmin.ClgAdminActivity;
import nilay.android.eventhook.coordinator.CoordinatorActivity;
import nilay.android.eventhook.mainadmin.AdminActivity;
import nilay.android.eventhook.model.CollegeAdmin;
import nilay.android.eventhook.model.UserLog;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.model.Volunteer;
import nilay.android.eventhook.students.StudentActivity;
import nilay.android.eventhook.volunteer.VolunteerActivity;

public class LoginFragment extends Fragment {

    private TextInputLayout emailAddressLayout, passwordLayout;
    private TextInputEditText txtEmailAddress, txtPassword;
    private MaterialButton btnLogin;

    private boolean logFlag = false;
    private boolean notParticipant = true;
    private Dialog dialog;
    private LocationManager locationManager;

    private String date = "";
    private String time = "";

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        emailAddressLayout = view.findViewById(R.id.emailAddressLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        txtEmailAddress = view.findViewById(R.id.emailAddress);
        txtPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.btnLogin);
        txtEmailAddress.requestFocus();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateTime = dateFormat.format(c.getTime());
        date = dateTime.substring(0, 10);
        time = dateTime.substring(11, 19);
        System.out.println(dateTime);

        if (dialog != null)
            dialog.dismiss();

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Dexter.withActivity(getActivity())
                    .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            if (response.isPermanentlyDenied()) {
                                new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                                        .setTitle("Permission Denied\n")
                                        .setMessage("Permission to access device location is permanently denied! \nYou need to go to Settings to Allow the Permission.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK", (DialogInterface dialog, int which) -> {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.fromParts("package", getActivity().getPackageName(), null));
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            } else {
                                Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .check();
        }

        txtEmailAddress.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtEmailAddress, emailAddressLayout));
        txtPassword.setOnFocusChangeListener(new AddFocusChangedListener(getContext(), txtPassword, passwordLayout));

        btnLogin.setOnClickListener((View v) -> {

            String username = txtEmailAddress.getText().toString();
            String password = txtPassword.getText().toString();

            if (username.equals("")) {
                txtEmailAddress.requestFocus();
                emailAddressLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (password.equals("")) {
                txtPassword.requestFocus();
                passwordLayout.setError("FIELD CANNOT BE EMPTY");
            } else {

                dbRef = database.getReference("Users");
                Query query = dbRef.orderByChild("email_id").equalTo(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dialog = new Dialog(Objects.requireNonNull(getActivity()));
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog);
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.show();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userDataSnapShot : dataSnapshot.getChildren()) {
                                Users user = userDataSnapShot.getValue(Users.class);
                                assert user != null;
                                if (user.getPassword().equals(password)) {
                                    logFlag = true;
                                    DatabaseReference refRole = database.getReference("UserRole");
                                    Query queryRole = refRole.orderByChild("role_id").equalTo(user.getRole_id());
                                    queryRole.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot roleDataSnapshot) {
                                            if(roleDataSnapshot.exists()){
                                                for(DataSnapshot childRoleDataSnapShot : roleDataSnapshot.getChildren()){
                                                    UserRole userRole = childRoleDataSnapShot.getValue(UserRole.class);
                                                    assert userRole != null;
                                                    String role = userRole.getRole_name();
                                                    switch (role) {
                                                        case "Admin":
                                                            String logId = insertUserLog(user.getUser_id());
                                                            createSharedPref(logId, user.getUser_id(), user.getUser_name(), user.getCollege_id(), user.getRole_id());
                                                            Intent i = new Intent(getActivity(), AdminActivity.class);
                                                            startActivity(i);
                                                            Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                                                            break;
                                                        case "College Admin":
                                                            DatabaseReference clgAdminRef = database.getReference("CollegeAdmin").child(user.getUser_id());
                                                            clgAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    CollegeAdmin collegeAdmin = dataSnapshot.getValue(CollegeAdmin.class);
                                                                    if (collegeAdmin != null && collegeAdmin.getValid_user().equals("1")) {
                                                                        String logId = insertUserLog(user.getUser_id());
                                                                        createSharedPref(logId, user.getUser_id(), user.getUser_name(), user.getCollege_id(), user.getRole_id());
                                                                        Intent i = new Intent(getActivity(), ClgAdminActivity.class);
                                                                        startActivity(i);
                                                                    } else {
                                                                        Toast.makeText(getActivity(), "Please Wait for Request to be Authenticated", Toast.LENGTH_SHORT).show();
                                                                        dialog.dismiss();
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                            break;
                                                        case "Coordinator":
                                                            validateUser(userRole, user);
                                                            break;
                                                        case "Volunteer":
                                                            validateUser(userRole, user);
                                                            break;
                                                        case "Student":
                                                            String logid = insertUserLog(user.getUser_id());
                                                            createSharedPref(logid, user.getUser_id(), user.getUser_name(), user.getCollege_id(), user.getRole_id());
                                                            i = new Intent(getActivity(), StudentActivity.class);
                                                            startActivity(i);
                                                            break;
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Invalid Password!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Invalid Username!", Toast.LENGTH_LONG).show();
                        }
                        /*for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            if (childDataSnapshot.child("email_id").exists() && childDataSnapshot.child("email_id").getValue().toString().equals(username) && childDataSnapshot.child("password").getValue().toString().equals(password)) {
                                logFlag = true;
                                Users users = childDataSnapshot.getValue(Users.class);
                                DatabaseReference refRole = database.getReference("UserRole");
                                refRole.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            if (childSnapshot.child("role_id").exists() && childSnapshot.child("role_id").getValue().toString().equals(users.getRole_id())) {
                                                UserRole userRole = childSnapshot.getValue(UserRole.class);
                                                assert userRole != null;
                                                String role = userRole.getRole_name();
                                                DatabaseReference logRef = database.getReference("UserLog");
                                                String id = "";
                                                UserLog userLog;
                                                switch (role) {
                                                    case "Admin":
                                                        id = logRef.push().getKey();
                                                        assert id != null;
                                                        userLog = new UserLog(id, users.getUser_id(), date, time);
                                                        logRef.child(id).setValue(userLog);
                                                        createSharedPref(id, users.getUser_id(), users.getUser_name(), users.getCollege_id(), users.getRole_id());
                                                        Intent i = new Intent(getActivity(), AdminActivity.class);
                                                        startActivity(i);
                                                        Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case "College Admin":
                                                        DatabaseReference clgAdminRef = database.getReference("CollegeAdmin").child(users.getUser_id());
                                                        clgAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                CollegeAdmin collegeAdmin = dataSnapshot.getValue(CollegeAdmin.class);
                                                                if (collegeAdmin != null && collegeAdmin.getValid_user().equals("1")) {
                                                                    insertUserLog(users.getUser_id());
                                                                    DatabaseReference logRef = database.getReference("UserLog");
                                                                    String id = logRef.push().getKey();
                                                                    assert id != null;
                                                                    UserLog userLog = new UserLog(id, users.getUser_id(), date, time);
                                                                    logRef.child(id).setValue(userLog);
                                                                    createSharedPref(id, users.getUser_id(), users.getUser_name(), users.getCollege_id(), users.getRole_id());
                                                                    Intent i = new Intent(getActivity(), ClgAdminActivity.class);
                                                                    startActivity(i);
                                                                } else {
                                                                    Toast.makeText(getActivity(), "Please Wait for Request to be Authenticated", Toast.LENGTH_SHORT).show();
                                                                    dialog.dismiss();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                        break;
                                                    case "Coordinator":
                                                        validateUser(userRole, users);
                                                        break;
                                                    case "Volunteer":
                                                        validateUser(userRole, users);
                                                        break;
                                                    case "Student":
                                                        logRef = database.getReference("UserLog");
                                                        id = logRef.push().getKey();
                                                        assert id != null;
                                                        userLog = new UserLog(id, users.getUser_id(), date, time);
                                                        logRef.child(id).setValue(userLog);
                                                        createSharedPref(id, users.getUser_id(), users.getUser_name(), users.getCollege_id(), users.getRole_id());
                                                        i = new Intent(getActivity(), StudentActivity.class);
                                                        startActivity(i);
                                                        break;
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }*/
                        /*if (!logFlag) {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Invalid Username or Password!", Toast.LENGTH_LONG).show();
                        }*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        });

        return view;
    }

    private String insertUserLog(String user_id) {
        DatabaseReference logRef = database.getReference("UserLog");
        String id = logRef.push().getKey();
        assert id != null;
        UserLog userLog = new UserLog(id, user_id, date, time);
        logRef.child(id).setValue(userLog);
        return  id;
    }

    private void validateUser(UserRole userRole, Users users) {
        dbRef = database.getReference(userRole.getRole_name());
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (userRole.getRole_name().equals("Coordinator")) {

                    for (DataSnapshot userChildDataSnapShot : dataSnapshot.getChildren()) {

                        if (userChildDataSnapShot.child("user_id").getValue().toString().equals(users.getUser_id())) {

                            if (userChildDataSnapShot.child("valid_user").getValue().toString().equals("1")) {

                                String logId = insertUserLog(users.getUser_id());

                                createSharedPref(logId, users.getUser_id(), users.getUser_name(), users.getCollege_id(), users.getRole_id());
                                Intent i = new Intent(getActivity(), CoordinatorActivity.class);
                                startActivity(i);

                            } else {

                                Toast.makeText(getActivity(), "Please Wait for Request to be Authenticated", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }

                        }
                    }

                } else if (userRole.getRole_name().equals("Volunteer")) {

                    for (DataSnapshot eventChildDataSnapShot : dataSnapshot.getChildren()) {

                        for (DataSnapshot userDataSnapShot : eventChildDataSnapShot.getChildren()) {

                            if (userDataSnapShot.child("user_id").getValue().toString().equals(users.getUser_id())) {
                                Volunteer volunteer = userDataSnapShot.getValue(Volunteer.class);
                                if (userDataSnapShot.child("valid_user").getValue().toString().equals("1")) {
                                    if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                        locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(Context.LOCATION_SERVICE);
                                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new AddLocationListener(volunteer));
                                    }
                                    checkifParticipant(users, volunteer);

                                } else {
                                    Toast.makeText(getContext(), "Please Wait for Request to be Authenticated", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }

                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkifParticipant(Users user, Volunteer volunteer) {
        if (getActivity() != null) {
            dbRef = database.getReference("UserParticipation");
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot eventData : dataSnapshot.getChildren()) {

                        for (DataSnapshot userData : eventData.getChildren()) {

                            UserParticipation userPart = userData.getValue(UserParticipation.class);
                            if (userPart != null && userPart.getUser_id().equals(user.getUser_id())) {
                                notParticipant = false;
                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Login Ambiguity\n")
                                        .setMessage("You are Registered as a Volunteer as well as a Student Participant\nLog in as...")
                                        .setCancelable(false)
                                        .setPositiveButton("Volunteer", (DialogInterface dialog, int which) -> {

                                            String logId = insertUserLog(user.getUser_id());
                                            createSharedPref(logId, user.getUser_id(), user.getUser_name(), user.getCollege_id(), volunteer.getEvent_id());
                                            Intent i = new Intent(getActivity(), VolunteerActivity.class);
                                            startActivity(i);
                                        })
                                        .setNegativeButton("Student", (DialogInterface dialog, int which) -> {

                                            String logId = insertUserLog(user.getUser_id());
                                            createSharedPref(logId, user.getUser_id(), user.getUser_name(), user.getCollege_id(), user.getRole_id());
                                            Intent i = new Intent(getActivity(), StudentActivity.class);
                                            startActivity(i);
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                        }

                    }
                    if (notParticipant) {
                        String logId = insertUserLog(user.getUser_id());
                        createSharedPref(logId, user.getUser_id(), user.getUser_name(), user.getCollege_id(), volunteer.getEvent_id());
                        Intent i = new Intent(getActivity(), VolunteerActivity.class);
                        startActivity(i);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void createSharedPref(String logId, String user_id, String user_name, String college_id, String role_id) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("logId", logId);
        editor.putString("userid", user_id);
        editor.putString("username", user_name);
        editor.putString("collegeid", college_id);
        editor.putString("roleid", role_id);
        editor.putString("logFlag", "1");
        editor.apply();
        Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialog != null)
            dialog.dismiss();
    }

    private class AddLocationListener implements LocationListener {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("Volunteer");
        Volunteer volunteer;
        int pos = 0;

        public AddLocationListener(Volunteer volunteer) {
            this.volunteer = volunteer;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (pos == 0) {
                dbRef.child(volunteer.getEvent_id()).child(volunteer.getUser_id()).child("latitude").setValue(location.getLatitude());
                dbRef.child(volunteer.getEvent_id()).child(volunteer.getUser_id()).child("longitude").setValue(location.getLongitude());
                pos = 1;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
