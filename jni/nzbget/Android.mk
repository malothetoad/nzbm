LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_ARM_MODE := arm
LOCAL_MODULE := nzbget

LOCAL_SRC_FILES := ArticleDownloader.cpp Log.cpp Observer.cpp Scanner.cpp Util.cpp \
		BinRpc.cpp LoggableFrontend.cpp Options.cpp Scheduler.cpp WebDownloader.cpp \
		ColoredFrontend.cpp NCursesFrontend.cpp ParChecker.cpp ScriptController.cpp WebServer.cpp \
		Connection.cpp NewsServer.cpp PrePostProcessor.cpp ServerPool.cpp XmlRpc.cpp \
		Decoder.cpp NNTPConnection.cpp QueueCoordinator.cpp \
		DiskState.cpp QueueEditor.cpp Thread.cpp \
		DownloadInfo.cpp NZBFile.cpp RemoteClient.cpp TLS.cpp \
		Frontend.cpp nzbget.cpp RemoteServer.cpp UrlCoordinator.cpp \
		Android.cpp
		
LOCAL_LDLIBS := -llog -lz

LOCAL_STATIC_LIBRARIES := sigc par2 ssl-static crypto-static xml2 unrar

LOCAL_CFLAGS += -O3 -DHAVE_CONFIG_H -DANDROID \
		-I$(LOCAL_PATH)/../ \
		-I$(LOCAL_PATH)/../android-external-openssl/include \
		-I$(LOCAL_PATH)/../libsigc++-2.2.10 \
		-I$(LOCAL_PATH)/../libxml2/include \
		-I$(LOCAL_PATH)/../unrar-4.1.3
#-DDEBUG \

include $(BUILD_SHARED_LIBRARY)

