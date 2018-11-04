package commrkjdylarge_project.github.stepwithfriends;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

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
