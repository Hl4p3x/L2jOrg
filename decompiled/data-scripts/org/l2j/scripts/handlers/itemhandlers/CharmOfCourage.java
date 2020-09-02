// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class CharmOfCourage implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        int level = activeChar.getLevel();
        final int itemLevel = item.getTemplate().getCrystalType().getId();
        if (level < 20) {
            level = 0;
        }
        else if (level < 40) {
            level = 1;
        }
        else if (level < 52) {
            level = 2;
        }
        else if (level < 61) {
            level = 3;
        }
        else if (level < 76) {
            level = 4;
        }
        else {
            level = 5;
        }
        if (itemLevel < level) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addItemName(item.getId());
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)sm });
            return false;
        }
        if (activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1L, (WorldObject)null, false)) {
            activeChar.setCharmOfCourage(true);
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new EtcStatusUpdate(activeChar) });
            return true;
        }
        return false;
    }
}
