// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.usercommandhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IUserCommandHandler;

public class Unstuck implements IUserCommandHandler
{
    private static final int[] COMMAND_IDS;
    
    public boolean useUserCommand(final int id, final Player player) {
        if (player.isJailed()) {
            player.sendMessage("You cannot use this function while you are jailed.");
            return false;
        }
        final int unstuckTimer = player.getAccessLevel().isGm() ? 1000 : (Config.UNSTUCK_INTERVAL * 1000);
        if (player.isInOlympiadMode()) {
            player.sendPacket(SystemMessageId.YOU_CANNOT_USE_THAT_SKILL_IN_A_OLYMPIAD_MATCH);
            return false;
        }
        if (player.isCastingNow(SkillCaster::isAnyNormalType) || player.isMovementDisabled() || player.isMuted() || player.isAlikeDead() || player.inObserverMode()) {
            return false;
        }
        final Skill escape = SkillEngine.getInstance().getSkill(2099, 1);
        final Skill GM_escape = SkillEngine.getInstance().getSkill(2100, 1);
        if (player.getAccessLevel().isGm()) {
            if (GM_escape != null) {
                player.doCast(GM_escape);
                return true;
            }
            player.sendMessage("You use Escape: 1 second.");
        }
        else {
            if (Config.UNSTUCK_INTERVAL == 300 && escape != null) {
                player.doCast(escape);
                return true;
            }
            final SkillCaster skillCaster = SkillCaster.castSkill((Creature)player, player.getTarget(), escape, (Item)null, SkillCastingType.NORMAL, false, false, unstuckTimer);
            if (skillCaster == null) {
                player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.get(SkillCastingType.NORMAL) });
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                return false;
            }
            if (Config.UNSTUCK_INTERVAL > 100) {
                player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, unstuckTimer / 60000));
            }
            else {
                player.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, unstuckTimer / 1000));
            }
        }
        return true;
    }
    
    public int[] getUserCommandList() {
        return Unstuck.COMMAND_IDS;
    }
    
    static {
        COMMAND_IDS = new int[] { 52 };
    }
}
