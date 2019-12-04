package nilay.android.eventhook.home;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;

import nilay.android.eventhook.Adapter;
import nilay.android.eventhook.Model;
import nilay.android.eventhook.R;
import nilay.android.eventhook.SampleActivity;

public class HomeActivity extends AppCompatActivity {

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    private Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        models = new ArrayList<>();
        models.add(new Model(R.drawable.coding, "Coding", "Crush your Mind, Churn your Mind and WIN!!!"));
        models.add(new Model(R.drawable.photography, "Photography", "A Picture says a Thousand Words...More the Words More you WIN!!!"));
        models.add(new Model(R.drawable.poster, "Poster", "A collection of art and drawing teching a valuable lesson...A Poster"));
        models.add(new Model(R.drawable.dance, "Culture", "What riches can buy a Culture...Bring your Culture to Life!!!"));

        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener((View view) -> {
            boolean ifOnline = isOnline();
            if (true) {
                Intent i = new Intent(HomeActivity.this, SampleActivity.class);
                startActivity(i);
            } else {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("No INTERNET Connection\n")
                        .setMessage("\n\nPlease Check Your Mobile Data or Wi-Fi Connection first!!!")
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.yes, (DialogInterface dialog, int which) -> {

                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

    }

    public boolean isOnline() {
        try {
            int timeoutMs = 2500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeoutMs);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
