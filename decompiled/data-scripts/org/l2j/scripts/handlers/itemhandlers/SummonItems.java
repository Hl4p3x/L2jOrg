// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.holders.PetItemRequest;
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
        final Player player = playable.getActingPlayer();
        if (!player.getFloodProtectors().getItemPetSummon().tryPerformAction("summon items") || player.getBlockCheckerArena() != -1 || player.inObserverMode() || player.isAllSkillsDisabled() || player.isCastingNow()) {
            return false;
        }
        if (player.isSitting()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ACTIONS_AND_SKILLS_WHILE_THE_CHARACTER_IS_SITTING);
            return false;
        }
        if (player.hasPet() || player.isMounted()) {
            player.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
            return false;
        }
        if (player.isAttackingNow()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_DURING_COMBAT);
            return false;
        }
        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
        if (petData == null || petData.getNpcId() == -1) {
            return false;
        }
        player.addRequest((AbstractRequest)new PetItemRequest(player, item));
        return super.useItem(playable, item, forceUse);
    }
}
