// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.DragonValley;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class LordIshka extends AbstractNpcAI
{
    private static final int LORDISHKA = 22100;
    private static final int SKILL = 50124;
    
    private LordIshka() {
        this.addKillId(22100);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        npc.broadcastPacket((ServerPacket)new ExShowScreenMessage(NpcStringId.GLORY_TO_THE_HEROES_WHO_HAVE_DEFEATED_LORD_ISHKA, 2, 5000, true, new String[0]));
        if (killer.isInParty()) {
            World.getInstance().forEachPlayerInRange((WorldObject)killer, 1000, player -> SkillCaster.triggerCast((Creature)npc, player, SkillEngine.getInstance().getSkill(50124, 1)), player -> killer.getParty().getMembers().contains(player));
        }
        SkillCaster.triggerCast((Creature)npc, (Creature)killer, SkillEngine.getInstance().getSkill(50124, 1));
        return super.onKill(npc, killer, isSummon);
    }
    
    public static AbstractNpcAI provider() {
        return new LordIshka();
    }
}
