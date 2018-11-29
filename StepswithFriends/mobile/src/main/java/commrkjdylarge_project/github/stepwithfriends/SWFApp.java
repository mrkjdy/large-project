package commrkjdylarge_project.github.stepwithfriends;

import android.app.Application;
import android.provider.Settings;

import com.loopj.android.http.*;

import org.json.*;

import cz.msebera.android.httpclient.Header;

public class SWFApp extends Application {

    private AsyncHttpClient asyncHttpClient = null;
    private JSONObject userData_User = null;
    private JSONObject userData_Workout = null;
    private boolean syncStatus = false;
    private JSONArray tempObject = null;
    private String url = null;

    public AsyncHttpClient getClient() {
        if(this.asyncHttpClient == null) {
            this.asyncHttpClient = new AsyncHttpClient();
            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
            this.asyncHttpClient.setCookieStore(cookieStore);
            if(Settings.Secure.getInt(this.getContentResolver(), Settings.Global.DEVELOPMENT_SETTINGS_ENABLED ,0) == 1) {
                System.out.println("using localhost");
                this.url = "http://localhost:5000";
            } else {
                System.out.println("using herokuapp");
                this.url = "https://large-project.herokuapp.com";
            }
            return this.asyncHttpClient;
        } else {
            return this.asyncHttpClient;
        }
    }

    // Use this to access the data object for a given table, will return null if invalid table or server error
    public JSONObject getUserData(String table) {
        JSONObject table_local;
        switch(table) {
            case "User":
                table_local = this.userData_User;
                break;

            case "Workout":
                table_local = this.userData_Workout;
                break;

            default:
                return null;
        }
        if(table_local == null) {
            RequestParams params = new RequestParams();
            params.put("table", table);
            this.asyncHttpClient.post(this.url + "/getuserdata", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // called when response HTTP status is "200 OK"
                    try {
                        switch(response.getString("table")) {
                            case "User":
                                userData_User = response.getJSONObject("value");
                                break;

                            case "Workout":
                                userData_Workout = response.getJSONObject("value");
                                break;
                        }
                    } catch(Exception e) {}
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }
            });
        }
        return table_local;
    }

    // WARNING: Do not use this unless you are ending your session
    public void resetUser() {
        this.asyncHttpClient = null;
        this.userData_User = null;
        this.userData_Workout = null;
        this.syncStatus = false;
    }

    public JSONArray getTop100(String group) {
        RequestParams params = new RequestParams();
        params.put("group", group);
        tempObject = null;
        try {
            this.asyncHttpClient.post(this.url + "/gettopusers", params, new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // called when response HTTP status is "200 OK"
                    tempObject = response;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }
            });
        } finally {
            return tempObject;
        }
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
            JSONObject table_local;
            switch(table) {
                case "User":
                    table_local = this.userData_User;
                    break;

                case "Workout":
                    table_local = this.userData_Workout;
                    break;

                default:
                    return false;
            }
            if(syncUserData(new String[] {field}, new Object[] {value}, table)) {
                try {
                    table_local.put(field, value);
                } catch(JSONException e) {
                    // TODO: attempt to resync local data
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
            // need a field and value of same size to update, and a table
            return false;
        } else {
            JSONObject table_local;
            switch(table) {
                case "User":
                    table_local = this.userData_User;
                    break;

                case "Workout":
                    table_local = this.userData_Workout;
                    break;

                default:
                    return false;
            }
            if(syncUserData(fields, values, table)) {
                try {
                    for(int i=0; i<fields.length; i++) {
                        table_local.put(fields[i], values[i]);
                    }
                } catch(JSONException e) {
                    // TODO: attempt to resync local data
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
        this.asyncHttpClient.post(this.url + "/updateuserdata", params, new JsonHttpResponseHandler() {
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