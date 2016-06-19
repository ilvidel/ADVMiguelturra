package org.advmiguelturra;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIDService() {
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("ADV Firebase", "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

   /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.e("ADV Firebase", "sendRegistrationToServer - Token: " + token);
    }
}
