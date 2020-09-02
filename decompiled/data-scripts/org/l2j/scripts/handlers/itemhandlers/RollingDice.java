// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.Dice;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class RollingDice implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        final int itemId = item.getId();
        if (activeChar.isInOlympiadMode()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            return false;
        }
        final int number = this.rollDice(activeChar);
        if (number == 0) {
            activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER);
            return false;
        }
        final double angle = MathUtil.convertHeadingToDegree(activeChar.getHeading());
        final double radian = Math.toRadians(angle);
        final double course = Math.toRadians(180.0);
        final int x1 = (int)(Math.cos(3.141592653589793 + radian + course) * 40.0);
        final int y1 = (int)(Math.sin(3.141592653589793 + radian + course) * 40.0);
        final int x2 = activeChar.getX() + x1;
        final int y2 = activeChar.getY() + y1;
        final int z = activeChar.getZ();
        final Location destination = GeoEngine.getInstance().canMoveToTargetLoc(activeChar.getX(), activeChar.getY(), activeChar.getZ(), x2, y2, z, activeChar.getInstanceWorld());
        Broadcast.toSelfAndKnownPlayers((Creature)activeChar, (ServerPacket)new Dice(activeChar.getObjectId(), itemId, number, destination.getX(), destination.getY(), destination.getZ()));
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_ROLLED_A_S2);
        sm.addString(activeChar.getName());
        sm.addInt(number);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
        if (activeChar.isInsideZone(ZoneType.PEACE)) {
            Broadcast.toKnownPlayers((Creature)activeChar, (ServerPacket)sm);
        }
        else if (activeChar.isInParty()) {
            activeChar.getParty().broadcastToPartyMembers(activeChar, (ServerPacket)sm);
        }
        return true;
    }
    
    private int rollDice(final Player player) {
        if (!player.getFloodProtectors().getRollDice().tryPerformAction("roll dice")) {
            return 0;
        }
        return Rnd.get(1, 6);
    }
}
