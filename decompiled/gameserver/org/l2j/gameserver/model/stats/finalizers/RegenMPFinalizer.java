// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.stats.finalizers;

import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.world.zone.type.MotherTreeZone;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.world.zone.type.CastleZone;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.ClanHallZone;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Optional;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.stats.IStatsFunction;

public class RegenMPFinalizer implements IStatsFunction
{
    @Override
    public double calc(final Creature creature, final Optional<Double> base, final Stat stat) {
        this.throwIfPresent(base);
        double baseValue = GameUtils.isPlayer(creature) ? creature.getActingPlayer().getTemplate().getBaseMpRegen(creature.getLevel()) : creature.getTemplate().getBaseMpReg();
        if (creature.isRaid()) {
            baseValue *= Config.RAID_MP_REGEN_MULTIPLIER;
        }
        if (GameUtils.isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            if (player.isInsideZone(ZoneType.CLAN_HALL) && player.getClan() != null && player.getClan().getHideoutId() > 0) {
                final ClanHallZone zone = ZoneManager.getInstance().getZone(player, ClanHallZone.class);
                final int posChIndex = (zone == null) ? -1 : zone.getResidenceId();
                final int clanHallIndex = player.getClan().getHideoutId();
                if (clanHallIndex > 0 && clanHallIndex == posChIndex) {
                    final AbstractResidence residense = ClanHallManager.getInstance().getClanHallById(player.getClan().getHideoutId());
                    if (residense != null) {
                        final ResidenceFunctionData func = residense.getFunction(ResidenceFunctionType.MP_REGEN);
                        if (func != null) {
                            baseValue *= func.getValue();
                        }
                    }
                }
            }
            if (player.isInsideZone(ZoneType.CASTLE) && player.getClan() != null && player.getClan().getCastleId() > 0) {
                final CastleZone zone2 = ZoneManager.getInstance().getZone(player, CastleZone.class);
                final int posCastleIndex = (zone2 == null) ? -1 : zone2.getResidenceId();
                final int castleIndex = player.getClan().getCastleId();
                if (castleIndex > 0 && castleIndex == posCastleIndex) {
                    final Castle castle = CastleManager.getInstance().getCastleById(player.getClan().getCastleId());
                    if (castle != null) {
                        final Castle.CastleFunction func2 = castle.getCastleFunction(3);
                        if (func2 != null) {
                            baseValue *= func2.getLevel() / 100.0f;
                        }
                    }
                }
            }
            if (player.isInsideZone(ZoneType.MOTHER_TREE)) {
                final MotherTreeZone zone3 = ZoneManager.getInstance().getZone(player, MotherTreeZone.class);
                final int mpBonus = (zone3 == null) ? 0 : zone3.getMpRegenBonus();
                baseValue += mpBonus;
            }
            if (player.isSitting()) {
                baseValue *= 1.5;
            }
            else if (!player.isMoving()) {
                baseValue *= 1.1;
            }
            else if (player.isRunning()) {
                baseValue *= 0.7;
            }
            baseValue *= creature.getLevelMod() * BaseStats.MEN.calcBonus(creature);
        }
        else if (GameUtils.isPet(creature)) {
            baseValue = ((Pet)creature).getPetLevelData().getPetRegenMP() * Config.PET_MP_REGEN_MULTIPLIER;
        }
        return Stat.defaultValue(creature, stat, baseValue);
    }
}
