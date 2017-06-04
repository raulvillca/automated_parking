package soa.mvp.parkingfinder;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import soa.mvp.parkingfinder.presenter.VoiceRecognitionCallback;
import soa.mvp.parkingfinder.view.LoginFragment;

/**
 * Created by raulvillca on 15/5/17.
 */

public class SecondActivity extends AppCompatActivity implements VoiceRecognitionCallback {
    private static String TAG = "SecondActivity";
    private static int FINGERPRINT_PERMISSION_REQUEST_CODE = 10;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second);

        try {
            requestPermissions(new String[]{
                            Manifest.permission.INTERACT_ACROSS_USERS,
                            android.Manifest.permission.USE_FINGERPRINT},
                    FINGERPRINT_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            Log.e(TAG, "No existe permisos para huella digital");
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .add(R.id.activity_second, new LoginFragment())
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINGERPRINT_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //si recibimos codigo 10, entonces tenemos permisos para usar los servicios de huella digital
            getSystemService(FingerprintManager.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            List<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //Separo el texto en palabras.
            String words = matches.get(0).toString();
            //false indica que el texto no debe enviarse en el momento.
            Toast.makeText(this, words, Toast.LENGTH_LONG ).show();

            if (words.contains("confirmar")) {
            }

            Log.e("Mostrar palabras", words);
        }
    }

    @Override
    public void notifyRecognizer() {
        // Definición del intent para realizar en análisis del mensaje
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Indicamos el modelo de lenguaje para el intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Lanzamos la actividad esperando resultados
        startActivityForResult(intent, 1);
    }
}
