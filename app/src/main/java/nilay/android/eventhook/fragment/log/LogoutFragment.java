package nilay.android.eventhook.fragment.log;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nilay.android.eventhook.SampleActivity;
import nilay.android.eventhook.home.HomeTwoActivity;

public class LogoutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String dateTime = dateFormat.format(c.getTime());
        String date = dateTime.substring(0, 10);
        String time = dateTime.substring(11, 19);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String logId = sharedPref.getString("logId", "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert logId != null;
        DatabaseReference dbRef = database.getReference("UserLog").child(logId);
        dbRef.child("logout_date").setValue(date);
        dbRef.child("logout_time").setValue(time);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("logId", "");
        editor.putString("userid", "");
        editor.putString("username", "");
        editor.putString("collegeid", "");
        editor.putString("roleid", "");
        editor.putString("logFlag", "0");
        editor.apply();

        Intent i = new Intent(getActivity(), SampleActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);

        return null;
    }

}
