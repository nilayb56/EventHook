package nilay.android.eventhook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import nilay.android.eventhook.model.Event;

public class CollegeAdminActivity extends AppCompatActivity {

    private CardView cardAddEvent;
    private ImageView imgSelectImg;
    private Button btnAddEvent,btnChooseImg,btnUploadImg;
    private RelativeLayout rlAddEvent,rlUploadImg;
    private Spinner spinnerClg;
    private EditText txtAddEvent,txtRegStart,txtRegEnd,txtEventDate,txtGroupMembers,txtCancelDate,txtEventFees;
    private CheckBox checkPrivateEvent,checkGroupEvent;
    private ArrayList<String> ClgName = new ArrayList<>();
    private ArrayList<String> ClgID = new ArrayList<>();

    String clgName="";
    String clgid="";
    String eventname="";
    String eventid="";
    String img_url="";
    String reg_start="";
    String reg_end="";
    String event_date="";
    String cancel_date="";
    Integer mode_private=0;
    Integer group_event=0;
    Integer group_members=0;
    String event_fees="";
    Context context;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    private FirebaseStorage storage = FirebaseStorage.getInstance();;
    private StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_admin);

        context=this;

        rlAddEvent = (RelativeLayout) findViewById(R.id.rlAddEvent);
        rlUploadImg = (RelativeLayout) findViewById(R.id.rlUploadImg);

        cardAddEvent = (CardView) findViewById(R.id.cardAddEvent);

        txtAddEvent = (EditText) findViewById(R.id.txtAddEvent);
        txtRegStart = (EditText) findViewById(R.id.txtRegStart);
        txtRegEnd = (EditText) findViewById(R.id.txtRegEnd);
        txtEventDate = (EditText) findViewById(R.id.txtEventDate);
        txtCancelDate = (EditText) findViewById(R.id.txtCancelDate);
        txtGroupMembers = (EditText) findViewById(R.id.txtGroupMembers);
        txtEventFees = (EditText) findViewById(R.id.txtEventFees);

        checkPrivateEvent = (CheckBox) findViewById(R.id.checkPrivateEvent);
        checkGroupEvent = (CheckBox) findViewById(R.id.checkGroupEvent);

        spinnerClg = (Spinner) findViewById(R.id.spinnerClg);

        btnAddEvent = (Button) findViewById(R.id.btnAddEvent);
        btnChooseImg = (Button) findViewById(R.id.btnChooseImg);
        btnUploadImg = (Button) findViewById(R.id.btnUploadImg);

        imgSelectImg = (ImageView) findViewById(R.id.imgSelectImg);

        cardAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlAddEvent.setVisibility(View.VISIBLE);
                ClgID.clear();
                ClgName.clear();
                loadSpinnerClgData();
            }
        });

        txtRegStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEditTextDatePicker datePicker = new MyEditTextDatePicker(context,v.getId());
                datePicker.onClick(v);
            }
        });
        txtRegEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEditTextDatePicker datePicker = new MyEditTextDatePicker(context,v.getId());
                datePicker.onClick(v);
            }
        });
        txtEventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEditTextDatePicker datePicker = new MyEditTextDatePicker(context,v.getId());
                datePicker.onClick(v);
            }
        });
        txtCancelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyEditTextDatePicker datePicker = new MyEditTextDatePicker(context,v.getId());
                datePicker.onClick(v);
            }
        });

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        btnUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        checkPrivateEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(CollegeAdminActivity.this, "Event made Private", Toast.LENGTH_SHORT).show();
                    mode_private=1;
                }
                else{
                    Toast.makeText(CollegeAdminActivity.this, "Event made Public", Toast.LENGTH_SHORT).show();
                    mode_private=0;
                }
            }
        });

        checkGroupEvent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(CollegeAdminActivity.this, "Group Event", Toast.LENGTH_SHORT).show();
                    group_event=1;
                    txtGroupMembers.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(CollegeAdminActivity.this, "Single Person Event", Toast.LENGTH_SHORT).show();
                    group_event=0;
                    txtGroupMembers.setVisibility(View.GONE);
                    txtGroupMembers.setText("");
                }
            }
        });

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventname = txtAddEvent.getText().toString();
                reg_start = txtRegStart.getText().toString();
                reg_end = txtRegEnd.getText().toString();
                event_date = txtEventDate.getText().toString();
                cancel_date = txtCancelDate.getText().toString();
                event_fees = txtEventFees.getText().toString();
                if(!txtGroupMembers.getText().toString().equals(""))
                    group_members = Integer.parseInt(txtGroupMembers.getText().toString());
                addEvent();
                rlAddEvent.setVisibility(View.GONE);
                rlUploadImg.setVisibility(View.VISIBLE);
            }
        });

        spinnerClg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                clgName=spinnerClg.getItemAtPosition(spinnerClg.getSelectedItemPosition()).toString();
                clgid=ClgID.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void addEvent() {
        dbRef =database.getReference("Event");
        eventid = dbRef.push().getKey();
        Event event = new Event(eventid,eventname,clgid,group_event,group_members,mode_private,reg_start,reg_end,event_date,cancel_date,event_fees);
        dbRef.child(eventid).setValue(event);
        Toast.makeText(this, "Event Added", Toast.LENGTH_SHORT).show();
        clearForm();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("Events/"+clgid+"/"+eventid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CollegeAdminActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference dbupdt = database.getReference("Event").child(eventid);
                                    dbupdt.child("img_url").setValue(uri.toString());
                                    rlUploadImg.setVisibility(View.GONE);
                                    clearVar();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CollegeAdminActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                File file = new File(filePath.toString());
                long fileLength = file.length();
                fileLength = fileLength/1024;
                Toast.makeText(this, String.valueOf(fileLength), Toast.LENGTH_SHORT).show();
                imgSelectImg.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void loadSpinnerClgData() {
        ClgID.add("0");
        ClgName.add("Select College");
        dbRef = database.getReference("College");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    ClgID.add(childDataSnapshot.getKey());
                    ClgName.add(childDataSnapshot.child("college_name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        spinnerClg.setAdapter(new ArrayAdapter<String>(CollegeAdminActivity.this, android.R.layout.simple_spinner_dropdown_item, ClgName));
    }

    private void clearForm() {
        for (int i = 0, count = rlAddEvent.getChildCount(); i < count; ++i) {
            View view = rlAddEvent.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }
            if(view instanceof CheckBox){
                ((CheckBox) view).setChecked(false);
            }
            if(view instanceof Spinner){
                ((Spinner) view).setSelection(0);
            }
        }
    }

    private void clearVar() {
        clgName="";
        clgid="";
        eventname="";
        eventid="";
        img_url="";
        reg_end="";
        reg_start="";
        event_date="";
        mode_private=0;
        group_event=0;
        group_members=0;
        event_fees="";
    }
}
