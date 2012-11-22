#include "config.h"
#include <stdlib.h>
#include <string.h>
#include <cstdio>
#include <fstream>
#include <stdarg.h>
#include <unistd.h>

#include "nzbget.h"
#include "Log.h"
#include "Options.h"
#include "QueueCoordinator.h"
#include "UrlCoordinator.h"
#include "QueueEditor.h"
#include "PrePostProcessor.h"
#include "Util.h"

#include "Android.h"

extern int main( int argc, char *argv[], char *argp[] );
extern void ExitProc();
extern Options* g_pOptions;
extern QueueCoordinator* g_pQueueCoordinator;
extern bool g_bReloading;

const char* svn_version(void)
{
	const char* SVN_Version = "508";
	return SVN_Version;
}


extern "C"
{
    JNIEXPORT int JNICALL Java_com_malothetoad_nzbm_service_Nzbget_main( JNIEnv *env, jobject thiz, jobjectArray strArray );
    JNIEXPORT void JNICALL Java_com_malothetoad_nzbm_service_Nzbget_pause( JNIEnv *env, jobject thiz );
    JNIEXPORT void JNICALL Java_com_malothetoad_nzbm_service_Nzbget_resume( JNIEnv *env, jobject thiz );
    JNIEXPORT void JNICALL Java_com_malothetoad_nzbm_service_Nzbget_shutdown( JNIEnv *env, jobject thiz );
    JNIEXPORT jboolean JNICALL Java_com_malothetoad_nzbm_service_Nzbget_append( JNIEnv *env, jobject thiz, jstring path, jstring category, bool addToTop );
    JNIEXPORT jstring JNICALL Java_com_malothetoad_nzbm_service_Nzbget_version( JNIEnv *env, jobject thiz );
};

JNIEXPORT int JNICALL Java_com_malothetoad_nzbm_service_Nzbget_main( JNIEnv *env, jobject thiz, jobjectArray strArray )
{
	int i;

	jsize len = env->GetArrayLength(strArray);

	const char *argv[len];
	for( i=0; i<len; i++ )
	{
		jstring str = (jstring)env->GetObjectArrayElement(strArray,i);
		argv[i] = env->GetStringUTFChars( str, 0 );
	}

	int ret = main( i, (char **)argv, (char **)"" );

	g_bReloading = true;

	for( i=0; i<len; i++ )
		env->ReleaseStringUTFChars( (jstring)env->GetObjectArrayElement(strArray,i), argv[i] );

	return ret;
}

JNIEXPORT jstring JNICALL Java_com_malothetoad_nzbm_service_Nzbget_version( JNIEnv *env, jobject thiz )
{
	return env->NewStringUTF( Util::VersionRevision() );
}

JNIEXPORT void JNICALL Java_com_malothetoad_nzbm_service_Nzbget_pause( JNIEnv *env, jobject thiz )
{
	if( g_pOptions )
		g_pOptions->SetPauseDownload( true );
}

JNIEXPORT void JNICALL Java_com_malothetoad_nzbm_service_Nzbget_resume( JNIEnv *env, jobject thiz )
{
	if( g_pOptions )
		g_pOptions->SetPauseDownload( false );
}

JNIEXPORT void JNICALL Java_com_malothetoad_nzbm_service_Nzbget_shutdown( JNIEnv *env, jobject thiz )
{
	ExitProc();
}

JNIEXPORT jboolean JNICALL Java_com_malothetoad_nzbm_service_Nzbget_append( JNIEnv *env, jobject thiz, jstring path, jstring category, bool addToTop )
{
	bool success = false;
	const char *nzb = env->GetStringUTFChars( path, 0 );
	const char *cat = env->GetStringUTFChars( category, 0 );

	NZBFile* pNZBFile = NZBFile::CreateFromFile( nzb, cat );
	if ( pNZBFile )
	{
		info( "Request: Queue collection %s", nzb );
		g_pQueueCoordinator->AddNZBFileToQueue( pNZBFile, addToTop );
		delete pNZBFile;
		success = true;
	}
	else
	{
		success = false;
	}

	env->ReleaseStringUTFChars( path, nzb );
	env->ReleaseStringUTFChars( category, cat );

	return success;
}
