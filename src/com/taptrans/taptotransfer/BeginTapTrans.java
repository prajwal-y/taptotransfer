package com.taptrans.taptotransfer;

import com.taptrans.filebrowser.FileBrowser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BeginTapTrans extends Activity {

	private static final int REQUEST_PATH = 1;
	String curFileName;
	EditText edittext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_begin_tap_trans);
		edittext = (EditText) findViewById(R.id.editText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_begin_tap_trans, menu);
		return true;
	}

	public void getFile(View view) {
		String extState = Environment.getExternalStorageState();
		if (!extState.equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(
					this,
					"External storage is not mounted. Please mount and try again.",
					Toast.LENGTH_LONG).show();
		} else {
			Intent intent1 = new Intent(this, FileBrowser.class);
			startActivityForResult(intent1, REQUEST_PATH);
		}
	}

	public void transferFile(View view) {

	}

	// Listen for results.
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// See which child activity is calling us back.
		if (requestCode == REQUEST_PATH) {
			if (resultCode == RESULT_OK) {
				curFileName = data.getStringExtra("GetFileName");
				edittext.setText(curFileName);
			}
		}
	}

}
