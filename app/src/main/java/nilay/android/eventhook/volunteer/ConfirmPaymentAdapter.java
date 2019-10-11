package nilay.android.eventhook.volunteer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.Attendance;

public class ConfirmPaymentAdapter extends ArrayAdapter<Attendance> {

    List<Attendance> userList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;
    String groupname = "";

    public ConfirmPaymentAdapter(@NonNull Context context, int resource, @NonNull List<Attendance> users) {
        super(context, resource, users);
        this.context = context;
        this.resource = resource;
        this.userList = users;
    }

    public List<Attendance> getUserList() {
        return userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AttndAdapter.ViewHolder viewHolder;

        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(resource, parent, false);

            // well set up the ViewHolder
            viewHolder = new AttndAdapter.ViewHolder();
            viewHolder.lblAtGrpName = convertView.findViewById(R.id.lblAtGrpName);
            viewHolder.lblAtContName = convertView.findViewById(R.id.lblAtContName);
            viewHolder.chkContAttend = convertView.findViewById(R.id.chkContAttend);

            // store the holder with the view.
            convertView.setTag(viewHolder);

            Attendance attendance = userList.get(position);

            if(position==0)
                groupname = "";

            viewHolder.lblAtGrpName.setTypeface(null, Typeface.BOLD);

            if(attendance.getGroupname().equals("0")){
                viewHolder.lblAtGrpName.setVisibility(View.GONE);
            } else {

                if (!groupname.equals(attendance.getGroupname())) {
                    viewHolder.lblAtGrpName.setText("Group: ");
                    viewHolder.lblAtGrpName.append(attendance.getGroupname());
                }
                else
                    viewHolder.lblAtGrpName.setVisibility(View.GONE);

                groupname = attendance.getGroupname();
            }

            viewHolder.lblAtContName.setText("");
            viewHolder.lblAtContName.append(attendance.getUsername()+"\n("+attendance.getEmailid()+")");
            viewHolder.chkContAttend.setChecked(false);

            viewHolder.chkContAttend.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
                if(isChecked) {
                    attendance.setSelected("1");
                } else {
                    attendance.setSelected("0");
                }
            });

        } else{
            viewHolder = (AttndAdapter.ViewHolder) convertView.getTag();
        }

        return convertView;
    }

}
