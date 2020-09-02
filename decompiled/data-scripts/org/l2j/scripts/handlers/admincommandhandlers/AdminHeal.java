// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminHeal implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_heal")) {
            this.handleHeal(activeChar);
        }
        else if (command.startsWith("admin_heal")) {
            try {
                final String healTarget = command.substring(11);
                this.handleHeal(activeChar, healTarget);
            }
            catch (StringIndexOutOfBoundsException e) {
                if (Config.DEVELOPER) {
                    AdminHeal.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/StringIndexOutOfBoundsException;)Ljava/lang/String;, e));
                }
                BuilderUtil.sendSysMessage(activeChar, "Incorrect target/radius specified.");
            }
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminHeal.ADMIN_COMMANDS;
    }
    
    private void handleHeal(final Player activeChar) {
        this.handleHeal(activeChar, null);
    }
    
    private void handleHeal(final Player activeChar, final String player) {
        WorldObject obj = activeChar.getTarget();
        if (player != null) {
            final Player plyr = World.getInstance().findPlayer(player);
            if (plyr != null) {
                obj = (WorldObject)plyr;
            }
            else {
                try {
                    final int radius = Integer.parseInt(player);
                    World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Creature.class, character -> {
                        character.setCurrentHpMp((double)character.getMaxHp(), (double)character.getMaxMp());
                        if (GameUtils.isPlayer((WorldObject)character)) {
                            character.setCurrentCp((double)character.getMaxCp());
                        }
                        return;
                    });
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radius));
                    return;
                }
                catch (NumberFormatException ex) {}
            }
        }
        if (obj == null) {
            obj = (WorldObject)activeChar;
        }
        if (GameUtils.isCreature(obj)) {
            final Creature target = (Creature)obj;
            target.setCurrentHpMp((double)target.getMaxHp(), (double)target.getMaxMp());
            if (GameUtils.isPlayer((WorldObject)target)) {
                target.setCurrentCp((double)target.getMaxCp());
            }
        }
        else {
            activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminRes.class);
        ADMIN_COMMANDS = new String[] { "admin_heal" };
    }
}
