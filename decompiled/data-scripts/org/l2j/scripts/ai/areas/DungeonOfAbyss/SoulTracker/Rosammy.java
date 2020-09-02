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

public class Rosammy extends AbstractNpcAI
{
    private static final int SOUL_TRACKER_ROSAMMY = 31777;
    private static final int KEY_OF_EAST_WING = 90011;
    private static final Map<String, Location> LOCATIONS;
    
    private Rosammy() {
        this.addStartNpc(31777);
        this.addTalkId(31777);
        this.addFirstTalkId(31777);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (npc.getId() == 31777) {
            final QuestState qs = player.getQuestState("Q00935_ExploringTheEastWingOfTheDungeonOfAbyss");
            switch (event) {
                case "1": {
                    if (qs != null && qs.isStarted()) {
                        player.teleToLocation((ILocational)Rosammy.LOCATIONS.get(event), false);
                        break;
                    }
                    return "no_enter.htm";
                }
                case "2": {
                    player.teleToLocation((ILocational)Rosammy.LOCATIONS.get(event), false);
                    break;
                }
                case "3": {
                    if (!hasQuestItems(player, 90011)) {
                        return "no_key.htm";
                    }
                    player.teleToLocation((ILocational)Rosammy.LOCATIONS.get(event), false);
                    break;
                }
                case "4": {
                    player.teleToLocation((ILocational)Rosammy.LOCATIONS.get(event), false);
                    break;
                }
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new Rosammy();
    }
    
    static {
        (LOCATIONS = new HashMap<String, Location>()).put("1", new Location(-110067, -177733, -6751));
        Rosammy.LOCATIONS.put("2", new Location(-120318, -179626, -6752));
        Rosammy.LOCATIONS.put("3", new Location(-112632, -178671, -6751));
        Rosammy.LOCATIONS.put("4", new Location(146945, 26764, -2200));
    }
}
