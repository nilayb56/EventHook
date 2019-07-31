package nilay.android.eventhook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nilay.android.eventhook.mainadmin.AdminActivity;

public class LoginActivity extends AppCompatActivity {
    private Button btnLogin;
    private EditText txtUnm,txtPwd;
    private String userName,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        txtUnm = (EditText)findViewById(R.id.txtUserName);
        txtPwd = (EditText)findViewById(R.id.txtPwd);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = txtUnm.getText().toString();
                password = txtPwd.getText().toString();

                if(userName.length()==0){
                    txtUnm.requestFocus();
                    txtUnm.setError("FIELD CANNOT BE EMPTY");
                } else if(password.length()==0){
                    txtPwd.requestFocus();
                    txtPwd.setError("FIELD CANNOT BE EMPTY");
                } else {
                    if(userName.equals("admin")&&password.equals("123")){
                        Toast.makeText(getApplicationContext(),"Login Successful!",Toast.LENGTH_LONG).show();
                        Intent i = new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(i);
                    }
                }

            }
        });
    }
}
