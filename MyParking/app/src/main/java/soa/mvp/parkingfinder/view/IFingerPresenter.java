package soa.mvp.parkingfinder.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;
import com.samsung.android.sdk.pass.SpassInvalidStateException;

import java.util.ArrayList;

import soa.mvp.parkingfinder.R;
import soa.mvp.parkingfinder.presenter.FingerPresenter;

/**
 * Created by raulvillca on 16/5/17.
 */

public class IFingerPresenter implements Handler.Callback, FingerPresenter, FingerPresenter.Model<String, Boolean> {

    private SpassFingerprint mSpassFingerprint;
    private Spass mSpass;
    private Fragment mContext;

    private ArrayList<Integer> designatedFingers = null;
    private boolean needRetryIdentify = false;
    private boolean onReadyIdentify = false;
    private boolean hasRegisteredFinger = false;

    private boolean isFeatureEnabled_fingerprint = false;
    private boolean isFeatureEnabled_index = false;

    private Handler mHandler;
    private static final int MSG_AUTH = 1000;
    private static final int MSG_CANCEL = 1003;
    private static final int MSG_GET_NAME = 1005;
    private static final int MSG_GET_UNIQUEID = 1006;
    private static final int MSG_AUTH_INDEX = 1007;
    //private int button;

    private FingerPresenter.View fingerPresenter;

