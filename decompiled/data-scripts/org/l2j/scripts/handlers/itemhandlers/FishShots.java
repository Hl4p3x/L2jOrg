// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.item.type.WeaponType;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class FishShots implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        final Player player = playable.getActingPlayer();
        final Item weaponInst = player.getActiveWeaponInstance();
        if (Objects.isNull(weaponInst) || weaponInst.getItemType() != WeaponType.FISHING_ROD) {
            return false;
        }
        if (player.isChargedShot(ShotType.SOULSHOTS)) {
            return false;
        }
        if (item.getCount() < 1L) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
            return false;
        }
        player.chargeShot(ShotType.SOULSHOTS, 1.5);
        player.destroyItemWithoutTrace("Consume", item.getObjectId(), 1L, (WorldObject)null, false);
        final WorldObject oldTarget = player.getTarget();
        player.setTarget((WorldObject)player);
        final List<ItemSkillHolder> skills = (List<ItemSkillHolder>)item.getSkills(ItemSkillType.NORMAL);
        if (skills == null) {
            FishShots.LOGGER.warn("is missing skills!");
            return false;
        }
        final Creature creature;
        skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(creature, (ServerPacket)new MagicSkillUse(creature, (WorldObject)creature, holder.getSkillId(), holder.getLevel(), 0, 0), 600));
        player.setTarget(oldTarget);
        return true;
    }
}
