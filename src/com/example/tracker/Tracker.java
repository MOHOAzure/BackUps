package com.example.tracker;
//with appcompat_v7_7

/*
 * ｳ]ｸm ｨC､�:00 ｮﾉｷ|ｶiｦ讀@ｶｵ､uｧ@  [setRepeating]
 * */
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class Tracker extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tracker);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		//Android 3.0ｪｩ･ｻ､ｧｫ盪�sｨ恝W･[､F､@ｨﾇｭｭｨ釭Aｦbｦｹｸﾑｶ}
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()  
        .detectDiskWrites()  
        .detectNetwork()  
        .penaltyLog()  
        .build());  
        
        //ｰ｣ｿ弴ﾕ･ﾎ
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
        .detectLeakedSqlLiteObjects()   
        .penaltyLog()  
        .penaltyDeath()  
        .build());  

        init_DailyWork();
        //test();
	}

	private void init_DailyWork(){
		System.out.println("init_DailyWork");
    	Calendar c=Calendar.getInstance();
        
        c.set(2014, 7, 17, 0, 0, 0); //set calendar in one line
        
        //ｦ�s  Activity & Service
        Intent intent = new Intent("com.example.tracker");
        intent.setClass(this, AlarmReceiver.class);//ｶﾇｰeｰTｮｧｵｹ AlarmReceiver.class｡Aｨ莚tｳdｳBｲzｱｵｦｬｪｺAlarm

        //ｸﾜ･ｼｨﾓBroadcastｪｺｱｵｦｬｱ｡ｪp
        PendingIntent pi=PendingIntent.getBroadcast(this, 0, intent,0);
        
        //ｳ]ｸm､@ｭﾓPendingIntentｹ�H｡Aｵoｰeｼsｼｽ
        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE); 
        
        //testing
        //ｨⅱlarmManagerｹ�H｡A//ｭYcalendarｹLｴﾁ､F｡Aｷ|･ﾟｧYｵo･X
        //am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi); //ｰ讀@ｦｸ 
        
        //the real one
        int interval = 10*60*1000; //24hr*60min*60sec*1000ms
        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), interval, pi); //ｳ]ｸmｭｫｽﾆｰ譯Aｨ�ﾆｲﾄ2ｭﾓｰﾑｼﾆｮﾉｶ｡ｳ讎�Oms//ｶiｦ讓C､鬢uｧ@
        
        System.out.println("DailyWork completed");
    }
	
	
	
	
	
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tracker, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tracker,
					container, false);
			return rootView;
		}
	}

}
