package tpsoa.soa.com.parkings.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import tpsoa.soa.com.parkings.MainActivity;
import tpsoa.soa.com.parkings.R;
import tpsoa.soa.com.parkings.model.User;
import tpsoa.soa.com.parkings.presenter.FingerPresenter;
import tpsoa.soa.com.parkings.refactor.ParkingFirebase;

public class RegisterFragment extends Fragment implements View.OnClickListener, FingerPresenter.View<Boolean> {
    public static String TAG = "RegisterFragment";
    private Button button;
    private IFingerPresenter presenter;
    private EditText edit_fullname;
    private EditText edit_username;
    private EditText edit_pass;
    private ParkingFirebase firebase;

    public RegisterFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebase = new ParkingFirebase(this);
        firebase.connect();
        firebase.getUserList();

        button = (Button) getActivity().findViewById(R.id.fragment_register_button_login);
        TextView register_fingerprint = (TextView) getActivity().
                findViewById(R.id.fragment_register_text_register_fingerprint);

        button.setOnClickListener(this);
        register_fingerprint.setOnClickListener(this);

        //TODO iniciamos el servicio de samsung para utilizar el registro por
        //reconocimiento de huellas digitales
        try {
            presenter = new IFingerPresenter(this);
            presenter.registerBroadcastReceiver();
        } catch (Exception e) {
            presenter = null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_register_button_login:

                edit_fullname = (EditText) getActivity().findViewById(R.id.fragment_register_edit_fullname);
                edit_username = (EditText) getActivity().findViewById(R.id.fragment_register_edit_username);
                edit_pass = (EditText) getActivity().findViewById(R.id.fragment_register_edit_pass);

                TextInputLayout layout_username = (TextInputLayout) getActivity().findViewById(R.id.fragment_layout_register_edit_username);
                TextInputLayout layout_pass = (TextInputLayout) getActivity().findViewById(R.id.fragment_layout_register_edit_pass);

                boolean username = "".equals((edit_username.getText()+"").trim());
                boolean password = "".equals((edit_pass.getText()+"").trim());

                if (username) {
                    layout_username.setError(getString(R.string.register_error_username));
                } else {
                    layout_username.setError("");
                }

                if (password) {
                    layout_pass.setError(getString(R.string.register_error_password));
                } else {
                    layout_pass.setError("");
                }

                if ( ! username && ! password) {
                    User user = new User();
                    user.setUser_email(edit_username.getText()+"");
                    user.setUser_fullname(edit_fullname.getText()+"");
                    user.setUser_password(edit_pass.getText()+"");
                    user.setUser_access_noun(true);
                    user.setUser_gcm(FirebaseInstanceId.getInstance().getToken());

                    if ( ! firebase.userExist(user.getUser_email())) {

                        firebase.createNewUser(user);
                        firebase.saveLogIn(user, true);
                        firebase.setLogin(true);
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    } else {
                        layout_username.setError(getString(R.string.register_user_exists));
                    }
                }

                break;
            case R.id.fragment_register_text_register_fingerprint:
                FingerprintPopup popup = new FingerprintPopup((AppCompatActivity) getActivity(), R.layout.recognize_fingerprint);
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

    /***
     * obtenemos un valor booleano del resultado del sensor, si coincide la huella
     * devuelve true, sino false
     *
     * @param o
     */
    @Override
    public void getFingerResult(Boolean o) {
        if (o) {
            //TODO si es true entonces iniciamos MainActivity
            //guardamos el usuario que inicio y token
            //ponemos en el logeo True
            //y matamos la actividad SecondActivity
            User user = new User();
            user.setUser_access_noun(false);
            user.setUser_gcm(FirebaseInstanceId.getInstance().getToken());

            firebase.setLogin(true);
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }

        Toast.makeText(getActivity(), "result: " + o, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (presenter != null) presenter.unregisterBroadcastReceiver();
    }
}
