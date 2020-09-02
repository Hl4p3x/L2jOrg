// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class OpenDwarfRecipeBook extends AbstractEffect
{
    private OpenDwarfRecipeBook() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector)) {
            return;
        }
        final Player player = effector.getActingPlayer();
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.ITEM_CREATION_IS_NOT_POSSIBLE_WHILE_ENGAGED_IN_A_TRADE);
            return;
        }
        RecipeController.getInstance().requestBookOpen(player, true);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final OpenDwarfRecipeBook INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "OpenDwarfRecipeBook";
        }
        
        static {
            INSTANCE = new OpenDwarfRecipeBook();
        }
    }
}
