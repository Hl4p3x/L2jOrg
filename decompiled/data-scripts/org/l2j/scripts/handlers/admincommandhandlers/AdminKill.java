// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.FriendlyNpc;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.World;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminKill implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_kill")) {
            final StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken();
            if (st.hasMoreTokens()) {
                final String firstParam = st.nextToken();
                final Player plyr = World.getInstance().findPlayer(firstParam);
                if (plyr != null) {
                    if (st.hasMoreTokens()) {
                        try {
                            final int radius = Integer.parseInt(st.nextToken());
                            World.getInstance().forEachVisibleObjectInRange((WorldObject)plyr, (Class)Creature.class, radius, knownChar -> {
                                if (knownChar instanceof ControllableMob || knownChar instanceof FriendlyNpc || knownChar == activeChar) {
                                    return;
                                }
                                else {
                                    this.kill(activeChar, (Creature)knownChar);
                                    return;
                                }
                            });
                            BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radius));
                            return true;
                        }
                        catch (NumberFormatException e) {
                            BuilderUtil.sendSysMessage(activeChar, "Invalid radius.");
                            return false;
                        }
                    }
                    this.kill(activeChar, (Creature)plyr);
                }
                else {
                    try {
                        final int radius = Integer.parseInt(firstParam);
                        World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)Creature.class, radius, wo -> {
                            if (wo instanceof ControllableMob || wo instanceof FriendlyNpc) {
                                return;
                            }
                            else {
                                this.kill(activeChar, wo);
                                return;
                            }
                        });
                        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radius));
                        return true;
                    }
                    catch (NumberFormatException e) {
                        BuilderUtil.sendSysMessage(activeChar, "Usage: //kill <player_name | radius>");
                        return false;
                    }
                }
            }
            else {
                final WorldObject obj = activeChar.getTarget();
                if (obj instanceof ControllableMob || !GameUtils.isCreature(obj)) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                }
                else {
                    this.kill(activeChar, (Creature)obj);
                }
            }
        }
        return true;
    }
    
    private void kill(final Player activeChar, final Creature target) {
        if (GameUtils.isPlayer((WorldObject)target)) {
            if (!target.isGM()) {
                target.stopAllEffects();
            }
            target.reduceCurrentHp((double)(target.getMaxHp() + target.getMaxCp() + 1), (Creature)activeChar, (Skill)null, DamageInfo.DamageType.OTHER);
        }
        else if (Config.CHAMPION_ENABLE && target.isChampion()) {
            target.reduceCurrentHp((double)(target.getMaxHp() * Config.CHAMPION_HP + 1), (Creature)activeChar, (Skill)null, DamageInfo.DamageType.OTHER);
        }
        else {
            boolean targetIsInvul = false;
            if (target.isInvul()) {
                targetIsInvul = true;
                target.setIsInvul(false);
            }
            target.reduceCurrentHp((double)(target.getMaxHp() + 1), (Creature)activeChar, (Skill)null, DamageInfo.DamageType.OTHER);
            if (targetIsInvul) {
                target.setIsInvul(true);
            }
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminKill.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_kill", "admin_kill_monster" };
    }
}
