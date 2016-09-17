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
import com.quartzo.ircontrol.persistence.Appliance;
import com.quartzo.ircontrol.persistence.Device;
import com.quartzo.ircontrol.persistence.MySQLiteHelper;
import com.quartzo.ircontrol.persistence.OperationType;


public class ApplianceActivity extends Activity {


    private EditText editApplianceDesc;
    private Button btnApplianceInsert;
    private Appliance applianceSelected = null;
    private Device deviceSelected;
    private MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance);

        myApplication = ((MyApplication) getApplication());

        editApplianceDesc = (EditText) findViewById(R.id.editApplianceDescription);
        btnApplianceInsert = (Button) findViewById(R.id.buttonApplianceInsert);

        btnApplianceInsert.setOnClickListener(evtInsert);

        Bundle b = getIntent().getExtras();

        deviceSelected = myApplication.getDeviceSelected();

        if (b != null && b.getString("opType", null) != null && b.getString("opType").equals(OperationType.EDIT.name())) {
            try {
                //appliancePersisted = MySQLiteHelper.getInstance(getApplicationContext()).getApplianceById(((MyApplication) getApplication()).getApplianceSelected().getId());
                applianceSelected = myApplication.getApplianceSelected();
                editApplianceDesc.setText(applianceSelected.getDescription());
            } catch (Exception ex) {
                onDestroy();
            }
        }
    }

    //private long idRoom;
    private View.OnClickListener evtInsert = new View.OnClickListener() {

        @SuppressWarnings("unchecked")
        public void onClick(View v) {

            String descricao = editApplianceDesc.getText().toString();

            if (descricao.equals("")) {
                Toast toast = Toast.makeText(getApplicationContext(), "Você precisa digitar uma descrição", Toast.LENGTH_SHORT);
                toast.show();

            } else {

                Appliance newAppliance = null;

                if (applianceSelected == null) {
                    try {
                        newAppliance = new Appliance();
                        newAppliance.setDevice(deviceSelected);
                    } catch (Exception ex) {
                        onDestroy();
                    }
                } else {
                    newAppliance = applianceSelected;
                }

                newAppliance.setDescription(descricao);
                MySQLiteHelper.getInstance(getApplicationContext()).insertUpdateAppliance(newAppliance);
                finish();
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
