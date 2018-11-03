package commrkjdylarge_project.github.stepwithfriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.*;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final AsyncHttpClient client = new AsyncHttpClient();
        final EditText username = (EditText) findViewById(R.id.loginField);
        final EditText password = (EditText) findViewById(R.id.passwordField);
        final TextView error = (TextView) findViewById(R.id.errorBox);
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PersistentCookieStore cookieStore = new PersistentCookieStore(Login.this);
                client.setCookieStore(cookieStore);
                RequestParams params = new RequestParams();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                client.post("https://", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // called when response HTTP status is "200 OK"
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }

                    //@Override
                    public void onFailure(int statusCode, Header[] headers, JSONObject errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        // 401 Unauthorized
                        if(statusCode == 401) {
                            error.setText("Username or Password is not valid");
                        // Other Error
                        } else {
                            error.setText("Login is currently unavailable");
                        }
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }
                });
            }
        });

        Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, RegisterPopup.class));
            }
        });
    }
}
