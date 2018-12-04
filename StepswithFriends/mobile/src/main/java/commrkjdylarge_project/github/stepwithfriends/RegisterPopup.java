package commrkjdylarge_project.github.stepwithfriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_popup);

        //DisplayMetrics dm = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(dm);
        //int windowWidth = dm.widthPixels;
        //int windowHeight = dm.heightPixels;
        //getWindow().setLayout((int) (windowWidth*.8), (int) (windowHeight*.8)); // Enter size Multiplier
        final AsyncHttpClient client = ((SWFApp) this.getApplication()).getClient();
        Button registerButton = findViewById(R.id.register_button);
        Button backButton = findViewById(R.id.backButton);
        final TextView error = findViewById(R.id.errorBox);
        final EditText firstname = findViewById(R.id.firstnameField);
        final EditText lastname = findViewById(R.id.lastnameField);
        final EditText username = findViewById(R.id.usernameField);
        final EditText password1 = findViewById(R.id.password1Field);
        final EditText password2 = findViewById(R.id.password2Field);
        final EditText weight = findViewById(R.id.weightField);
        final EditText height = findViewById(R.id.heightField);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First name valid
                if(firstname.length() == 0) {
                    error.setText("First Name is a required field");
                } else if(firstname.getText().toString().matches("/^[a-z]{1,20}$/i")) {
                    error.setText("First Name must be no more than 20 alphabetic characters");
                // Last name valid
                } else if(lastname.length() == 0) {
                    error.setText("Last Name is a required field");
                } else if(lastname.getText().toString().matches("/^[a-z]{1,20}$/i")) {
                    error.setText("Last Name must be no more than 20 alphabetic characters");
                // Username valid
                } else if(username.length() == 0) {
                    error.setText("Username is a required field");
                } else if(username.getText().toString().matches("/^[a-z|\\d]{1,20}$/i")) {
                    error.setText("Username must be 5-20 alphanumeric characters");
                // Password valid
                } else if(password1.length() == 0 || password2.length() == 0) {
                    error.setText("Passwords field are required");
                } else if(!password1.getText().toString().matches(password2.getText().toString())) {
                    error.setText("Password fields do not match");
                // Weight valid
                } else if(weight.length() == 0) {
                    error.setText("Weight is a required field");
                // Height valid
                } else if(height.length() == 0) {
                    error.setText("Height is a required field");
                // All fields are valid, do registration
                } else {
                    RequestParams params = new RequestParams();
                    params.put("firstname", firstname.getText().toString());
                    params.put("lastname", lastname.getText().toString());
                    params.put("username", username.getText().toString());
                    params.put("password", password1.getText().toString());
                    params.put("weight", Integer.parseInt(weight.getText().toString()));
                    params.put("height", Integer.parseInt(height.getText().toString()));
                    client.post(((SWFApp) getApplication()).getURL() + "/register", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            // called before request is started
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // called when response HTTP status is "200 OK"
                            Intent intent = new Intent(RegisterPopup.this, Login.class);
                            intent.putExtra("reg_success", true);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            // 401 Unauthorized
                            if(statusCode == 401) {
                                error.setText("Unable to register account");
                            // 470: Username unavailable
                            } else if(statusCode == 470) {
                                error.setText("Username is already registered");
                            // TODO: Find solution to onSuccess bug and get rid of this temporary fix
                            } else if(statusCode == 200) {
                                Intent intent = new Intent(RegisterPopup.this, Login.class);
                                intent.putExtra("reg_success", true);
                                startActivity(intent);
                            // Other Error
                            } else {
                                error.setText("Registration is currently unavailable (Error code: " + Integer.toString(statusCode) + ")");

                            }
                        }

                        @Override
                        public void onRetry(int retryNo) {
                            // called when request is retried
                        }
                    });
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterPopup.this, Login.class));
            }
        });
    }
}
