// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExRequestCostumeUseItem extends ClientPacket
{
    private int itemObjectId;
    
    @Override
    protected void readImpl() throws Exception {
        this.itemObjectId = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final Item item = player.getInventory().getItemByObjectId(this.itemObjectId);
        if (Objects.nonNull(item) && CostumeEngine.getInstance().checkCostumeAction(player)) {
            final Creature creature;
            item.forEachSkill(ItemSkillType.NORMAL, skill -> SkillCaster.triggerCast(creature, creature, skill.getSkill(), item, true));
        }
    }
}
