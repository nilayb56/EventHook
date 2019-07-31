package nilay.android.eventhook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import nilay.android.eventhook.mainadmin.AdminActivity;
import nilay.android.eventhook.model.College;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.registration.RegistrationActivity;

public class HomeTwoActivity extends AppCompatActivity {

    private ImageView imgLogin,imageView,imgReg;
    private GridLayout gridCardView;
    private Context context;
    private CardView cardView;
    private LinearLayout.LayoutParams layoutParamsCard;
    private ViewGroup.LayoutParams layoutParamsText;
    private LinearLayout.LayoutParams layoutParamsLinear;
    private TextView textView;
    private LinearLayout linearLayout;
    private Spinner spinnerHomeClg;
    private ArrayList<String> ClgName = new ArrayList<>();
    private ArrayList<String> ClgId = new ArrayList<>();
    private String clgname="";
    private String clgid="";
    private String eventname="";
    private String eventid = "";
    private String modeprivate,groupevent="";
    private String eventdetails = "";

    Event event = new Event();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    private FirebaseStorage storage = FirebaseStorage.getInstance();;
    private StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_two);

        context = getApplicationContext();
        gridCardView = (GridLayout) findViewById(R.id.gridCardView);
        imgLogin = (ImageView)findViewById(R.id.imgLogin);
        imgReg = (ImageView)findViewById(R.id.imgReg);
        spinnerHomeClg = (Spinner) findViewById(R.id.spinnerHomeClg);
        ClgId.clear();
        ClgName.clear();
        loadSpinnerClg();

        imgReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeTwoActivity.this,RegistrationActivity.class);
                startActivity(i);
            }
        });
        imgLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeTwoActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        spinnerHomeClg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clgid="";
                clgname="";
                clgname=spinnerHomeClg.getItemAtPosition(spinnerHomeClg.getSelectedItemPosition()).toString();
                clgid=ClgId.get(i);
                fillEvent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void fillEvent() {
        eventid = "";
        eventname = "";
        gridCardView.removeAllViews();
        dbRef = database.getReference("Event");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    if(!clgid.equals("0")) {
                        if (childDataSnapshot.child("img_url").exists() && childDataSnapshot.child("college_id").getValue().toString().equals(clgid)) {
                            eventname = childDataSnapshot.child("event_name").getValue().toString();
                            eventid = childDataSnapshot.child("event_id").getValue().toString();
                            createCardView(childDataSnapshot.child("img_url").getValue().toString(), eventname, eventid);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createCardView(String img_url, String eventname, String event_id) {
        layoutParamsCard = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParamsCard.setMargins(12,12,12,12);
        layoutParamsLinear = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ViewGroup.MarginLayoutParams layoutParamsImg = new LinearLayout.LayoutParams(400, 400);
        layoutParamsText = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        cardView = new CardView(context);
        cardView.setLayoutParams(layoutParamsCard);
        cardView.setCardElevation(6);
        cardView.setRadius(12);
        cardView.setBackgroundColor(Color.parseColor("#E9F6FB"));
        cardView.setTag(event_id);
        cardView.setOnClickListener(getIdOnclickListener);

        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParamsLinear);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setPadding(16,16,16,16);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParamsImg);

        textView = new TextView(context);
        textView.setLayoutParams(layoutParamsText);
        textView.setText(eventname);
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(18);

        linearLayout.addView(imageView,0);
        Glide.with(HomeTwoActivity.this)
                .load(img_url)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
        linearLayout.addView(textView,1);
        cardView.addView(linearLayout);
        gridCardView.addView(cardView);
    }

    private View.OnClickListener getIdOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String eid = v.getTag().toString();
            dbRef = database.getReference("Event").child(eid);
            dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    event = dataSnapshot.getValue(Event.class);
                    if(event.getMode_private()==0){
                        modeprivate = "It is not a Private Event";
                    }else {
                        modeprivate = "It is a Private Event";
                    }
                    if(event.getGroup_event()==0){
                        groupevent = "It is not a Group Event";
                    }else {
                        groupevent = "It is a Group Event\n\u2022Event allows maximum "+event.getGroup_members().toString()+" Members";
                    }
                    eventdetails = event.getEvent_name()+"\n\u2022Event is Organized by "+clgname+"\n\u2022"+groupevent+"\n\u2022"+modeprivate+"\n\u2022Registrations are started from Date "+event.getReg_start_date()+"\n\u2022Last Date of Registration is "+event.getReg_end_date()+"\n\u2022Last Date of Cancelling Registration is "+event.getCancel_date()+"\n\u2022Entry Fees for the Event is \u20B9"+event.getEvent_fees()+"\n\u2022THE BIG EVENT DATE IS "+event.getEvent_date();
                    Intent i = new Intent(HomeTwoActivity.this, DetailActivity.class);
                    i.putExtra("eventid",eid);
                    i.putExtra("eventname",event.getEvent_name());
                    i.putExtra("collegename",clgname);
                    i.putExtra("grpmem",String.valueOf(event.getGroup_members()));
                    i.putExtra("param",eventdetails);
                    HomeTwoActivity.this.startActivity(i);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };


    private void loadSpinnerClg() {
        ClgId.clear();
        ClgName.clear();
        ClgId.add("0");
        ClgName.add("Select College");
        dbRef = database.getReference("College");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    ClgId.add(childDataSnapshot.getKey());
                    ClgName.add(childDataSnapshot.child("college_name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        spinnerHomeClg.setAdapter(new ArrayAdapter<String>(HomeTwoActivity.this, android.R.layout.simple_spinner_dropdown_item, ClgName));
    }
}
