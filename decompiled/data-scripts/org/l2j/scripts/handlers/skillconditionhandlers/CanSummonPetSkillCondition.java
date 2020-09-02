// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.skillconditionhandlers;

import org.w3c.dom.Node;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillCondition;

public class CanSummonPetSkillCondition implements SkillCondition
{
    private CanSummonPetSkillCondition() {
    }
    
    public boolean canUse(final Creature caster, final Skill skill, final WorldObject target) {
        final Player player = caster.getActingPlayer();
        if (player == null || player.isSpawnProtected() || player.isTeleportProtected()) {
            return false;
        }
        boolean canSummon = true;
        if (((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).restoreSummonOnReconnect() && PlayerSummonTable.getInstance().getPets().containsKey(player.getObjectId())) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_MULTIPLE_PETS_AT_THE_SAME_TIME);
            canSummon = false;
        }
        else if (player.hasPet()) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_MULTIPLE_PETS_AT_THE_SAME_TIME);
            canSummon = false;
        }
        else if (player.getActiveTradeList() != null || player.hasItemRequest() || player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
            canSummon = false;
        }
        else if (AttackStanceTaskManager.getInstance().hasAttackStanceTask((Creature)player)) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_DURING_COMBAT);
            canSummon = false;
        }
        else if (player.isFlyingMounted() || player.isMounted() || player.inObserverMode() || player.isTeleporting()) {
            canSummon = false;
        }
        return canSummon;
    }
    
    public static final class Factory extends SkillConditionFactory
    {
        private static final CanSummonPetSkillCondition INSTANCE;
        
        public SkillCondition create(final Node xmlNode) {
            return (SkillCondition)Factory.INSTANCE;
        }
        
        public String conditionName() {
            return "CanSummonPet";
        }
        
        static {
            INSTANCE = new CanSummonPetSkillCondition();
        }
    }
}
