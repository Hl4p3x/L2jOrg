// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.DungeonOfAbyss.SoulTracker;

import java.util.HashMap;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import java.util.Map;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Ingrit extends AbstractNpcAI
{
    private static final int SOUL_TRACKER_INRGIT = 31775;
    private static final int KEY_OF_WEST_WING = 90010;
    private static final Map<String, Location> LOCATIONS;
    
    private Ingrit() {
        this.addStartNpc(31775);
        this.addTalkId(31775);
        this.addFirstTalkId(31775);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (npc.getId() == 31775) {
            final QuestState qs = player.getQuestState("Q00933_ExploringTheWestWingOfTheDungeonOfAbyss");
            switch (event) {
                case "1": {
                    if (qs != null && qs.isStarted()) {
                        player.teleToLocation((ILocational)Ingrit.LOCATIONS.get(event), false);
                        break;
                    }
                    return "no_enter.htm";
                }
                case "2": {
                    player.teleToLocation((ILocational)Ingrit.LOCATIONS.get(event), false);
                    break;
                }
                case "3": {
                    if (!hasQuestItems(player, 90010)) {
                        return "no_key.htm";
                    }
                    player.teleToLocation((ILocational)Ingrit.LOCATIONS.get(event), false);
                    break;
                }
                case "4": {
                    player.teleToLocation((ILocational)Ingrit.LOCATIONS.get(event), false);
                    break;
                }
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new Ingrit();
    }
    
    static {
        (LOCATIONS = new HashMap<String, Location>()).put("1", new Location(-119533, -179641, -6751));
        Ingrit.LOCATIONS.put("2", new Location(-120325, -182444, -6752));
        Ingrit.LOCATIONS.put("3", new Location(-116975, -178699, -6751));
        Ingrit.LOCATIONS.put("4", new Location(146945, 26764, -2200));
    }
}
