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
import edu.stevens.cs522.multipane.constants.constant;


/**
 * Created by MehulGupta on 4/3/16.
 */
public class SendMessageDialog extends DialogFragment implements OnEditorActionListener, OnClickListener {

    public static final String TAG = SendMessageDialog.class.getCanonicalName();
	public interface EditNameDialogListener {
		void onFinishEditDialog(String chatroom, String msg);
	}

	private EditText mEditText;
	private EditText mChatroomText;
	private Button mSendBtn;
	private Button mCancelBtn;

	public SendMessageDialog() {
		// Empty constructor required for DialogFragment
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		SharedPreferences prefs = getActivity().getSharedPreferences(constant.SHARED_PREF, 0);
		String chatroom = prefs.getString(constant.CHAT_ROOM, "_default");

		View view = inflater.inflate(R.layout.fragment_add_message, container);
		mEditText = (EditText) view.findViewById(R.id.txt_your_name);
		mChatroomText = (EditText) view.findViewById(R.id.txt_chatroom_name);
		mChatroomText.setText(chatroom);
		mSendBtn= (Button) view.findViewById(R.id.sendbtn);
		mCancelBtn =(Button) view.findViewById(R.id.cancelbtn);
		getDialog().setTitle("MESSAGE");

		// Show soft keyboard automatically
		mEditText.requestFocus();
		getDialog().getWindow().setSoftInputMode(4);
		mEditText.setOnEditorActionListener(this);
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
			EditNameDialogListener activity = (EditNameDialogListener) getActivity();
			activity.onFinishEditDialog(mChatroomText.getText().toString(),mEditText.getText().toString());
			this.dismiss();
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	        case R.id.sendbtn:
	    	    EditNameDialogListener activity = (EditNameDialogListener) getActivity();
			    activity.onFinishEditDialog(mChatroomText.getText().toString(),mEditText.getText().toString());
	            this.dismiss();
	            break;
	        case R.id.cancelbtn:
    	    	Log.i(TAG, "SEND Dialog CANCELLED");
	            this.dismiss();
	            break;
	        default:
                Log.i(TAG, "onCLick(): default case");
	            break;
	   }
	}
}
