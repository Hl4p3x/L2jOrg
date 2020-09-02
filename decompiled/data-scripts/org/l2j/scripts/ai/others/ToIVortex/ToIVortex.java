// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.ToIVortex;

import java.util.HashMap;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import java.util.Map;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ToIVortex extends AbstractNpcAI
{
    private static final int KEPLON = 30949;
    private static final int EUCLIE = 30950;
    private static final int PITHGON = 30951;
    private static final int DIMENSION_VORTEX_1 = 30952;
    private static final int DIMENSION_VORTEX_2 = 30953;
    private static final int DIMENSION_VORTEX_3 = 30954;
    private static final int GREEN_DIMENSION_STONE = 4404;
    private static final int BLUE_DIMENSION_STONE = 4405;
    private static final int RED_DIMENSION_STONE = 4406;
    private static final Map<String, Integer> TOI_FLOOR_ITEMS;
    private static final Map<String, Location> TOI_FLOORS;
    private static final Map<String, Integer> DIMENSION_TRADE;
    
    private ToIVortex() {
        this.addStartNpc(new int[] { 30949, 30950, 30951, 30952, 30953, 30954 });
        this.addTalkId(new int[] { 30949, 30950, 30951, 30952, 30953, 30954 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final int npcId = npc.getId();
        switch (event) {
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "10": {
                final Location loc = ToIVortex.TOI_FLOORS.get(event);
                final int itemId = ToIVortex.TOI_FLOOR_ITEMS.get(event);
                if (hasQuestItems(player, itemId)) {
                    takeItems(player, itemId, 1L);
                    player.teleToLocation((ILocational)loc, true);
                    break;
                }
                return "no-stones.htm";
            }
            case "GREEN":
            case "BLUE":
            case "RED": {
                if (player.getAdena() >= 100000L) {
                    takeItems(player, 57, 100000L);
                    giveItems(player, (int)ToIVortex.DIMENSION_TRADE.get(event), 1L);
                    break;
                }
                return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId);
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new ToIVortex();
    }
    
    static {
        TOI_FLOOR_ITEMS = new HashMap<String, Integer>();
        TOI_FLOORS = new HashMap<String, Location>();
        DIMENSION_TRADE = new HashMap<String, Integer>();
        ToIVortex.TOI_FLOORS.put("1", new Location(114356, 13423, -5096));
        ToIVortex.TOI_FLOORS.put("2", new Location(114666, 13380, -3608));
        ToIVortex.TOI_FLOORS.put("3", new Location(111982, 16028, -2120));
        ToIVortex.TOI_FLOORS.put("4", new Location(114636, 13413, -640));
        ToIVortex.TOI_FLOORS.put("5", new Location(114152, 19902, 928));
        ToIVortex.TOI_FLOORS.put("6", new Location(117131, 16044, 1944));
        ToIVortex.TOI_FLOORS.put("7", new Location(113026, 17687, 2952));
        ToIVortex.TOI_FLOORS.put("8", new Location(115571, 13723, 3960));
        ToIVortex.TOI_FLOORS.put("9", new Location(114649, 14144, 4976));
        ToIVortex.TOI_FLOORS.put("10", new Location(118507, 16605, 5984));
        ToIVortex.TOI_FLOOR_ITEMS.put("1", 4404);
        ToIVortex.TOI_FLOOR_ITEMS.put("2", 4404);
        ToIVortex.TOI_FLOOR_ITEMS.put("3", 4404);
        ToIVortex.TOI_FLOOR_ITEMS.put("4", 4405);
        ToIVortex.TOI_FLOOR_ITEMS.put("5", 4405);
        ToIVortex.TOI_FLOOR_ITEMS.put("6", 4405);
        ToIVortex.TOI_FLOOR_ITEMS.put("7", 4406);
        ToIVortex.TOI_FLOOR_ITEMS.put("8", 4406);
        ToIVortex.TOI_FLOOR_ITEMS.put("9", 4406);
        ToIVortex.TOI_FLOOR_ITEMS.put("10", 4406);
        ToIVortex.DIMENSION_TRADE.put("GREEN", 4404);
        ToIVortex.DIMENSION_TRADE.put("BLUE", 4405);
        ToIVortex.DIMENSION_TRADE.put("RED", 4406);
    }
}
