/*
 *  This file if part of nzbget
 *
 *  Copyright (C) 2004  Sven Henkel <sidddy@users.sourceforge.net>
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

#include "Observer.h"
#include "Log.h"

Subject::Subject()
{
	m_Observers.clear();
}

void Subject::Attach(Observer* Observer)
{
	m_Observers.push_back(Observer);
}

void Subject::Detach(Observer* Observer)
{
	m_Observers.remove(Observer);
}

void Subject::Notify(void* Aspect)
{
	debug("Notifying observers");
	
	for (std::list<Observer*>::iterator it = m_Observers.begin(); it != m_Observers.end(); it++)
	{
        Observer* Observer = *it;
		Observer->Update(this, Aspect);
	}
}
