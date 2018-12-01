package commrkjdylarge_project.github.stepwithfriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ConfirmDeletePopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_friend);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int) (height*.3)); // Enter size Multiplier

        final AsyncHttpClient client = ((SWFApp) getApplication()).getClient();

        TextView text = findViewById(R.id.tv_to_delete);
        text.setText("Do you want to delete " + ListAdapter.toDelete);

        Button yesButton = findViewById(R.id.bt_confirm_delete);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = ((SWFApp) getApplicationContext()).deleteFriend(ListAdapter.toDelete);
                ListAdapter.deleted = true;
                //notifyAll();
                finish();
            }
        });

        Button noButton = (Button) findViewById(R.id.bt_deny_delete);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAdapter.deleted = false;
                //notifyAll();
                finish();
            }
        });
    }
}
