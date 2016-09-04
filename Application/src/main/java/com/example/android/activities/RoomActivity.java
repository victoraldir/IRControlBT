package com.example.android.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.application.MyApplication;
import com.example.android.persistence.Ambiente;
import com.example.android.persistence.MySQLiteHelper;
import com.example.android.persistence.OperationType;


public class RoomActivity extends ActionBarActivity {

    EditText editAmbienteDescricao;
    Button buttonAmbienteCadastrar;
    Ambiente roomSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        editAmbienteDescricao = (EditText) findViewById(R.id.editAmbienteDescricao);
        buttonAmbienteCadastrar = (Button) findViewById(R.id.buttonAmbienteCadastrar);

        buttonAmbienteCadastrar.setOnClickListener(eventoCadastrar);

        Bundle b = getIntent().getExtras();

        if(b != null && b.getString("opType", null) != null && b.getString("opType").equals(OperationType.EDIT.name())){
            try {
                //ambienteBanco = MySQLiteHelper.getInstance(getApplicationContext()).getRoomById(b.getLong("idRoom"));
                roomSelected = ((MyApplication) getApplication()).getRoomSelected();
                editAmbienteDescricao.setText(roomSelected.getDescricao());
            }catch (Exception ex){
                onDestroy();
            }
        }
    }

    private View.OnClickListener eventoCadastrar = new View.OnClickListener() {

        @SuppressWarnings("unchecked")
        public void onClick(View v) {

                String descricao = editAmbienteDescricao.getText().toString();

                if(descricao.equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Você precisa digitar uma descrição", Toast.LENGTH_SHORT);
                    toast.show();

                }else{

                    Ambiente newAmbiente = null;

                    if(roomSelected == null) {
                        newAmbiente = new Ambiente();

                    }else{
                        newAmbiente = roomSelected;
                    }

                    newAmbiente.setDescricao(descricao);
                    MySQLiteHelper.getInstance(getApplicationContext()).inserirAtualizarAmbiente(newAmbiente);
                    finish();
                }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ambiente, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
