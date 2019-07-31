package nilay.android.eventhook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nilay.android.eventhook.registration.RegistrationActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Button btnEventReg = findViewById(R.id.btnEventReg);
        TextView textView = findViewById(R.id.textView);

        textView.setText("");
        textView.setText(getIntent().getStringExtra("param"));

        btnEventReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, RegistrationActivity.class);
                i.putExtra("eventid",getIntent().getStringExtra("eventid"));
                i.putExtra("eventname",getIntent().getStringExtra("eventname"));
                i.putExtra("collegename",getIntent().getStringExtra("collegename"));
                startActivity(i);
            }
        });
    }
}
