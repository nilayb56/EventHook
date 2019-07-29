package nilay.android.eventhook;

import androidx.appcompat.app.AppCompatActivity;
import android.animation.ArgbEvaluator;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    ViewPager viewPager;
    Adapter adapter;
    List<Model> models;
    private Button btnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        models = new ArrayList<>();
        models.add(new Model(R.drawable.coding, "Coding", "Crush your Mind, Churn your Mind and WIN!!!"));
        models.add(new Model(R.drawable.photography, "Photography", "A Picture says a Thousand Words...More the Words More you WIN!!!"));
        models.add(new Model(R.drawable.poster, "Poster", "A collection of art and drawing teching a valuable lesson...A Poster"));
        models.add(new Model(R.drawable.dance, "Culture", "What riches can buy a Culture...Bring your Culture to Life!!!"));

        adapter = new Adapter(models, this);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130, 0, 130, 0);

        btnGo = (Button)findViewById(R.id.btnGo);
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this,HomeTwoActivity.class);
                startActivity(i);
            }
        });

    }
}
