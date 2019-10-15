package nilay.android.eventhook.fragment.collegeadmin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import nilay.android.eventhook.AddListenerOnTextChange;
import nilay.android.eventhook.MyEditTextDatePicker;
import nilay.android.eventhook.R;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.viewmodels.ClgAdminViewModel;

import static android.app.Activity.RESULT_OK;

public class AddEventFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView lblClgData;
    private LinearLayout linearAddEvent;
    private RelativeLayout rlUploadImg;
    private ImageView imgSelectImg;
    private TextInputLayout txtEventNameLayout, txtRegStartLayout, txtGrpMaxLayout, txtGrpMinLayout, txtRegEndLayout, txtEventDateLayout, txtCancelDateLayout, txtEventFeesLayout;
    private TextInputEditText txtAddEvent, txtRegStart, txtRegEnd, txtEventDate, txtGroupMembers, txtMinGroupMembers, txtCancelDate, txtEventFees;
    private CheckBox checkGroupEvent, checkUploadWork;
    private ProgressBar progressBar;
    private MaterialButton btnAddEvent, btnChooseImg, btnUploadImg;

    private String collegename = "";
    private String collegeid = "";
    private String eventname = "";
    private String eventid = "";
    private String img_url = "";
    private String reg_start = "";
    private String reg_end = "";
    private String event_date = "";
    private String cancel_date = "";
    private Integer upload_work = 0;
    private Integer group_event = 0;
    private Integer group_members = 0;
    private Integer mingroup_members = 0;
    private String event_fees = "";
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private ClgAdminViewModel clgAdminViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    public AddEventFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static AddEventFragment newInstance(String param1, String param2) {
        AddEventFragment fragment = new AddEventFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        view.startAnimation(animfadein);

        if (getActivity() != null) {
            clgAdminViewModel = new ViewModelProvider(getActivity()).get(ClgAdminViewModel.class);
            collegeid = clgAdminViewModel.getCollegeid();
            collegename = clgAdminViewModel.getCollegename();
        }

        lblClgData = view.findViewById(R.id.lblClgData);
        lblClgData.setText(collegename);
        rlUploadImg = view.findViewById(R.id.rlUploadImg);
        linearAddEvent = view.findViewById(R.id.linearAddEvent);

        txtEventNameLayout = view.findViewById(R.id.txtEventNameLayout);
        txtGrpMaxLayout = view.findViewById(R.id.txtGrpMaxLayout);
        txtGrpMinLayout = view.findViewById(R.id.txtGrpMinLayout);
        txtRegStartLayout = view.findViewById(R.id.txtRegStartLayout);
        txtRegEndLayout = view.findViewById(R.id.txtRegEndLayout);
        txtEventDateLayout = view.findViewById(R.id.txtEventDateLayout);
        txtCancelDateLayout = view.findViewById(R.id.txtCancelDateLayout);
        txtEventFeesLayout = view.findViewById(R.id.txtEventFeesLayout);

        txtAddEvent = view.findViewById(R.id.txtAddEvent);
        txtRegStart = view.findViewById(R.id.txtRegStart);
        txtRegEnd = view.findViewById(R.id.txtRegEnd);
        txtEventDate = view.findViewById(R.id.txtEventDate);
        txtCancelDate = view.findViewById(R.id.txtCancelDate);
        txtGroupMembers = view.findViewById(R.id.txtGroupMembers);
        txtMinGroupMembers = view.findViewById(R.id.txtMinGroupMembers);
        txtEventFees = view.findViewById(R.id.txtEventFees);

        checkUploadWork = view.findViewById(R.id.checkUploadWork);
        checkGroupEvent = view.findViewById(R.id.checkGroupEvent);
        progressBar = view.findViewById(R.id.progressBar);

        btnAddEvent = view.findViewById(R.id.btnAddEvent);
        btnChooseImg = view.findViewById(R.id.btnChooseImg);
        btnUploadImg = view.findViewById(R.id.btnUploadImg);

        imgSelectImg = view.findViewById(R.id.imgSelectImg);

        txtGroupMembers.setText("");
        txtMinGroupMembers.setText("");

        txtRegStart.setOnClickListener((View v) -> {
            MyEditTextDatePicker datePicker = new MyEditTextDatePicker(getContext(), v.getId());
            datePicker.onClick(v);
        });
        txtRegEnd.setOnClickListener((View v) -> {
            MyEditTextDatePicker datePicker = new MyEditTextDatePicker(getContext(), v.getId());
            datePicker.onClick(v);
        });
        txtEventDate.setOnClickListener((View v) -> {
            MyEditTextDatePicker datePicker = new MyEditTextDatePicker(getContext(), v.getId());
            datePicker.onClick(v);
        });
        txtCancelDate.setOnClickListener((View v) -> {
            MyEditTextDatePicker datePicker = new MyEditTextDatePicker(getContext(), v.getId());
            datePicker.onClick(v);
        });

        btnChooseImg.setOnClickListener((View v) -> {
            chooseImage();
        });

        btnUploadImg.setOnClickListener((View v) -> {
            progressBar.setVisibility(View.VISIBLE);
            imgSelectImg.setVisibility(View.GONE);
            btnChooseImg.setVisibility(View.GONE);
            btnUploadImg.setVisibility(View.GONE);
            uploadImage();
        });

        checkUploadWork.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Final Submission to be Uploaded!", Toast.LENGTH_LONG).show();
                upload_work = 1;

            } else {
                Toast.makeText(getContext(), "NO Content to be Uploaded!", Toast.LENGTH_LONG).show();
                upload_work = 0;
            }
        });

        checkGroupEvent.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "Group Event", Toast.LENGTH_SHORT).show();
                group_event = 1;
                txtGrpMaxLayout.setVisibility(View.VISIBLE);
                txtGrpMinLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(getContext(), "Single Person Event", Toast.LENGTH_SHORT).show();
                group_event = 0;
                group_members=0;
                mingroup_members = 0;
                txtGrpMaxLayout.setVisibility(View.GONE);
                txtGroupMembers.setText("");
                txtGrpMinLayout.setVisibility(View.GONE);
                txtMinGroupMembers.setText("");
            }
        });

        txtAddEvent.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtAddEvent, txtEventNameLayout, "^[\\p{L} .'-]+$", "ENTER ONLY ALPHABETS"));
        txtGroupMembers.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtGroupMembers, txtGrpMaxLayout, "^[0-9]*$", "ENTER ONLY NUMBERS AND NO DECIMAL"));
        txtMinGroupMembers.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtMinGroupMembers, txtGrpMinLayout, "^[0-9]*$", "ENTER ONLY NUMBERS AND NO DECIMAL"));
        txtRegStart.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtRegStart, txtRegStartLayout));
        txtRegEnd.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtRegEnd, txtRegEndLayout));
        txtEventDate.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtEventDate, txtEventDateLayout));
        txtCancelDate.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtCancelDate, txtCancelDateLayout));
        txtEventFees.addTextChangedListener(new AddListenerOnTextChange(getContext(),txtEventFees, txtEventFeesLayout, "^[0-9]*$", "ENTER ONLY NUMBERS AND NO DECIMAL"));

        btnAddEvent.setOnClickListener((View v) -> {
            eventname = txtAddEvent.getText().toString();
            reg_start = txtRegStart.getText().toString();
            reg_end = txtRegEnd.getText().toString();
            event_date = txtEventDate.getText().toString();
            cancel_date = txtCancelDate.getText().toString();
            event_fees = txtEventFees.getText().toString();
            String maxMem = txtGroupMembers.getText().toString();
            String minMem = txtMinGroupMembers.getText().toString();
            if (!txtGroupMembers.getText().toString().equals(""))
                group_members = Integer.parseInt(maxMem);
            if (!txtMinGroupMembers.getText().toString().equals(""))
                mingroup_members = Integer.parseInt(minMem);

            if (eventname.equals("")) {
                txtAddEvent.requestFocus();
                txtEventNameLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!eventname.matches("^[\\p{L} .'-]+$")) {
                txtAddEvent.requestFocus();
                txtEventNameLayout.setError("ENTER ONLY ALPHABETS");
            } else if (reg_start.equals("")) {
                txtRegStart.requestFocus();
                txtRegStartLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (reg_end.equals("")) {
                txtRegEnd.requestFocus();
                txtRegEndLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (event_date.equals("")) {
                txtEventDate.requestFocus();
                txtEventDateLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (cancel_date.equals("")) {
                txtCancelDate.requestFocus();
                txtCancelDateLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (event_fees.equals("")) {
                txtEventFees.requestFocus();
                txtEventFeesLayout.setError("FIELD CANNOT BE EMPTY");
            } else if (!event_fees.matches("^[0-9]*$")) {
                txtEventFees.requestFocus();
                txtEventFeesLayout.setError("ENTER ONLY NUMBERS AND NO DECIMAL");
            } else {
                if(group_event==1) {
                    if(maxMem.equals("")) {
                        txtGroupMembers.requestFocus();
                        txtGrpMaxLayout.setError("FIELD CANNOT BE EMPTY");
                    } else if(maxMem.matches("^[0-9]*$")) {
                        txtGroupMembers.requestFocus();
                        txtGrpMaxLayout.setError("ENTER ONLY NUMBERS AND NO DECIMAL");
                    } else if(minMem.equals("")) {
                        txtMinGroupMembers.requestFocus();
                        txtGrpMinLayout.setError("FIELD CANNOT BE EMPTY");
                    } else if(minMem.matches("^[0-9]*$")) {
                        txtMinGroupMembers.requestFocus();
                        txtGrpMinLayout.setError("ENTER ONLY NUMBERS AND NO DECIMAL");
                    } else {
                        addEvent();
                        linearAddEvent.setVisibility(View.GONE);
                        rlUploadImg.setVisibility(View.VISIBLE);
                    }
                } else {
                    addEvent();
                    linearAddEvent.setVisibility(View.GONE);
                    rlUploadImg.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private void addEvent() {
        dbRef = database.getReference("Event");
        eventid = dbRef.push().getKey();
        Event event = new Event(eventid, eventname, collegeid, upload_work, group_event, group_members, mingroup_members, reg_start, reg_end, event_date, cancel_date, event_fees);
        dbRef.child(eventid).setValue(event);
        Toast.makeText(getContext(), "Event Added", Toast.LENGTH_SHORT).show();
        //clearForm();
    }

    private void clearForm() {
        for (int i = 0, count = linearAddEvent.getChildCount(); i < count; ++i) {
            View view = linearAddEvent.getChildAt(i);
            if (view instanceof TextInputEditText) {
                ((TextInputEditText) view).setText("");
            }
            if (view instanceof CheckBox) {
                ((CheckBox) view).setChecked(false);
            }
        }
        /*Fragment fragment = null;
        Class fragmentClass = AddEventFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(rlUploadImg.getId(), fragment).commit();
        }*/
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if (filePath != null) {
            final StorageReference ref = storageReference.child("Events/" + collegeid + "/" + eventid);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference dbupdt = database.getReference("Event").child(eventid);
                                    dbupdt.child("img_url").setValue(uri.toString());
                                    progressBar.setVisibility(View.GONE);
                                    rlUploadImg.setVisibility(View.GONE);
                                    linearAddEvent.setVisibility(View.VISIBLE);
                                    clearForm();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress((int) progress);
                            //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Select an Image First!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), filePath);
                File file = new File(filePath.toString());
                long fileLength = file.length();
                fileLength = fileLength / 1024;
                //Toast.makeText(getContext(), String.valueOf(fileLength), Toast.LENGTH_SHORT).show();
                imgSelectImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearVar() {
        collegeid = "";
        collegename = "";
        eventname = "";
        eventid = "";
        img_url = "";
        reg_end = "";
        reg_start = "";
        event_date = "";
        group_event = 0;
        group_members = 0;
        mingroup_members = 0;
        event_fees = "";
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
    public void onResume() {
        super.onResume();
        clgAdminViewModel = ViewModelProviders.of(getActivity()).get(ClgAdminViewModel.class);
        collegeid = clgAdminViewModel.getCollegeid();
        collegename = clgAdminViewModel.getCollegename();
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
