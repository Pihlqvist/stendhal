package games.stendhal.server.maps.semos.plains;

import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * The miller (original name: Jenny). She mills flour for players who bring
 * grain. 
 */
public class MillerNPC extends SpeakerNPCFactory {

	@Override
	protected void createDialog(SpeakerNPC npc) {
		npc.addJob("I run this windmill, where I can #mill people's #grain into flour for them. I also supply the bakery in Semos.");
		npc.addReply("grain",
		        "There's a farm nearby; they usually let people harvest there. You'll need a scythe, of course.");
		npc.addHelp("Do you know the bakery in Semos? I'm proud to say they use my flour. But the wolves ate my delivery boy again recently... they're probably running out.");
		npc.addGoodbye();

		// Jenny mills flour if you bring her grain.
		Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
		requiredResources.put("grain", 5);

		ProducerBehaviour behaviour = new ProducerBehaviour("jenny_mill_flour",
				"mill", "flour", requiredResources, 2 * 60);

		npc.addProducer(behaviour,
		        "Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.");
	}
}
