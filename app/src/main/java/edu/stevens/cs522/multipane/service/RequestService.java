package edu.stevens.cs522.multipane.service;

import java.util.UUID;

import edu.stevens.cs522.multipane.constants.constant;
import edu.stevens.cs522.multipane.contract.Contract;
import edu.stevens.cs522.multipane.entity.Peer;
import edu.stevens.cs522.multipane.R;
import edu.stevens.cs522.multipane.provider.MessageProviderCloud;
import edu.stevens.cs522.multipane.request.Register;
import edu.stevens.cs522.multipane.request.RequestProcessor;
import edu.stevens.cs522.multipane.request.Synchronize;
import edu.stevens.cs522.multipane.request.Unregister;
import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.ResultReceiver;
import android.util.Log;

public class RequestService extends IntentService {

    public static final String TAG = RequestService.class.getCanonicalName();
    ResultReceiver resultReceiver;
	public Context mContext;

	RequestProcessor requestProcessor;

	public RequestService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public RequestService() {
		super("RequestService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		requestProcessor = new RequestProcessor(this.getContentResolver());
        Log.i(TAG, "Starting Services");

		int type = intent.getExtras().getInt(constant.TYPE);
        Log.i(TAG, "TYPE = "+type);
		switch (type) {
            case 0:
			    Register register = intent.getExtras().getParcelable(constant.REGISTER);
                resultReceiver = (ResultReceiver) intent.getExtras().getParcelable(constant.RECEIVER);
                requestProcessor.perform(register, resultReceiver);
                sendNotify(1, register.username);
                break;
            case 1:
                Log.i(TAG, "RequestService triggered, sync");

                Synchronize sync = new Synchronize();
                SharedPreferences sharedPreferences = this.getSharedPreferences(constant.SHARED_PREF, 0);
                String uuid = sharedPreferences.getString(constant.CLIENT_UUID, UUID.randomUUID().toString());
                String chatroom = sharedPreferences.getString(constant.CHAT_ROOM, "_default");
                sync.regid = UUID.fromString(uuid);

                ContentResolver cr = getContentResolver();
                Cursor cursor = cr.query(MessageProviderCloud.CONTENT_URI, null, null, null, "messages.messageid");
                int messageid = 0;
                if (cursor.moveToFirst()) {
                    messageid = Contract.getMessageId(cursor);
                }
                Log.i(TAG, "Message Sequence #:"+String.valueOf(messageid));
                sync.id = messageid;

                String name = sharedPreferences.getString(constant.NAME, "DefaultClient");
                Cursor c = cr.query(MessageProviderCloud.CONTENT_URI_PEER, null, "peers.name=?", new String[]{name}, null);
			    Peer peer = new Peer();
			    if (c.moveToFirst()) {
				    do{
					    peer = new Peer(c);
				    }while(c.moveToNext());
			    }
			    c.close();
                Log.i(TAG, "Chat Room #:" + chatroom);
                sync.chatroom=chatroom;
				sync.userId = String.valueOf(peer.id);

                String addr = "http://"+ sharedPreferences.getString(constant.HOST, "localhost")+ ":"+sharedPreferences.getString(constant.PORT, "8080");
                sync.addr = addr;
                SharedPreferences prefs = this.getSharedPreferences(constant.SHARED_PREF,Context.MODE_PRIVATE);
                if(!sync.userId.equals("0")){
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("userId",sync.userId);
                    edit.commit();
                }
                if(sync.userId.equals("0")){
                    sync.userId = prefs.getString("userId","1");
                }

                Log.i(TAG, "test Sync service: " + sync.addr + "/" + sync.id + "/" + sync.userId);
                requestProcessor.perform(sync);

                break;
		case 2:
			Log.i(TAG, "Unregister service started: ");
			Unregister unregister = intent.getExtras().getParcelable(constant.UNREGISTER);
			requestProcessor.perform(unregister);
			break;
		default:
			break;
		}
	}

	@SuppressLint("NewApi")
	private void sendNotify(int id, String text) {
		Intent intent = new Intent();
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		Notification notification = new Notification.Builder(this)
				.setContentTitle("Client Registered!").setContentText(text)
				.setSmallIcon(R.drawable.ic_launcher).setContentIntent(pIntent)
				.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, notification);
	}
}
