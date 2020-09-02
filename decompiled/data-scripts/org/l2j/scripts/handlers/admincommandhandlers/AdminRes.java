// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.function.Consumer;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminRes implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.startsWith("admin_res ")) {
            this.handleRes(activeChar, command.split(" ")[1]);
        }
        else if (command.equals("admin_res")) {
            this.handleRes(activeChar);
        }
        else if (command.startsWith("admin_res_monster ")) {
            this.handleNonPlayerRes(activeChar, command.split(" ")[1]);
        }
        else if (command.equals("admin_res_monster")) {
            this.handleNonPlayerRes(activeChar);
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminRes.ADMIN_COMMANDS;
    }
    
    private void handleRes(final Player activeChar) {
        this.handleRes(activeChar, null);
    }
    
    private void handleRes(final Player activeChar, final String resParam) {
        WorldObject obj = activeChar.getTarget();
        if (resParam != null) {
            final Player plyr = World.getInstance().findPlayer(resParam);
            if (plyr != null) {
                obj = (WorldObject)plyr;
            }
            else {
                try {
                    final int radius = Integer.parseInt(resParam);
                    World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)Player.class, radius, (Consumer)this::doResurrect);
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radius));
                    return;
                }
                catch (NumberFormatException e) {
                    BuilderUtil.sendSysMessage(activeChar, "Enter a valid player name or radius.");
                    return;
                }
            }
        }
        if (obj == null) {
            obj = (WorldObject)activeChar;
        }
        if (obj instanceof ControllableMob) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        this.doResurrect((Creature)obj);
    }
    
    private void handleNonPlayerRes(final Player activeChar) {
        this.handleNonPlayerRes(activeChar, "");
    }
    
    private void handleNonPlayerRes(final Player activeChar, final String radiusStr) {
        final WorldObject obj = activeChar.getTarget();
        try {
            int radius = 0;
            if (!radiusStr.isEmpty()) {
                radius = Integer.parseInt(radiusStr);
                World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)Creature.class, radius, knownChar -> {
                    if (!GameUtils.isPlayer(knownChar) && !(knownChar instanceof ControllableMob)) {
                        this.doResurrect((Creature)knownChar);
                    }
                    return;
                });
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radius));
            }
        }
        catch (NumberFormatException e) {
            BuilderUtil.sendSysMessage(activeChar, "Enter a valid radius.");
            return;
        }
        if (obj == null || GameUtils.isPlayer(obj) || obj instanceof ControllableMob) {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
            return;
        }
        this.doResurrect((Creature)obj);
    }
    
    private void doResurrect(final Creature targetChar) {
        if (!targetChar.isDead()) {
            return;
        }
        if (GameUtils.isPlayer((WorldObject)targetChar)) {
            ((Player)targetChar).restoreExp(100.0);
        }
        else {
            DecayTaskManager.getInstance().cancel(targetChar);
        }
        targetChar.doRevive();
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_res", "admin_res_monster" };
    }
}
