#include "rar.hpp"

char *_android_unrar_logs;
char *_android_unrar_curr_file_logs;

extern "C"
{
	char* android_unrar_curr_file_logs()
	{
		return _android_unrar_curr_file_logs;
	}

	char* android_unrar_logs()
	{
		return _android_unrar_logs;
	}

	int android_unrar( int argc, char *argv[] )
	{
		if( _android_unrar_curr_file_logs != NULL )
			free( _android_unrar_curr_file_logs );
		_android_unrar_curr_file_logs = (char*)malloc(android_buffer);

		if( _android_unrar_logs != NULL )
			free( _android_unrar_logs );
		_android_unrar_logs = (char*)malloc(android_buffer);

		setlocale( LC_ALL, "" );
		setbuf( stdout, NULL );

		ErrHandler.SetSignalHandlers( true );
		RARInitData();

		CommandData Cmd;
		Cmd.PreprocessCommandLine( argc, argv );
		if ( !Cmd.ConfigDisabled )
		{
			Cmd.ReadConfig();
			Cmd.ParseEnvVar();
		}
		Cmd.ParseCommandLine( argc,argv );

		InitConsoleOptions( Cmd.MsgStream, Cmd.Sound );
		InitLogOptions( Cmd.LogName );
		ErrHandler.SetSilent( Cmd.AllYes || Cmd.MsgStream==MSG_NULL );
		ErrHandler.SetShutdown( Cmd.Shutdown );

		Cmd.OutTitle();
		Cmd.ProcessCommand();

		File::RemoveCreated();

		return( ErrHandler.GetErrorCode() );
	}
}
