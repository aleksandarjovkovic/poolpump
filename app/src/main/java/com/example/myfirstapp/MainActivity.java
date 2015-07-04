package com.example.myfirstapp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.sax.TextElementListener;
import android.text.format.DateFormat;
import android.text.style.ReplacementSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;




@SuppressLint("ValidFragment")
public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	private ProgressBar pb;
	private EditText current_time;
	private EditText start_time;
	private EditText avg_hr;
	private EditText avg_min;
	private Button send_btn;
	private String string_to_server;
    private String response = "";
    private String tokens[];
    private String btn_control = "OFF";
    
    private TextView pmp_stat;
    private TextView temp_pump;
    private TextView currtime_pump;
    private TextView starttime_pump;
    private TextView avgrt_pump;
    private ToggleButton tog_btn;
	
	//public TextView theResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pb=(ProgressBar)findViewById(R.id.progressBar1);
        pb.setVisibility(View.GONE);

        //new action bar tabs enabled
        ActionBar actionbar = getActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        ActionBar.Tab Frag1Tab = actionbar.newTab().setIcon(R.drawable.ic_action_refresh);
        //ActionBar.Tab Frag1Tab = actionbar.newTab().setText("Status");
        //ActionBar.Tab Frag2Tab = actionbar.newTab().setText("Update");
        ActionBar.Tab Frag2Tab = actionbar.newTab().setIcon(R.drawable.ic_action_send);
        //ActionBar.Tab Frag3Tab = actionbar.newTab().setText("Usage");
        
        Fragment Fragment1 = new Fragment_1();
        Fragment Fragment2 = new Fragment_2();
        //Fragment Fragment3 = new Fragment_3();
        
        Frag1Tab.setTabListener(new MyTabsListener(Fragment1));
        Frag2Tab.setTabListener(new MyTabsListener(Fragment2));
        //Frag3Tab.setTabListener(new MyTabsListener(Fragment3));
        
        actionbar.addTab(Frag1Tab);
        actionbar.addTab(Frag2Tab);
        //actionbar.addTab(Frag3Tab);
        
        //------------------------------
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_activity_actions,  menu);
    	return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
            	//pb.setVisibility(View.VISIBLE);
            	//new MyAsyncTask().execute("Hello test test test sdfkaljherlk wherkj jisoajfo oh weior rjw ejlr");
                //openSearch();
                return true;
            case R.id.action_settings:
                //openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void onToggleClicked(View view) {
        // Is the toggle on?
    	tog_btn = (ToggleButton)findViewById(R.id.toggleButton1);
    	boolean on = tog_btn.isChecked();
        
        if (on) {
        	btn_control = "ON";
            // Enable vibrate
        } else {
        	btn_control = "OFF";
            // Disable vibrate
        }
    }
    
    public void getdatafromserver(View v)
    {
        currtime_pump = (TextView)findViewById(R.id.curr_time_view);
        starttime_pump = (TextView)findViewById(R.id.start_time_view);
        avgrt_pump = (TextView)findViewById(R.id.avg_rt_view);
        pmp_stat = (TextView)findViewById(R.id.pump_status_view);
        temp_pump = (TextView)findViewById(R.id.temp_pump_view);
        
        string_to_server = "Getdata";
        new MyAsyncTask().execute(string_to_server);
    }
    
    public void senddatatoserver(View v)
    {
        current_time = (EditText)findViewById(R.id.curr_time);
        start_time = (EditText)findViewById(R.id.start_time);
        avg_hr = (EditText)findViewById(R.id.hr_rt);
        avg_min = (EditText)findViewById(R.id.min_rt);        
        string_to_server = current_time.getText().toString() + '!' + start_time.getText().toString() + '!' + avg_hr.getText().toString() + ':' + avg_min.getText().toString();

        string_to_server = string_to_server.replace(" ","" );
        string_to_server = string_to_server.replace("!"," ");
        string_to_server = string_to_server  + ' ' + btn_control;
        
        pb.setVisibility(View.VISIBLE);
    	new MyAsyncTask().execute(string_to_server);
    }
    
    public void showStartPickerDialog(View v) {
    	StartPickerFrag time_dlg = new StartPickerFrag();
    	time_dlg.show(getFragmentManager(), "startdialog");
    }
    
    public void showTimePickerDialog(View v) {
    	TimePickerFrag time_dlg = new TimePickerFrag();
    	time_dlg.show(getFragmentManager(), "timedialog");
    }
    
    public void showHourPicker(View v){
    	DialogFragment newFragment = new HourPickerFragment();
        newFragment.show(getFragmentManager(), "hourPicker");
    }
    
    public void showMinutePicker(View v){
    	DialogFragment newFragment = new MinutePickerFragment();
        newFragment.show(getFragmentManager(), "minutePicker");
    }

