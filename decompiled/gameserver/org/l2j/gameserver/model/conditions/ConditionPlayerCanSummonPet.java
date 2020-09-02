// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanSummonPet extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanSummonPet(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        final Player player = effector.getActingPlayer();
        if (player == null) {
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
        else if (player.isFlyingMounted() || player.isMounted() || player.inObserverMode() || player.isTeleporting()) {
            canSummon = false;
        }
        return this._val == canSummon;
    }
}
