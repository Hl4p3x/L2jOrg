// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.TowerOfInsolence.HeavenlyRift;

import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ExChangeNpcState;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Bomb extends AbstractNpcAI
{
    private static final int[] ITEM_DROP_1;
    private static final int[] ITEM_DROP_2;
    
    public Bomb() {
        this.addKillId(18003);
        this.addSpawnId(new int[] { 18003 });
    }
    
    public String onSpawn(final Npc npc) {
        npc.broadcastPacket((ServerPacket)new ExChangeNpcState(npc.getObjectId(), 1));
        return super.onSpawn(npc);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (Rnd.chance(33)) {
            addSpawn(20139, (IPositionable)npc, false, 1800000L);
        }
        else {
            World.getInstance().forEachVisibleObjectInRange((WorldObject)npc, (Class)Playable.class, 200, creature -> {
                if (creature != null && !creature.isDead()) {
                    creature.reduceCurrentHp((double)Rnd.get(300, 400), (Creature)npc, (Skill)null, DamageInfo.DamageType.ZONE);
                }
                return;
            });
            if (Rnd.chance(50)) {
                if (Rnd.chance(90)) {
                    npc.dropItem((Creature)killer.getActingPlayer(), Bomb.ITEM_DROP_1[Rnd.get(Bomb.ITEM_DROP_1.length)], 1L);
                }
                else {
                    npc.dropItem((Creature)killer.getActingPlayer(), Bomb.ITEM_DROP_2[Rnd.get(Bomb.ITEM_DROP_2.length)], 1L);
                }
            }
        }
        if (HeavenlyRift.getAliveNpcCount(npc.getId()) == 0) {
            GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
            GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    static {
        ITEM_DROP_1 = new int[] { 49756, 49762, 49763 };
        ITEM_DROP_2 = new int[] { 49760, 49761 };
    }
}
