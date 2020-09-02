// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class UnsummonPet implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        final Summon pet = (Summon)player.getPet();
        if (pet == null) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
        }
        else if (((Pet)pet).isUncontrollable()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_YOUR_PET_WHEN_ITS_HUNGER_GAUGE_IS_AT_0);
        }
        else if (pet.isBetrayed()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_YOUR_PET_WHEN_ITS_HUNGER_GAUGE_IS_AT_0);
        }
        else if (pet.isDead()) {
            player.sendPacket(SystemMessageId.DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM);
        }
        else if (pet.isAttackingNow() || pet.isInCombat() || pet.isMovementDisabled()) {
            player.sendPacket(SystemMessageId.A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE);
        }
        else if (pet.isHungry()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_RESTORE_A_HUNGRY_PET);
        }
        else {
            pet.unSummon(player);
        }
    }
}
