// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.TowerOfInsolence.HeavenlyRift;

import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Tower extends AbstractNpcAI
{
    public Tower() {
        this.addDeathId(18004);
    }
    
    public void onCreatureKill(final Creature creature, final Creature killer) {
        HeavenlyRift.getZone().broadcastPacket((ServerPacket)new ExShowScreenMessage(NpcStringId.YOU_HAVE_FAILED, 2, 5000, new String[0]));
        HeavenlyRift.getZone().forEachCreature(riftMonster -> riftMonster.decayMe(), riftMonster -> GameUtils.isMonster((WorldObject)riftMonster) && riftMonster.getId() == 20139 && !riftMonster.isDead());
        GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
        GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
        GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
    }
    
    public static AbstractNpcAI provider() {
        return new Tower();
    }
}
