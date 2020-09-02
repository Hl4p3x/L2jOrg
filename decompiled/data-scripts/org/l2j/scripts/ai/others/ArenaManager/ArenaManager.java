// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.ArenaManager;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public class ArenaManager extends AbstractNpcAI
{
    private static final int ARENA_MANAGER = 31226;
    private static final SkillHolder[] BUFFS;
    private static final SkillHolder CP_RECOVERY;
    private static final SkillHolder HP_RECOVERY;
    private static final int CP_COST = 1000;
    private static final int HP_COST = 1000;
    private static final int BUFF_COST = 2000;
    
    private ArenaManager() {
        this.addStartNpc(31226);
        this.addTalkId(31226);
        this.addFirstTalkId(31226);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "CPrecovery": {
                if (player.getAdena() >= 1000L) {
                    takeItems(player, 57, 1000L);
                    this.startQuestTimer("CPrecovery_delay", 2000L, npc, player);
                    break;
                }
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                break;
            }
            case "HPrecovery": {
                if (player.getAdena() >= 1000L) {
                    takeItems(player, 57, 1000L);
                    this.startQuestTimer("HPrecovery_delay", 2000L, npc, player);
                    break;
                }
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                break;
            }
            case "Buff": {
                if (player.getAdena() >= 2000L) {
                    takeItems(player, 57, 2000L);
                    for (final SkillHolder skill : ArenaManager.BUFFS) {
                        SkillCaster.triggerCast((Creature)npc, (Creature)player, skill.getSkill());
                    }
                    break;
                }
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                break;
            }
            case "CPrecovery_delay": {
                if (player != null && !player.isInsideZone(ZoneType.PVP)) {
                    npc.setTarget((WorldObject)player);
                    npc.doCast(ArenaManager.CP_RECOVERY.getSkill());
                    break;
                }
                break;
            }
            case "HPrecovery_delay": {
                if (player != null && !player.isInsideZone(ZoneType.PVP)) {
                    npc.setTarget((WorldObject)player);
                    npc.doCast(ArenaManager.HP_RECOVERY.getSkill());
                    break;
                }
                break;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new ArenaManager();
    }
    
    static {
        BUFFS = new SkillHolder[] { new SkillHolder(6805, 1), new SkillHolder(6806, 1), new SkillHolder(6807, 1), new SkillHolder(6808, 1), new SkillHolder(6804, 1), new SkillHolder(6812, 1) };
        CP_RECOVERY = new SkillHolder(4380, 1);
        HP_RECOVERY = new SkillHolder(6817, 1);
    }
}
