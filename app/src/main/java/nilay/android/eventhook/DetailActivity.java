package nilay.android.eventhook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nilay.android.eventhook.registration.RegistrationActivity;

public class DetailActivity extends AppCompatActivity {

    String eventid="";
    String eventname="";
    String collegename="";
    String grpmem="";
    String param="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button btnEventReg = findViewById(R.id.btnEventReg);
        TextView textView = findViewById(R.id.textView);

        Intent intent = getIntent();
        param = intent.getStringExtra("param");
        eventid = intent.getStringExtra("eventid");
        eventname = intent.getStringExtra("eventname");
        collegename = intent.getStringExtra("collegename");
        grpmem = intent.getStringExtra("grpmem");

        textView.setText("");
        textView.setText(param);

        btnEventReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, RegistrationActivity.class);
                i.putExtra("eventid",eventid);
                i.putExtra("eventname",eventname);
                i.putExtra("collegename",collegename);
                i.putExtra("grpmem",grpmem);
                startActivity(i);
            }
        });
    }
}
