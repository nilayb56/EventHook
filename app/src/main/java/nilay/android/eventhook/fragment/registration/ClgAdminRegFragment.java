package nilay.android.eventhook.fragment.registration;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.CollegeAdmin;
import nilay.android.eventhook.viewmodels.RegistrationViewModel;

import static android.app.Activity.RESULT_OK;

public class ClgAdminRegFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button btnChooseImg, btnUploadImg;
    private ImageView imgSelectImg;
    private ProgressBar progressBar;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private RegistrationViewModel registrationViewModel;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();

    public ClgAdminRegFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static ClgAdminRegFragment newInstance(String param1, String param2) {
        ClgAdminRegFragment fragment = new ClgAdminRegFragment();
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
        View view = inflater.inflate(R.layout.fragment_clg_admin_reg, container, false);

        imgSelectImg = view.findViewById(R.id.imgSelectImg);
        btnChooseImg = view.findViewById(R.id.btnChooseImg);
        btnUploadImg = view.findViewById(R.id.btnUploadImg);
        progressBar = view.findViewById(R.id.progressBar);

        if (getActivity() != null)
            registrationViewModel = new ViewModelProvider(getActivity()).get(RegistrationViewModel.class);

        btnChooseImg.setOnClickListener((View v) -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        btnUploadImg.setOnClickListener((View v) -> {
            progressBar.setVisibility(View.VISIBLE);
            imgSelectImg.setVisibility(View.GONE);
            uploadImage();
        });

        return view;
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

    private void uploadImage() {
        if (filePath != null) {
            final StorageReference ref = storageReference.child("IDCard").child(registrationViewModel.getUserid());
            ref.putFile(filePath)
                    .addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                        Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener((Uri uri) -> {
                            CollegeAdmin collegeAdmin = new CollegeAdmin(registrationViewModel.getUserid(), registrationViewModel.getUsername(), registrationViewModel.getEmail_id(), registrationViewModel.getCollegename(), uri.toString());
                            dbRef = database.getReference("CollegeAdmin").child(registrationViewModel.getUserid());
                            dbRef.setValue(collegeAdmin);
                            progressBar.setVisibility(View.GONE);
                            Fragment fragment = null;
                            Class fragmentClass = CommonRegistrationFragment.class;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if(fragment!=null) {
                                FragmentManager fragmentManager = getFragmentManager();
                                if(fragmentManager!=null)
                                    fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment).commit();
                            }
                        });
                    })
                    .addOnFailureListener((@NonNull Exception e) -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress((int) progress);
                        //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    });
        } else {
            Toast.makeText(getContext(), "Select an Image First!", Toast.LENGTH_SHORT).show();
        }
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
