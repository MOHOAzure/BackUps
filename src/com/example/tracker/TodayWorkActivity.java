package com.example.tracker;

/*
 * 負責檢查資料庫
 * 確認  今日會開機的tracker 的 開機時間
 * 並設置一個時間 -- 最終檢查 訊息 內容的時間
 * */

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

public class TodayWorkActivity extends Activity {
public static final String str = "BroadCast in TodayWorkActivity";
	
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        
        System.out.println("In the TodayWorkActivity"+str);//test
        
        //register the filter
        IntentFilter filter = new IntentFilter();
		filter.addAction(this.str);
		AlarmReceiver receiver = new AlarmReceiver();
		registerReceiver(receiver, filter);
		
        //check the table and retrieve the time of trackers, which start in today
		//should this part move to OnResume? <-- waiting issue
        LinkDB_TracerTimer_SetTodayWork();        
        
    }
    
    private void LinkDB_TracerTimer_SetTodayWork() {
		String TAG ="LinkDB_TracerTimer_SetTodayWork";
    	try{
    		String result = DBConnector.executeQuery("SELECT * FROM tracertimer where status ='waiting' and StartTime like \"2014 8 18%\""); //like "today"
    		if(result != "null\n"){
	    		JSONArray jsonArray = new JSONArray(result);
	    		JSONObject jsonData = null;
	    		
	    		String tracerNumber;

	            int numOfTracer = jsonArray.length();;//今日會開啟的Tracker數量
	            System.out.println("numOfTracer: "+numOfTracer);//test
	            String StartTime_string[] = new String[numOfTracer];//the turn on point of one tracker
	            
	    		for (int i=0; i<numOfTracer; ++i){
	    			jsonData = jsonArray.getJSONObject(i);
	    			tracerNumber = jsonData.getString("TGT");
	    			StartTime_string[i] = jsonData.getString("StartTime");
	    			Log.i(TAG, "tracerNumber: "+tracerNumber+" StartTime: "+StartTime_string[i]);//test
	    			
	    			//parsing the StartTime_string to details and in the form of integer
	    			String[] AfterSplit = StartTime_string[i].split("[ \"]+");//split at " and space
	    			int time_numbers[]=new int[AfterSplit.length];
	    			for (int j = 0; j <AfterSplit.length; ++j){
	    				time_numbers[j] = Integer.parseInt(AfterSplit[j]);
	    				System.out.println("Split: j"+j+", "+time_numbers[j]);//check
	    			}
	    			
		    		//Set the time to do the final check of the table
	    			Calendar c=Calendar.getInstance();
	    			c.set(time_numbers[0], time_numbers[1]-1, time_numbers[2], time_numbers[3], time_numbers[4], time_numbers[5]); //set calendar in one line
	    			
			        Intent intent=new Intent(this.str);
			        intent.putExtra("TGT",tracerNumber);
			        
			        PendingIntent pi = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			        AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
			        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
	    		}
    		}else{
    			Log.i(TAG, "LinkDB_RetrieveTimerSet\nThe result is "+result);
    		}
    	} catch(Exception e){
    		Log.e(TAG, e.toString());
        }
	}
    
}
