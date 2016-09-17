package com.quartzo.ircontrol.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.quartzo.ircontrol.BuildConfig;
import com.quartzo.ircontrol.R;
import com.quartzo.ircontrol.application.MyApplication;
import com.quartzo.ircontrol.persistence.Appliance;
import com.quartzo.ircontrol.persistence.Command;
import com.quartzo.ircontrol.persistence.Device;
import com.quartzo.ircontrol.persistence.MySQLiteHelper;
import com.quartzo.ircontrol.persistence.Position;
import com.quartzo.ircontrol.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ExecutionException;


public class CommandActivity extends Activity {

    private Appliance applianceSelected;
    private Context mContext;
    private RelativeLayout rel;
    private Dialog dialogLabel;
    private String codeReceived;
    private Button buttonSelected;
    private MySQLiteHelper db;
    private String TAG = "myIrcontrolLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);

        rel = (RelativeLayout) findViewById(R.id.layout_commands);
        applianceSelected = ((MyApplication) getApplication()).getApplianceSelected();
        mContext = getApplication();
        db = MySQLiteHelper.getInstance(mContext);

        loadCustomDialogForm();
        initButtons();
    }

    private View.OnClickListener evtShortClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final Command cmd = db.getCommandById(applianceSelected.getId(), Position.valueOf(v.getTag().toString()).ordinal());

            buttonSelected = (Button) v;

            if (cmd == null) {
                registerNewCommand();
            } else {
                sendCommand(cmd.getCode());
            }

        }
    };
    private View.OnLongClickListener evtLongClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            final Command cmd = db.getCommandById(applianceSelected.getId(), Position.valueOf(v.getTag().toString()).ordinal());

            buttonSelected = (Button) v;

            if (cmd != null) {

                final CharSequence[] items = {
                        "Editar", "Excluir"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(CommandActivity.this);
                //builder.setTitle("Make your selection");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Editar")) {

                            dialogLabel = new Dialog(CommandActivity.this);

                            dialogLabel.setContentView(R.layout.dialog_custon_command);
                            dialogLabel.setTitle("Rótulo do botão");

                            // set the custom dialog components - text, image and button
                            Button button = (Button) dialogLabel.findViewById(R.id.btnSave);
                            final EditText edtRotulo = (EditText) dialogLabel.findViewById(R.id.buttonLabel);
                            // if button is clicked, close the custom dialog
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //dialogLabel.dismiss();

                                    cmd.setDescription(edtRotulo.getText().toString());

                                    edtRotulo.setText("");

                                    db.insertUpdateCommand(cmd);

                                    buttonSelected.setText(cmd.getDescription());
                                    dialogLabel.dismiss();
                                }
                            });

                            dialogLabel.show();

                        } else {

                            db.deleteCommand(cmd.getAppliance().getId(), cmd.getPosition().ordinal());
                            buttonSelected.setText("Empty");
                            dialogLabel.dismiss();
                        }

                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }

            return false;
        }


    };

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
                Command cmd = new Command();

                cmd.setCode(codeReceived);
                cmd.setDescription(edtRodulo.getText().toString());
                cmd.setPosition(Position.valueOf(buttonSelected.getTag().toString()));
                cmd.setAppliance(applianceSelected);

                db.insertUpdateCommand(cmd);

                buttonSelected.setText(cmd.getDescription());
                dialogLabel.dismiss();
            }
        });

    }

    private void initButtons() {

        Map<Position, Command> commands = db.listCommandByApplianceId(applianceSelected.getId());

        for (int i = 0; i < rel.getChildCount(); i++) {
            View child = rel.getChildAt(i);
            child.setOnClickListener(evtShortClick);
            child.setOnLongClickListener(evtLongClick);

            Position pos = Position.valueOf(child.getTag().toString());

            Command cmd = commands.get(pos);

            if (cmd != null) {
                ((Button) child).setText(cmd.getDescription());
            } else {
                ((Button) child).setText("Empty");
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_command, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    public class IRReceiverAsync extends AsyncTask<String, Integer, Boolean> {

        private Context mContext;
        private Device device;

        public IRReceiverAsync(Context mContext, Device device) {
            super();
            this.mContext = mContext;
            this.device = device;

        }

        // a progress bar - displayed when tweets are retrieved
        private ProgressDialog progressDialog;

        private AlertDialog.Builder evtCodeReceived = new AlertDialog.Builder(CommandActivity.this)
                .setTitle("Code received!")
                .setMessage("Your new command has been received.")

                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialogLabel.show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        private AlertDialog.Builder evtCodeNotReceived = new AlertDialog.Builder(CommandActivity.this)
                .setTitle("Code hasn't been received!")
                .setMessage("Do you want to try again?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        IRReceiverAsync irReceiver = new IRReceiverAsync(getApplication(),applianceSelected.getDevice());
                        irReceiver.execute();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);

        @Override
        protected Boolean doInBackground(String... params) {
            boolean msg = true;

            String URL = device.getURLReceiver().toString();

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, future, future);
            VolleySingleton.getVolleySingleton(mContext).addToRequestQueue(request);

            try {
                JSONObject response = future.get();
                String code = response.getString("code");
                codeReceived = code;

                if(BuildConfig.DEBUG){
                    Log.d(TAG,"Code received was: " + code);
                }

            } catch (InterruptedException e) {
                msg = false;
            } catch (ExecutionException e) {
                msg = false;
            } catch (JSONException e) {
                msg = false;
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
                evtCodeNotReceived.show();
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

//    public class IRSenderAsync extends AsyncTask<String, Integer, Boolean> {
//
//
//        private Context mContext;
//        private Device device;
//        private String irCommand;
//
//        // a progress bar - displayed when tweets are retrieved
//        private ProgressDialog progressDialog;
//        private AlertDialog.Builder evtCodeSent = new AlertDialog.Builder(CommandActivity.this)
//                .setTitle("Código Enviado")
//                .setMessage("Funcionou?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialogLabel.show();
//                    }
//                })
//                .setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        IRReceiverAsync irReceiver = new IRReceiverAsync(getApplication(), device);
//                        irReceiver.execute();
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert);
//
//
//        public IRSenderAsync(Context mContext, Device device, String irCommand) {
//            super();
//            this.mContext = mContext;
//            this.device = device;
//            this.irCommand = irCommand;
//        }
//
//        @Override
//        protected Boolean doInBackground(String... params) {
//            boolean msg = true;
//
//            JsonObjectRequest jsObjRequest = new JsonObjectRequest
//                    (Request.Method.GET, device.getURLSender(irCommand).toString(), null, new Response.Listener<JSONObject>() {
//
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Toast.makeText(CommandActivity.this, response.toString(), Toast.LENGTH_LONG).show();
//                        }
//                    }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Toast.makeText(CommandActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//
//                        }
//                    });
//
//            VolleySingleton.getVolleySingleton(mContext).addToRequestQueue(jsObjRequest);
//
//
//            return msg;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            // show the progress bar
//            this.showProgressDialog("Enviando comando...");
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            if (result) {
//                evtCodeSent.show();
//            } else {
//
//            }
//
//            hideProgressDialog();
//        }
//
//        /**
//         * Shows a Progress Dialog with a Cancel Button
//         *
//         * @param msg
//         */
//        public void showProgressDialog(String msg) {
//            // check for existing progressDialog
//            if (progressDialog == null) {
//                // create a progress Dialog
//                progressDialog = new ProgressDialog(CommandActivity.this);
//
//                // remove the ability to hide it by tapping back button
//                progressDialog.setIndeterminate(true);
//
//                progressDialog.setCancelable(false);
//
//                progressDialog.setMessage(msg);
//
//                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
//                        new Dialog.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
//                                    finalize();
//                                } catch (Throwable throwable) {
//                                    throwable.printStackTrace();
//                                }
//                            }
//                        });
//
//            }
//
//            // now display it.
//            progressDialog.show();
//        }
//
//
//        /**
//         * Hides the Progress Dialog
//         */
//        public void hideProgressDialog() {
//
//            if (progressDialog != null) {
//                progressDialog.dismiss();
//            }
//
//            progressDialog = null;
//        }
//
//        public void postExecute(String response) {
//            // hide the progress bar and then show a Toast
//            this.hideProgressDialog();
//            Toast.makeText(mContext, response, Toast.LENGTH_LONG).show();
//        }
//    }

    private Response.Listener listener = new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            Toast.makeText(CommandActivity.this, response.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(CommandActivity.this, "Something went wrong " + error.getMessage(), Toast.LENGTH_LONG).show();

        }
    };

    private JsonObjectRequest createJsonObjectRequest(String url, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, listener, errorListener);

        return  jsObjRequest;
    }

    public void sendCommand(String code){

        String URL = applianceSelected.getDevice().getURLSender(code).toString();

        VolleySingleton.getVolleySingleton(mContext).addToRequestQueue(createJsonObjectRequest(URL,listener,errorListener));

    }

    public void registerNewCommand(){
        CommandActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(CommandActivity.this)
                        .setTitle("Empty postition")
                        .setMessage("Do you want to register a new command?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                IRReceiverAsync irReceiver = new IRReceiverAsync(getApplication(),applianceSelected.getDevice());
                                irReceiver.execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

}
