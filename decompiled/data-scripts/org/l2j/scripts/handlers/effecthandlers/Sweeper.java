// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Party;
import java.util.Iterator;
import java.util.Collection;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class Sweeper extends AbstractEffect
{
    private Sweeper() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector) || !GameUtils.isAttackable((WorldObject)effected)) {
            return;
        }
        final Player player = effector.getActingPlayer();
        final Attackable monster = (Attackable)effected;
        if (!monster.checkSpoilOwner(player, false)) {
            return;
        }
        if (!player.getInventory().checkInventorySlotsAndWeight(monster.getSpoilLootItems(), false, false)) {
            return;
        }
        final Collection<ItemHolder> items = (Collection<ItemHolder>)monster.takeSweep();
        if (Objects.nonNull(items)) {
            for (final ItemHolder sweepedItem : items) {
                final Party party = player.getParty();
                if (party != null) {
                    party.distributeItem(player, sweepedItem, true, monster);
                }
                else {
                    player.addItem("Sweeper", sweepedItem, (WorldObject)effected, true);
                }
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final Sweeper INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "Sweeper";
        }
        
        static {
            INSTANCE = new Sweeper();
        }
    }
}