    public IFingerPresenter(Fragment activity) {
        this.mContext = activity;
        fingerPresenter = (FingerPresenter.View) activity;

        mHandler = new Handler(this);

        mSpass = new Spass();

        try {
            mSpass.initialize(this.mContext.getActivity());
        } catch (SsdkUnsupportedException e) {
            Log.d("","Exception: " + e);
        } catch (UnsupportedOperationException e) {
            Log.d("","Fingerprint Service is not supported in the device");
        }
        isFeatureEnabled_fingerprint = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if (isFeatureEnabled_fingerprint) {
            mSpassFingerprint = new SpassFingerprint(this.mContext.getActivity());
            Log.d("","Fingerprint Service is supported in the device.");
        } else {
            Log.d("","Fingerprint Service is not supported in the device.");
            return;
        }

        isFeatureEnabled_index = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_FINGER_INDEX);
    }

    public void init() {

        mHandler.sendEmptyMessage(MSG_AUTH);
    }

    private BroadcastReceiver mPassReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (SpassFingerprint.ACTION_FINGERPRINT_RESET.equals(action)) {
                Toast.makeText(mContext.getActivity(), "all fingerprints are removed", Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_REMOVED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext.getActivity(), fingerIndex + " fingerprints is removed", Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_ADDED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext.getActivity(), fingerIndex + " fingerprints is added", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_RESET);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_REMOVED);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_ADDED);
        mContext.getActivity().registerReceiver(mPassReceiver, filter);
    };

    public void unregisterBroadcastReceiver() {
        try {
            if (mContext != null) {
                mContext.getActivity().unregisterReceiver(mPassReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetAll() {
        designatedFingers = null;
        needRetryIdentify = false;
        onReadyIdentify = false;
        hasRegisteredFinger = false;
    }

    private SpassFingerprint.IdentifyListener mIdentifyListener = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            Log.d("","identify finished : reason =" + getEventStatusName(eventStatus));

            int FingerprintIndex = 0;
            String FingerprintGuideText = null;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                Log.d("",ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                Log.d("","onFinished() : Identify authentification Success with FingerprintIndex : " + FingerprintIndex);

                Toast.makeText(mContext.getActivity(), " fingerprints OK", Toast.LENGTH_SHORT).show();
                putModel(getEventStatusName(eventStatus));

            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                Log.d("","onFinished() : Password authentification Success");
            } else if (eventStatus == SpassFingerprint.STATUS_OPERATION_DENIED) {
                Log.d("","onFinished() : Authentification is blocked because of fingerprint service internally.");
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED) {
                Log.d("","onFinished() : User cancel this identify.");
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                Log.d("","onFinished() : The time for identify is finished.");
            } else if (eventStatus == SpassFingerprint.STATUS_QUALITY_FAILED) {
                Log.d("","onFinished() : Authentification Fail for identify.");
                needRetryIdentify = true;
                FingerprintGuideText = mSpassFingerprint.getGuideForPoorQuality();
                Toast.makeText(mContext.getActivity(), FingerprintGuideText, Toast.LENGTH_SHORT).show();
            } else {
                Log.d("","onFinished() : Authentification Fail for identify");
                needRetryIdentify = true;
            }
            if (!needRetryIdentify) {
                resetIdentifyIndex();
            }
        }

        @Override
        public void onReady() {

        }

        @Override
        public void onStarted() {

        }

        @Override
        public void onCompleted() {
            onReadyIdentify = false;
            if (needRetryIdentify) {
                needRetryIdentify = false;
                mHandler.sendEmptyMessageDelayed(MSG_AUTH, 100);
            }
        }
    };

    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_BUTTON_PRESSED:
                return "STATUS_BUTTON_PRESSED";
            case SpassFingerprint.STATUS_OPERATION_DENIED:
                return "STATUS_OPERATION_DENIED";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH:
                startIdentify();
                break;
            case MSG_CANCEL:
                cancelIdentify();
                break;
            case MSG_GET_NAME:
                getFingerprintName();
                break;
            case MSG_GET_UNIQUEID:
                getFingerprintUniqueID();
                break;
            case MSG_AUTH_INDEX:
                makeIdentifyIndex(1);
                startIdentify();
                break;
        }
        return true;
    }

    private void startIdentify() {
        if (onReadyIdentify == false) {
            try {
                onReadyIdentify = true;
                if (mSpassFingerprint != null) {
                    setIdentifyIndex();
                    mSpassFingerprint.startIdentify(mIdentifyListener);
                }
                if (designatedFingers != null) {
                    Log.d("","Please identify finger to verify you with " + designatedFingers.toString() + " finger");
                } else {
                    Log.d("","Please identify finger to verify you");
                }
            } catch (SpassInvalidStateException ise) {
                onReadyIdentify = false;
                resetIdentifyIndex();
                if (ise.getType() == SpassInvalidStateException.STATUS_OPERATION_DENIED) {
                    Log.d("","Exception: " + ise.getMessage());
                }
            } catch (IllegalStateException e) {
                onReadyIdentify = false;
                resetIdentifyIndex();
                Log.d("","Exception: " + e);
            }
        } else {
            Log.d("","The previous request is remained. Please finished or cancel first");
        }
    }

    private void cancelIdentify() {
        if (onReadyIdentify == true) {
            try {
                if (mSpassFingerprint != null) {
                    mSpassFingerprint.cancelIdentify();
                }
                Log.d("","cancelIdentify is called");
            } catch (IllegalStateException ise) {
                Log.d("",ise.getMessage());
            }
            onReadyIdentify = false;
            needRetryIdentify = false;
        } else {
            Log.d("","Please request Identify first");
        }
    }

    private void getFingerprintName() {
        SparseArray<String> mList = null;
        Log.d("","=Fingerprint Name=");
        if (mSpassFingerprint != null) {
            mList = mSpassFingerprint.getRegisteredFingerprintName();
        }
        if (mList == null) {
            Log.d("","Registered fingerprint is not existed.");
        } else {
            for (int i = 0; i < mList.size(); i++) {
                int index = mList.keyAt(i);
                String name = mList.get(index);
                Log.d("","index " + index + ", Name is " + name);
            }
        }
    }

    private void getFingerprintUniqueID() {
        SparseArray<String> mList = null;
        try {
            Log.d("","=Fingerprint Unique ID=");
            if (mSpassFingerprint != null) {
                mList = mSpassFingerprint.getRegisteredFingerprintUniqueID();
            }
            if (mList == null) {
                Log.d("","Registered fingerprint is not existed.");
            } else {
                for (int i = 0; i < mList.size(); i++) {
                    int index = mList.keyAt(i);
                    String ID = mList.get(index);
                    Log.d("","index " + index + ", Unique ID is " + ID);
                }
            }
        } catch (IllegalStateException ise) {
            Log.d("",ise.getMessage());
        }
    }

    private void setIdentifyIndex() {
        if (isFeatureEnabled_index) {
            if (mSpassFingerprint != null && designatedFingers != null) {
                mSpassFingerprint.setIntendedFingerprintIndex(designatedFingers);
            }
        }
    }

    private void makeIdentifyIndex(int i) {
        if (designatedFingers == null) {
            designatedFingers = new ArrayList<Integer>();
        }
        for(int j = 0; j< designatedFingers.size(); j++){
            if(i == designatedFingers.get(j)){
                return;
            }
        }
        designatedFingers.add(i);
    }

    private void resetIdentifyIndex() {
        designatedFingers = null;
    }

    private void setButtonEnable() {
        if (mSpassFingerprint == null) {
            return;
        }
        try {
            hasRegisteredFinger = mSpassFingerprint.hasRegisteredFinger();
        } catch (UnsupportedOperationException e) {
            Log.d("","Fingerprint Service is not supported in the device");
        }
    }

    @Override
    public void putModel(String o) {
        if ("STATUS_AUTHENTIFICATION_SUCCESS".equals(o))
            getModel(true);
        else
            getModel(false);
    }

    @Override
    public void getModel(Boolean o) {
        fingerPresenter.getFingerResult(o);
    }
}
