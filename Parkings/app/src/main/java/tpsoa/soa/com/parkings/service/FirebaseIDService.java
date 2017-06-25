package tpsoa.soa.com.parkings.service;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //TODO se genera un token y se registra en firebase, y desde este lado
        //Podemos ver cual fue el token que fue registrado o guardarlos
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, token);
    }
}
