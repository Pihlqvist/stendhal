/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.piedpiper;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.RPZonePath;
import games.stendhal.server.core.pathfinder.MultiZonesFixedPath;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.interaction.NPCChatting;
import games.stendhal.server.entity.npc.interaction.NPCFollowing;
import games.stendhal.server.maps.quests.ThePiedPiper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class AwaitingPhase extends TPPQuest {
	private final SpeakerNPC piedpiper = new SpeakerNPC("Pied Piper");	
	private final SpeakerNPC mainNPC = TPPQuestHelperFunctions.getMainNPC();
	private final int minPhaseChangeTime;
	private int maxPhaseChangeTime;
	private List<RPZonePath> fullpathin = 
		new LinkedList<RPZonePath>();
	private List<RPZonePath> fullpathout = 
			new LinkedList<RPZonePath>();
	private final List<String> conversations = new LinkedList<String>();
	private final String explainations = 
			"I see that our city's savoiur is here. I have to speak with him quickly. "+
			"Please speak with me again after we finish talking.";
	
	private void addConversations() {
		TPP_Phase myphase = AWAITING;
		
		// Player asking about rats.
		mainNPC.add(
				ConversationStates.ATTENDING, 
				Arrays.asList("rats", "rats!"), 
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING, 
				"Well, we tried to clean up the city. "+
	    		"You can get a #reward for your help now, ask about #details "+
				  "if you want to know more.", 
				null);
		
		// Player asking about details.
		mainNPC.add(
				ConversationStates.ATTENDING, 
				"details", 
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING, 
				null, 
				new DetailsKillingsAction());
		
		// Player asked about reward
		mainNPC.add(
				ConversationStates.ATTENDING, 
				"reward", 
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING, 
				null, 
				new RewardPlayerAction());
	}
	
	private void fillConversations() {
		//piper
		conversations.add("Good day, Mayor Chalmers. What did you call me here for?");
		//mayor
		conversations.add("Hello, very glad to see our respectable hero here. Who hasn't heard about you, there is almost...");
		//piper
		conversations.add("Please talk about your business to me, my time is precious.");
		//mayor
		conversations.add("... ok, what was I saying? Ah yes, our city has a little problem with #rats.");
		//piper
		conversations.add("Again?");
		//mayor
		conversations.add("Yes, these animals are too stupid to remember a lesson they learnt only recently.");
		//piper
		conversations.add("I can help, if you are ready to pay.");
		//mayor
		conversations.add("Ados City has no other way to eliminate this nuisance. We will pay you.");
		//piper
		conversations.add("Do you know my usual price?");
		//mayor
		conversations.add("Yes, I have it written somewhere in my papers.");
		//piper
		conversations.add("Good. I will return for my reward soon, please prepare it.");
		//mayor
		conversations.add("Don't worry, how can I break your trust in me and my city?");
	}
	
	/**
	 * constructor
	 * @param timings - a pair of time parameters for phase timeout
	 */
	public AwaitingPhase(final Map<String, Integer> timings) {
		super(timings);
		minPhaseChangeTime = timings.get(AWAITING_TIME_MIN);
		maxPhaseChangeTime = timings.get(AWAITING_TIME_MAX);
		addConversations();
		fillConversations();
	}

	public void prepare() {
		createPiedPiper();
	}
	

	
	/**
	 * helper class for switching phase to next phase, 
	 * wrapper of observer around a function.
	 * 
	 * @author yoriy
	 */
	private final class PhaseSwitcher implements Observer {

		private ITPPQuest myphase; 
		
		public void update(Observable arg0, Object arg1) {
			myphase.phaseToNextPhase(
					ThePiedPiper.getNextPhaseClass(ThePiedPiper.getPhase()), 
					Arrays.asList("normal switching"));			
		}
		
		public PhaseSwitcher(ITPPQuest phase) {
			myphase = phase;
		}
		
	}

	/**
	 * prepare NPC to walk through his multizone pathes and do some actions during that.
	 */
	private void leadNPC() {
		final StendhalRPZone zone = fullpathin.get(0).get().first();
		final int x=fullpathin.get(0).get().second().get(0).getX();
		final int y=fullpathin.get(0).get().second().get(0).getY();
		piedpiper.setPosition(x, y);
		zone.add(piedpiper);
		Observer o = new MultiZonesFixedPath(piedpiper, fullpathin, 
						new NPCFollowing(mainNPC, piedpiper,
							new NPCChatting(piedpiper, mainNPC, conversations, explainations,
								new MultiZonesFixedPath(piedpiper, fullpathout, 
									new PhaseSwitcher(this)))));
		o.update(null, null);
	}
	
	public int getMinTimeOut() {
		return minPhaseChangeTime;
	}
	

	public int getMaxTimeOut() {
		return maxPhaseChangeTime;
	}


	public void phaseToDefaultPhase(List<String> comments) {
		destroyPiedPiper();
		super.phaseToDefaultPhase(comments);		
	}


	public void phaseToNextPhase(ITPPQuest nextPhase, List<String> comments) {
		destroyPiedPiper();
		super.phaseToNextPhase(nextPhase, comments);
	}
	
	
	/*
	 *  Pied Piper sent rats away:-)
	 */
	public String getSwitchingToNextPhaseMessage() {
		final String text = 
			"Mayor Chalmers shouts: Thankfully, all the #rats are gone now, " +
			"the Pied Piper hypnotized them and led them away to the dungeons. "+
			"Those of you who helped Ados City with the rats problem "+
			"can get your #reward now.";		
		return text;
	}

	public TPP_Phase getPhase() {
		return TPP_Phase.TPP_AWAITING;
	}
	
	/**
	 * function for creating pied piper npc
	 */
	private void createPiedPiper() {
		piedpiper.setEntityClass("holidaymakernpc");
		piedpiper.initHP(1000);
		piedpiper.setResistance(0);
		piedpiper.setVisibility(100);
		piedpiper.setAllowToActAlone(true);
		piedpiper.add(ConversationStates.IDLE, 
							ConversationPhrases.GREETING_MESSAGES, 
							new GreetingMatchesNameCondition(piedpiper.getName()), true, 
							ConversationStates.IDLE, 
							"hello", 
							null);
		fullpathin = PathesBuildHelper.getAwaitingPhasePathIn();
		fullpathout = PathesBuildHelper.getAwaitingPhasePathOut();
		leadNPC();
	}
	
	/**
	 * function will remove piped piper npc object
	 */
	private void destroyPiedPiper() {
		piedpiper.getZone().remove(piedpiper);
	}		
	
}

