package nilay.android.eventhook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nilay.android.eventhook.model.College;

public class AdminIndexActivity extends AppCompatActivity {
    private CardView cardAddClg;
    private ImageView imgAddClg;
    private Button btnAddClg;
    private RelativeLayout rlAddClg;
    private EditText txtAddClg;
    private ArrayList<String> ClgName = new ArrayList<>();
    private ArrayList<String> ClgID = new ArrayList<>();
    String clgName="";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_index);

        rlAddClg = (RelativeLayout) findViewById(R.id.rlAddClg);
        cardAddClg = (CardView) findViewById(R.id.cardAddClg);
        imgAddClg = (ImageView) findViewById(R.id.imgAddClg);
        txtAddClg = (EditText) findViewById(R.id.txtAddClg);

        btnAddClg = (Button) findViewById(R.id.btnAddClg);

        cardAddClg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlAddClg.setVisibility(View.VISIBLE);
            }
        });

        imgAddClg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlAddClg.setVisibility(View.VISIBLE);
            }
        });

        btnAddClg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clgName = txtAddClg.getText().toString();
                addCollege();
            }
        });

    }

    private void addCollege() {
        dbRef =database.getReference("College");
        String id = dbRef.push().getKey();
        College college = new College(id,clgName);
        dbRef.child(id).setValue(college);
        txtAddClg.setText("");
        clgName="";
        Toast.makeText(this, "College Added", Toast.LENGTH_SHORT).show();
        rlAddClg.setVisibility(View.GONE);
    }

}
