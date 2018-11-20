package commrkjdylarge_project.github.stepwithfriends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

import org.json.JSONObject;

public class InfoPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int) (height*.8)); // Enter size Multiplier

        final TextView infoBox = findViewById(R.id.infoText);
        JSONObject info = ((SWFApp) getApplication()).getUserData("User");
        if(info == null) {
            infoBox.setText("Error retrieving user data");
        } else {
            infoBox.setText(info.toString());
        }

    }

}
