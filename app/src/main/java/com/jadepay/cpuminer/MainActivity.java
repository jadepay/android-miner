package com.jadepay.cpuminer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	String fserver,fport,fuser,fpass;
	private CPUMinerApplication application;
	private Button startButton;
	private Button stopButton;
	private Button clearButton;
	private Button saveButton;
	private TextView logView;
	public Spinner spinner;
	public Spinner spinner1;
	public EditText serverKey;
	public EditText portKey;
	public EditText userKey;
	public EditText passKey;
	private StateReceiver stateReceiver;
	private LogReceiver logReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String server = prefs.getString(getString(R.string.pref_server_key), null);
		String port = prefs.getString(getString(R.string.pref_port_key), null);
		String username = prefs.getString(getString(R.string.pref_username_key), null);
		String password = prefs.getString(getString(R.string.pref_password_key), null);

		serverKey   = (EditText)findViewById(R.id.id_server_key);
		if (server != null)
			serverKey.setText(server);
		portKey = (EditText)findViewById(R.id.id_port_key);
		if (port != null)
			portKey.setText(port);
		userKey   = (EditText)findViewById(R.id.id_username_key);
		if (username != null)
			userKey.setText(username);
		passKey = (EditText)findViewById(R.id.id_pass_key);
		if (password != null)
			passKey.setText(password);

		spinner = (Spinner)findViewById(R.id.spinner);
		spinner.setOnItemSelectedListener(new ItemSelectedListener());
		List<String> Algorithm = new ArrayList<String>();
		Algorithm.add("jad8");
		Algorithm.add("scrypt");
		Algorithm.add("sha256d");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Algorithm);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		spinner1 = (Spinner)findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new ItemSelectedListener());
		List<String> Protocol = new ArrayList<String>();
		Protocol.add("stratum+tcp://");
		Protocol.add("http://");
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Protocol);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(dataAdapter1);



		// Get a reference to the application
		application = (CPUMinerApplication)getApplication();

		// Initialize the log view
		this.logView = (TextView)this.findViewById(R.id.textlog);
		this.logView.setMovementMethod(new ScrollingMovementMethod());
		logView.setText(application.getTextLog());

		// Initialize the start/stop buttons
		this.startButton = (Button)this.findViewById(R.id.start_button);
		this.stopButton = (Button)this.findViewById(R.id.stop_button);

		this.startButton.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				startMining();
		    }
		  });
		this.stopButton.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				stopMining();
		    }
		  });

		// Initialize the clear button
		this.clearButton = (Button)this.findViewById(R.id.clear_button);
		this.clearButton.setOnClickListener(new OnClickListener() {
			@Override
		    public void onClick(View v) {
				CPUMinerApplication app = (CPUMinerApplication)getApplication();
				app.clearTextLog();
				logView.setText("");
				logView.scrollTo(0, 0);
		    }
		  });

		// Initialize the clear button
		this.saveButton = (Button)this.findViewById(R.id.save_button);
		final SharedPreferences prefes = PreferenceManager.getDefaultSharedPreferences(this);
		this.saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				serverKey   = (EditText)findViewById(R.id.id_server_key);
				portKey = (EditText)findViewById(R.id.id_port_key);
				userKey   = (EditText)findViewById(R.id.id_username_key);
				passKey = (EditText)findViewById(R.id.id_pass_key);
				fserver=serverKey.getText().toString();
				fport=portKey.getText().toString();
				fuser=userKey.getText().toString();
				fpass=passKey.getText().toString();
				String algoItem = String.valueOf(spinner.getSelectedItem());
				String protoItem = String.valueOf(spinner1.getSelectedItem());
				SharedPreferences.Editor edit = prefes.edit();
				edit.putString(getString(R.string.pref_server_key), fserver);
				edit.putString(getString(R.string.pref_port_key), fport);
				edit.putString(getString(R.string.pref_username_key), fuser);
				edit.putString(getString(R.string.pref_password_key), fpass);
				edit.putString(getString(R.string.pref_algorithm_key), algoItem);
				edit.putString(getString(R.string.pref_protocol_key), protoItem);
				edit.apply();
				Toast.makeText(MainActivity.this,"Saved!!",Toast.LENGTH_LONG).show();
				startButton.setEnabled(true);
				startButton.setAlpha(1f);
				log("Saved configurations:\nPool:" + protoItem + fserver +":"+ fport +"\nuser&pass:"+ fuser +":"+ fpass + "\nAlgo:" +algoItem);

			}
		});
		// Register for logging messages
		IntentFilter logIntentFilter = new IntentFilter("com.jadepay.cpuminer.logMessage");
		this.logReceiver = new LogReceiver(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(logReceiver, logIntentFilter);

		// Register for logging messages
		IntentFilter stateIntentFilter = new IntentFilter("com.jadepay.cpuminer.stateMessage");
		this.stateReceiver = new StateReceiver(this);
		LocalBroadcastManager.getInstance(this).registerReceiver(stateReceiver, stateIntentFilter);

		// This may log, so we can't do it before we create the handler
		this.updateButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem preferences = menu.findItem(R.id.action_settings);
		Intent prefsIntent = new Intent(this.getApplicationContext(), CPUMinerPreferences.class);
		preferences.setIntent(prefsIntent);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_settings:
	        this.startActivity(item.getIntent());
	        break;
	    }
	    return true;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
			this.updateButtons();
	}

	public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

		//get strings of first item
		String algoItem = "jad8";
		String algoItem1 = "scrypt";
		String algoItem2 = "sha256d";
		String protoItem = "stratum+tcp://";
		String protoItem1 = "http://";
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		SharedPreferences.Editor edit = prefs.edit();
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			String checkItem = parent.getItemAtPosition(pos).toString();
			if (checkItem.equals(algoItem) || checkItem.equals(algoItem1) || checkItem.equals(algoItem2)) {
				edit.putString(getString(R.string.pref_algorithm_key), checkItem);
				edit.apply();

				Toast.makeText(parent.getContext(),
						"Selected Algorithm: " + checkItem,
						Toast.LENGTH_LONG).show();
			} else if (checkItem.equals(protoItem) || checkItem.equals(protoItem1)){
				edit.putString(getString(R.string.pref_protocol_key), checkItem);
				edit.apply();

				Toast.makeText(parent.getContext(),
						"Selected Protocol: " + checkItem,
						Toast.LENGTH_LONG).show();
				// Todo when item is selected by the user
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg) {

		}

	}

	protected void updateButtons() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String server = prefs.getString(getString(R.string.pref_server_key), "");
		String port = prefs.getString(getString(R.string.pref_port_key), "");
		String username = prefs.getString(getString(R.string.pref_username_key), "");
		String password = prefs.getString(getString(R.string.pref_password_key), "");


		// Stop button should only be available if we're running
		if (application.isRunning() && !application.isStopping()) {
			this.stopButton.setEnabled(true);
			this.stopButton.setAlpha(1f);
		} else {
			this.stopButton.setEnabled(false);
			this.stopButton.setAlpha(0.5f);
		}
		// Don't attempt to start if we're missing configurations
		// The worker thread however, may take a while to exit
		// We don't want to allow the user to restart until it is finished
		if (application.isRunning() || application.isStopping()) {
			this.startButton.setEnabled(false);
			this.startButton.setAlpha(0.5f);
		} else if (server.length() > 0 && port.length() > 0 && username.length() > 0 && password.length() > 0){
			this.startButton.setEnabled(true);
			this.startButton.setAlpha(1f);
		} else {
			if (this.startButton.isEnabled())
				log("Save configurations and start mining!");
		    this.startButton.setEnabled(false);
			this.startButton.setAlpha(0.5f);
		}
	}

	// Start mining
	private void startMining() {
		application.start();
		this.updateButtons();
		this.saveButton.setEnabled(false);
		this.saveButton.setAlpha(0.5f);
	}

	// Stop mining
	private void stopMining() {
		application.stop();
		this.updateButtons();
		this.saveButton.setEnabled(true);
		this.saveButton.setAlpha(1f);
	}

	protected void log(String message)
	{
		// This gets executed on the UI thread so it can safely modify Views
		application.appendToTextLog(message);
		if (logView.getLineCount() < 2)
			logView.setText(application.getTextLog());
		else
			logView.append(message + "\n");

		// Scroll to the bottom of the textview
		final Layout layout = logView.getLayout();
		if (layout != null) {
			int scrollDelta = layout.getLineBottom(logView.getLineCount() - 1)
					- logView.getScrollY() - logView.getHeight();
			if(scrollDelta > 0)
				logView.scrollBy(0, scrollDelta);
		}
	}

	@Override
	protected void onDestroy() {
		// Unregister receivers since the activity is about to be closed.
		LocalBroadcastManager.getInstance(this).unregisterReceiver(logReceiver);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(stateReceiver);
		super.onDestroy();
	}

}
