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

package com.quartzo.ircontrol.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
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

import com.quartzo.ircontrol.application.MyApplication;
import com.quartzo.ircontrol.R;
import com.quartzo.ircontrol.persistence.Device;
import com.quartzo.ircontrol.persistence.MySQLiteHelper;
import com.quartzo.ircontrol.persistence.OperationType;

import java.util.List;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class DeviceListActivity extends ActionBarActivity {

    public static final String TAG = "RoomListActivity";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    ListView listViewRoom;
    android.view.ActionMode mActionMode;
    Context mContext;
    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;
    /**
     * Local Bluetooth adapter
     */

    private AdapterView.OnItemClickListener evtClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Device room = ((Device) listViewRoom.getAdapter().getItem(position));
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

            AlertDialog.Builder builder = new AlertDialog.Builder(DeviceListActivity.this);
            //builder.setTitle("Make your selection");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    Device room = ((Device) listViewRoom.getAdapter().getItem(position));

                    if (items[item].equals("Editar")) {

                        ((MyApplication) getApplication()).setRoomSelected(room);

                        Bundle b = new Bundle();
                        b.putString("opType", OperationType.EDIT.name());
                        Intent roomIntent = new Intent(getApplication(), DeviceActivity.class);
                        roomIntent.putExtras(b);
                        startActivity(roomIntent);

                    } else {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        mContext = getApplicationContext();

        listViewRoom = (ListView) findViewById(R.id.listViewRoom);

        listViewRoom.setOnItemLongClickListener(evtLongClick);

        listViewRoom.setOnItemClickListener(evtClick);

        carregarListaAmbientes();

    }

    @Override
    protected void onResume() {
        carregarListaAmbientes();
        super.onResume();
    }

    private void carregarListaAmbientes() {
        List<Device> ambientes = MySQLiteHelper.getInstance(getApplicationContext()).listarAmbientes();

        if (ambientes != null && ambientes.size() != 0) {
            ArrayAdapter<Device> adapter = new ArrayAdapter<Device>(this, android.R.layout.simple_list_item_1, ambientes);

            listViewRoom.setAdapter(adapter);
        } else {
            listViewRoom.setAdapter(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.add:
                Intent it = new Intent(getApplicationContext(),DeviceActivity.class);
                startActivity(it);
                break;
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
