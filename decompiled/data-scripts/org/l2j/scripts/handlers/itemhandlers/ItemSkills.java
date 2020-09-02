// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;

public class ItemSkills extends ItemSkillsTemplate
{
    @Override
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        final Player player = playable.getActingPlayer();
        if (Objects.nonNull(player) && player.isInOlympiadMode()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_ITEM_IN_A_OLYMPIAD_MATCH);
            return false;
        }
        return super.useItem(playable, item, forceUse);
    }
}
