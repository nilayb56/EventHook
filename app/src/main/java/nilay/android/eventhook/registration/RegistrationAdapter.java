package nilay.android.eventhook.registration;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import nilay.android.eventhook.R;
import nilay.android.eventhook.model.Users;

public class RegistrationAdapter extends PagerAdapter {

    private List<Users> users;
    private LayoutInflater layoutInflater;
    private Context context;

    public RegistrationAdapter(List<Users> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.registration, container, false);

        EditText txtUserName,txtEmailid,txtUserPwd;
        Button btnUserReg;

        txtUserName = view.findViewById(R.id.txtUserName);
        txtEmailid = view.findViewById(R.id.txtEmailid);
        txtUserPwd = view.findViewById(R.id.txtUserPwd);
        btnUserReg = view.findViewById(R.id.btnUserReg);

        /*txtUserName.setText(users.get(position).getUser_name());
        txtEmailid.setText(users.get(position).getEmail_id());
        txtUserPwd.setText(users.get(position).getPassword());*/

        btnUserReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("param", users.get(position).getUser_name());
                context.startActivity(intent);*/
                // finish();
            }
        });
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
