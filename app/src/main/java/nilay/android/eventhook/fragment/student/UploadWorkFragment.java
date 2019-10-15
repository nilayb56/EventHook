package nilay.android.eventhook.fragment.student;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.fragment.registration.CommonRegistrationFragment;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.EventStudentSubmission;
import nilay.android.eventhook.model.UserParticipation;
import nilay.android.eventhook.viewmodels.StudentViewModel;

import static android.app.Activity.RESULT_OK;

public class UploadWorkFragment extends Fragment {

    private StudentViewModel studentViewModel;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int PICK_IMAGE_REQUEST = 71;
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            studentViewModel.setFilePath(data.getData());
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), studentViewModel.getFilePath());
                File file = new File(studentViewModel.getFilePath().toString());
                long fileLength = file.length();
                fileLength = fileLength / 1024;
                //Toast.makeText(getContext(), String.valueOf(fileLength), Toast.LENGTH_SHORT).show();
                studentViewModel.getImageview().setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_work, container, false);

        if(getActivity()!=null)
            studentViewModel = new ViewModelProvider(getActivity()).get(StudentViewModel.class);

        RecyclerView recyclerEventWork = view.findViewById(R.id.recyclerEventWork);
        recyclerEventWork.setLayoutManager(new LinearLayoutManager(recyclerEventWork.getContext()));
        recyclerEventWork.setAdapter(new UploadWorkRecyclerViewAdapter(getActivity(), recyclerEventWork,
                studentViewModel.getParticipations(), studentViewModel.getParticipatedEvents()));



        return view;
    }

    public class UploadWorkRecyclerViewAdapter extends RecyclerView.Adapter<UploadWorkRecyclerViewAdapter.ViewHolder> {

        private FragmentActivity mActivity;
        private RecyclerView recyclerView;
        private List<UserParticipation> participations;
        private List<Event> participatedEvents;
        private StudentViewModel studentViewModel;

        private final int PICK_IMAGE_REQUEST = 71;

        private FirebaseDatabase database = FirebaseDatabase.getInstance();
        private DatabaseReference dbRef = database.getReference();

        private FirebaseStorage storage = FirebaseStorage.getInstance();
        private StorageReference storageReference = storage.getReference();

        public UploadWorkRecyclerViewAdapter(FragmentActivity mActivity, RecyclerView recyclerView, List<UserParticipation> participations, List<Event> participatedEvents) {
            this.mActivity = mActivity;
            this.recyclerView = recyclerView;
            this.participations = participations;
            this.participatedEvents = participatedEvents;
            studentViewModel = new ViewModelProvider(mActivity).get(StudentViewModel.class);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final MaterialTextView lblEventName;
            public final ContentLoadingProgressBar progressBar;
            public final ImageView imgSelectImg;
            public final Button btnChooseImg, btnUploadImg;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lblEventName = itemView.findViewById(R.id.lblEventName);
                progressBar = itemView.findViewById(R.id.progressBar);
                imgSelectImg = itemView.findViewById(R.id.imgSelectImg);
                btnChooseImg = itemView.findViewById(R.id.btnChooseImg);
                btnUploadImg = itemView.findViewById(R.id.btnUploadImg);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.upload_work_recycler_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Event event = participatedEvents.get(position);
            UserParticipation participation = participations.get(position);

            holder.lblEventName.setText("Event Name: "+event.getEvent_name()+"\nPlease Choose the Image for Final Submission!!");

            holder.btnChooseImg.setOnClickListener((View v) -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                studentViewModel.setImageview(holder.imgSelectImg);
            });

            holder.btnUploadImg.setOnClickListener((View v) -> {
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.imgSelectImg.setVisibility(View.GONE);
                uploadImage(holder, event);
            });

        }

        private void uploadImage(ViewHolder holder, Event event) {
            if (studentViewModel.getFilePath() != null) {
                final StorageReference ref = storageReference.child("EventStudentSubmission").child(studentViewModel.getUserid());
                ref.putFile(studentViewModel.getFilePath())
                        .addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                            Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener((Uri uri) -> {
                                EventStudentSubmission submission = new EventStudentSubmission(studentViewModel.getUserid(),event.getEvent_id());
                                dbRef = database.getReference("EventStudentSubmission").child(studentViewModel.getUserid());
                                dbRef.setValue(submission);
                                holder.progressBar.setVisibility(View.GONE);
                                Fragment fragment = null;
                                Class fragmentClass = CommonRegistrationFragment.class;
                                try {
                                    fragment = (Fragment) fragmentClass.newInstance();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if(fragment!=null) {
                                    FragmentManager fragmentManager = getChildFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.flGetReg, fragment).commit();
                                }
                            });
                        })
                        .addOnFailureListener((@NonNull Exception e) -> {
                            holder.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            holder.progressBar.setVisibility(View.VISIBLE);
                            holder.progressBar.setProgress((int) progress);
                            //progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        });
            } else {
                Toast.makeText(getContext(), "Select an Image First!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            return participations.size();
        }
    }

}
