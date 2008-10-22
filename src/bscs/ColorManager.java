/*
 * By: Werner Schuster (murphee)
 * 
 * Copyright (c) 2005-2006 Werner Schuster (murphee)
 * 
 * This file is part of the EclipseShell project. EclipseShell is subject 
 * to the Eclipse Public License (EPL) available at
 * http://www.eclipse.org/org/documents/epl-v10.php
 * or in the file /epl-v10.html in the source for other info
 * see the /eclipseshell.license file
 */
package bscs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager implements IColorManager
{
	private static ColorManager mSingleton=null;
	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			 e.next().dispose();
	}
	public Color getColor(RGB rgb) {
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
	public static ColorManager instance()
	{
		synchronized (ColorManager.class)
		{
			if (mSingleton==null)
			{
				mSingleton=new ColorManager();
			}
			return mSingleton;
		}
	}
	//see if I can get the color constants from somewhere
	public Color getColor(String key) {
		// TODO Auto-generated method stub
		return getColor(ColorConstants.STRING);
	}
}
