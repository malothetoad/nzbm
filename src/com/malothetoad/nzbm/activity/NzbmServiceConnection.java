package com.malothetoad.nzbm.activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.malothetoad.nzbm.service.Nzbget;
import com.malothetoad.nzbm.service.NzbmService;
import com.malothetoad.utility.Utility;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class NzbmServiceConnection extends SherlockActivity
{
	public NzbmService nzbservice;
	public Nzbget nzbget;
	public boolean isBound = false;
	
	@Override
    public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate(savedInstanceState);
    }
	@Override
    public void onDestroy()
    {
		this.unbindService();
    	super.onDestroy();
    }
	
	protected void onServiceBindStart()
	{
		Utility.logd( this.getClass(), "onBindStart" );
	}
	protected void onServiceBindEnd( final boolean success, final String info )
	{
		Utility.logd( this.getClass(), "onBindEnd: "+success+" ("+info+")" );
		if( success )
		{
			this.onServiceStarted();
			nzbget = nzbservice.nzbget;
		}
	}
	
	protected void onServiceStopped()
	{
		Utility.logd( this.getClass(), "onServiceStopped" );
	}
	protected void onServiceStarted()
	{
		Utility.logd( this.getClass(), "onServiceStarted" );
	}
	
	public void startService()
	{
		startService( new Intent( NzbmServiceConnection.this, NzbmService.class ) );
		this.bindService();
	}
	
	public void stopService()
	{
		stopService( new Intent( NzbmServiceConnection.this, NzbmService.class ) );
		this.unbindService();
		this.onServiceStopped();
	}
	
	public void bindService() 
    {
    	Utility.logd( this.getClass(), "bind" );
    	
    	this.onServiceBindStart();
    	
    	if( isBound && nzbservice != null )
    	{
    		this.onServiceBindEnd( true, "service already bound" );
    		return;
    	}
    	
    	isBound = false;
    	if( bindService( new Intent( NzbmServiceConnection.this, NzbmService.class ), connection, Context.BIND_AUTO_CREATE ) )
    	{
    		isBound = true;
    		this.connect();
    	}
	    else
	    	this.onServiceBindEnd( false, "could not bind service" );

        Utility.logd( this.getClass(), "bindService: " + isBound );	
    }
    
	public void unbindService() 
    {
    	Utility.logd( this.getClass(), "unbind" );
        if( this.isBound ) 
        {
        	this.unbindService( connection );
        	this.isBound = false;
        }
    }
	
    private void connect()
    {
    	Thread t = new Thread( new Runnable()
		{
			@Override
			public void run() 
			{
				int retry = 1;
				boolean running = false;
				
				while( retry < 11 )
				{
					Utility.logd( NzbmServiceConnection.this.getClass(), "waiting for nzbget server..." );
					
					try 
					{
						Thread.sleep( 1000 );
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}

					if( nzbservice != null && nzbservice.isRunning() )
					{
						running = true;
						break;
					}
					retry++;
				}
				
				if( running )
					onServiceBindEnd( true, "connected to service" );
				else
					onServiceBindEnd( false, "could not connect to service" );
			}
		});
		t.start();
    }
    
    private ServiceConnection connection = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) 
        {
        	nzbservice = ((NzbmService.LocalBinder)service).getService();
        }

        public void onServiceDisconnected(ComponentName className)
        {
        	nzbservice = null;
        }
    };
}

