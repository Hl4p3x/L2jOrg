// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IBypassHandler;

public class Observation implements IBypassHandler
{
    private static final String[] COMMANDS;
    private static final int[][] LOCATIONS;
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        if (!(target instanceof org.l2j.gameserver.model.actor.instance.Observation)) {
            return false;
        }
        if (player.hasSummon()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_OBSERVE_A_SIEGE_WITH_A_SERVITOR_SUMMONED);
            return false;
        }
        if (player.isOnEvent()) {
            player.sendMessage("Cannot use while current Event");
            return false;
        }
        final String _command = command.split(" ")[0].toLowerCase();
        int param;
        try {
            param = Integer.parseInt(command.split(" ")[1]);
        }
        catch (NumberFormatException nfe) {
            Observation.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()), (Throwable)nfe);
            return false;
        }
        if (param < 0 || param > Observation.LOCATIONS.length - 1) {
            return false;
        }
        final int[] locCost = Observation.LOCATIONS[param];
        final Location loc = new Location(locCost[0], locCost[1], locCost[2]);
        final long cost = locCost[3];
        final String s = _command;
        switch (s) {
            case "observesiege": {
                if (SiegeManager.getInstance().getSiege((ILocational)loc) != null) {
                    doObserve(player, (Npc)target, loc, cost);
                }
                else {
                    player.sendPacket(SystemMessageId.OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE);
                }
                return true;
            }
            case "observeoracle": {
                doObserve(player, (Npc)target, loc, cost);
                return true;
            }
            case "observe": {
                doObserve(player, (Npc)target, loc, cost);
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static void doObserve(final Player player, final Npc npc, final Location pos, final long cost) {
        if (player.reduceAdena("Broadcast", cost, (WorldObject)npc, true)) {
            player.enterObserverMode(pos);
            player.sendItemList();
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
    }
    
    public String[] getBypassList() {
        return Observation.COMMANDS;
    }
    
    static {
        COMMANDS = new String[] { "observesiege", "observeoracle", "observe" };
        LOCATIONS = new int[][] { { -18347, 114000, -2360, 500 }, { -18347, 113255, -2447, 500 }, { 22321, 155785, -2604, 500 }, { 22321, 156492, -2627, 500 }, { 112000, 144864, -2445, 500 }, { 112657, 144864, -2525, 500 }, { 116260, 244600, -775, 500 }, { 116260, 245264, -721, 500 }, { 78100, 36950, -2242, 500 }, { 78744, 36950, -2244, 500 }, { 147457, 9601, -233, 500 }, { 147457, 8720, -252, 500 }, { 147542, -43543, -1328, 500 }, { 147465, -45259, -1328, 500 }, { 20598, -49113, -300, 500 }, { 18702, -49150, -600, 500 }, { 77541, -147447, 353, 500 }, { 77541, -149245, 353, 500 }, { 148416, 46724, -3000, 80 }, { 149500, 46724, -3000, 80 }, { 150511, 46724, -3000, 80 }, { -77200, 88500, -4800, 500 }, { -75320, 87135, -4800, 500 }, { -76840, 85770, -4800, 500 }, { -76840, 85770, -4800, 500 }, { -79950, 85165, -4800, 500 }, { -79185, 112725, -4300, 500 }, { -76175, 113330, -4300, 500 }, { -74305, 111965, -4300, 500 }, { -75915, 110600, -4300, 500 }, { -78930, 110005, -4300, 500 } };
    }
}
