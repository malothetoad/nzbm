/*
 *  This file if part of nzbget
 *
 *  Copyright (C) 2004 Sven Henkel <sidddy@users.sourceforge.net>
 *  Copyright (C) 2007-2008 Andrey Prygunkov <hugbug@users.sourceforge.net>
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
 * $Revision: 424 $
 * $Date: 2012-06-23 20:58:56 +0200 (sam. 23 juin 2012) $
 *
 */


#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#ifdef WIN32
#include "win32.h"
#endif

#include <stdlib.h>
#include <string.h>

#include "nzbget.h"
#include "NewsServer.h"

NewsServer::NewsServer(const char* szHost, int iPort, const char* szUser, const char* szPass, bool bJoinGroup, bool bTLS, int iMaxConnections, int iLevel)
{
	m_szHost = NULL;
	m_iPort = iPort;
	m_szUser = NULL;
	m_szPassword = NULL;
	m_iLevel = iLevel;
	m_iMaxConnections = iMaxConnections;
	m_bJoinGroup = bJoinGroup;
	m_bTLS = bTLS;

	if (szHost)
	{
		m_szHost = strdup(szHost);
	}
	if (szUser)
	{
		m_szUser = strdup(szUser);
	}
	if (szPass)
	{
		m_szPassword = strdup(szPass);
	}
}

NewsServer::~NewsServer()
{
	if (m_szHost)
	{
		free(m_szHost);
	}
	if (m_szUser)
	{
		free(m_szUser);
	}
	if (m_szPassword)
	{
		free(m_szPassword);
	}
}
