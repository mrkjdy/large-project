package commrkjdylarge_project.github.stepwithfriends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.CompoundButton;
import android.widget.Switch;

public class NotificationPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int) (height*.8)); // Enter size Multiplier

        Switch waterSwitch = (Switch) findViewById(R.id.water_switch);
        waterSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // Water notification on
                } else {
                    // Water notification off
                }
            }
        });

        Switch walkSwitch = (Switch) findViewById(R.id.walk_switch);
        walkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    // Walk notification on
                } else {
                    // Walk notification off
                }
            }
        });

        //Other notifications
    }
}
