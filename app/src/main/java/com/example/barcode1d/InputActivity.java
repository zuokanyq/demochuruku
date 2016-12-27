package com.example.barcode1d;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InputActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_layout);
		Button button1 = (Button) findViewById(R.id.button_1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(InputActivity.this, SecondActivity.class);
//				intent.putExtra("param1", "data1");
//				intent.putExtra("param2", "data2");
//				startActivity(intent);
//				SecondActivity.actionStart(InputActivity.this, "data1", "data2");
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("InputActivity", "onRestart");
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				String returnedData = data.getStringExtra("data_return");
				Log.d("InputActivity", returnedData);
			}
			break;
		default:
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.input, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.action_output:
				showoutput();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showoutput(){

	}

}