//-------------------------------------------------------------------------------
    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
        @Override
        protected Double doInBackground(String... params) {
        // TODO Auto-generated method stub
        postData(params[0]);
        return null;
        }
         
        protected void onPostExecute(Double result){
        pb.setVisibility(View.GONE);
        tokens = response.split(" ");
        Toast.makeText(getApplicationContext(), "SUCCESS", Toast.LENGTH_LONG).show();
        response = "";
        if(string_to_server == "Getdata")
	        {
	        currtime_pump.setText(tokens[0]);
	        starttime_pump.setText(tokens[1]);
	        avgrt_pump.setText(tokens[2]);
        	pmp_stat.setText(tokens[3]);
	        temp_pump.setText(tokens[4] + "F");
	        }
        }
        protected void onProgressUpdate(Integer... progress){
        pb.setProgress(progress[0]);
        }
         
        public void postData(String valueIWantToSend) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.1.127/poolreq2.php");
         
        try {
        // Add your data
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("say", valueIWantToSend));
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
         
        // Execute HTTP Post Request
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        response = httpclient.execute(httppost, responseHandler);

        //This is the response from a php application
       // String reverseString = response;
        //Toast.makeText(getApplicationContext(), "response" + reverseString, Toast.LENGTH_LONG).show();
         
        } catch (ClientProtocolException e) {
        // TODO Auto-generated catch block
        } catch (IOException e) {
        // TODO Auto-generated catch block
        }
        }
        //--------------------
        }
//-------------------------------------------------------------------------------
	class MyTabsListener implements ActionBar.TabListener{
		public Fragment fragment;
		
		public MyTabsListener(Fragment fragment){
			this.fragment = fragment;
		}
	
		@Override
		public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			ft.replace(R.id.fragment_container, fragment);
		}
	
		@Override
		public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
		}
	}
@SuppressLint("ValidFragment")
//-------------------------------------------------------------------------------
	class StartPickerFrag extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
	
		
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
		DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker myview, int hourOfDay, int minute) {
		// Do something with the time chosen by the user
			String time_message = null;
			if(hourOfDay > 9 && minute > 9){
				time_message = String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
			}
			else if (hourOfDay <= 9 && minute > 9) {
				time_message = "0" + String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
			}
			else if (hourOfDay > 9 && minute <= 9){
				time_message = String.valueOf(hourOfDay) + " : 0" + String.valueOf(minute);
			}
			else{
				time_message = "0" + String.valueOf(hourOfDay) + " : 0" + String.valueOf(minute);
			}
			EditText et = (EditText) findViewById(R.id.start_time);
			et.setText(time_message);
			//EditText userInput = (EditText) findViewById(R.id.start_time);`
		}

		
		public void showSentMsg(String msg)
		{
			AlertDialog.Builder msg_dlg = new AlertDialog.Builder(getActivity());
			msg_dlg.setMessage(msg);
			msg_dlg.setTitle("Time Entered is");
			msg_dlg.show();
		}
	
	}



