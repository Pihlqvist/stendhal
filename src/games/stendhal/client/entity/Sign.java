/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import marauroa.common.game.*;
import games.stendhal.client.*;
import java.awt.*;
import java.awt.geom.*;

public class Sign extends Entity {
	private final static long STANDARD_PERSISTENCE_TIME = 5000;

	private String text;

	private Sprite textImage;

	private long textPersistTime;

	// Give Signs same color on Screen and Log window. intensifly@gmx.com
	static final private Color signColor = new Color(0x006400); // dark green

	public Sign(GameObjects gameObjects, RPObject object)
			throws AttributeNotFoundException {
		super(gameObjects, object);
	}

	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);
		GameScreen screen = GameScreen.get();

		if (diff.has("text")) {
			text = diff.get("text");

			Graphics g2d = screen.expose();

			String[] lines = text.split("\n");

			int lineLengthPixels = 0;
			for (String line : lines) {
				int val = g2d.getFontMetrics().stringWidth(line);
				if (val > lineLengthPixels) {
					lineLengthPixels = val;
				}
			}

			GraphicsConfiguration gc = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();
			int width = lineLengthPixels + 4;
			int height = 16 * lines.length;

			Image image = gc.createCompatibleImage(width, height,
					Transparency.BITMASK);

			Graphics g = image.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			g.setColor(signColor);
			g.drawRect(0, 0, width - 1, height - 1);

			int j = 0;
			for (String line : lines) {
				g.setColor(signColor);
				// Give 1 more pixel distance to top sign border.
				// intensifly@gmx.com
				g.drawString(line, 2, 12 + j * 16);
				j++;
			}

			textImage = new Sprite(image);

			textPersistTime = Math.max(STANDARD_PERSISTENCE_TIME, text.length()
					* STANDARD_PERSISTENCE_TIME / 50);
		}
	}

	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1, 1);
	}

	public String defaultAction() {
		return "Look";
	}

	public String[] offeredActions() {
		String[] list = { "Look" };
		return list;
	}

	public void onAction(StendhalClient client, String action, String... params) {
		if (action.equals("Look")) {
			gameObjects.addText(this, textImage, textPersistTime);
			StendhalClient.get().addEventLine("You read \"" + text + "\"", signColor);
		}
	}

	public int compare(Entity entity) {
		return -1;
	}

}
