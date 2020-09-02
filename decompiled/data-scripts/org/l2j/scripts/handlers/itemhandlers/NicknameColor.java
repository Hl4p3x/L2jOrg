// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.network.serverpackets.ExRequestChangeNicknameColor;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class NicknameColor implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        playable.sendPacket(new ServerPacket[] { (ServerPacket)new ExRequestChangeNicknameColor(item.getObjectId()) });
        return true;
    }
}
