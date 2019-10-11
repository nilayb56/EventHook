package nilay.android.eventhook.mainadmin;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.ApproveUser;
import nilay.android.eventhook.viewmodels.AppAdminViewModel;
import nilay.android.eventhook.volunteer.AttndAdapter;

public class ApproveClgAdminAdapter extends ArrayAdapter<ApproveUser> {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    List<ApproveUser> clgAdminList;
    FragmentActivity activity;
    int resource;

    AppAdminViewModel adminViewModel;

    public ApproveClgAdminAdapter(@NonNull FragmentActivity activity, int resource, @NonNull List<ApproveUser> clgAdminList) {
        super(activity, resource, clgAdminList);
        this.activity = activity;
        this.resource = resource;
        this.clgAdminList = clgAdminList;
    }

    private class ViewHolder {
        TextView lblUserClg;
        TextView lblAprvUserName;
        ImageView imgApproveUser;
        ImageView imgRejectUser;
        ImageView imgViewId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView==null) {
            // inflate the layout
            LayoutInflater inflater = (activity).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);

            // well set up the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.lblUserClg = convertView.findViewById(R.id.lblUserClg);
            viewHolder.lblAprvUserName = convertView.findViewById(R.id.lblAprvUserName);
            viewHolder.imgApproveUser = convertView.findViewById(R.id.imgApproveUser);
            viewHolder.imgRejectUser = convertView.findViewById(R.id.imgRejectUser);
            viewHolder.imgViewId = convertView.findViewById(R.id.imgViewId);

            if(clgAdminList.size()==0){
                Toast.makeText(activity, "No College Admin Registered", Toast.LENGTH_SHORT).show();
            }

            if(activity!=null){
                adminViewModel = new ViewModelProvider(activity).get(AppAdminViewModel.class);
            }

            ApproveUser clgAdmin = clgAdminList.get(position);

            viewHolder.lblUserClg.setVisibility(View.VISIBLE);
            viewHolder.lblUserClg.setText(clgAdmin.getClg_name());
            viewHolder.lblAprvUserName.setText(clgAdmin.getUser_name());
            viewHolder.lblAprvUserName.append("\n("+clgAdmin.getEmail_id()+")");
            viewHolder.imgViewId.setVisibility(View.VISIBLE);
            Glide.with(activity)
                    .load(clgAdmin.getImg_url())
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                            viewHolder.imgViewId.startAnimation(animfadein);
                            return false;
                        }
                    })
                    .error(R.drawable.ic_error_black_24dp)
                    .override(30,30)
                    .into(viewHolder.imgViewId);

            viewHolder.imgViewId.setOnClickListener( (View v) -> {
                LayoutInflater idinflater = (activity).getLayoutInflater();
                View imgView = idinflater.inflate(R.layout.enlarged_image,null);
                ImageView imgEnlargedImg = imgView.findViewById(R.id.imgEnlargedImg);
                Glide.with(activity)
                        .load(clgAdmin.getImg_url())
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Animation animfadein = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
                                imgEnlargedImg.startAnimation(animfadein);
                                return false;
                            }
                        })
                        .error(R.drawable.ic_error_black_24dp)
                        .into(imgEnlargedImg);

                new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                        .setTitle("Identity Card\n")
                        .setCancelable(false)
                        .setView(imgView)
                        .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {

                        })
                        .setIcon(android.R.drawable.ic_menu_report_image)
                        .show();
            });

            viewHolder.imgApproveUser.setOnClickListener( (View v) -> {
                dbRef = database.getReference("CollegeAdmin").child(clgAdmin.getUser_id());
                dbRef.child("valid_user").setValue("1");
                adminViewModel.getStateChange().setValue("1");
                Toast.makeText(activity, "Approved", Toast.LENGTH_SHORT).show();
            });

            viewHolder.imgRejectUser.setOnClickListener( (View v) -> {
                DatabaseReference dbref = database.getReference("Users");
                dbref.child(clgAdmin.getUser_id()).removeValue();
                clgAdminList.remove(position);

                adminViewModel.getStateChange().setValue("0");
                Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
            });

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

}
