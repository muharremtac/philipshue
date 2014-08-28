package com.philips.lighting.quickstart;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGRawMulti;
import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueError;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

/**
 * MyApplicationActivity - The starting point for creating your own Hue App.  
 * Currently contains a simple view with a button to change your lights to random colours.  Remove this and add your own app implementation here! Have fun!
 * 
 * @author SteveyO
 *
 */
public class MyApplicationActivity extends Activity {
    private PHHueSDK phHueSDK;
    private static final int MAX_HUE=65535;
    public static final String TAG = "QuickStart";
    TGDevice tgDevice;
    BluetoothAdapter bluetoothAdapter;
    TextView tv;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.textView1);
        
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
        	// Alert user that Bluetooth is not available
        	Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_LONG).show();
        	Log.v("HelloA", "Bluetooth not available");
        	finish();
        	return;
        }else {
        	/* create the TGDevice */
        	tgDevice = new TGDevice(bluetoothAdapter, handler);
        	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
        		tgDevice.connect(false);   
        	Log.v("HelloA", "Bluetooth is available");
        }  
        
        phHueSDK = PHHueSDK.create();
        Button randomButton;
        randomButton = (Button) findViewById(R.id.buttonRand);
        randomButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                randomLights();
//            	sendData(12312);
            }

        });

    }

    public void randomLights() {
        PHBridge bridge = phHueSDK.getSelectedBridge();

        List<PHLight> allLights = bridge.getResourceCache().getAllLights();
        Random rand = new Random();
        
        for (PHLight light : allLights) {
            PHLightState lightState = new PHLightState();
            int random=rand.nextInt(MAX_HUE);
            lightState.setHue(random);
            // To validate your lightstate is valid (before sending to the bridge) you can use:  
            // String validState = lightState.validateState();
            bridge.updateLightState(light, lightState, listener);
            tv.setText("Random: "+ random);
            //  bridge.updateLightState(light, lightState);   // If no bridge response is required then use this simpler form.
        }
    }

    public void sendData(int hueData) {
    	PHBridge bridge = phHueSDK.getSelectedBridge();
    	
    	List<PHLight> allLights = bridge.getResourceCache().getAllLights();
    	
    	for (PHLight light : allLights) {
    		PHLightState lightState = new PHLightState();
    		lightState.setHue(hueData);
    		bridge.updateLightState(light, lightState, listener);
    	}
    }
    // If you want to handle the response from the bridge, create a PHLightListener object.
    PHLightListener listener = new PHLightListener() {
        
        @Override
        public void onSuccess() {  
        }
        
        @Override
        public void onStateUpdate(Hashtable<String, String> arg0, List<PHHueError> arg1) {
           Log.w(TAG, "Light has updated");
        }
        
        @Override
        public void onError(int arg0, String arg1) {  
        }
    };
    
    @Override
    protected void onDestroy() {
        PHBridge bridge = phHueSDK.getSelectedBridge();
        if (bridge != null) {
            
            if (phHueSDK.isHeartbeatEnabled(bridge)) {
                phHueSDK.disableHeartbeat(bridge);
            }
            
            phHueSDK.disconnect(bridge);
            super.onDestroy();
        }
    }
    
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            
        	switch (msg.what) {
            case TGDevice.MSG_STATE_CHANGE:

                switch (msg.arg1) {
	                case TGDevice.STATE_IDLE:
	                	 Log.v("HelloA", "Idle");
	                    break;
	                case TGDevice.STATE_CONNECTING:		                	
//	                	tv.setText("Connecting...\n");
	                	Log.v("HelloA", "Connecting");
	                	break;		                    
	                case TGDevice.STATE_CONNECTED:
//	                	tv.setText("Connected.\n");
	                	tgDevice.start();
	                	Log.v("HelloA", "Connected");
	                    break;
	                case TGDevice.STATE_NOT_FOUND:
//	                	tv.setText("Can't find\n");
	                	Log.v("HelloA", "State not found");
	                	break;
	                case TGDevice.STATE_NOT_PAIRED:
//	                	tv.setText("not paired\n");
	                	Log.v("HelloA", "State not paired");
	                	break;
	                case TGDevice.STATE_DISCONNECTED:
	                	Log.v("HelloA", "State disconnect");
//	                	tv.setText("Disconnected mang\n");
                }

                break;
            case TGDevice.MSG_POOR_SIGNAL:
            		//signal = msg.arg1;
//            	tv.setText("PoorSignal: " + msg.arg1 + "\n");
            		Log.v("HelloA", "POOR SIGNAL");
                break;
            case TGDevice.MSG_RAW_DATA:	  
            		int raw1 = msg.arg1;
//            		 sendData(raw1);
//            		tv.setText("Got raw: " + msg.arg1 + "\n");
            		Log.v("HelloA", "Got raw: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_HEART_RATE:
//            	tv.setText("Heart rate: " + msg.arg1 + "\n");
        		Log.v("HelloA", "Heart rate: " + msg.arg1 + "\n");
                break;
            case TGDevice.MSG_ATTENTION:
            		int att = msg.arg1*60;
//            		tv.setText("Attention: " + att + "\n");
//                	sendData(att);
//            		Log.v("HelloA", "Attention: " + att + "\n");
            	break;
            case TGDevice.MSG_MEDITATION:
        			att = msg.arg1*90;
        			tv.setText("Meditation: " + att + "\n");
        			sendData(att*3);//stop
	            	 
            	 Log.v("HelloA", "Medidation");
            	break;
            case TGDevice.MSG_BLINK:
//            	tv.setText("Blink: " + msg.arg1 + "\n");
            		Log.v("HelloA", "Blink: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_RAW_COUNT:
//            	tv.setText("Raw Count: " + msg.arg1 + "\n");
//       		 sendData(msg.arg1);
            		Log.v("HelloA", "Raw Count: " + msg.arg1 + "\n");
            	break;
            case TGDevice.MSG_LOW_BATTERY:
            	Toast.makeText(getApplicationContext(), "Low battery!", Toast.LENGTH_SHORT).show();
            	Log.v("HelloA", "LOW BATTERY");
            	break;
            case TGDevice.MSG_RAW_MULTI:
            	TGRawMulti rawM = (TGRawMulti)msg.obj;
//            	tv.setText("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            	Log.v("HelloA", "Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
            default:
            	Log.v("HelloA", "NOT MESSAGE");
            	break;
        }
        }
    };
    
}
