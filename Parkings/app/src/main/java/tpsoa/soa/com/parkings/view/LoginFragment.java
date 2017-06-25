package tpsoa.soa.com.parkings.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import tpsoa.soa.com.parkings.MainActivity;
import tpsoa.soa.com.parkings.R;
import tpsoa.soa.com.parkings.model.User;
import tpsoa.soa.com.parkings.presenter.FingerPresenter;
import tpsoa.soa.com.parkings.refactor.ParkingFirebase;
import tpsoa.soa.com.parkings.service.ParkingFirebaseRequest;

public class LoginFragment extends Fragment implements View.OnClickListener,
        FingerPresenter.View<Boolean>,
        ParkingFirebaseRequest.UserFirebaseResponse<User> {
    public static String TAG = "LoginFragment";

    private Button buttonLogin;
    private TextView textViewRegister;
    private TextView textViewRegisterFingerPrint;
    private IFingerPresenter presenter;
    private ParkingFirebase firebase;
    private FingerprintPopup popup;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebase = new ParkingFirebase(this);
        firebase.connect();
        firebase.setUserFirebase(this);
        firebase.getUserList();

        buttonLogin = (Button) getActivity().findViewById(R.id.fragment_login_button_login);
        textViewRegister = (TextView) getActivity().findViewById(R.id.fragment_login_text_register);
        textViewRegisterFingerPrint = (TextView) getActivity().findViewById(R.id.fragment_login_text_change_login);

        buttonLogin.setOnClickListener(this);
        textViewRegister.setOnClickListener(this);
        textViewRegisterFingerPrint.setOnClickListener(this);

        //TODO iniciamos la escucha del sensor de huellas
        try {
            presenter = new IFingerPresenter(this);
            //Registramos el broadcast para recibir mensajes del sistema de huellas de samsung
            presenter.registerBroadcastReceiver();
        } catch (Exception e) {
            presenter = null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_login_button_login:
                //TODO obtenemos los datos cargados y verificamos que coincida con la informacion
                //registrada en firebase

                EditText editText_username = (EditText) getActivity().findViewById(R.id.fragment_login_edit_username);
                EditText editText_pass = (EditText) getActivity().findViewById(R.id.fragment_login_edit_pass);

                TextInputLayout layout_username = (TextInputLayout) getActivity().findViewById(R.id.fragment_layout_login_edit_username);
                TextInputLayout layout_pass = (TextInputLayout) getActivity().findViewById(R.id.fragment_layout_login_edit_pass);

                Log.e("Token FCM", FirebaseInstanceId.getInstance().getToken());

                boolean username = firebase.userExist(editText_username.getText()+"");
                boolean password = firebase.userCompare(editText_username.getText()+"", editText_pass.getText()+"");

                //TODO si no existe mail, enviamos mensaje de error
                if ( ! username ) {
                    layout_username.setError(getString(R.string.login_error_username));
                    Log.e("Error", "email");
                } else {
                    layout_username.setError("");
                }

                //TODO si no coincide el password, enviamos mensaje de error
                if ( ! password ) {
                    layout_pass.setError(getString(R.string.login_error_password));
                    Log.e("Error", "Contraseña");
                } else {
                    layout_pass.setError("");
                }

                //TODO si usuario y contraseña fueron ok, entonces iniciamos MainActivity
                //guardamos datos del usuario y ponemos login en True
                //para no volverse a logear cuando inicia la aplicacion en otra ocasion
                if (username && password) {

                    firebase.setGCM(FirebaseInstanceId.getInstance().getToken());
                    firebase.setLogin(true);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    //TODO se mata SecondActivity
                    getActivity().finish();
                }

                break;
            case R.id.fragment_login_text_register:
                //TODO Si toco "aun no tienes cuenta"
                //entonces inicia el fragmento de registro
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.activity_second, new RegisterFragment(), RegisterFragment.TAG)
                        .commit();
                break;
            case R.id.fragment_login_text_change_login:
                //TODO si toca "iniciar con huella digital"
                //Entonces creamos el popup con el icono de la huella
                //lo mostramos e iniciamos la escucha del popup
                popup = new FingerprintPopup((AppCompatActivity) getActivity(), R.layout.recognize_fingerprint);
                popup.initPopup(getString(R.string.login_title), true);
                popup.start();

                try {
                    presenter.init();
                } catch (Exception e) {
                    presenter = null;
                    alert("Sensor de huella " + getString(R.string.alert_title), getString(R.string.alert_message));
                }

                break;
        }
    }

    //TODO usamos los alerts para informar de algun error, por ejemplo que no hay servicio para las huellas
    private void alert(String title, String msj){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        alertDialog.setTitle(title);
        alertDialog.setMessage(msj);
        alertDialog.setPositiveButton(getString(R.string.alert_accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    /****
     * Obtenemos resultado del login, True si coincide con la huella registrada
     * False en caso contrario
     * @param aBoolean
     */
    @Override
    public void getFingerResult(Boolean aBoolean) {

        if (aBoolean) {
            popup.closePopup();
            firebase.setGCM(FirebaseInstanceId.getInstance().getToken());
            firebase.setLogin(true);
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null)
            presenter.unregisterBroadcastReceiver();
    }

    @Override
    public void userResponse(User user) {
    }
}
