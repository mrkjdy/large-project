package commrkjdylarge_project.github.stepwithfriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = findViewById(R.id.loginField);
        final EditText password = findViewById(R.id.passwordField);
        final TextView error = findViewById(R.id.errorBox);
        final AsyncHttpClient client = ((SWFApp) this.getApplication()).getClient();
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.length() == 0) {
                    error.setText(getResources().getString(R.string.login_username_required));
                } else if(password.length() == 0) {
                    error.setText(getResources().getString(R.string.login_password_required));
                } else {
                    RequestParams params = new RequestParams();
                    params.put("username", username.getText().toString());
                    params.put("password", password.getText().toString());
                    client.post(((SWFApp) getApplication()).getURL() + "/login", params, new JsonHttpResponseHandler() {
                        // TODO: Fix bug where login fails when heroku is idle
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            // called when response HTTP status is "200 OK"
                            ((SWFApp) getApplication()).getUserData("User");
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            // 401 Unauthorized
                            if (statusCode == 401) {
                                error.setText(getResources().getString(R.string.login_invalid_field));
                                // Other Error
                            } else {
                                error.setText(getResources().getString(R.string.login_error_other));
                            }
                        }
                    });
                }
            }
        });

        Button registerButton = (findViewById(R.id.register_button));
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, RegisterPopup.class));
            }
        });

        if(getIntent().getBooleanExtra("reg_success", false)) {
            error.setText("Registration successful");
        }
    }
}
