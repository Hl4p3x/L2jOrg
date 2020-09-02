// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.holders.ItemSkillHolder;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.instancemanager.CastleManorManager;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class Seed implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!Config.ALLOW_MANOR) {
            return false;
        }
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final WorldObject tgt = playable.getTarget();
        if (!GameUtils.isNpc(tgt)) {
            playable.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }
        if (!GameUtils.isMonster(tgt) || ((Monster)tgt).isRaid() || tgt instanceof Chest) {
            playable.sendPacket(SystemMessageId.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
            return false;
        }
        final Monster target = (Monster)tgt;
        if (target.isDead()) {
            playable.sendPacket(SystemMessageId.INVALID_TARGET);
            return false;
        }
        if (target.isSeeded()) {
            playable.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
            return false;
        }
        final org.l2j.gameserver.model.Seed seed = CastleManorManager.getInstance().getSeed(item.getId());
        if (seed == null) {
            return false;
        }
        final Castle taxCastle = target.getTaxCastle();
        if (taxCastle == null || seed.getCastleId() != taxCastle.getId()) {
            playable.sendPacket(SystemMessageId.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        target.setSeeded(seed, activeChar);
        final List<ItemSkillHolder> skills = (List<ItemSkillHolder>)item.getSkills(ItemSkillType.NORMAL);
        if (skills != null) {
            skills.forEach(holder -> activeChar.useMagic(holder.getSkill(), item, false, false));
        }
        return true;
    }
}
