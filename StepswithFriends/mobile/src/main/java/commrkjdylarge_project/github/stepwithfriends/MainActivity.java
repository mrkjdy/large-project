package commrkjdylarge_project.github.stepwithfriends;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId())
                {
                    case R.id.action_home:
                        Toast.makeText(MainActivity.this, "Action Home Click", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "Action Settings Click", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_walk:
                        Toast.makeText(MainActivity.this, "Action Walk Click", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_leaderboard:
                        Toast.makeText(MainActivity.this, "Action Leaderboard Click", Toast.LENGTH_SHORT).show();
                        break;
                }
            return true ;
            }
        });

        // Example of a call to a native method
        // TextView tv = (TextView) findViewById(R.id.sample_text);
        // tv.setText(stringFromJNI());

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
