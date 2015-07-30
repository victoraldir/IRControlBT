package com.example.android.asynctasks;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

public class _IRReceiverAssync extends AsyncTask<String, Integer, Boolean> {


	Context mContext;

	// a progress bar - displayed when tweets are retrieved
	private static ProgressDialog progressDialog;
	
	public _IRReceiverAssync(Context mContext) {
		super();
		this.mContext = mContext;

	}

	
	@Override
    protected Boolean doInBackground(String... params) {
        boolean msg = true;

        try {
            Thread.sleep(5000);
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

		} else {

		}
    }

	/**
	 * Shows a Progress Dialog with a Cancel Button
	 *
	 * @param msg
	 */
	public void showProgressDialog(String msg)
	{
		// check for existing progressDialog
		if (progressDialog == null) {
			// create a progress Dialog
			progressDialog = new ProgressDialog(mContext);

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
