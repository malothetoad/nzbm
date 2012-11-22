package com.malothetoad.nzbm.activity;

import java.io.File;

import com.chartboost.sdk.ChartBoost;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.malothetoad.nzbm.Config;
import com.malothetoad.nzbm.Paths;
import com.malothetoad.nzbm.R;
import com.malothetoad.nzbm.views.ServerView;
import com.malothetoad.utility.Utility;
import com.malothetoad.utility.UtilityDecompressRaw;
import com.malothetoad.utility.UtilityMessage;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;

public class Main extends NzbmServiceConnection implements AdListener
{
	private final static String adUnitId = "a150a508e6a8c3c";
	private final static String adDevice1 = "015d24a7f6140816";
	private final static String adDevice2 = "304D19C94B398F2F";
			
	private UtilityMessage dialog;
	private ServerView serverView;
	private ImageView startStopMenu;

	private AdView bannerTop;
	private AdView bannerBottom;
	
	private Config conf;
	
	//private Point screen;
	private boolean isTablet = false;
	
	@Override
    public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate(savedInstanceState);

        isTablet = Utility.isTablet( this );
        //screen = Utility.getScreenSize( this );
       
        // go fullscreen (remove notification bar) if we are not on tablet
        if( !isTablet )
        	this.getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
        setContentView(R.layout.main);
        
        serverView = (ServerView)this.findViewById( R.id.ServerView );
        startStopMenu = (ImageView)this.findViewById( R.id.menu_stop );
        
        dialog = new UtilityMessage( this );

        if( new File( Paths.webui ).exists() )
        {
        	conf = new Config( Paths.config );
        	startService();
        }
        else
        	extractWebui();
       
        // Banners
        bannerTop =  new AdView( this, isTablet ? AdSize.IAB_BANNER : AdSize.BANNER, adUnitId );
        bannerTop.setAdListener( this );
        LinearLayout top = (LinearLayout)findViewById( R.id.banner_top_layout );
        top.addView( bannerTop );
        
        bannerBottom = new AdView( this, AdSize.SMART_BANNER, adUnitId );
        LinearLayout bottom = (LinearLayout)findViewById( R.id.banner_bottom_layout );
        bottom.addView( bannerBottom );
   
        // ChartBoost
        ChartBoost _cb = ChartBoost.getSharedChartBoost(this);
        _cb.setAppId( "50a5020a16ba472f22000000" );
        _cb.setAppSignature( "8abe2c3c033c5f1704175e53e1403338b8d9fa0f" );
        _cb.install();
        _cb.showInterstitial();
    }
	
	@Override  
	protected void onActivityResult( int requestCode, int resultCode, Intent intent )
	{  
	   	if( requestCode == ServerView.FILECHOOSER_RESULTCODE )  
	   	{  
	   		if ( serverView.uploadMessage == null ) 
	   			return;  
				 
	   		Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();  
	   		serverView.uploadMessage .onReceiveValue( result );  
	   		serverView.uploadMessage  = null;      
	   	}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();

        loadBanners( true );
	}

    @Override
    public void onConfigurationChanged( Configuration newConfig) 
    {
    	Utility.logd( this.getClass(), "onConfigurationChanged" );
    	super.onConfigurationChanged( newConfig );
    	loadBanners( true );
	}
    
    @Override
    protected void onServiceBindStart()
	{
    	dialog.show( "Please wait while connecting to nzbget" );
		super.onServiceBindStart();
	}
    @Override
	protected void onServiceBindEnd( final boolean success, final String info )
	{
		super.onServiceBindEnd( success, info );
		
		dialog.hide();
		
		if( !success )
		{
			dialog.showMessageError( info );
			return;
		}
	
		Main.this.runOnUiThread( new Runnable()
		{
			@Override
			public void run() 
			{
				if( success )
					serverView.load( Main.this, conf );
				else
					dialog.showMessageError( "Sorry a problem occured, nzbget server could not be started" );
			}
		});
	}
    
    @Override
	protected void onServiceStopped()
	{
    	super.onServiceStopped();
    	Main.this.runOnUiThread( new Runnable()
		{
			@Override
			public void run() 
			{
				startStopMenu.setImageDrawable( Main.this.getResources().getDrawable( R.drawable.menu_start ) );
			}
		});
	}
    @Override
	protected void onServiceStarted()
    {
    	super.onServiceStarted();
    	Main.this.runOnUiThread( new Runnable()
		{
			@Override
			public void run() 
			{
				startStopMenu.setImageDrawable( Main.this.getResources().getDrawable( R.drawable.menu_stop ) );
			}
		});
    }

    public void onMenuStartStop( View v )
    {
    	Utility.logd( this.getClass(), "onServiceStartStop" );
    	
    	if( isBound && nzbservice != null )
	    	stopService();
    	else
    		startService();
    }
    
    public void onMenuInfo( View v )
    {
    	String service = "Service: " + (nzbservice == null ? "stopped" : "running") + "\n";
    	String server = "Server: " + (nzbservice.isRunning() ? "running" : "stopped") + "\n";
    	String _ip = Utility.getLocalIpAddress();
    	String ip = "Web interface: " + (_ip == null ? "could not retrieve address" : "http://"+_ip+":6789/" );
    	
    	dialog.showMessageInfo( "\n" + service + server + ip + "\n" );
    }

    private void extractWebui()
    {
    	dialog.show( "Please wait while extracting data..." );
    	
    	UtilityDecompressRaw d = new UtilityDecompressRaw( this, this.getPackageName() )
    	{
    		@Override
    		public void OnTerminate( boolean success )
    		{
    			dialog.hide();
    			
    			if( success )
    			{
    				runOnUiThread( new Runnable( )
    				{
    					@Override
    					public void run() 
    					{
    						conf = new Config( Paths.config );
    						startService();
    					}	
    				});
    			}
    			else
    				dialog.showMessageErrorExit( "Sorry, a fatal error occured while extracting data" );
    		}
    	};
    	d.add( "webui", Paths.webui+"/" );
    	d.process();
    }
 
    private void loadBanners( boolean top )
    {
    	Utility.logd( this.getClass(), "loadBanners (top="+top+")" );
    	
    	AdRequest request = new AdRequest();

		bannerTop.setVisibility( top ? View.VISIBLE : View.GONE );
		bannerBottom.setVisibility( top ? View.GONE : View.VISIBLE );

		if( top )
			bannerTop.loadAd( request );
		else
			bannerBottom.loadAd( request );
    }
   
	@Override
	public void onDismissScreen( Ad arg0 ) 
	{
		Utility.logd( Main.this.getClass(), "onDismissScreen" );
	}

	@Override
	public void onFailedToReceiveAd( Ad ad, ErrorCode arg1 ) 
	{
		Utility.logd( Main.this.getClass(), "onFailedToReceiveAd: "+arg1 );
		loadBanners( false );
	}

	@Override
	public void onLeaveApplication(Ad arg0) 
	{
		// TODO Auto-generated method stub
		Utility.logd( Main.this.getClass(), "onLeaveApplication" );
	}

	@Override
	public void onPresentScreen(Ad arg0) 
	{
		// TODO Auto-generated method stub
		Utility.logd( Main.this.getClass(), "onPresentScreen" );
	}

	@Override
	public void onReceiveAd(Ad ad)
	{
		Utility.logd( Main.this.getClass(), "onReceiveAd: "+ad.isReady() );
		
		// assume banner is too large, switch to bottom banner
		if( bannerTop.getHeight() == 0 )
		{
			Utility.logd( Main.this.getClass(), "bannerTop could not be displayed" );
			loadBanners( false );
		}
	}
}

