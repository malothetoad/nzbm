package com.malothetoad.nzbm.activity;

import com.malothetoad.utility.UtilityMessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public class Receiver extends NzbmServiceConnection
{
	private UtilityMessage dialog;
	private String nzbPath;
	
	@Override
    public void onCreate( Bundle savedInstanceState ) 
    {
        super.onCreate( savedInstanceState );
        dialog = new UtilityMessage( this );
        
        if( getIntent().getData() != null )
        {
        	nzbPath = getIntent().getData().getEncodedPath();
        	this.startService();
        }
        else
        {
        	finish();
        }
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
		Receiver.this.runOnUiThread( new Runnable()
		{
			@Override
			public void run() 
			{
				dialogConfirmAddNzb( nzbPath );
			}
		});
	}
	
	private void dialogConfirmAddNzb( final String nzbPath )
    {
    	new AlertDialog.Builder( Receiver.this )
    	.setTitle( "Confirm" )
        .setMessage( "Add nzb to download queue ?\n\n" + nzbPath )
        .setPositiveButton( "Add", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton) 
            {
            	nzbget.addNZB( nzbPath );
            	finish();
            }
        })
        .setNegativeButton( "Cancel", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
            	finish();
            }
        })
        .create().show();
    }
}
