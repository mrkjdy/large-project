package commrkjdylarge_project.github.stepwithfriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mainFrame;
    private SettingsFragment settingsFrame;
    private HomeFragment homeFrame;
    private WalkFragment walkFrame;
    private LeaderboardFragment leaderboardFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final AsyncHttpClient client = ((SWFApp) this.getApplication()).getClient();

        mainFrame = findViewById(R.id.main_frame);
        settingsFrame = new SettingsFragment();
        homeFrame = new HomeFragment();
        walkFrame = new WalkFragment();
        leaderboardFrame = new LeaderboardFragment();

        setFragment(homeFrame);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.action_home:
                        setFragment(homeFrame);
                        break;
                    case R.id.action_settings:
                        setFragment(settingsFrame);
                        break;
                    case R.id.action_walk:
                        setFragment(walkFrame);
                        break;
                    case R.id.action_leaderboard:
                        setFragment(leaderboardFrame);
                        break;
                }
                return true ;
            }
        });

        Button testButton = findViewById(R.id.button_test);
        final TextView testText = findViewById(R.id.text_test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.post("https://large-project.herokuapp.com/getuserdata", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // called before request is started
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // called when response HTTP status is "200 OK"
                        testText.setText(response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        // 401 Unauthorized
                        testText.setText("Error code " + statusCode);
                    }

                    @Override
                    public void onRetry(int retryNo) {
                        // called when request is retried
                    }
                });
            }
        });
    }

    private void setFragment(android.support.v4.app.Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
