package tpsoa.soa.com.parkings;

import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import tpsoa.soa.com.parkings.view.LoginFragment;

public class SecondActivity extends AppCompatActivity {
    private static String TAG = "SecondActivity";
    private static int FINGERPRINT_PERMISSION_REQUEST_CODE = 10;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);

        try {
            //hacemos peticion de permisos para tener comunicacion con el sensor de huellas
            requestPermissions(new String[]{
                            Manifest.permission.INTERACT_ACROSS_USERS,
                            android.Manifest.permission.USE_FINGERPRINT},
                    FINGERPRINT_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            Log.e(TAG, "No existe permisos para huella digital");
        }

        //TODO Iniciamos con el fragmento de login
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.activity_second, new LoginFragment())
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //TODO Obtenemos el valor del permiso, si es el que enviamos 10, entonces iniciamos el servicio de huellas de samsung
        if (requestCode == FINGERPRINT_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //si recibimos codigo 10, entonces tenemos permisos para usar los servicios de huella digital
            getSystemService(FingerprintManager.class);
        }
    }
}
