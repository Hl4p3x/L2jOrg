// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Objects;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public class ConditionPlayerCanSummonSiegeGolem extends Condition
{
    private final boolean _val;
    
    public ConditionPlayerCanSummonSiegeGolem(final boolean val) {
        this._val = val;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (!GameUtils.isPlayer(effector)) {
            return !this._val;
        }
        final Player player = effector.getActingPlayer();
        boolean canSummonSiegeGolem = true;
        if (player.isAlikeDead() || player.getClan() == null) {
            canSummonSiegeGolem = false;
        }
        final Castle castle = CastleManager.getInstance().getCastle(player);
        if (castle == null) {
            canSummonSiegeGolem = false;
        }
        if (castle != null && castle.getId() == 0) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        else if (Objects.nonNull(castle) && !castle.getSiege().isInProgress()) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        else if (player.getClanId() != 0 && Objects.nonNull(castle) && Objects.isNull(castle.getSiege().getAttackerClan(player.getClanId()))) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        return this._val == canSummonSiegeGolem;
    }
}
