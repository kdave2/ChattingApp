package edu.stevens.cs522.multipane.activity;

import java.util.Date;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import edu.stevens.cs522.multipane.R;
import edu.stevens.cs522.multipane.constants.constant;
import edu.stevens.cs522.multipane.entity.ChatMessage;
import edu.stevens.cs522.multipane.fragment.AddRoomDialog;
import edu.stevens.cs522.multipane.fragment.SendMessageDialog;
import edu.stevens.cs522.multipane.fragment.WarningRoomDIalog;
import edu.stevens.cs522.multipane.provider.MessageProviderCloud;
import edu.stevens.cs522.multipane.service.AlarmReceiver;

/**
 * Created by KhushaliDave on 5/3/17.
 */
public class PeersFragment extends FragmentActivity {

    public static final String TAG = PeersFragment.class.getCanonicalName();

    ContentResolver cr;
    SimpleCursorAdapter msgAdapter;
    public String[] from = new String[] { MessageProviderCloud.ID, "name" };
    public int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
    ListView myListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_peers_fragment);
        Log.i(TAG, "onCreate()");

        SharedPreferences prefs = this.getSharedPreferences(constant.SHARED_PREF, Context.MODE_PRIVATE);
        Intent myIntent = getIntent();
        String clientName = prefs.getString(constant.NAME, "DefaultClient");

        try{
            //Peer
            PeerFragment pf = (PeerFragment) this.getSupportFragmentManager().findFragmentById(R.id.peers);
            pf.fresh();

            //messages
            if(clientName.equalsIgnoreCase("DefaultClient")){
                Log.i(TAG, "Error Finding Peer");
                Toast.makeText(PeersFragment.this, "Error Finding Peer", Toast.LENGTH_SHORT).show();
            }else {
                Cursor c = cr.query(MessageProviderCloud.CONTENT_URI_PEER, null, "name=?", new String[]{clientName}, null);
                long peerId = -1;
                if (c.moveToFirst()) {
                    peerId = c.getLong(0);
                    if (peerId < 0) {
                        Log.i(TAG, "Peer Not Available");
                    }
                }
                MessageFragment detailsFragment = (MessageFragment) this.getSupportFragmentManager().findFragmentById(R.id.messages);
                detailsFragment.fresh(peerId);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static class DetailsActivity extends FragmentActivity {
        int id;
        ListView msgList;
        ContentResolver cr;
        SimpleCursorAdapter msgAdapter;
        public String[] from = new String[]{MessageProviderCloud.ID, MessageProviderCloud.SENDERNAME};
        public int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        String clientId;
        private String clientName;
        private String portNo;
        private String hostStr;
        private String uuidStr;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // If the screen is now in landscape mode, we can show the
                // dialog in-line with the list so we don't need this activity.
                finish();
                return;
            }
            if (savedInstanceState == null) {
                // During initial setup, plug in the details fragment.
                MessageFragment msg = new MessageFragment();
                id = getIntent().getIntExtra(constant.INDEX, 0);
                msg.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, msg).commit();
            }
            SharedPreferences prefs = this.getSharedPreferences(constant.SHARED_PREF, Context.MODE_PRIVATE);
            Intent myIntent = getIntent();
            clientName = prefs.getString(constant.NAME, "DefaultClient");

            try{
                //Peer
                PeerFragment pf = (PeerFragment) this.getSupportFragmentManager().findFragmentById(R.id.peers);
                pf.fresh();

                //messages
                if(clientName.equalsIgnoreCase("DefaultClient")){
                    Log.i(TAG, "Error Finding Peer");
                    Toast.makeText(getApplicationContext(), "Error Finding Peer", Toast.LENGTH_SHORT).show();
                }else {
                    Cursor c = cr.query(MessageProviderCloud.CONTENT_URI_PEER, null, "name=?", new String[]{clientName}, null);
                    long peerId = -1;
                    if (c.moveToFirst()) {
                        peerId = c.getLong(0);
                        if (peerId < 0) {
                            Log.i(TAG, "Peer Not Available");
                        }
                    }
                    MessageFragment detailsFragment = (MessageFragment) this.getSupportFragmentManager().findFragmentById(R.id.messages);
                    detailsFragment.fresh(peerId);
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class PeerFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
        boolean mDualPane;
        int mCurCheckPosition = 0;
        int mShownCheckPosition = -1;

        ListView msgList;
        ContentResolver cr;
        SimpleCursorAdapter msgAdapter;
        public String[] from = new String[]{MessageProviderCloud.ID, MessageProviderCloud.SENDERNAME};
        public int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        String clientId;

        public void fresh() {
            cr = getActivity().getContentResolver();
            Cursor cursor = cr.query(MessageProviderCloud.CONTENT_URI_PEER, null, null, null, null);
            msgAdapter.changeCursor(cursor);
            setListAdapter(msgAdapter);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            cr = getActivity().getContentResolver();
            Cursor cursor = cr.query(MessageProviderCloud.CONTENT_URI_PEER, null, null, null, null);

            msgAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, cursor, from, to);

            msgAdapter.changeCursor(cursor);
            setListAdapter(msgAdapter);

            // Check to see if we have a frame in which to embed the messages
            // fragment directly in the containing UI.
            View detailsFrame = getActivity().findViewById(R.id.messages);
            mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

            if (savedInstanceState != null) {
                // Restore last state for checked position.
                mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
                mShownCheckPosition = savedInstanceState.getInt("shownChoice", -1);
            }

            if (mDualPane) {
                // In dual-pane mode, the list view highlights the selected
                // item.
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                // Make sure our UI is in the correct state.
                showDetails(mCurCheckPosition, 0);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", mCurCheckPosition);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            SharedPreferences prefs = getActivity().getSharedPreferences(constant.SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            Cursor c = ((SimpleCursorAdapter) l.getAdapter()).getCursor();
            c.moveToPosition(position);
            String selection = "_default";
            selection = c.getString(1);
            editor.putString(constant.PEER, selection);
            editor.commit();
            showDetails(position, id);
            Log.i(TAG, "item clicked, position:" + String.valueOf(position) + " id:" + String.valueOf(id));
        }

        /**
         * Helper function to show the details of a selected item, either by
         * displaying a fragment in-place in the current UI, or starting a whole
         * new activity in which it is displayed.
         */
        void showDetails(int position, long id) {
            mCurCheckPosition = position;
            //		Log.d("Show Detail for", String.valueOf(position));
            if (mDualPane) {
                // We can display everything in-place with fragments, so update
                // the list to highlight the selected item and show the data.
                getListView().setItemChecked(position, true);

                // Check what fragment is currently shown, replace if needed.
                if (mShownCheckPosition != mCurCheckPosition) {
                    // If we are not currently showing a fragment for the new
                    // position, we need to create and install a new one.
                    MessageFragment messageFragment = MessageFragment.newInstance((int) id);

                    // Execute a transaction, replacing any existing fragment
                    // with this one inside the frame.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.messages, messageFragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                    mShownCheckPosition = position;
                }
            } else {
                // Otherwise we need to launch a new activity to display
                // the dialog fragment with selected text.
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetailsActivity.class);
                intent.putExtra(constant.INDEX, (int) id);
                startActivity(intent);
            }
        }

        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

            return new CursorLoader(getActivity(),
                    MessageProviderCloud.CONTENT_URI_PEER,
                    MessageProviderCloud.peerProjection, null, null, null);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            this.msgAdapter.swapCursor(cursor);
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            this.msgAdapter.swapCursor(null);
        }
    }

    public static class MessageFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
        ListView msgList;
        ContentResolver cr;
        SimpleCursorAdapter msgAdapter;
        public String[] from = new String[]{MessageProviderCloud.SENDER, MessageProviderCloud.TEXT};
        public int[] to = new int[]{android.R.id.text1, android.R.id.text2};
        String clientId;

        /**
         * Create a new instance of DetailsFragment, initialized to show the
         * text at 'index'.
         */
        public void fresh(long mCurrentPosition) {
            updateMsgListAdapter(mCurrentPosition);
        }

        public static MessageFragment newInstance(int index) {
            MessageFragment messageFragment = new MessageFragment();
            // Supply index input as an argument.
            Bundle args = new Bundle();
            args.putInt(constant.INDEX, index);
            messageFragment.setArguments(args);
            return messageFragment;
        }

        public int getShownIndex() {
            int temp;
            Bundle b = getArguments();
            temp = b.getInt(constant.INDEX, 0);
            return temp;
        }

        int mCurrentPosition;

        @Override
        public void onStart() {
            super.onStart();

            // During startup, check if there are arguments passed to the fragment.
            // onStart is a good place to do this because the layout has already been
            // applied to the fragment at this point so we can safely call the method
            // below that sets the article text.
            Bundle args = getArguments();
            if (args != null) {
                // Set article based on argument passed in
                updateMsgListAdapter(getShownIndex());
            } else if (mCurrentPosition != -1) {
                // Set article based on saved instance state defined during onCreateView
                updateMsgListAdapter(mCurrentPosition);
            }
        }

        public void updateMsgListAdapter(long itemId) {
            // TextView article = (TextView)
            // getActivity().findViewById(R.id.article);
            // article.setText(Ipsum.Articles[position]);
            //mCurrentPosition = itemId;
            cr = getActivity().getContentResolver();
            Cursor cursor = cr.query(
                    Uri.parse(MessageProviderCloud.CONTENT_URI_CHATROOM.toString() + "/0"),
                    new String[]{"cols", "timestamp"}, MessageProviderCloud.SENDERID_FK+"=?",
                    new String[]{String.valueOf(itemId)}, null);

            msgAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_2, null, new String[]{"cols", "timestamp"}, new int[]{android.R.id.text1, android.R.id.text2});
            msgAdapter.swapCursor(cursor);
            msgAdapter.changeCursor(cursor);
            setListAdapter(msgAdapter);
        }

        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
            return new CursorLoader(getActivity(),
                    MessageProviderCloud.CONTENT_URI,
                    MessageProviderCloud.messageProjection, null, null, null);
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt(constant.INDEX, mCurrentPosition);
        }

        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            this.msgAdapter.swapCursor(cursor);
            this.msgAdapter.changeCursor(cursor);
        }

        public void onLoaderReset(Loader<Cursor> loader) {
            this.msgAdapter.swapCursor(null);
        }
    }


}
