/*
 *  This file is part of nzbget
 *
 *  Copyright (C) 2005  Bo Cordes Petersen <placebodk@users.sourceforge.net>
 *  Copyright (C) 2007  Andrey Prygunkov <hugbug@users.sourceforge.net>
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * $Revision: 411 $
 * $Date: 2012-05-03 12:51:13 +0200 (jeu. 03 mai 2012) $
 *
 */


#ifndef REMOTESERVER_H
#define REMOTESERVER_H

#include "Thread.h"
#include "Connection.h"

class RemoteServer : public Thread
{
private:
	Connection*			m_pConnection;

public:
						RemoteServer();
						~RemoteServer();
	virtual void		Run();
	virtual void 		Stop();
};

class RequestProcessor : public Thread
{
private:
	SOCKET				m_iSocket;

public:
	virtual void		Run();
	void				SetSocket(SOCKET iSocket) { m_iSocket = iSocket; };
};

#endif
