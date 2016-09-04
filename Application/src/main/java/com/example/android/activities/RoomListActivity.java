/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.application.MyApplication;
import com.example.android.persistence.Ambiente;
import com.example.android.persistence.MySQLiteHelper;
import com.example.android.persistence.OperationType;

import java.util.List;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class RoomListActivity extends ActionBarActivity {

    public static final String TAG = "RoomListActivity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    ListView listViewRoom;

    android.view.ActionMode mActionMode;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        mContext = getApplicationContext();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();

        }

//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
//            // Otherwise, setup the chat session
//        } else if (mChatService == null) {
//            setupChat();
//        }

        listViewRoom = (ListView) findViewById(R.id.listViewRoom);

        listViewRoom.setOnItemLongClickListener(evtLongClick);

        listViewRoom.setOnItemClickListener(evtClick);

        carregarListaAmbientes();

    }

    private AdapterView.OnItemClickListener evtClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Ambiente room = ((Ambiente) listViewRoom.getAdapter().getItem(position));
            ((MyApplication) getApplication()).setRoomSelected(room);
            //Bundle b = new Bundle();

            //b.putLong("idRoom", room.getId());
            Intent applianceIntent = new Intent(getApplication(), ApplianceListActivity.class);
            //applianceIntent.putExtras(b);
            startActivity(applianceIntent);
        }
    };

    private AdapterView.OnItemLongClickListener evtLongClick = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            final CharSequence[] items = {
                    "Editar", "Excluir"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(RoomListActivity.this);
            //builder.setTitle("Make your selection");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    Ambiente room = ((Ambiente) listViewRoom.getAdapter().getItem(position));

                    if(items[item].equals("Editar")){

                        ((MyApplication) getApplication()).setRoomSelected(room);

                        Bundle b = new Bundle();
                        b.putString("opType", OperationType.EDIT.name());
                        Intent roomIntent = new Intent(getApplication(), RoomActivity.class);
                        roomIntent.putExtras(b);
                        startActivity(roomIntent);

                    }else{
                        MySQLiteHelper.getInstance(getApplication()).deleteRoom(room.getId());
                        carregarListaAmbientes();
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
    };

    @Override
    protected void onResume() {
        carregarListaAmbientes();
        super.onResume();
    }

    private void carregarListaAmbientes(){
        List<Ambiente> ambientes = MySQLiteHelper.getInstance(getApplicationContext()).listarAmbientes();

        if(ambientes != null && ambientes.size() != 0) {
            ArrayAdapter<Ambiente> adapter = new ArrayAdapter<Ambiente>(this, android.R.layout.simple_list_item_1, ambientes);

            listViewRoom.setAdapter(adapter);
        }else{
            listViewRoom.setAdapter(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getApplication(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getApplication(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.adicionarAmbiente: {
                Bundle b = new Bundle();
                b.putString("opType", OperationType.INSERT.name());
                Intent ambienteIntent = new Intent(getApplication(), RoomActivity.class);
                ambienteIntent.putExtras(b);
                startActivity(ambienteIntent);
                return true;
            }
            case R.id.bluetooth: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getApplication(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {
                // Ensure this device is discoverable by others
                //ensureDiscoverable();
                return true;
            }
        }
        return false;
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:

                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:

                break;
            case REQUEST_ENABLE_BT:

        }
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        //FragmentActivity activity = getActivity();
        if (null == this) {
            return;
        }
        final ActionBar actionBar = this.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle);
    }

}
