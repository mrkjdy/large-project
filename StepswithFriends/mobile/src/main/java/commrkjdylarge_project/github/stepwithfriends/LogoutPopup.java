package commrkjdylarge_project.github.stepwithfriends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LogoutPopup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int) (height*.8)); // Enter size Multiplier

        final AsyncHttpClient client = ((SWFApp) getApplication()).getClient();
        Button yesButton = findViewById(R.id.yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.post("https://large-project.herokuapp.com/logout", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        // called when response HTTP status is "200 OK"
                        ((SWFApp) getApplication()).resetUser();
                        startActivity(new Intent(LogoutPopup.this, Login.class));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        // TODO: Fix onSuccess bug here too
                        if(statusCode == 200) {
                            ((SWFApp) getApplication()).resetUser();
                            startActivity(new Intent(LogoutPopup.this, Login.class));
                        }
                    }
                });
            }
        });

        Button noButton = (Button) findViewById(R.id.no_button);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
