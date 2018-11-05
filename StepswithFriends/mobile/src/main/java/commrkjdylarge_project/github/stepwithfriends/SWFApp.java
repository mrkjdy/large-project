package commrkjdylarge_project.github.stepwithfriends;

import android.app.Application;
import com.loopj.android.http.*;

public class SWFApp extends Application {

    private AsyncHttpClient asyncHttpClient = null;
    //private

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
}