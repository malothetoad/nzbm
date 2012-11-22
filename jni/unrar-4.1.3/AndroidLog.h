#ifndef ANDROIDLOG_H
#define ANDROIDLOG_H

#include <stdio.h>
#include <android/log.h>

extern char *_android_unrar_curr_file_logs;
extern char *_android_unrar_logs;
#define android_buffer 1024

/* C files should define LOG_TAG before including this header */
#ifndef LOG_TAG
# define  LOG_TAG    "nzb4droid"
#endif

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define LOGW(...)  __android_log_print(ANDROID_LOG_WARNING,LOG_TAG,__VA_ARGS__)

#define printf(...) snprintf( _android_unrar_logs, android_buffer, __VA_ARGS__ )
#define mprintf(...) snprintf( _android_unrar_logs, android_buffer, __VA_ARGS__ )
#define eprintf(...) snprintf( _android_unrar_logs, android_buffer, __VA_ARGS__ )

/*
#define printf(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define mprintf(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define eprintf(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
*/

#endif 

