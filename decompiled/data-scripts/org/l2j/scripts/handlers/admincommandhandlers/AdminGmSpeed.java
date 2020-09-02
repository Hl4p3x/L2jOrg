// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.EnumSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Set;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public final class AdminGmSpeed implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    private static final Set<Stat> SPEED_STATS;
    
    public boolean useAdminCommand(final String command, final Player player) {
        final StringTokenizer st = new StringTokenizer(command);
        final String cmd = st.nextToken();
        if (cmd.equals("admin_gmspeed")) {
            if (!st.hasMoreTokens()) {
                BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
                return false;
            }
            final String token = st.nextToken();
            if (Config.USE_SUPER_HASTE_AS_GM_SPEED) {
                AdminCommandHandler.getInstance().useAdminCommand(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, AdminSuperHaste.ADMIN_COMMANDS[0], token), false);
                return true;
            }
            if (!Util.isFloat(token)) {
                BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
                return false;
            }
            final double runSpeedBoost = Double.parseDouble(token);
            if (runSpeedBoost < 0.0 || runSpeedBoost > 10.0) {
                BuilderUtil.sendSysMessage(player, "//gmspeed [0...10]");
                return false;
            }
            final WorldObject target = player.getTarget();
            Creature targetCharacter;
            if (GameUtils.isCreature(target)) {
                targetCharacter = (Creature)target;
            }
            else {
                targetCharacter = (Creature)player;
            }
            AdminGmSpeed.SPEED_STATS.forEach(speedStat -> targetCharacter.getStats().removeFixedValue(speedStat));
            if (runSpeedBoost > 0.0) {
                final Creature creature;
                AdminGmSpeed.SPEED_STATS.forEach(speedStat -> creature.getStats().addFixedValue(speedStat, Double.valueOf(creature.getTemplate().getBaseValue(speedStat, 120.0) * runSpeedBoost)));
            }
            targetCharacter.getStats().recalculateStats(false);
            if (GameUtils.isPlayer((WorldObject)targetCharacter)) {
                ((Player)targetCharacter).broadcastUserInfo();
            }
            else {
                targetCharacter.broadcastInfo();
            }
            BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;D)Ljava/lang/String;, targetCharacter.getName(), runSpeedBoost * 100.0));
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminGmSpeed.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_gmspeed" };
        SPEED_STATS = EnumSet.of(Stat.RUN_SPEED, new Stat[] { Stat.WALK_SPEED, Stat.SWIM_RUN_SPEED, Stat.SWIM_WALK_SPEED, Stat.FLY_RUN_SPEED, Stat.FLY_WALK_SPEED });
    }
}
