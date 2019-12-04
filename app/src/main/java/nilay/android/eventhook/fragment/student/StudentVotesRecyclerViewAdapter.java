package nilay.android.eventhook.fragment.student;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import nilay.android.eventhook.R;
import nilay.android.eventhook.home.ImageRequester;
import nilay.android.eventhook.model.Event;
import nilay.android.eventhook.model.EventStudentSubmission;
import nilay.android.eventhook.model.EventTheme;
import nilay.android.eventhook.model.StudentRatingRecord;
import nilay.android.eventhook.viewmodels.StudentViewModel;

public class StudentVotesRecyclerViewAdapter extends RecyclerView.Adapter<StudentVotesRecyclerViewAdapter.ViewHolder> {

    private ImageRequester imageRequester;
    private FragmentActivity activity;
    private List<EventStudentSubmission> studentSubmissionList;
    private StudentViewModel studentViewModel;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public StudentVotesRecyclerViewAdapter(FragmentActivity activity, List<EventStudentSubmission> studentSubmissionList) {
        this.activity = activity;
        this.studentSubmissionList = studentSubmissionList;
        imageRequester = ImageRequester.getInstance();
        studentViewModel = new ViewModelProvider(activity).get(StudentViewModel.class);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_rating_layout, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventStudentSubmission studentSubmission = studentSubmissionList.get(position);

        DatabaseReference eventDbRef = database.getReference("Event").child(studentSubmission.getEvent_id());
        eventDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                assert event!=null;
                holder.lblEventName.setText(event.getEvent_name());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference themeDbRef = database.getReference("EventTheme").child(studentSubmission.getEvent_id()).child(studentSubmission.getTheme_id());
        themeDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    EventTheme theme = dataSnapshot.getValue(EventTheme.class);
                    assert theme != null;
                    holder.lblEventTheme.setText(theme.getTheme_name());
                } else {
                    holder.lblEventTheme.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageRequester.setImageFromUrl(holder.content_image, studentSubmission.getContent_url());
        holder.content_image.setOnClickListener((View v)->{
            LayoutInflater idinflater = (activity).getLayoutInflater();
            View imgView = idinflater.inflate(R.layout.enlarged_image,null);
            ImageView imgEnlargedImg = imgView.findViewById(R.id.imgEnlargedImg);
            Glide.with(activity)
                    .load(studentSubmission.getContent_url())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Animation animfadein = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
                            imgEnlargedImg.startAnimation(animfadein);
                            return false;
                        }
                    })
                    .error(R.drawable.ic_error_black_24dp)
                    .into(imgEnlargedImg);

            new AlertDialog.Builder(Objects.requireNonNull(activity))
                    .setTitle("Event Submitted Content\n")
                    .setCancelable(false)
                    .setView(imgView)
                    .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {

                    })
                    .setIcon(android.R.drawable.ic_menu_report_image)
                    .show();
        });

        holder.ratingBar.setOnRatingBarChangeListener((RatingBar ratingBar, float rating, boolean fromUser) -> {

            holder.ratings = rating;
            Toast.makeText(activity, ""+rating+" "+studentSubmission.getEvent_id(), Toast.LENGTH_SHORT).show();

        });

        holder.btnSubmit.setOnClickListener((View v)->{
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference submissionDbRef = database.getReference("EventStudentSubmission").child(studentSubmission.getSubmission_id());
            submissionDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    EventStudentSubmission submission = dataSnapshot.getValue(EventStudentSubmission.class);
                    if(submission!=null) {
                        Float ratings = submission.getRatings();
                        ratings += holder.ratings;
                        submissionDbRef.child("ratings").setValue(ratings);

                        DatabaseReference dbRatingRef = database.getReference("StudentRatingRecord").child(studentSubmission.getEvent_id());
                        StudentRatingRecord ratingRecord = new StudentRatingRecord(1,holder.ratings);
                        dbRatingRef.child(studentViewModel.getUserid()).setValue(ratingRecord);

                        studentSubmissionList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, studentSubmissionList.size());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Toast.makeText(activity, "Ratings Submitted!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return studentSubmissionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView lblEventName, lblEventTheme;
        NetworkImageView content_image;
        RatingBar ratingBar;
        MaterialButton btnSubmit;
        Float ratings = 0f;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblEventName = itemView.findViewById(R.id.lblEventName);
            lblEventTheme = itemView.findViewById(R.id.lblEventTheme);
            content_image = itemView.findViewById(R.id.content_image);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnSubmit = itemView.findViewById(R.id.btnSubmit);
        }
    }

}
