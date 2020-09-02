// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public final class Harvester implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!Config.ALLOW_MANOR) {
            return false;
        }
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final List<ItemSkillHolder> skills = (List<ItemSkillHolder>)item.getSkills(ItemSkillType.NORMAL);
        if (skills == null) {
            Harvester.LOGGER.warn(": is missing skills!");
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        final WorldObject target = activeChar.getTarget();
        if (!GameUtils.isMonster(target) || !((Creature)target).isDead()) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            activeChar.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
            return false;
        }
        skills.forEach(holder -> activeChar.useMagic(holder.getSkill(), item, false, false));
        return true;
    }
}
