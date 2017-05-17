package edu.stevens.cs522.multipane.activity;

import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import edu.stevens.cs522.multipane.R;
import edu.stevens.cs522.multipane.helper.ServiceHelper;
import edu.stevens.cs522.multipane.request.Register;
import edu.stevens.cs522.multipane.service.AlarmReceiver;
import edu.stevens.cs522.multipane.constants.constant;

@SuppressLint("NewApi")
public class Settings extends Activity {

	public static final String TAG = Settings.class.getCanonicalName();

	public static boolean networkOn = false;
	AckReceiver receiver;
	String clientName, portNo, hostStr, uuid;
	//int ipt;
	Context mContext;
	AlarmManager alarmManager;
	ContentResolver cr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /*try{
			cr.delete(MessageProviderCloud.CONTENT_URI, null, null);
			cr.delete(MessageProviderCloud.CONTENT_URI_PEER, null, null);
			cr.delete(MessageProviderCloud.CONTENT_URI_CHATROOM, null, null);
		}catch(Exception e){
			e.printStackTrace();
		}*/

        setContentView(R.layout.login);
		receiver = new AckReceiver(new Handler());
		mContext = this.getApplicationContext();

        //SharedPreferences prfs = getSharedPreferences(constant.SHARED_PREF, 0);
		//uuid = UUID.randomUUID().toString();
		//SharedPreferences.Editor editor = prfs.edit();
		//editor.putString(constant.CLIENT_UUID, uuid);
		//editor.commit();

        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(this, 12, intentAlarm, 0);
		try {
			alarmManager.cancel(sender);
		} catch (Exception e) {
			Log.e(TAG, "AlarmManager update was not canceled. " + e.toString());
		}
		// NAME = res.getString(R.string.user_name) ;
		// name_field = (EditText) findViewById(R.id.name_field) ;
	}

	public class AckReceiver extends ResultReceiver {
		public AckReceiver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		protected void onReceiveResult(int resultCode, Bundle result) {
			switch (resultCode) {
                case 0:
                    Log.i(TAG, "OnReceiveResult: case 0 : Login Failed, Can't find server");
                    Toast.makeText(mContext, "Login Fail, Can't find server.", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    String client = result.getString(constant.CLIENT_ID);
                    if (client != null) {
                        Log.i(TAG, "OnReceiveResult: case 1: id: "+client);
                    }
                    Intent i = new Intent(mContext, FragmentLayout.class);
                    i.putExtra(constant.NAME, clientName);
                    i.putExtra(constant.PORT, portNo);
                    i.putExtra(constant.HOST, hostStr);
                    i.putExtra(constant.CLIENT_ID, client);
                    startActivity(i);
                    break;
                case 2:
                    Log.i(TAG, "OnReceiveResult case 2: Client already exists");
                    Toast.makeText(mContext, "Client Name already exists",Toast.LENGTH_SHORT).show();
                    break;
			    default:
				    break;
			}
		}
	};

	public void login(View view) {
        Log.i(TAG, "Starting Login(View)");

        EditText host = (EditText) findViewById(R.id.dest_text);
		EditText port = (EditText) findViewById(R.id.port_text);
        EditText username = (EditText) findViewById(R.id.name_field);

		hostStr = host.getText().toString();
        portNo = port.getText().toString();
        clientName = username.getText().toString();

		if (username.getText().toString().matches("") || port.getText().toString().matches("") || host.getText().toString().matches("")) {
			Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
		} else {

            SharedPreferences prfs = getSharedPreferences(constant.SHARED_PREF, Context.MODE_PRIVATE);
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prfs.edit();
            editor.putString(constant.CLIENT_UUID, uuid);

            Register register = new Register(0, UUID.fromString(uuid), clientName, "http://" + hostStr + ":" + portNo);

            editor.putString(constant.NAME, register.username);
            editor.putString(constant.PORT, portNo);
            editor.putString(constant.HOST, hostStr);
            editor.commit();

			ServiceHelper.getInstance(this).register(register, receiver);
		}
        Log.i(TAG, "End Login(View)");
	}
}
