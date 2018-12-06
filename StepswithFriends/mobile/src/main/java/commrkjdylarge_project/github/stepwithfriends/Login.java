package commrkjdylarge_project.github.stepwithfriends;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText username = findViewById(R.id.loginField);
        final EditText password = findViewById(R.id.passwordField);
        final TextView error = findViewById(R.id.errorBox);
        final AsyncHttpClient client = ((SWFApp) this.getApplication()).getClient();
        Button loginButton = findViewById(R.id.login_button);

        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null)
        {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.length() == 0) {
                    error.setText(getResources().getString(R.string.login_username_required));
                } else if(password.length() == 0) {
                    error.setText(getResources().getString(R.string.login_password_required));
                }
                else if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) == null){
                    Toast.makeText(getApplicationContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
                }
                else {
                    RequestParams params = new RequestParams();
                    params.put("username", username.getText().toString());
                    params.put("password", password.getText().toString());
                    client.post(((SWFApp) getApplication()).getURL() + "/login", params, new JsonHttpResponseHandler() {
                        // TODO: Fix bug where login fails when heroku is idle
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // called when response HTTP status is "200 OK"
                            ((SWFApp) getApplication()).getUserData("User");
                            startActivity(new Intent(Login.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            // 401 Unauthorized
                            Log.d(TAG, "onFailure: " + statusCode);
                            try {
                                error.setText(response.get("errorMessage").toString());
                            } catch (JSONException e1) {
                                e1.printStackTrace();
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
                if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) == null){
                    Toast.makeText(getApplicationContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
                }
                else{
                    startActivity(new Intent(Login.this, RegisterPopup.class));
                   // mSensorManager.unregisterListener(mStepDetectorSensor);
                }
            }
        });

        if(getIntent().getBooleanExtra("reg_success", false)) {
            error.setText("Registration successful");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
