package com.example.rtktvapiservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rtktvapiservice.service.MessengerService;
import com.example.rtktvapiservice.service.RemoteService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etVal1, etVal2;
    private TextView lblTotal;

    //bind service
    // Messenger for communicating with the service.
    private Messenger mMessengerService = null;
    // Flag indicating whether we have called bind on the service.
    private boolean mMessengerBound = false;
    // Class for interacting with the main interface of the service.
    private final ServiceConnection mMessengerConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            mMessengerService = new Messenger(service);
            mMessengerBound = true;
            Log.d("bindMessenger", getString(R.string.service_bind));
            triggerSumTotal();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mMessengerService = null;
            mMessengerBound = false;
            Log.d("bindMessenger", getString(R.string.service_unbind));
        }
    };

    private void bindMessengerService(){
        try {
            if (bindService(new Intent(this, MessengerService.class),
                    mMessengerConnection, Context.BIND_AUTO_CREATE)) {
                if(mMessengerService != null){
                    mMessengerBound = true;
                    triggerSumTotal();
                }else{
                    Log.d("msgService", getString(R.string.service_null));
                }
            } else {
                Toast.makeText(this, "req service not exist", Toast.LENGTH_SHORT).show();
                Log.d("bindMsgSvc", "Error: The requested service doesn't " +
                        "exist, or this client isn't allowed access to it.");
            }
        }catch (Exception e){
            Log.e("errBindMsgSvc", e.getMessage());
        }
    }

    private void unbindMessengerService(){
        if(mMessengerBound){
            unbindService(mMessengerConnection);
            mMessengerBound =  false;
            Log.d("unbindMsgService",getString(R.string.service_unbind));
        }
    }

    private void startMessengerService(){
        Intent intent = new Intent(this, MessengerService.class);
        intent.putExtra(MessengerService.KEY_X, Integer.parseInt(etVal1.getText()!= null && etVal1.getText().toString().length() >0 ? etVal1.getText().toString() :  "0"));
        intent.putExtra(MessengerService.KEY_Y, Integer.parseInt(etVal2.getText()!= null && etVal2.getText().toString().length() >0 ? etVal2.getText().toString() :  "0"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else{
            ContextCompat.startForegroundService(this, intent);
//            startService(intent);
        }
    }

    private void stopMessengerService(){
        Intent serviceIntent = new Intent(this, MessengerService.class);
        stopService(serviceIntent);
        Log.d("stopMsgService *", getString(R.string.service_stopped));
    }

    private void triggerSumTotal(){
        try {
            if (!mMessengerBound) {
                Toast.makeText(this, getString(R.string.service_null), Toast.LENGTH_SHORT).show();
                return;
            }
            // Create and send a message to the service, using a supported 'what' value
            Message msg = Message.obtain(null, MessengerService.MSG_SHOW_SUM, 0, 0);
            // pass total value via bundle
            Bundle b = new Bundle();
            b.putInt(MessengerService.KEY_X, Integer.parseInt(etVal1.getText()!= null && etVal1.getText().toString().length() >0 ? etVal1.getText().toString() :  "0"));
            b.putInt(MessengerService.KEY_Y, Integer.parseInt(etVal2.getText()!= null && etVal2.getText().toString().length() >0 ? etVal2.getText().toString() :  "0"));
            msg.setData(b);
            mMessengerService.send(msg);
        }catch (NumberFormatException e1){
            Log.e("errNumFmt", e1.getMessage());
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindMessengerService();
        unbindAIDLService();
    }

    //receive broadcast and update total label
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                int total = bundle.getInt(MessengerService.KEY_TOTAL, 0);
                lblTotal.setText(String.format(getString(R.string.equal_regex), total));
            }else{
                Toast.makeText(MainActivity.this, "bundle is null", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI(){
        etVal1  = findViewById(R.id.etVal1);
        etVal2  = findViewById(R.id.etVal2);
        lblTotal = findViewById(R.id.lblTotal);

        Button btnEqual = findViewById(R.id.btnEqual);
        Button btnAIDLEqual = findViewById(R.id.btnAIDL);
        Button btnShowMenu = findViewById(R.id.btnMenu);
        btnEqual.setOnClickListener(this);
        btnAIDLEqual.setOnClickListener(this);
        btnShowMenu.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //start messenger foreground service
        startMessengerService();

        // start AIDL service
        startAIDLService();
//        //bind AIDL
//        bindAIDLService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindMessengerService();
        unbindAIDLService();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(MessengerService.PACKAGE_NAME));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        dismissKeyboard();
        int vId = v.getId();
        if(vId == R.id.btnEqual){
            bindMessengerService();
        }else if(vId == R.id.btnAIDL){
            triggerAIDLSum();
        }else if(vId == R.id.btnMenu){
            showMenuPage();
        }
    }

    private void showMenuPage(){
        Intent i = new Intent(this, MenuActivity.class);
        startActivity(i);
    }

    private void dismissKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etVal1.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etVal2.getWindowToken(), 0);
    }

    //aidl
//    private boolean mBound;
    private IRemoteService iRemoteService = null;
    private final ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            iRemoteService = IRemoteService.Stub.asInterface(service);
//            mBound=true;
            aidlCalculateTotal();
            Log.d("aidlSvcC", getString(R.string.service_bind));
        }

        // Called when the connection with the service disconnects unexpectedly
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("aidlSvcDC", getString(R.string.service_disconnected_unexpected));
            iRemoteService = null;
