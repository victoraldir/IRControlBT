package com.example.android.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.application.MyApplication;
import com.example.android.bluetoothchat.R;
import com.example.android.persistence.Comando;
import com.example.android.persistence.Dispositivo;
import com.example.android.persistence.MySQLiteHelper;
import com.example.android.persistence.Posicao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class CommandActivity extends ActionBarActivity {

    Dispositivo applianceSelected;
    Context mContext;

    List<Button> buttons = new ArrayList<Button>();

    RelativeLayout rel;

    Dialog dialogLabel;

    String codeReceived = null;

    Button buttonSelected;
    private View.OnClickListener evtShortClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                Comando cmd = MySQLiteHelper.getInstance(mContext).getCommandById(applianceSelected.getId(), Posicao.valueOf(v.getTag().toString()).ordinal());

                buttonSelected = (Button) v;

                if (cmd == null) {
                    CommandActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new AlertDialog.Builder(CommandActivity.this)
                                    .setTitle("Posição vazia")
                                    .setMessage("Deseja cadastrar um novo comando?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            IRReceiverAssync irReceiver = new IRReceiverAssync(getApplication());
                                            irReceiver.execute();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // do noth        ing
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });

                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        rel = (RelativeLayout) findViewById(R.id.layout_commands);

        rel.getChildCount();

        applianceSelected = ((MyApplication) getApplication()).getApplianceSelected();

        mContext = getApplication();

        loadCustomDialogForm();

        initButtons();
    }

    private void loadCustomDialogForm() {
        dialogLabel = new Dialog(CommandActivity.this);

        dialogLabel.setContentView(R.layout.dialog_custon_command);
        dialogLabel.setTitle("Rótulo do botão");

        // set the custom dialog components - text, image and button
        Button button = (Button) dialogLabel.findViewById(R.id.btnSave);
        final EditText edtRodulo = (EditText) dialogLabel.findViewById(R.id.buttonLabel);
        // if button is clicked, close the custom dialog
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogLabel.dismiss();
                Comando cmd = new Comando();

                cmd.setCodigo(codeReceived);
                cmd.setDescricao(edtRodulo.getText().toString());
                cmd.setPosicao(Posicao.valueOf(buttonSelected.getTag().toString()));
                cmd.setDispositivo(applianceSelected);

                MySQLiteHelper.getInstance(mContext).inserirAtualizarComando(cmd);

                buttonSelected.setText(cmd.getDescricao());
                dialogLabel.dismiss();
            }
        });

    }

    private void initButtons() {

        for (int i = 0; i < rel.getChildCount(); i++) {
            View child = rel.getChildAt(i);
            child.setOnClickListener(evtShortClick);

            try {
                Comando cmd = MySQLiteHelper.getInstance(mContext).getCommandById(applianceSelected.getId(), Posicao.valueOf(child.getTag().toString()).ordinal());

                if (cmd != null) {
                    ((Button) child).setText(cmd.getDescricao());
                } else {
                    ((Button) child).setText("Vazio");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_command, menu);
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


    public class IRReceiverAssync extends AsyncTask<String, Integer, Boolean> {


        Context mContext;


        // a progress bar - displayed when tweets are retrieved
        private ProgressDialog progressDialog;
        private AlertDialog.Builder evtCodeReceived = new AlertDialog.Builder(CommandActivity.this)
                .setTitle("Código Recebido")
                .setMessage("Deseja testar o comando?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        IREmiterAssync irReceiver = new IREmiterAssync(getApplication(), codeReceived);
                        irReceiver.execute();

//                synchronized (irReceiver) {
//                    String dd ="";
//                }


                    }
                })
                .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        IRReceiverAssync irReceiver = new IRReceiverAssync(getApplication());
                        irReceiver.execute();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do noth        ing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);


        public IRReceiverAssync(Context mContext) {
            super();
            this.mContext = mContext;

        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean msg = true;

            try {
                //TODO add request code here
                Thread.sleep(1000);
                codeReceived = "CODE RECEIVED";
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return msg;
        }

        @Override
        protected void onPreExecute() {
            // show the progress bar
            this.showProgressDialog("Aguardando comando...");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                evtCodeReceived.show();
            } else {

            }

            hideProgressDialog();
        }

        /**
         * Shows a Progress Dialog with a Cancel Button
         *
         * @param msg
         */
        public void showProgressDialog(String msg) {
            // check for existing progressDialog
            if (progressDialog == null) {
                // create a progress Dialog
                progressDialog = new ProgressDialog(CommandActivity.this);

                // remove the ability to hide it by tapping back button
                progressDialog.setIndeterminate(true);

                progressDialog.setCancelable(false);

                progressDialog.setMessage(msg);

                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    finalize();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        });

            }

            // now display it.
            progressDialog.show();
        }


        /**
         * Hides the Progress Dialog
         */
        public void hideProgressDialog() {

            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            progressDialog = null;
        }

        public void postExecute(String response) {
            // hide the progress bar and then show a Toast
            this.hideProgressDialog();
            Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
        }
    }

    public class IREmiterAssync extends AsyncTask<String, Integer, Boolean> {


        Context mContext;
        String codeToSend;

        // a progress bar - displayed when tweets are retrieved
        private ProgressDialog progressDialog;
        private AlertDialog.Builder evtCodeSent = new AlertDialog.Builder(CommandActivity.this)
                .setTitle("Código Enviado")
                .setMessage("Funcionou?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialogLabel.show();
                    }
                })
                .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        IRReceiverAssync irReceiver = new IRReceiverAssync(getApplication());
                        irReceiver.execute();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);


        public IREmiterAssync(Context mContext, String codeToSend) {
            super();
            this.mContext = mContext;
            this.codeToSend = codeToSend;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            boolean msg = true;

            try {
                //Toast.makeText(CommandActivity.this, "Enviando... " + codeToSend, Toast.LENGTH_LONG).show();
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return msg;
        }

        @Override
        protected void onPreExecute() {
            // show the progress bar
            this.showProgressDialog("Enviando comando...");
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                evtCodeSent.show();
            } else {

            }

            hideProgressDialog();
        }

        /**
         * Shows a Progress Dialog with a Cancel Button
         *
         * @param msg
         */
        public void showProgressDialog(String msg) {
            // check for existing progressDialog
            if (progressDialog == null) {
                // create a progress Dialog
                progressDialog = new ProgressDialog(CommandActivity.this);

                // remove the ability to hide it by tapping back button
                progressDialog.setIndeterminate(true);

                progressDialog.setCancelable(false);

                progressDialog.setMessage(msg);

                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
                        new Dialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    finalize();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }
                        });

            }

            // now display it.
            progressDialog.show();
        }


        /**
         * Hides the Progress Dialog
         */
        public void hideProgressDialog() {

            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            progressDialog = null;
        }

        public void postExecute(String response) {
            // hide the progress bar and then show a Toast
            this.hideProgressDialog();
            Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
        }
    }
}
