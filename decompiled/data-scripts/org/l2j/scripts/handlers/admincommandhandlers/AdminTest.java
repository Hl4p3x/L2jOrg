// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import java.util.NoSuchElementException;
import org.l2j.gameserver.util.BuilderUtil;
import java.util.StringTokenizer;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminTest implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        if (command.equals("admin_stats")) {
            activeChar.sendMessage(ThreadPool.getInstance().getStats().toString());
        }
        else if (command.startsWith("admin_skill_test")) {
            try {
                final StringTokenizer st = new StringTokenizer(command);
                st.nextToken();
                final int id = Integer.parseInt(st.nextToken());
                if (command.startsWith("admin_skill_test")) {
                    this.adminTestSkill(activeChar, id, true);
                }
                else {
                    this.adminTestSkill(activeChar, id, false);
                }
            }
            catch (NumberFormatException e) {
                BuilderUtil.sendSysMessage(activeChar, "Command format is //skill_test <ID>");
            }
            catch (NoSuchElementException nsee) {
                BuilderUtil.sendSysMessage(activeChar, "Command format is //skill_test <ID>");
            }
        }
        return true;
    }
    
    private void adminTestSkill(final Player activeChar, final int id, final boolean msu) {
        final WorldObject target = activeChar.getTarget();
        Creature caster;
        if (!GameUtils.isCreature(target)) {
            caster = (Creature)activeChar;
        }
        else {
            caster = (Creature)target;
        }
        final Skill _skill = SkillEngine.getInstance().getSkill(id, 1);
        if (_skill != null) {
            caster.setTarget((WorldObject)activeChar);
            if (msu) {
                caster.broadcastPacket((ServerPacket)new MagicSkillUse(caster, (WorldObject)activeChar, id, 1, _skill.getHitTime(), _skill.getReuseDelay()));
            }
            else {
                caster.doCast(_skill);
            }
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminTest.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_stats", "admin_skill_test" };
    }
}
