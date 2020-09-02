// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.PetItemHolder;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;

public class SummonItems extends ItemSkillsTemplate
{
    @Override
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player activeChar = playable.getActingPlayer();
        if (!activeChar.getFloodProtectors().getItemPetSummon().tryPerformAction("summon items") || activeChar.getBlockCheckerArena() != -1 || activeChar.inObserverMode() || activeChar.isAllSkillsDisabled() || activeChar.isCastingNow()) {
            return false;
        }
        if (activeChar.isSitting()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_USE_ACTIONS_AND_SKILLS_WHILE_THE_CHARACTER_IS_SITTING);
            return false;
        }
        if (activeChar.hasPet() || activeChar.isMounted()) {
            activeChar.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
            return false;
        }
        if (activeChar.isAttackingNow()) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_DURING_COMBAT);
            return false;
        }
        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
        if (petData == null || petData.getNpcId() == -1) {
            return false;
        }
        activeChar.addScript((Object)new PetItemHolder(item));
        return super.useItem(playable, item, forceUse);
    }
}
