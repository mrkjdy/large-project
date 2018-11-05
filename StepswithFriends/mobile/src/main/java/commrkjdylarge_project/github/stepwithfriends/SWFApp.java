package commrkjdylarge_project.github.stepwithfriends;

import android.app.Application;
import android.content.Intent;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SWFApp extends Application {

    private AsyncHttpClient asyncHttpClient = null;
    private JSONObject userData = null;
    private boolean syncStatus = false;

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

    // WARNING: Do not use this unless you are logging in, to update user data use updateUserData()
    public void setUserData(JSONObject data) {
        this.userData = data;
    }

    public JSONObject getUserData() {
        // Gets user data if it somehow wasn't initialized
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

    public void resetUser() {
        this.asyncHttpClient = null;
        this.userData = null;
    }

    // TO UPDATE/SYNC USER DATA:
    // Call updateUserData(fields, values, table);
    // In "fields", put a string or array of strings representing the fields being updated
    // In "values", put an object or array of objects representing the values being updated
    // NOTE: order of values must match order of fields
    // In "table", put a string naming the table being updated
    // Function will return true if data successfully synced, false if not
    //
    // Used to update one user value
    public boolean updateUserData(String field, Object value, String table) {
        if(field == null || value == null || table == null) {
            // need a field and value to update
            return false;
        } else {
            if(syncUserData(new String[] {field}, new Object[] {value}, table)) {
                try {
                    this.userData.put(field, value);
                } catch(JSONException e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    // Used to update multiple user values
    public boolean updateUserData(String[] fields, Object[] values, String table) {
        if(fields == null || values == null || fields.length != values.length || table == null) {
            // need a field and value of same size to update
            return false;
        } else {
            if(syncUserData(fields, values, table)) {
                try {
                    for(int i=0; i<fields.length; i++) {
                        this.userData.put(fields[i], values[i]);
                    }
                } catch(JSONException e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean syncUserData(Object[] fields, Object[] values, String table) {
        this.syncStatus = false;
        RequestParams params = new RequestParams();
        params.put("fields", fields);
        params.put("values", values);
        params.put("table", table);
        this.asyncHttpClient.post("https://large-project.herokuapp.com/updateuserdata", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                syncStatus = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                syncStatus = false;
            }
        });
        return this.syncStatus;
    }


}