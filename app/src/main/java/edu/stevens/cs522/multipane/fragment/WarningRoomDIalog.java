package edu.stevens.cs522.multipane.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import edu.stevens.cs522.multipane.R;

/**
 * Created by MehulGupta on 4/3/16.
 */
public class WarningRoomDIalog extends DialogFragment {

    public static final String TAG = AddRoomDialog.class.getCanonicalName();

    private EditText mChatroomText;
    private Button mSendBtn;
    private Button mCancelBtn;

    public WarningRoomDIalog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("ChatRoom Name Already Taken")
                .setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "onClick: yes");
                                dismiss();
                            }
                        });
        return builder.create();
    }
}
