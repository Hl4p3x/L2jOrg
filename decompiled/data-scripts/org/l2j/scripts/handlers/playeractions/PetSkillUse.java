// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class PetSkillUse implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        if (player.getTarget() == null) {
            return;
        }
        final Pet pet = player.getPet();
        if (pet == null) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
        }
        else if (pet.isUncontrollable()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_YOUR_PET_WHEN_ITS_HUNGER_GAUGE_IS_AT_0);
        }
        else if (pet.isBetrayed()) {
            player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
        }
        else if (pet.getLevel() - player.getLevel() > 20) {
            player.sendPacket(SystemMessageId.YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL);
        }
        else {
            final int skillLevel = PetDataTable.getInstance().getPetData(pet.getId()).getAvailableLevel(action.getOptionId(), pet.getLevel());
            if (skillLevel > 0) {
                pet.setTarget(player.getTarget());
                pet.useMagic(SkillEngine.getInstance().getSkill(action.getOptionId(), skillLevel), (Item)null, ctrlPressed, shiftPressed);
            }
            if (action.getOptionId() == CommonSkill.PET_SWITCH_STANCE.getId()) {
                pet.switchMode();
            }
        }
    }
}
