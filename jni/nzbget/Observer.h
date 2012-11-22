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
 * $Revision: 397 $
 * $Date: 2011-05-24 14:52:41 +0200 (mar. 24 mai 2011) $
 *
 */


#ifndef OBSERVER_H
#define OBSERVER_H

#include <list>

class Observer;

class Subject
{
private:
	std::list<Observer*> 	m_Observers;

public:
					Subject();
	void 			Attach(Observer* Observer);
	void 			Detach(Observer* Observer);
	void 			Notify(void* Aspect);
};

class Observer
{
public:
	virtual			~Observer() {};
	virtual void	Update(Subject* Caller, void* Aspect) = 0;
};

#endif