//            mBound= false;
        }
    };

    private void startAIDLService(){
        try{
            Intent intent = new Intent(this, RemoteService.class);
            intent.setAction(RemoteService.SERVICE_NAME);
            intent.setPackage(RemoteService.PACKAGE_NAME);
            startService(intent); //start service
        }catch (Exception e){
            Log.e("errStartAIDL",e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean bindAIDLService(){
        try {
            if(iRemoteService == null){
                Intent i = new Intent(this, RemoteService.class);
                i.setAction(RemoteService.SERVICE_NAME);
                i.setPackage(RemoteService.PACKAGE_NAME);
                return bindService(i, mConnection, Service.BIND_AUTO_CREATE);
            }else{
                return true;
            }
        }catch (Exception e){
            Toast.makeText(this, "!! : "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void triggerAIDLSum(){
        try{
            if(bindAIDLService()){
                if(iRemoteService!= null){
                    aidlCalculateTotal();
                }else{
                    Log.d("bindAIDL", getString(R.string.service_null));
                }
            }else{
                Toast.makeText(this, getString(R.string.service_not_bind), Toast.LENGTH_LONG).show();
                Log.d("bindAIDL", getString(R.string.service_not_bind));
            }
        }catch (Exception e){
            Log.e("err", e.getMessage());
            Toast.makeText(this, "aidl err: "+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void aidlCalculateTotal(){
        if(iRemoteService != null) {
            int sum = 0;
            try {
                sum = iRemoteService.addNumbers(Integer.parseInt(etVal1.getText() != null && etVal1.getText().toString().length() > 0 ? etVal1.getText().toString() : "0"), Integer.parseInt(etVal2.getText() != null && etVal2.getText().toString().length() > 0 ? etVal2.getText().toString() : "0"));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            lblTotal.setText(String.format(getString(R.string.equal_regex), sum));
        }else{
            Toast.makeText(this, getString(R.string.service_null), Toast.LENGTH_LONG).show();
            Log.e("aidlCalTotal", getString(R.string.service_null));
        }
    }

    private void unbindAIDLService(){
        //unbind aidl service
        if(iRemoteService != null){
            // Release information about the service's state.
            iRemoteService = null;
            unbindService(mConnection);
//            mBound = false;
            Log.d("unbindAIDL",getString(R.string.service_unbind));
        }
    }
}