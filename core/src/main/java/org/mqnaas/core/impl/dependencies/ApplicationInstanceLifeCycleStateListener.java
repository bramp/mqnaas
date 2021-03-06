package org.mqnaas.core.impl.dependencies;

/*
 * #%L
 * MQNaaS :: Core
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import org.mqnaas.core.impl.ApplicationInstance;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public interface ApplicationInstanceLifeCycleStateListener {

	/**
	 * Called when an ApplicationInstance is instantiated
	 * 
	 * @param application
	 */
	public void instantiated(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is resolved (its last dependency is assigned)
	 * 
	 * @param application
	 */
	public void resolved(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is activated
	 * 
	 * @param application
	 */
	public void activated(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is deactivated
	 * 
	 * @param application
	 */
	public void deactivated(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is unresolved (had all dependencies assigned and it loses one)
	 * 
	 * @param application
	 */
	public void unresolved(ApplicationInstance application);

	/**
	 * Called when an ApplicationInstance is destroyed
	 * 
	 * @param application
	 */
	public void destroyed(ApplicationInstance application);

}
