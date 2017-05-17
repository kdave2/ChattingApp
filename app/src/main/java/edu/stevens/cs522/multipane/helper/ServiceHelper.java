package edu.stevens.cs522.multipane.helper;

import edu.stevens.cs522.multipane.constants.constant;
import edu.stevens.cs522.multipane.request.Register;
import edu.stevens.cs522.multipane.request.Unregister;
import edu.stevens.cs522.multipane.service.RequestService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class ServiceHelper {

	public static final String TAG = ServiceHelper.class.getCanonicalName();

	int type;
    ResultReceiver resultReceiver;
	Register register;
	public Context ctx;
	private static Object lock = new Object();
	private static ServiceHelper instance;

	private ServiceHelper(Context ctx) {
		this.ctx = ctx.getApplicationContext();
	}

	public static ServiceHelper getInstance(Context ctx) {
		synchronized (lock) {
            Log.i(TAG, "getInstance(): new Service Helper instance created");
            if (instance == null) {
				instance = new ServiceHelper(ctx);
			}else{
				instance.ctx = ctx.getApplicationContext();
			}
		}
		return instance;
	}

    public void register(Register register, ResultReceiver receiver) {
        Log.i(TAG, "Registering: " + this.ctx.getClass().toString());
        Intent i = new Intent(constant.REGISTER_ACTION);
        i.putExtra(constant.REGISTER, register);
        i.putExtra(constant.RECEIVER, receiver);
        this.ctx.startService(i);
    }

	public void sync() {
        Log.i(TAG, "Syncing: " + this.ctx.getClass().toString());
        Intent i = new Intent(ctx, RequestService.class);
        i.putExtra(constant.TYPE, 1);
		this.ctx.startService(i);
	}

	public void unregister(Unregister unregister) {
        Log.i(TAG, "unRegistering: " + this.ctx.getClass().toString());
        Intent i = new Intent(constant.UNREGISTER_ACTION);
        i.putExtra(constant.UNREGISTER, unregister);
		i.putExtra(constant.TYPE, 2);
		this.ctx.startService(i);
	}
}
