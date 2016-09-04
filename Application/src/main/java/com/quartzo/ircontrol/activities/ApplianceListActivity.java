package com.quartzo.ircontrol.activities;

import android.app.AlertDialog;
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
import com.example.android.bluetoothchat.R;
import com.quartzo.ircontrol.persistence.Ambiente;
import com.quartzo.ircontrol.persistence.Dispositivo;
import com.quartzo.ircontrol.persistence.MySQLiteHelper;
import com.quartzo.ircontrol.persistence.OperationType;

import java.text.ParseException;
import java.util.List;


public class ApplianceListActivity extends ActionBarActivity {

    private ListView listViewAppliance;

    //private long idRoom;
    private Ambiente roomSelected;
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

                    if (items[item].equals("Editar")) {

                        ((MyApplication) getApplication()).setApplianceSelected(appliance);
                        Bundle b = new Bundle();
                        b.putString("opType", OperationType.EDIT.name());
                        Intent roomIntent = new Intent(getApplication(), ApplianceActivity.class);
                        roomIntent.putExtras(b);
                        startActivity(roomIntent);

                    } else {
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

    private void loadAppliances() {

        try {
            List<Dispositivo> appliances = MySQLiteHelper.getInstance(getApplicationContext()).listAppliancesByIdRoom(roomSelected.getId());

            if (appliances != null && appliances.size() != 0) {
                ArrayAdapter<Dispositivo> adapter = new ArrayAdapter<Dispositivo>(this, android.R.layout.simple_list_item_1, appliances);

                listViewAppliance.setAdapter(adapter);
            } else {
                listViewAppliance.setAdapter(null);
            }

        } catch (ParseException e) {
            onDestroy();
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
                Intent it = new Intent(getApplicationContext(), ApplianceActivity.class);
                startActivity(it);
        }

        return false;
    }


}
