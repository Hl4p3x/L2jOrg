// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.ShotType;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;

public class SoulShots extends AbstractShot
{
    @Override
    protected boolean canUse(final Player player) {
        if (Objects.isNull(player.getActiveWeaponInstance()) || !player.isAutoShotEnabled(ShotType.SOULSHOTS)) {
            player.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
            return false;
        }
        return true;
    }
    
    @Override
    protected ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }
    
    @Override
    protected boolean isBlessed() {
        return false;
    }
    
    @Override
    protected double getBonus(final Player player) {
        return player.getStats().getValue(Stat.SOUL_SHOTS_BONUS, 1.0) * 2.0;
    }
    
    @Override
    protected SystemMessageId getEnabledShotsMessage() {
        return SystemMessageId.YOUR_SOULSHOTS_ARE_ENABLED;
    }
}
