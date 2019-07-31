package nilay.android.eventhook.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.UserRole;
import nilay.android.eventhook.model.Users;

public class RegistrationActivity extends AppCompatActivity {
    private String eventname = "";
    private String eventid = "";
    private String collegename = "";
    private Integer groupmembers;
    private String roleid = "";
    private String rolename = "";
    private Integer cnt = 0;
    private TextView lblEventData;
    private Spinner spinnerUserRole;
    private ArrayList<String> roleName = new ArrayList<>();
    private ArrayList<String> roleId = new ArrayList<>();

    ViewPager viewPagerRegistration;
    RegistrationAdapter registrationAdapter;
    List<Users> users = new ArrayList<>();

    Event event = new Event();
    UserRole userRole = new UserRole();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        lblEventData = (TextView) findViewById(R.id.lblEventData);
        spinnerUserRole = (Spinner) findViewById(R.id.spinnerUserRole);
        viewPagerRegistration = (ViewPager) findViewById(R.id.viewPagerRegistration);

        loadSpinnerUserRole();

        Intent i = getIntent();
        eventname = i.getStringExtra("eventname");
        eventid = i.getStringExtra("eventid");
        collegename = i.getStringExtra("collegename");
        groupmembers = Integer.parseInt(i.getStringExtra("grpmem"));

        for(int k=0;k<groupmembers;k++){
            users.add(new Users());
        }
        Log.e("Count: ",String.valueOf(users.size()));

        registrationAdapter = new RegistrationAdapter(users, this);

        viewPagerRegistration = findViewById(R.id.viewPagerRegistration);
        viewPagerRegistration.setAdapter(registrationAdapter);
        viewPagerRegistration.setPadding(30, 0, 30, 10);

        spinnerUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                roleid = "";
                rolename = "";
                rolename = spinnerUserRole.getItemAtPosition(spinnerUserRole.getSelectedItemPosition()).toString();
                roleid = roleId.get(i);
                if (rolename.equals("College Admin")&&eventid==null) {
                    lblEventData.setTextColor(Color.BLUE);
                    lblEventData.setText("Proceed with Registration");
                } else {
                    if(eventid==null) {
                        lblEventData.setTextColor(Color.RED);
                        lblEventData.setText("Select an EVENT from Event List");
                    }
                    else{
                        lblEventData.setTextColor(Color.BLACK);
                        lblEventData.setText("\u2022Event Name: "+eventname+"\n\u2022Organized By: "+collegename);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void loadSpinnerUserRole() {
        roleId.clear();
        roleName.clear();
        roleId.add("0");
        roleName.add("Select Role");
        dbRef = database.getReference("UserRole");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    cnt++;
                    roleId.add(childDataSnapshot.getKey());
                    roleName.add(childDataSnapshot.child("role_name").getValue().toString());
                    if(childDataSnapshot.child("role_name").getValue().toString().equals("Student")){
                        spinnerUserRole.setSelection(cnt);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        spinnerUserRole.setAdapter(new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_dropdown_item, roleName));
        cnt = 0;
    }

}
