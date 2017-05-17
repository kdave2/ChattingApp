package edu.stevens.cs522.multipane.fragment;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import edu.stevens.cs522.multipane.R;


/**
 * Created by KhushaliDave on 4/3/16.
 */
public class AddRoomDialog extends DialogFragment implements OnEditorActionListener, OnClickListener {

	public static final String TAG = AddRoomDialog.class.getCanonicalName();

	public interface EditAddRoomDialogListener {
		void onFinishAddDialog(String chatroom);
	}
	private EditText mChatroomText;
	private Button mSendBtn;
	private Button mCancelBtn;

	public AddRoomDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_send_room, container);
		//mEditText = (EditText) view.findViewById(R.id.txt_chatroom);
		
		mChatroomText = (EditText) view.findViewById(R.id.txt_chatroom);
	//	mChatroomText.setText(chatroom);
		mSendBtn= (Button) view.findViewById(R.id.addroombtn);
		mCancelBtn =(Button) view.findViewById(R.id.cancelroombtn);
		getDialog().setTitle("Create ChatRoom");

		// Show soft keyboard automatically
		mChatroomText.requestFocus();
		getDialog().getWindow().setSoftInputMode(4);
		mChatroomText.setOnEditorActionListener(this);
		mSendBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);
		return view;
	}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_DONE == actionId) {
			// Return input text to activity
			EditAddRoomDialogListener activity = (EditAddRoomDialogListener) getActivity();
			activity.onFinishAddDialog(mChatroomText.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	    case R.id.addroombtn:
	    	EditAddRoomDialogListener activity = (EditAddRoomDialogListener) getActivity();
			activity.onFinishAddDialog(mChatroomText.getText().toString());
	        this.dismiss();
	        break; 
	    case R.id.cancelroombtn:
	    	Log.d(TAG, "Cancel btn: clicked");
	        this.dismiss();
	        break;
	    default:
            Log.i(TAG, "onClick(): default case");
	        break;
	   }
	}
}
