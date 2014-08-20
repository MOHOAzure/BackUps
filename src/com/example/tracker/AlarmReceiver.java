package com.example.tracker;


import org.json.JSONArray;
import org.json.JSONObject;

import com.example.tracker.DBConnector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
 
public class AlarmReceiver extends BroadcastReceiver {
	
	public void onReceive(Context context, Intent intent) {
		System.out.println("On AlarmReceiver");
		String action = intent.getAction();
		
		if(action.equals(TodayWorkActivity.str)){
			/*
			 * GSMｧYｱNｶ}ｾ�｡A ﾀﾋｬd ｶﾇｰT､ｺｮeｪｺｸ�ﾆｮw
			 * ｧ筥UTrackerｭn ｦｬｪｺｰTｮｧｾ罔X｡BｱH･X
			 * */
			Bundle extras = intent.getExtras();
            String TGT = extras.getString("TGT");
			System.out.println("TGT: "+TGT);//test
			
			
			/* SMS */
			SendSMS(TGT);
		}
		
		else if(action.equals("com.example.tracker")){
			/*
			 * ｨC､鬢uｧ@ｮﾉｶ｡ｨ�ｶ}ｩlｶiｦ讀uｧ@
			 * */
			System.out.println(action);//test

			Intent it = new Intent(context, TodayWorkActivity.class);
			it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
			
		}
		else{
			System.out.println("Receiver error");
		}
	}
	
	/**/
	/*SMS*/
	private void SendSMS(String tracerNumber){
    	String TAG = "SendSMS()";
    	try {
    		String checkTGT = tracerNumber.substring(0, 2);
    		//check if it's a phone number
    		if( checkTGT.equals("09")){
	            String result = DBConnector.executeQuery("SELECT * FROM member_cmd_sms WHERE status='notSend' and TGT="+tracerNumber);
	            if(!result.equals("null\n"))
	            {
		            JSONArray jsonArray = new JSONArray(result);
		            JSONObject jsonData = null;
		            String smsBody;
	            
		            for(int i = 0; i < jsonArray.length(); ++i) {
		            	jsonData = jsonArray.getJSONObject(i);
	                
		                smsBody = jsonData.getString("cmd");
		
		                /* Send out the message*/
		                try {
		                	// Get the default instance of the SmsManager
				        	SmsManager smsManager = SmsManager.getDefault();
				        	smsManager.sendTextMessage(tracerNumber,
									        			null,  
									        			smsBody.toString(),
									        			null,
									        			null);
				        	
				        	Log.i(TAG, "Your sms has successfully sent to "+tracerNumber);
				        	//update the DB
				        	AfterSendSMS(tracerNumber);
		        			
		        		} catch (Exception ex) {
		        			ex.printStackTrace();
		        		}//end of send sms
		            }
		            //All things down
		            Log.i(TAG, "Completed");
		        } else{
		        	Log.i(TAG, "The result is "+result);
		        }
    		} else{
    			Log.e(TAG, "\""+tracerNumber+"\" is not a phone number");
    		}
        } catch(Exception e) {
            Log.e("log_tag", e.toString());
        }
    }
    
    private void AfterSendSMS(String tracerNumber){
    	try{
    		String result_updateCMDSMSTable = DBConnector.executeQuery("UPDATE member_cmd_sms set status='Sent' where TGT='"+tracerNumber+"' and status='notSend' and cmd_type='set_GPSGSM_On'");
    		String result_updateTTimerTable = DBConnector.executeQuery("UPDATE TracerTimer set status='Sent' where TGT='"+tracerNumber+"' and status='waiting'");
    		
    		//String result_insertTracerReport = DBConnector.executeQuery("INSERT INTO tracer_report_sms (tracerNumber, smsBody, time) VALUES(0963915102, 'testSMS', now())");
    		//System.out.println("LinkDB_updateTable: "+result);
    	} catch(Exception e) {
    		Log.e("log_tag", e.toString());
    	}
    }
	/*

	private void SMS(Context context){
		try{
			SmsManager smsManager = SmsManager.getDefault();
			String tracerNumber = "0975091679";//test phone
			String smsBody ="Sent from AlarmCalendar";
			smsManager.sendTextMessage(tracerNumber,
        			null,  
        			smsBody,
        			null,
        			null);
			Toast.makeText(context, "Your sms has successfully sent to "+tracerNumber, Toast.LENGTH_LONG).show();

		}catch(Exception e){
			Toast.makeText(context, "SMS was Failed", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	private void Timer_checkTimerTable(int countDownSec){
        new CountDownTimer(1000*countDownSec, 1000){
            
            public void onFinish() {
                //mTextView.setText("CheckTimerTable!");
                LinkDB_RetrieveTimerSet();
                //Timer_checkTimerTable(TimeCheckTracerTimer);//call self
            }
			public void onTick(long millisUntilFinished) {
                //mTextView.setText("CheckTimerTable\n Seconds remaining:"+millisUntilFinished/1000);
			}
        }.start();
    }
   
    private void LinkDB_RetrieveTimerSet() {
		String TAG ="LinkDB_RetrieveTimerSet()";
    	try{
    		String result = DBConnector.executeQuery("SELECT * FROM TracerTimer where status ='waiting'");
    		if(result != "null\n"){
    		JSONArray jsonArray = new JSONArray(result);
    		JSONObject jsonData = null;
    		String tracerNumber;
    		int countDownTime;
    		
    		for (int i=0; i<jsonArray.length(); ++i){
    			jsonData = jsonArray.getJSONObject(i);
    			tracerNumber = jsonData.getString("TGT");
    			countDownTime = Integer.parseInt(jsonData.getString("Timer"));
    			Log.e(TAG, "tracerNumber: "+tracerNumber+" countDownTime: "+countDownTime);
    			Timer_SendSMS(tracerNumber, countDownTime);
    		}
    		}else{
    			Log.e(TAG, "LinkDB_RetrieveTimerSet\nThe result is "+result);
    		}
    	} catch(Exception e){
    		Log.e(TAG, e.toString());
        }
	}

    private void Timer_SendSMS(final String tracerNumber, int countDownTime){
    	 new CountDownTimer(1000*countDownTime, 1000){
             public void onFinish() {
                 Log.e("Timer_SendSMS", "Send "+tracerNumber);
         		SendSMS(tracerNumber);
             }
             public void onTick(long millisUntilFinished) {
                 //mTextView.setText("Send\n Seconds remaining:"+millisUntilFinished/1000);
 			 }
         }.start();
    }
    */
	
}