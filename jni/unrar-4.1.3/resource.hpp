#ifndef _RAR_RESOURCE_
#define _RAR_RESOURCE_

#ifdef ANDROID
const char  *St  (MSGID StringId);
const wchar *StW (MSGID StringId);
#else
#ifdef RARDLL
#define St(x)  ( "")
#define StW(x) (L"")
#else
const char  *St  (MSGID StringId);
const wchar *StW (MSGID StringId);
#endif
#endif //ANDROID

#endif
