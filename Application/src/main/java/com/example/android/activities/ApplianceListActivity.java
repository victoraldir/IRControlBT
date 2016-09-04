package com.example.android.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.application.MyApplication;
import com.example.android.persistence.Ambiente;
import com.example.android.persistence.Dispositivo;
import com.example.android.persistence.MySQLiteHelper;
import com.example.android.persistence.OperationType;

import java.text.ParseException;
import java.util.List;


public class ApplianceListActivity extends ActionBarActivity {

    private ListView listViewAppliance;

    //private long idRoom;
    private Ambiente roomSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance_list);

        //Bundle b = getIntent().getExtras();

//        if(b != null && b.getLong("idRoom", 0) != 0){
//                idRoom = b.getLong("idRoom",0);
//        }

        roomSelected = ((MyApplication) getApplication()).getRoomSelected();

        listViewAppliance = (ListView) findViewById(R.id.listViewAppliance);

        listViewAppliance.setOnItemLongClickListener(evtLongClick);

        listViewAppliance.setOnItemClickListener(evtClick);


        loadAppliances();
    }

    @Override
    protected void onResume() {
        loadAppliances();
        super.onResume();
    }

    private void loadAppliances(){

        try {
            List<Dispositivo> appliances = MySQLiteHelper.getInstance(getApplicationContext()).listAppliancesByIdRoom(roomSelected.getId());

            if(appliances != null && appliances.size() != 0) {
                ArrayAdapter<Dispositivo> adapter = new ArrayAdapter<Dispositivo>(this, android.R.layout.simple_list_item_1, appliances);

                listViewAppliance.setAdapter(adapter);
            }else{
                listViewAppliance.setAdapter(null);
            }

        } catch (ParseException e) {
            onDestroy();
        }

    }

    private AdapterView.OnItemClickListener evtClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Dispositivo appliance = ((Dispositivo) listViewAppliance.getAdapter().getItem(position));

            ((MyApplication) getApplication()).setApplianceSelected(appliance);

            //Bundle b = new Bundle();

            //b.putLong("idAppliance", appliance.getId());
            Intent commandIntent = new Intent(getApplication(), CommandActivity.class);
            //commandIntent.putExtras(b);
            startActivity(commandIntent);
        }
    };

    private AdapterView.OnItemLongClickListener evtLongClick = new AdapterView.OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            final CharSequence[] items = {
                    "Editar", "Excluir"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ApplianceListActivity.this);
            //builder.setTitle("Make your selection");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    Dispositivo appliance = ((Dispositivo) listViewAppliance.getAdapter().getItem(position));

                    if(items[item].equals("Editar")){

                        ((MyApplication) getApplication()).setApplianceSelected(appliance);
                        Bundle b = new Bundle();
                        b.putString("opType", OperationType.EDIT.name());
                        Intent roomIntent = new Intent(getApplication(), ApplianceActivity.class);
                        roomIntent.putExtras(b);
                        startActivity(roomIntent);

                    }else{
                        MySQLiteHelper.getInstance(getApplication()).deletarDispositivo(appliance.getId());
                        loadAppliances();
                    }

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
    };

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
                //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getApplication(), DeviceListActivity.class);
                //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.adicionarAmbiente: {

                Intent applianceIntent = new Intent(getApplication(), ApplianceActivity.class);
                Bundle b = new Bundle();
                b.putString("opType", OperationType.INSERT.name());
                applianceIntent.putExtras(b);
                //Bundle b = new Bundle();
                //b.putLong("idRoom", idRoom);
                //applianceIntent.putExtras(b);
                startActivity(applianceIntent);
                return true;
            }
            case R.id.bluetooth: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getApplication(), DeviceListActivity.class);
                //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
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


}
