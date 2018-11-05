package commrkjdylarge_project.github.stepwithfriends;

import android.app.Application;
import android.content.Intent;

import com.loopj.android.http.*;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SWFApp extends Application {

    private AsyncHttpClient asyncHttpClient = null;
    private JSONObject userData = null;

    public AsyncHttpClient getClient() {
        if(this.asyncHttpClient == null) {
            this.asyncHttpClient = new AsyncHttpClient();
            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
            this.asyncHttpClient.setCookieStore(cookieStore);
            return this.asyncHttpClient;
        } else {
            return this.asyncHttpClient;
        }
    }

    public void setUserData(JSONObject data) {
        this.userData = data;
    }

    public JSONObject getUserData() {
        if(this.userData == null) {
            this.asyncHttpClient.post("https://large-project.herokuapp.com/getuserdata", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // called when response HTTP status is "200 OK"
                    userData = response;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }
            });
        }
        return this.userData;
    }
}