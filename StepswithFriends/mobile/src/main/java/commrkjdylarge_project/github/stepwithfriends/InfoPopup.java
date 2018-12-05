package commrkjdylarge_project.github.stepwithfriends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class InfoPopup extends AppCompatActivity {

    private static final String TAG = "infoPopup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int) (height*.5)); // Enter size Multiplier

        final TextView fN = findViewById(R.id.tv_first_name);
        final TextView lN = findViewById(R.id.tv_last_name);
        final TextView W = findViewById(R.id.tv_weight);
        final TextView H = findViewById(R.id.tv_height);

        final EditText eFN = findViewById(R.id.et_first_name);
        final EditText eLN = findViewById(R.id.et_last_name);
        final EditText eW = findViewById(R.id.et_weight);
        final EditText eH = findViewById(R.id.et_height);

        final Button uFN = findViewById(R.id.bt_up_fn);
        final Button uLN = findViewById(R.id.bt_up_ln);
        final Button uW = findViewById(R.id.bt_up_w);
        final Button uH = findViewById(R.id.bt_up_h);

        final Button chFN = findViewById(R.id.bt_chng_fn);
        final Button chLN = findViewById(R.id.bt_chng_ln);
        final Button chW = findViewById(R.id.bt_chng_w);
        final Button chH = findViewById(R.id.bt_chng_h);

        JSONObject info = ((SWFApp) getApplication()).getUserData("User");

        if (info != null)
        {
            try {
                fN.setText(info.get("firstName").toString());
                lN.setText(info.get("lastName").toString());
                W.setText(info.get("weight").toString());
                H.setText(info.get("height").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        uFN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fN.setVisibility(View.GONE);
                uFN.setVisibility(View.GONE);

                eFN.setVisibility(View.VISIBLE);
                chFN.setVisibility(View.VISIBLE);
            }
        });

        uLN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lN.setVisibility(View.GONE);
                uLN.setVisibility(View.GONE);

                eLN.setVisibility(View.VISIBLE);
                chLN.setVisibility(View.VISIBLE);
            }
        });

        uW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                W.setVisibility(View.GONE);
                uW.setVisibility(View.GONE);

                eW.setVisibility(View.VISIBLE);
                chW.setVisibility(View.VISIBLE);
            }
        });

        uH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                H.setVisibility(View.GONE);
                uH.setVisibility(View.GONE);

                eH.setVisibility(View.VISIBLE);
                chH.setVisibility(View.VISIBLE);
            }
        });


        // Updaters //
        chFN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eFN.getText().toString().equals(""))
                {
                }
                else
                {
                    String temp = '"' + eFN.getText().toString() + '"';
                    Log.d(TAG, "onClick: " + temp);
                    boolean res = ((SWFApp) getApplication()).updateUserData("firstName", eFN.getText().toString(),"User");
                    fN.setText(eFN.getText().toString());
                }
                fN.setVisibility(View.VISIBLE);
                uFN.setVisibility(View.VISIBLE);

                eFN.setVisibility(View.GONE);
                chFN.setVisibility(View.GONE);
                eFN.setText("");
            }
        });

        chLN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eLN.getText().toString().equals(""))
                {
                }
                else
                {
                    String temp = '"' + eLN.getText().toString() + '"';
                    Log.d(TAG, "onClick: " + temp);
                    boolean res = ((SWFApp) getApplication()).updateUserData("lastName", eLN.getText().toString(),"User");
                    lN.setText(eLN.getText().toString());
                }
                lN.setVisibility(View.VISIBLE);
                uLN.setVisibility(View.VISIBLE);

                eLN.setVisibility(View.GONE);
                chLN.setVisibility(View.GONE);
                eLN.setText("");
            }
        });

        chW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eW.getText().toString().equals("") || (eW.getText().toString().charAt(0) < '0' || eW.getText().toString().charAt(0) > '9'))
                {
                }
                else
                {
                    boolean res = ((SWFApp) getApplication()).updateUserData("weight", Integer.parseInt(eW.getText().toString()),"User");
                    W.setText(eW.getText().toString());
                }
                W.setVisibility(View.VISIBLE);
                uW.setVisibility(View.VISIBLE);

                eW.setVisibility(View.GONE);
                chW.setVisibility(View.GONE);
                eW.setText("");
            }
        });

        chH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eH.getText().toString().equals("") || (eH.getText().toString().charAt(0) < '0' || eH.getText().toString().charAt(0) > '9'))
                {
                }
                else
                {
                    boolean res = ((SWFApp) getApplication()).updateUserData("height", Integer.parseInt(eH.getText().toString()),"User");
                    H.setText(eH.getText().toString());
                }
                H.setVisibility(View.VISIBLE);
                uH.setVisibility(View.VISIBLE);

                eH.setVisibility(View.GONE);
                chH.setVisibility(View.GONE);
                eH.setText("");
            }
        });
    }

}
