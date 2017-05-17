package edu.stevens.cs522.multipane.activity;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.multipane.R;
import edu.stevens.cs522.multipane.database.DbAdapter;
import edu.stevens.cs522.multipane.entity.ChatMessage;
import edu.stevens.cs522.multipane.entity.Peer;
import edu.stevens.cs522.multipane.provider.MessageProviderCloud;


public class PeersActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = PeersActivity.class.getCanonicalName();
    ContentResolver cr;
    SimpleCursorAdapter msgAdapter;
    public String[] from = new String[] { MessageProviderCloud.ID, "name" };
    public int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
    ListView myListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peers);

        Log.i(TAG, "onCreate()");

        myListView = (ListView) findViewById(android.R.id.list);
        cr = this.getContentResolver();
        final Cursor cursor = cr.query(MessageProviderCloud.CONTENT_URI_PEER, null, null, null, null);

        if(cursor.moveToFirst()){
            Log.i(TAG, "Cursor values, id= "+cursor.getString(cursor.getColumnIndex(MessageProviderCloud.ID))+"name = "+cursor.getString(cursor.getColumnIndex("name")));
        }
        msgAdapter = new SimpleCursorAdapter(getApplicationContext(), android.R.layout.simple_list_item_2, cursor, from, to);

        msgAdapter.swapCursor(cursor);
        setListAdapter(msgAdapter);


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "item id = " + id);
                cr = getApplicationContext().getContentResolver();
                Cursor cursorPeer = cr.query(MessageProviderCloud.CONTENT_URI_PEER,
                        new String[]{MessageProviderCloud.SENDERNAME},
                        MessageProviderCloud.DATABASE_TABLE_PEER+"."+MessageProviderCloud.ID+"=?",
                        new String[]{String.valueOf(id)},
                        null);
                if(cursorPeer.moveToFirst()){
                    String peerName = cursorPeer.getString(cursorPeer.getColumnIndex(MessageProviderCloud.SENDERNAME));
                    Log.i(TAG, "setOnOtemClickListener, peerName= "+peerName);

                    Cursor  cursorPeerMessages = cr.query(MessageProviderCloud.CONTENT_URI,
                            new String[]{MessageProviderCloud.ID, MessageProviderCloud.TEXT, MessageProviderCloud.SENDER, MessageProviderCloud.DATE, MessageProviderCloud.SENDERID_FK, MessageProviderCloud.CHATROOM_FK},
                            MessageProviderCloud.DATABASE_TABLE_MESSAGE+"."+MessageProviderCloud.SENDER+"=?",
                            new String[]{peerName},
                            null);

                    if(cursorPeerMessages.moveToFirst()){
                        ChatMessage message = new ChatMessage(cursorPeerMessages);
                        Log.i(TAG, "setOnOtemClickListener, peer Messages, msg= "+message.messageText);

                    }
                }

                /*String name;
                name = chatDbAdapter.getNameById(id);
                Log.i(TAG, "search peer: "+name);
                Cursor c = chatDbAdapter.getMessgeByPeer(name);
                startManagingCursor(c);
                String[] from = new String[] {"text", "sender" };
                int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
                dbAdapter = new SimpleCursorAdapter(getBaseContext(), android.R.layout.simple_list_item_2, c, from, to);
                setListAdapter(dbAdapter);
                setSelection(0);*/
            }
        });

        registerForContextMenu(myListView);

    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        return new CursorLoader(this,
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
