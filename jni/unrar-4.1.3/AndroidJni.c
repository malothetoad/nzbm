#include <jni.h>
#include "AndroidLog.h"
/*
#include "AndroidUnrar.h"

jint
Java_fr_mydedibox_nzb4droid_UtilityNzbJni_unrar( JNIEnv *env, jobject thiz, jobjectArray strArray )
{
	int i;

	// Compute arguments lenth (count)
	jsize len = (*env)->GetArrayLength(env,strArray);

	const char *argv[len];
	for( i=0; i<len; i++ )
	{
		jstring str = (jstring)(*env)->GetObjectArrayElement(env,strArray,i);
		argv[i] = (*env)->GetStringUTFChars( env, str, 0 );
	}

	return android_unrar( i, (char**)argv );
}
*/
