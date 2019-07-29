package nilay.android.eventhook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Button btnLike, btnDlike;
    TextView txtLike,txtDlike;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference();
    private Long likes,dlikes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent(getApplication(),HomeActivity.class);
        startActivity(i);

        btnLike = (Button) findViewById(R.id.btnLike);
        btnDlike = (Button) findViewById(R.id.btnDlike);
        txtLike = (TextView) findViewById(R.id.txtLike);
        txtDlike = (TextView) findViewById(R.id.txtDlike);

        dbRef = database.getReference("/LDCount/Likes");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes = dataSnapshot.getValue(Long.class);
                txtLike.setText(likes.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRef = database.getReference("/LDCount/Dislikes");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dlikes = dataSnapshot.getValue(Long.class);
                txtDlike.setText(dlikes.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef = database.getReference("/LDCount/Likes");
                likes++;
                dbRef.setValue(likes);
            }
        });

        btnDlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbRef = database.getReference("/LDCount/Dislikes");
                dlikes++;
                dbRef.setValue(dlikes);
            }
        });

    }
}
