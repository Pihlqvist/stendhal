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
package games.stendhal.server;

import marauroa.server.game.*;

import marauroa.common.game.*;
import marauroa.common.*;

public class StendhalRPWorld extends RPWorld
  {
  public StendhalRPWorld() throws Exception
    {
    super();

    Logger.trace("StendhalRPWorld::StendhalRPWorld",">");
    createRPClasses();
    Logger.trace("StendhalRPWorld::StendhalRPWorld","<");
    }
  
  private void createRPClasses()
    {
    Logger.trace("StendhalRPWorld::createRPClasses",">");
    RPClass player=new RPClass("player");
    player.add("name",RPClass.STRING);
    player.add("x",RPClass.FLOAT);
    player.add("y",RPClass.FLOAT);
    player.add("dx",RPClass.FLOAT);
    player.add("dy",RPClass.FLOAT); 
    player.add("dir",RPClass.BYTE);
    player.add("xp",RPClass.SHORT);
    player.add("hp",RPClass.SHORT);
    player.add("atk",RPClass.SHORT);
    player.add("def",RPClass.SHORT);
    player.add("text",RPClass.STRING);
    player.add("stopped",RPClass.FLAG,RPClass.HIDDEN);
    Logger.trace("StendhalRPWorld::createRPClasses","<");
    }
  
  public void onInit() throws Exception
    {
    StendhalRPZone village=new StendhalRPZone("village");
    village.setEntryPoint("26,43");
    village.addLayer("village_0_floor","games/stendhal/server/maps/village_0_floor.txt");
    village.addLayer("village_1_object","games/stendhal/server/maps/village_1_object.txt");
    village.addLayer("village_2_roof","games/stendhal/server/maps/village_2_roof.txt",false);
    addRPZone(village);


    StendhalRPZone city=new StendhalRPZone("city");
    city.setEntryPoint("10,34");
    city.addLayer("city_0_floor","games/stendhal/server/maps/city_0_floor.txt");
    city.addLayer("city_1_object","games/stendhal/server/maps/city_1_object.txt");
    city.addLayer("city_2_roof","games/stendhal/server/maps/city_2_roof.txt",false);
    
    for(int i=0;i<5;i++)
      {
      RPObject object=new RPObject();
      city.assignRPObjectID(object);
      object.put("type","pot");
      object.put("x",9+i);
      object.put("y",11);
      city.add(object);
      }
    
    RPObject wolf=new RPObject();
    city.assignRPObjectID(wolf);
    wolf.put("type","wolf");
    wolf.put("x",20);
    wolf.put("y",30);
    wolf.put("dx",0.5);
    wolf.put("dy",0);
    city.add(wolf);

    RPObject sheep=new RPObject();
    city.assignRPObjectID(sheep);
    sheep.put("type","sheep");
    sheep.put("x",60);
    sheep.put("y",30);
    sheep.put("dx",-0.1);
    sheep.put("dy",0);
    city.add(sheep);

    addRPZone(city);
    }
  
  public void onFinish() throws Exception
    {
    }
   
  }
