package com.quartzo.ircontrol.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quartzo.ircontrol.R;
import com.quartzo.ircontrol.application.MyApplication;
import com.quartzo.ircontrol.persistence.Device;
import com.quartzo.ircontrol.persistence.MySQLiteHelper;
import com.quartzo.ircontrol.persistence.OperationType;


public class DeviceActivity extends Activity {

    EditText editHost;
    EditText editPort;
    EditText editDescription;
    Button btnSave;
    Device deviceSelected = null;
    private View.OnClickListener evtSave = new View.OnClickListener() {

        @SuppressWarnings("unchecked")
        public void onClick(View v) {

            String host = editHost.getText().toString();
            String port = editPort.getText().toString();
            String description = editDescription.getText().toString();

            if (description.equals("")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Você precisa digitar uma descrição", Toast.LENGTH_SHORT);
                toast.show();

            } else {

                Device device = null;

                if (deviceSelected == null) {
                    device = new Device();

                } else {
                    device = deviceSelected;
                }

                device.setHost(host);
                device.setPort(Integer.parseInt(port));
                device.setDescription(description);
                MySQLiteHelper.getInstance(getApplicationContext()).insertUpdateDevice(device);
                finish();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        editHost = (EditText) findViewById(R.id.editHost);
        editPort = (EditText) findViewById(R.id.editPort);
        editDescription = (EditText) findViewById(R.id.editDescription);
        btnSave = (Button) findViewById(R.id.btnDeviceSave);

        btnSave.setOnClickListener(evtSave);

        Bundle b = getIntent().getExtras();

        if (b != null && b.getString("opType", null) != null && b.getString("opType").equals(OperationType.EDIT.name())) {
            try {

                deviceSelected = ((MyApplication) getApplication()).getDeviceSelected();

                editHost.setText(deviceSelected.getHost());
                editPort.setText(String.valueOf(deviceSelected.getPort()));
                editDescription.setText(deviceSelected.getDescription());

            } catch (Exception ex) {
                onDestroy();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_ambiente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}
