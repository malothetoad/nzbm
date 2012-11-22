package com.malothetoad.nzbm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.malothetoad.utility.Utility;


public class Config 
{
	private File file;
	private String string = null;
	
	private String mainDir = Paths.nzbget;
	private String webDir = "";
	private String controlPassword = "nzbget";
	private String controlPort = "6789";
	
	public Config( String pPath )
	{
		file = new File( pPath );
		if( !file.exists() )
		{
			Utility.loge( this.getClass(), "could not find nzbget config file: " + pPath );
			return;
		}
		load();
		parse();
	}
	
	public String getControlPassword()
	{
		return this.controlPassword;
	}
	public void setControlPassword( String password )
	{
		this.string = this.string.replace( "ControlPassword="+this.controlPassword, "ControlPassword="+password );
		this.controlPassword = password;
	}
	public String getControlPort()
	{
		return this.controlPort;
	}
	public void setControlPort( String port )
	{
		this.string = this.string.replace( "ControlPort="+this.controlPort, "ControlPort="+port );
		this.controlPort = port;
	}
	public String getMainDir()
	{
		return this.mainDir;
	}
	public void setMainDir( String path )
	{
		this.string = this.string.replace( "MainDir="+this.mainDir, "MainDir="+path );
		this.mainDir = path;
	}
	public String getWebDir()
	{
		return this.webDir;
	}
	public void setWebDir( String path )
	{
		this.string = this.string.replace( "WebDir="+this.webDir, "WebDir="+path );
		this.webDir = path;
	}
	public void save()
	{
		try
		{
			 FileWriter writer = new FileWriter( file );
			 BufferedWriter out = new BufferedWriter( writer );
			 out.write( this.string );
			 out.close();
		} 
		catch (java.io.IOException e) {}
	}
	
	private void parse()
	{
		int start, end = -1;
		
		if( string != null )
		{
			start = string.indexOf( "ControlPort=" ) + "ControlPort=".length();
			end = string.indexOf( '\n', start );
			controlPort = string.substring( start, end );
			Utility.logd( this.getClass(), "controlPort="+controlPort );
			
			start = string.indexOf( "ControlPassword=" ) + "ControlPassword=".length();
			end = string.indexOf( '\n', start );
			controlPassword = string.substring( start, end );
			Utility.logd( this.getClass(), "ControlPassword="+controlPassword );
			
			start = string.indexOf( "MainDir=" ) + "MainDir=".length();
			end = string.indexOf( '\n', start );
			mainDir = string.substring( start, end );
			
			start = string.indexOf( "WebDir=" ) + "WebDir=".length();
			end = string.indexOf( '\n', start );
			webDir = string.substring( start, end );
			
			//initialize mainDir to "sdcard/nzbget" if none is set
			if( mainDir.equals( "none" ) )
			{
				Utility.logd( this.getClass(), "mainDir not set, setting to default" );
				this.setMainDir( Paths.nzbget );
				this.setWebDir( Paths.webui );
				this.save();
			}
			Utility.logd( this.getClass(), "MainDir="+mainDir );	
		}
	}
	
	private void load()
	{
		try 
		{
			FileInputStream inputStream = new FileInputStream( file );
			FileChannel fc = inputStream.getChannel();
			MappedByteBuffer buffer = fc.map( FileChannel.MapMode.READ_ONLY, 0, fc.size() );
			string = Charset.defaultCharset().decode( buffer ).toString();
			inputStream.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			Utility.loge( this.getClass(), e.toString() );
		}
	}
}
