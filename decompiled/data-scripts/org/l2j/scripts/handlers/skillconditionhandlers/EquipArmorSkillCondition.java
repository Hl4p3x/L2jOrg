// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.l2j.gameserver.model.item.type.ArmorType;
import java.util.Arrays;
import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.BodyPart;
import java.util.Objects;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class EquipArmorSkillCondition implements SkillCondition
{
    public int armorsMask;
    
    private EquipArmorSkillCondition(final int mask) {
        this.armorsMask = mask;
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        if (!GameUtils.isPlayer((WorldObject)caster)) {
            return false;
        }
        final Inventory inv = caster.getInventory();
        final Item chest = inv.getPaperdollItem(InventorySlot.CHEST);
        if (Objects.isNull(chest)) {
            return false;
        }
        final int chestMask = chest.getTemplate().getItemMask();
        if ((this.armorsMask & chestMask) == 0x0) {
            return false;
        }
        final BodyPart chestBodyPart = chest.getBodyPart();
        if (chestBodyPart == BodyPart.FULL_ARMOR) {
            return true;
        }
        final Item legs = inv.getPaperdollItem(InventorySlot.LEGS);
        if (Objects.isNull(legs)) {
            return false;
        }
        final int legMask = legs.getTemplate().getItemMask();
        return (this.armorsMask & legMask) != 0x0;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        public SkillCondition create(final Node xmlNode) {
            final int mask = Arrays.stream(this.parseString(xmlNode.getAttributes(), "type").split(" ")).mapToInt(s -> ArmorType.valueOf(s).mask()).reduce(0, (a, b) -> a | b);
            return (SkillCondition)new EquipArmorSkillCondition(mask);
        }
        
        public String conditionName() {
            return "armor";
        }
    }
}
