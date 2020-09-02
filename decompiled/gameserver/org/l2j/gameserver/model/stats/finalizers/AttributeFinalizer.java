// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import java.util.Iterator;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.function.Predicate;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class AttributeFinalizer implements IStatsFunction
{
    private final AttributeType _type;
    private final boolean _isWeapon;
    
    public AttributeFinalizer(final AttributeType type, final boolean isWeapon) {
        this._type = type;
        this._isWeapon = isWeapon;
    }
    
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = creature.getTemplate().getBaseValue(stat, 0.0);
        if (GameUtils.isPlayable(creature)) {
            if (this._isWeapon) {
                final Item weapon = creature.getActiveWeaponInstance();
                if (weapon != null) {
                    final AttributeHolder weaponInstanceHolder = weapon.getAttribute(this._type);
                    if (weaponInstanceHolder != null) {
                        baseValue += weaponInstanceHolder.getValue();
                    }
                    final AttributeHolder weaponHolder = weapon.getTemplate().getAttribute(this._type);
                    if (weaponHolder != null) {
                        baseValue += weaponHolder.getValue();
                    }
                }
            }
            else {
                final Inventory inventory = creature.getInventory();
                if (inventory != null) {
                    for (final Item item : inventory.getPaperdollItems(Item::isArmor)) {
                        final AttributeHolder weaponInstanceHolder2 = item.getAttribute(this._type);
                        if (weaponInstanceHolder2 != null) {
                            baseValue += weaponInstanceHolder2.getValue();
                        }
                        final AttributeHolder weaponHolder2 = item.getTemplate().getAttribute(this._type);
                        if (weaponHolder2 != null) {
                            baseValue += weaponHolder2.getValue();
                        }
                    }
                }
            }
        }
        return Stat.defaultValue(creature, stat, baseValue);
    }
}
