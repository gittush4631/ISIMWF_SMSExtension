package com.cpc.custom;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

/************************************************************************
* IBM Confidential
* OCO Source Materials
* *** IBM Security Identity Manager ***
*
* (C) Copyright IBM Corp. 2015  All Rights Reserved.
*
* The source code for this program is not published or otherwise  
* divested of its trade secrets, irrespective of what has been 
* deposited with the U.S. Copyright Office.
*************************************************************************/


import com.ibm.itim.workflow.application.WorkflowApplication;
import com.ibm.itim.workflow.application.WorkflowExecutionContext;
import com.ibm.itim.workflow.model.ActivityResult;

/**
 * Custom class for synchronous activity
 */
public class SMSExtension implements WorkflowApplication {

	protected WorkflowExecutionContext ctx;

	public SMSExtension() {
	}

	/**
	 * Passes the workflow execution context to the application.
	 * 
	 * @param context
	 *            WorklowExecutionContext holding information about the
	 *            currently executing activity.
	 */
	public void setContext(WorkflowExecutionContext ctx) {
		this.ctx = ctx;
	}

	/**
	 * Perform change password extension synchronously
	 * 
	 * @return ActivityResult The result of the activity. If summary==PENDING,
	 *         then the activity will be executed asynchronously; otherwise the
	 *         activity is completed. There is no detail returned.
	 * 
	 */
	public ActivityResult sendSMS(String mobileno,String msg) {
		String output = "";
		String output1= "";
		
		try {
			String activityId = String.valueOf(ctx.getActivityVO().getId());
			
			
			Properties prop=new Properties();
			InputStream inputprop = getClass().getClassLoader().getResourceAsStream("smsgateway.properties");
			prop.load(inputprop);
			String username=prop.getProperty("userid");
			String password=prop.getProperty("password");
			String urlprop=prop.getProperty("url");
			//System.out.println("username "+username);
			//System.out.println("password "+password);
			String data = "";
			data += "method=sendMessage";
			data += "&userid="+username;
			data += "&password="+URLEncoder.encode(password, "UTF-8");
			data += "&msg=" + URLEncoder.encode(msg, "UTF-8");
			data += "&send_to=" +	URLEncoder.encode(mobileno, "UTF-8"); // a valid 10 digit phone no.
			data += "&v=1.1" ;
			data += "&msg_type=TEXT"; // Can by "FLASH" or "UNICODE_TEXT" or “BINARY”
			data += "&auth_scheme=PLAIN";
			URL url = new URL(urlprop + data);
			//System.out.println("final sms api url = "+url);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.connect();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			StringBuffer buffer = new StringBuffer(); while ((line = rd.readLine()) != null){
			buffer.append(line).append("\n");
			}
			output=buffer.toString();
			//System.out.println("response : "+output);
			output1="Sending SMS on mobile number "+mobileno+" response : "+output;
			System.out.println(output1);
			
			rd.close();
			conn.disconnect();
			
			
			return new ActivityResult(ActivityResult.STATUS_COMPLETE, ActivityResult.SUCCESS, output, null);
		} catch (Exception e) {
			return new ActivityResult(ActivityResult.FAILED, e.getClass()
					.getName()
					+ "Failure : " + output + e.getMessage(), null);
		}
	}

}