// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.data.xml.impl.PetSkillData;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IPlayerActionHandler;

public final class ServitorSkillUse implements IPlayerActionHandler
{
    public void useAction(final Player player, final ActionData action, final boolean ctrlPressed, final boolean shiftPressed) {
        final Summon summon = player.getAnyServitor();
        if (summon == null || !summon.isServitor()) {
            player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
            return;
        }
        int skillLevel;
        player.getServitors().values().forEach(servitor -> {
            if (summon.isBetrayed()) {
                player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
            }
            else {
                skillLevel = PetSkillData.getInstance().getAvailableLevel(servitor, action.getOptionId());
                if (skillLevel > 0) {
                    servitor.setTarget(player.getTarget());
                    servitor.useMagic(SkillEngine.getInstance().getSkill(action.getOptionId(), skillLevel), (Item)null, ctrlPressed, shiftPressed);
                }
            }
        });
    }
}
