package commrkjdylarge_project.github.stepwithfriends;

import android.app.Application;
import android.provider.Settings;

import com.loopj.android.http.*;

import org.json.*;

import cz.msebera.android.httpclient.Header;

public class SWFApp extends Application {

    //////////////////////////////////////
    private boolean useLOCALHOST = false;
    //////////////////////////////////////

    private AsyncHttpClient asyncHttpClient = null;
    private SyncHttpClient syncHttpClient = null;
    private JSONObject userData_User = null;
    private volatile boolean syncStatus = false;
    private volatile JSONArray tempObject = null;
    private volatile RequestParams tempParams = null;
    private volatile int tempInt = -1;
    private String url = "https://large-project.herokuapp.com";

    public String getURL() {
        return this.url;
    }

    public AsyncHttpClient getClient() {
        if(this.asyncHttpClient == null) {
            this.asyncHttpClient = new AsyncHttpClient();
            this.syncHttpClient = new SyncHttpClient();
            PersistentCookieStore cookieStore = new PersistentCookieStore(getApplicationContext());
            this.asyncHttpClient.setCookieStore(cookieStore);
            this.syncHttpClient.setCookieStore(cookieStore);
            if(useLOCALHOST) {
                System.out.println("using localhost");
                this.url = "http://10.0.2.2:5000";
            } else {
                System.out.println("using herokuapp");
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
        this.syncStatus = false;
    }

    public JSONArray getTop100(String group) {
        RequestParams params = new RequestParams();
        params.put("group", group);
        this.tempObject = null;
        this.syncStatus = false;
        this.tempParams = params;

        new Thread(new Runnable() {
            @Override
            public void run() {
                syncHttpClient.post(url + "/gettopusers", tempParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // called when response HTTP status is "200 OK"
                        tempObject = response;
                        syncStatus = true;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        syncStatus = true;
                    }
                });
            }
        }).start();

        while(!this.syncStatus) {
            try {
                Thread.sleep(100);
            } catch(Exception e) {}
        }
        return this.tempObject;
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

    //Does what it says on the tin
    public boolean addFriend(String username) {
        this.syncStatus = false;
        RequestParams params = new RequestParams();
        params.put("username", username);
        this.asyncHttpClient.post(this.url + "/addfriend", params, new JsonHttpResponseHandler() {
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

    //Ditto
    public boolean deleteFriend(String username) {
        this.syncStatus = false;
        RequestParams params = new RequestParams();
        params.put("username", username);
        this.asyncHttpClient.post(this.url + "/removefriend", params, new JsonHttpResponseHandler() {
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

    //Search for a user, returns null if error or user doesn't exist
    public JSONArray searchUser(String username) {
        RequestParams params = new RequestParams();
        params.put("username", username);
        this.tempObject = null;
        this.syncStatus = false;
        this.tempParams = params;

        new Thread(new Runnable() {
            @Override
            public void run() {
                syncHttpClient.post(url + "/searchuser", tempParams, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // called when response HTTP status is "200 OK"
                        tempObject = response;
                        syncStatus = true;
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        syncStatus = true;
                    }
                });
            }
        }).start();

        while(!this.syncStatus) {
            try {
                Thread.sleep(100);
            } catch(Exception e) {}
        }

        return this.tempObject;
    }

    //INSTRUCTIONS FOR USING THE SESSION API
    //When joining a session, simply call joinSession(). It will return an int with a session ID if it is successful, otherwise it will return -1
    //When leaving a session, call leaveSession(). It will return true if successful, false if not
    //To get the number of users in the current user's session, call getSession(). It will return the number of users ( >= 1 ) if successful, other it will return -1

    //Joins a session, returns with session ID on success and -1 if failure
    public int joinSession() {
        RequestParams params = new RequestParams();
        this.asyncHttpClient.post(this.url + "/joinsession", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                int session_id;
                try {
                    session_id = response.getInt("session_id");
                } catch(Exception e) {
                    return;
                }
                try {
                    userData_User.put("session_id", session_id);
                } catch(Exception e) {
                    return;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });
        int id = -1;
        try {
            id = userData_User.getInt("session_id");
        } catch(Exception e) {}
        return id;
    }

    //Returns true if left session, false if error
    public boolean leaveSession() {
        this.syncStatus = false;
        RequestParams params = new RequestParams();
        this.asyncHttpClient.post(this.url + "/leavesession", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                syncStatus = true;
                try {
                    userData_User.put("session_id", null);
                } catch(Exception e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });
        return this.syncStatus;
    }

    //Returns number of users in session, returns -1 if false
    public int getSession() {
        this.tempInt = -1;
        RequestParams params = new RequestParams();
        this.asyncHttpClient.post(this.url + "/getsession", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    tempInt = response.getInt("value");
                } catch(Exception e) {}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }
        });
        return this.tempInt;
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