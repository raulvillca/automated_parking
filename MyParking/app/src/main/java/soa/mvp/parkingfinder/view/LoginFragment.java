package soa.mvp.parkingfinder.view;

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
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import soa.mvp.parkingfinder.MainActivity;
import soa.mvp.parkingfinder.R;
import soa.mvp.parkingfinder.model.Item;
import soa.mvp.parkingfinder.model.ItemFirebase;
import soa.mvp.parkingfinder.model.ParkingListFactory;
import soa.mvp.parkingfinder.model.ParkingPoint;
import soa.mvp.parkingfinder.model.ParkingPositionListFactory;
import soa.mvp.parkingfinder.model.Time;
import soa.mvp.parkingfinder.model.User;
import soa.mvp.parkingfinder.presenter.FingerPresenter;
import soa.mvp.parkingfinder.refactor.ParkingFirebase;
import soa.mvp.parkingfinder.service.ParkingFirebaseRequest;

/**
 * Created by raulvillca on 15/5/17.
 */

public class LoginFragment extends Fragment implements View.OnClickListener,
        FingerPresenter.View<Boolean>,
        ParkingFirebaseRequest.UserFirebaseResponse<User> {
    public static String TAG = "LoginFragment";

    private Button buttonLogin;
    private TextView textViewRegister;
    private TextView textViewRegisterFingerPrint;
    private IFingerPresenter presenter;
    private ParkingFirebase firebase;

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
            case R.id.fragment_login_button_login:
                EditText editText_username = (EditText) getActivity().findViewById(R.id.fragment_login_edit_username);
                EditText editText_pass = (EditText) getActivity().findViewById(R.id.fragment_login_edit_pass);

                TextInputLayout layout_username = (TextInputLayout) getActivity().findViewById(R.id.fragment_layout_login_edit_username);
                TextInputLayout layout_pass = (TextInputLayout) getActivity().findViewById(R.id.fragment_layout_login_edit_pass);

                Log.e("Token FCM", FirebaseInstanceId.getInstance().getToken());

                boolean username = firebase.userExist(editText_username.getText()+"");
                boolean password = firebase.userCompare(editText_username.getText()+"", editText_pass.getText()+"");

                if ( ! username ) {
                    layout_username.setError(getString(R.string.login_error_username));
                    Log.e("Error", "email");
                } else {
                    layout_username.setError("");
                }

                if ( ! password ) {
                    layout_pass.setError(getString(R.string.login_error_password));
                    Log.e("Error", "Contraseña");
                } else {
                    layout_pass.setError("");
                }

                if (username && password) {

                    firebase.setGCM(FirebaseInstanceId.getInstance().getToken());
                    firebase.setLogin(true);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                }

                break;
            case R.id.fragment_login_text_register:
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.activity_second, new RegisterFragment(), RegisterFragment.TAG)
                        .commit();
                break;
            case R.id.fragment_login_text_change_login:
                FingerprintPopup popup = new FingerprintPopup((AppCompatActivity) getActivity(), R.layout.recognize_fingerprint);
                popup.initPopup(getString(R.string.login_title), true);
                popup.start();

                try {
                    presenter.init();
                } catch (Exception e) {
                    presenter = null;
                }

                break;
        }
    }

    @Override
    public void getFingerResult(Boolean aBoolean) {

        if (aBoolean) {
            firebase.setGCM(FirebaseInstanceId.getInstance().getToken());
            firebase.setLogin(true);
            startActivity(new Intent(getActivity(), MainActivity.class));
        }

        Toast.makeText(getActivity(), "result: " + aBoolean, Toast.LENGTH_LONG).show();
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
