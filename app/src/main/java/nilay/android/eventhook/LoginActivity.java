package nilay.android.eventhook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nilay.android.eventhook.collegeadmin.ClgAdminActivity;
import nilay.android.eventhook.coordinator.CoordinatorActivity;
import nilay.android.eventhook.mainadmin.AdminActivity;
import nilay.android.eventhook.model.CollegeAdmin;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;
import nilay.android.eventhook.students.StudentActivity;
import nilay.android.eventhook.volunteer.VolunteerActivity;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText txtUnm, txtPwd;
    private String userName, password;
    private boolean logFlag = false;
    private boolean notParticipant = true;
    private Dialog dialog;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtUnm = (EditText) findViewById(R.id.txtUserName);
        txtPwd = (EditText) findViewById(R.id.txtPwd);
        if (dialog != null)
            dialog.dismiss();
        btnLogin.setOnClickListener((View view) -> {
            userName = txtUnm.getText().toString();
            password = txtPwd.getText().toString();

            if (userName.length() == 0) {
                txtUnm.requestFocus();
                txtUnm.setError("FIELD CANNOT BE EMPTY");
            } else if (password.length() == 0) {
                txtPwd.requestFocus();
                txtPwd.setError("FIELD CANNOT BE EMPTY");
            } else {

                dbRef = database.getReference("Users");
                dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dialog = new Dialog(LoginActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog);
                        dialog.setCancelable(false);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.show();
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            if (childDataSnapshot.child("email_id").exists() && childDataSnapshot.child("email_id").getValue().toString().equals(userName) && childDataSnapshot.child("password").getValue().toString().equals(password)) {
                                logFlag = true;
                                Users users = childDataSnapshot.getValue(Users.class);
                                DatabaseReference refLogin = database.getReference("UserRole");
                                refLogin.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                            if (childSnapshot.child("role_id").exists() && childSnapshot.child("role_id").getValue().toString().equals(users.getRole_id())) {
                                                UserRole userRole = childSnapshot.getValue(UserRole.class);
                                                assert userRole != null;
                                                String role = userRole.getRole_name();
                                                switch (role) {
                                                    case "Admin":
                                                        Intent i = new Intent(LoginActivity.this, AdminActivity.class);
                                                        startActivity(i);
                                                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                                                        break;
                                                    case "College Admin":
                                                        DatabaseReference clgAdminRef = database.getReference("CollegeAdmin").child(users.getUser_id());
                                                        clgAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                CollegeAdmin collegeAdmin = dataSnapshot.getValue(CollegeAdmin.class);
                                                                if (collegeAdmin != null && collegeAdmin.getValid_user().equals("1")) {
                                                                    createSharedPref(users.getUser_id(), users.getUser_name(), users.getCollege_id(), users.getRole_id());
                                                                    Intent i = new Intent(LoginActivity.this, ClgAdminActivity.class);
                                                                    startActivity(i);
                                                                } else {
                                                                    Toast.makeText(LoginActivity.this, "Please Wait for Request to be Authenticated", Toast.LENGTH_SHORT).show();
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
                                                        createSharedPref(users.getUser_id(), users.getUser_name(), users.getCollege_id(), users.getRole_id());
                                                        i = new Intent(LoginActivity.this, StudentActivity.class);
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
                        }
                        if (!logFlag) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Invalid Username or Password!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
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

                                createSharedPref(users.getUser_id(), users.getUser_name(), users.getCollege_id(), users.getRole_id());
                                Intent i = new Intent(LoginActivity.this, CoordinatorActivity.class);
                                startActivity(i);

                            } else {

                                Toast.makeText(LoginActivity.this, "Please Wait for Request to be Authenticated", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();

                            }

                        }
                    }

                } else if (userRole.getRole_name().equals("Volunteer")) {

                    for (DataSnapshot eventChildDataSnapShot : dataSnapshot.getChildren()) {

                        for (DataSnapshot userDataSnapShot : eventChildDataSnapShot.getChildren()) {

                            if (userDataSnapShot.child("user_id").getValue().toString().equals(users.getUser_id())) {

                                if (userDataSnapShot.child("valid_user").getValue().toString().equals("1")) {

                                    checkifParticipant(users);

                                } else {
                                    Toast.makeText(LoginActivity.this, "Please Wait for Request to be Authenticated", Toast.LENGTH_SHORT).show();
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

    private void checkifParticipant(Users user) {
        dbRef = database.getReference("UserParticipation");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot eventData : dataSnapshot.getChildren()) {

                    for (DataSnapshot userData : eventData.getChildren()) {

                        UserParticipation userPart = userData.getValue(UserParticipation.class);
                        if (userPart != null && userPart.getUser_id().equals(user.getUser_id())) {
                            notParticipant = false;
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Login Ambiguity\n")
                                    .setMessage("You are Registered as a Volunteer as well as a Student Participant\nLog in as...")
                                    .setCancelable(false)
                                    .setPositiveButton("Volunteer", (DialogInterface dialog, int which) -> {
                                        createSharedPref(user.getUser_id(), user.getUser_name(), user.getCollege_id(), user.getRole_id());
                                        Intent i = new Intent(LoginActivity.this, VolunteerActivity.class);
                                        startActivity(i);
                                    })
                                    .setNegativeButton("Student", (DialogInterface dialog, int which) -> {
                                        createSharedPref(user.getUser_id(), user.getUser_name(), user.getCollege_id(), user.getRole_id());
                                        Intent i = new Intent(LoginActivity.this, StudentActivity.class);
                                        startActivity(i);
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }

                    }

                }
                if (notParticipant) {
                    createSharedPref(user.getUser_id(), user.getUser_name(), user.getCollege_id(), user.getRole_id());
                    Intent i = new Intent(LoginActivity.this, VolunteerActivity.class);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createSharedPref(String user_id, String user_name, String college_id, String role_id) {
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userid", user_id);
        editor.putString("username", user_name);
        editor.putString("collegeid", college_id);
        editor.putString("roleid", role_id);
        editor.putString("logFlag", "1");
        editor.apply();
        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (dialog != null)
            dialog.dismiss();
    }
}
