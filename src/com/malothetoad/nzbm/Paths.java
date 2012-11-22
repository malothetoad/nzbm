package com.malothetoad.nzbm;

import android.os.Environment;

public class Paths
{
	public static final String nzbget =
			Environment.getExternalStorageDirectory().getAbsolutePath() + "/nzbget";
	
	public static final String data =
			Environment.getExternalStorageDirectory().getAbsolutePath() + "/nzbget/data";
	
	public static final String webui =
			Environment.getExternalStorageDirectory().getAbsolutePath() + "/nzbget/data/webui";
	
	public static final String config = 
			Environment.getExternalStorageDirectory().getAbsolutePath() + "/nzbget/data/webui/nzbget.conf";
}