//---------------------------------------------------------------------------------
class TimePickerFrag extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	// Use the current time as the default values for the picker
	final Calendar c = Calendar.getInstance();
	int hour = c.get(Calendar.HOUR_OF_DAY);
	int minute = c.get(Calendar.MINUTE);

	
	// Create a new instance of TimePickerDialog and return it
	return new TimePickerDialog(getActivity(), this, hour, minute,
	DateFormat.is24HourFormat(getActivity()));
	}
	
	public void onTimeSet(TimePicker myview, int hourOfDay, int minute) {
	// Do something with the time chosen by the user
		String time_message = null;
		if(hourOfDay > 9 && minute > 9){
			time_message = String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
		}
		else if (hourOfDay <= 9 && minute > 9) {
			time_message = "0" + String.valueOf(hourOfDay) + " : " + String.valueOf(minute);
		}
		else if (hourOfDay > 9 && minute <= 9){
			time_message = String.valueOf(hourOfDay) + " : 0" + String.valueOf(minute);
		}
		else{
			time_message = "0" + String.valueOf(hourOfDay) + " : 0" + String.valueOf(minute);
		}
		EditText et = (EditText) findViewById(R.id.curr_time);
		et.setText(time_message);
		//EditText userInput = (EditText) findViewById(R.id.start_time);`
	}

	
	public void showSentMsg(String msg)
	{
		AlertDialog.Builder msg_dlg = new AlertDialog.Builder(getActivity());
		msg_dlg.setMessage(msg);
		msg_dlg.setTitle("Time Entered is");
		msg_dlg.show();
	}

}

//---------------------------------------------------------------------------------
@SuppressLint("ValidFragment")
//--------------------------------------------------------------------------------
	class HourPickerFragment extends DialogFragment{
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState){
			AlertDialog.Builder num_pick = new AlertDialog.Builder(getActivity());
			String avg_rt_hrs = "Enter Hours";
			// Add the buttons
			final NumberPicker np = new NumberPicker(getActivity());
			np.setMaxValue(23);
			np.setMinValue(0);
			
			num_pick.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User clicked OK button
			        	   String hr = null;
			        	   int hr_i = 0;
			        	   hr_i = np.getValue();
			        	   if(hr_i <= 9){
			        		   hr = "0" + String.valueOf(hr_i);
			        	   }
			        	   else{
			        		   hr = String.valueOf(hr_i);
			        	   }
			        	   EditText et = (EditText) findViewById(R.id.hr_rt);
			   			   et.setText(hr);
			   			   dialog.dismiss();
			           }
			       });
			num_pick.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User cancelled the dialog
			        	   dialog.dismiss();
			           }
			       });
			// Set other dialog properties
			num_pick.setTitle(avg_rt_hrs);
			num_pick.setView(np);
			// Create the AlertDialog
			AlertDialog new_dlg = num_pick.create();
			return new_dlg;
		}
	}
@SuppressLint("ValidFragment")
//-----------------------------------------------------------------------------------
	class MinutePickerFragment extends DialogFragment{
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState){
			AlertDialog.Builder num_pick = new AlertDialog.Builder(getActivity());
			String avg_rt_min = "Enter Minutes";
			// Add the buttons
			final NumberPicker np = new NumberPicker(getActivity());
			np.setMaxValue(59);
			np.setMinValue(0);
			
			num_pick.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User clicked OK button
			        	   String min = null;
			        	   int min_i = 0;
			        	   min_i = np.getValue();
			        	   if(min_i <= 9){
			        		   min = "0" + String.valueOf(min_i);
			        	   }
			        	   else{
			        		   min = String.valueOf(min_i);
			        	   }
			        	   EditText et = (EditText) findViewById(R.id.min_rt);
			   			   et.setText(min);
			   			   dialog.dismiss();
			           }
			       });
			num_pick.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // User cancelled the dialog
			        	   dialog.dismiss();
			           }
			       });
			// Set other dialog properties
			num_pick.setTitle(avg_rt_min);
			num_pick.setView(np);
			// Create the AlertDialog
			AlertDialog new_dlg = num_pick.create();
			return new_dlg;
		}
	}
//--------------------------------------------------------------------------------------	
}
