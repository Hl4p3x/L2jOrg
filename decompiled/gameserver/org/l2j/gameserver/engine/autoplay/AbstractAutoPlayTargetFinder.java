// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import java.util.Comparator;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Summon;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

abstract class AbstractAutoPlayTargetFinder implements AutoPlayTargetFinder
{
    @Override
    public boolean canBeTarget(final Player player, final WorldObject target) {
        return !GameUtils.isGM(target) && !player.isTargetingDisabled() && target.isTargetable() && target.isAutoAttackable(player) && this.checkRespectfulMode(player, target) && GeoEngine.getInstance().canSeeTarget(player, target) && GeoEngine.getInstance().canMoveToTarget(player, target);
    }
    
    private boolean checkRespectfulMode(final Player player, final WorldObject target) {
        final Monster monster;
        return !player.getAutoPlaySettings().isRespectfulMode() || !(target instanceof Monster) || (monster = (Monster)target) != target || this.canAttackRespectfully(player, monster);
    }
    
    private boolean canAttackRespectfully(final Player player, final Monster monster) {
        return Objects.isNull(monster.getTarget()) || monster.getTarget().equals(player) || monster.getAggroList().isEmpty() || this.checkFriendlyTarget(monster, player);
    }
    
    private boolean checkFriendlyTarget(final Monster monster, final Player player) {
        final WorldObject target = monster.getTarget();
        if (target != player.getPet()) {
            final WorldObject worldObject = target;
            final Summon s;
            if (!(worldObject instanceof Summon) || (s = (Summon)worldObject) != worldObject || !player.getServitors().containsValue(s)) {
                final WorldObject worldObject2 = target;
                final Player targetPlayer;
                if (!(worldObject2 instanceof Player) || (targetPlayer = (Player)worldObject2) != worldObject2 || !player.isInParty() || !player.getParty().containsPlayer(targetPlayer)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    protected Creature findNextTarget(final Player player, final Class<? extends Creature> targetClass, final int range) {
        return World.getInstance().findFirstVisibleObject(player, (Class<Creature>)targetClass, range, false, creature -> this.canBeTarget(player, creature), Comparator.comparingDouble(m -> MathUtil.calculateDistanceSq3D(player, m)));
    }
}
