package com.malothetoad.nzbm.service;

import com.malothetoad.nzbm.R;
import com.malothetoad.nzbm.activity.Main;
import com.malothetoad.utility.Utility;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

public class NzbmService extends Service
{
	public Nzbget nzbget;

	private int NOTIFICATION = 16031980;
	private PowerManager _powerManagement = null;
	private PowerManager.WakeLock _wakeLock = null;
	private WifiManager.WifiLock _wifiLock = null;
	private int wifiSleepPolicy = Settings.System.WIFI_SLEEP_POLICY_DEFAULT;
	
	@Override
    public void onCreate() 
    {
        createNotification(); 
        nzbget = new Nzbget();
        nzbget.start();
        keepOnStart();
        Toast.makeText( this, "nzbget service started", Toast.LENGTH_SHORT ).show();
    }
	
	public boolean isRunning()
	{
		return nzbget.isRunning();
	}
	
    @Override
    public void onDestroy() 
    {
    	keepOnStop();
    	nzbget.stop();
        Toast.makeText( this, "nzbget service stopped", Toast.LENGTH_SHORT ).show();
        super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }
	
	@Override
    public int onStartCommand( Intent intent, int flags, int startId ) 
    {
        Utility.log( "Received start id " + startId + ": " + intent );
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

	public class LocalBinder extends Binder 
    {
    	public NzbmService getService() 
        {
            return NzbmService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();
   
    /**
     * Show a notification while this service is running.
     */
    @SuppressLint("NewApi")
	private void createNotification() 
    {
    	CharSequence text = "nzbget service is running";
    	
    	Intent notificationIntent = new Intent( this, Main.class );
    	notificationIntent .setAction( "android.intent.action.MAIN" );
    	notificationIntent .addCategory( "android.intent.category.LAUNCHER" ); 
        PendingIntent contentIntent = 
        		PendingIntent.getActivity( this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT );
        Notification noti = null;
        
    	if( Integer.valueOf( android.os.Build.VERSION.SDK_INT ) >= 11 )
    	{
    		try
    		{
    			noti = new Notification.Builder( NzbmService.this )
    			.setContentTitle( "nzbm" )
    			.setContentText( text )
    			.setSmallIcon( R.drawable.ic_launcher )
    			//.setLargeIcon( aBitmap )
    			.setContentIntent( contentIntent )
    			.build();
    		}
    		catch ( java.lang.NoSuchMethodError e )
        	{
    			Utility.loge( e.toString() );
    			// Set the icon, scrolling text and timestamp
        		noti = new Notification( R.drawable.ic_launcher, text, System.currentTimeMillis() );
        		noti = new Notification( );
        		noti.setLatestEventInfo( this, "nzbm", text, contentIntent );
        	}
    	}
    	else
    	{  
	        // Set the icon, scrolling text and timestamp
    		noti = new Notification( R.drawable.ic_launcher, text, System.currentTimeMillis() );
    		noti = new Notification( );
    		noti.setLatestEventInfo( this, "nzbm", text, contentIntent );
    	}
    	
    	noti.flags |= Notification.FLAG_NO_CLEAR;
    	startForeground( NOTIFICATION, noti );
    }
    
	private void keepOnStart() 
 	{
		wifiSleepPolicy = 
				Settings.System.getInt( 
						getContentResolver(), 
						Settings.System.WIFI_SLEEP_POLICY, 
						Settings.System.WIFI_SLEEP_POLICY_DEFAULT );
		
		Settings.System.putInt( 
				getContentResolver(),
				Settings.System.WIFI_SLEEP_POLICY, 
				Settings.System.WIFI_SLEEP_POLICY_NEVER );
		
		if (_powerManagement == null) 
    		_powerManagement = (PowerManager) getSystemService(Context.POWER_SERVICE);
    	
    	if (_wakeLock == null) 
    		_wakeLock = _powerManagement.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "powerLock" );

        _wakeLock.acquire();
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) 
        {
        	_wifiLock = wifiManager.createWifiLock( WifiManager.WIFI_MODE_FULL | WifiManager.WIFI_MODE_FULL_HIGH_PERF, "wifiLock" );
          _wifiLock.acquire();
        }
	}

	private void keepOnStop() 
	{
		Settings.System.putInt( 
				getContentResolver(),
				Settings.System.WIFI_SLEEP_POLICY, 
				wifiSleepPolicy );
		
		if ((_wifiLock != null) && (_wifiLock.isHeld())) 
			_wifiLock.release();
    	  
		if ((_wakeLock != null) && (_wakeLock.isHeld())) 
			_wakeLock.release();
	}
}

