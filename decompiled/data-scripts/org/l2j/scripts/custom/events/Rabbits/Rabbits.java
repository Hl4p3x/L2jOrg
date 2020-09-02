// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.custom.events.Rabbits;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Iterator;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Collection;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.Event;

public final class Rabbits extends Event
{
    private static final int NPC_MANAGER = 900101;
    private static final int CHEST = 900102;
    private static final SkillHolder RABBIT_MAGIC_EYE;
    private static final SkillHolder RABBIT_TORNADO;
    private static final SkillHolder RABBIT_TRANSFORMATION;
    private static final SkillHolder RAID_CURSE;
    private static final int EVENT_TIME = 10;
    private static final int TOTAL_CHEST_COUNT = 75;
    private static final int TRANSFORMATION_ID = 105;
    private final Collection<Npc> _npcs;
    private final List<Player> _players;
    private boolean _isActive;
    private static final int[][] DROPLIST;
    
    private Rabbits() {
        this._npcs = (Collection<Npc>)ConcurrentHashMap.newKeySet();
        this._players = new ArrayList<Player>();
        this._isActive = false;
        this.addFirstTalkId(new int[] { 900101, 900102 });
        this.addTalkId(900101);
        this.addStartNpc(900101);
        this.addSkillSeeId(new int[] { 900102 });
        this.addAttackId(900102);
    }
    
    public boolean eventStart(final Player eventMaker) {
        if (this._isActive) {
            eventMaker.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getName()));
            return false;
        }
        if (!Config.CUSTOM_NPC_DATA) {
            eventMaker.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getName()));
            return false;
        }
        this._isActive = true;
        recordSpawn(this._npcs, 900101, -59227, -56939, -2039, 64106, false, 0L);
        for (int i = 0; i <= 75; ++i) {
            recordSpawn(this._npcs, 900102, Rnd.get(-60653, -58772), Rnd.get(-55830, -58146), -2030, 0, false, 600000L);
        }
        Broadcast.toAllOnlinePlayers("Rabbits Event: Chests spawned!");
        Broadcast.toAllOnlinePlayers("Rabbits Event: Go to Fantasy Isle and grab some rewards!");
        Broadcast.toAllOnlinePlayers("Rabbits Event: You have 10 minuntes!");
        Broadcast.toAllOnlinePlayers("Rabbits Event: After that time all chests will disappear...");
        this.startQuestTimer("END_RABBITS_EVENT", 600000L, (Npc)null, eventMaker);
        return true;
    }
    
    public boolean eventStop() {
        if (!this._isActive) {
            return false;
        }
        this._isActive = false;
        this.cancelQuestTimers("END_RABBITS_EVENT");
        for (final Npc npc : this._npcs) {
            if (npc != null) {
                npc.deleteMe();
            }
        }
        this._npcs.clear();
        for (final Player player : this._players) {
            if (player != null && player.getTransformationId() == 105) {
                player.untransform();
            }
        }
        this._players.clear();
        Broadcast.toAllOnlinePlayers("Rabbits Event: Event has finished.");
        return true;
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "900101-1.htm": {
                htmltext = "900101-1.htm";
                break;
            }
            case "transform": {
                if (player.isTransformed()) {
                    player.untransform();
                }
                if (player.isSitting()) {
                    player.standUp();
                }
                Rabbits.RABBIT_TRANSFORMATION.getSkill().applyEffects((Creature)player, (Creature)player);
                this._players.add(player);
                break;
            }
            case "END_RABBITS_EVENT": {
                Broadcast.toAllOnlinePlayers("Rabbits Event: Time up!");
                this.eventStop();
                break;
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public String onSkillSee(final Npc npc, final Player caster, final Skill skill, final WorldObject[] targets, final boolean isSummon) {
        if (skill.getId() == Rabbits.RABBIT_TORNADO.getSkillId()) {
            if (!npc.isInvisible() && CommonUtil.contains((Object[])targets, (Object)npc)) {
                dropItem(npc, caster, Rabbits.DROPLIST);
                npc.deleteMe();
                this._npcs.remove(npc);
                if (this._npcs.size() <= 1) {
                    Broadcast.toAllOnlinePlayers("Rabbits Event: No more chests...");
                    this.eventStop();
                }
            }
        }
        else if (skill.getId() == Rabbits.RABBIT_MAGIC_EYE.getSkillId() && npc.isInvisible() && MathUtil.isInsideRadius2D((ILocational)npc, (ILocational)caster, skill.getAffectRange())) {
            npc.setInvisible(false);
        }
        return super.onSkillSee(npc, caster, skill, targets, isSummon);
    }
    
    public String onAttack(final Npc npc, final Player attacker, final int damage, final boolean isSummon, final Skill skill) {
        if (this._isActive && (skill == null || skill.getId() != Rabbits.RABBIT_TORNADO.getSkillId())) {
            Rabbits.RAID_CURSE.getSkill().applyEffects((Creature)npc, (Creature)attacker);
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }
    
    private static void dropItem(final Npc npc, final Player player, final int[][] droplist) {
        final int chance = Rnd.get(100);
        for (final int[] drop : droplist) {
            if (chance > drop[1]) {
                npc.dropItem((Creature)player, drop[0], (long)Rnd.get(drop[2], drop[3]));
                return;
            }
        }
    }
    
    private static void recordSpawn(final Collection<Npc> npcs, final int npcId, final int x, final int y, final int z, final int heading, final boolean randomOffSet, final long despawnDelay) {
        final Npc npc = addSpawn(npcId, x, y, z, heading, randomOffSet, despawnDelay);
        if (npc.getId() == 900102) {
            npc.setIsImmobilized(true);
            npc.disableCoreAI(true);
            npc.setInvisible(true);
        }
        npcs.add(npc);
    }
    
    public boolean eventBypass(final Player activeChar, final String bypass) {
        return false;
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new Rabbits();
    }
    
    static {
        RABBIT_MAGIC_EYE = new SkillHolder(629, 1);
        RABBIT_TORNADO = new SkillHolder(630, 1);
        RABBIT_TRANSFORMATION = new SkillHolder(2428, 1);
        RAID_CURSE = new SkillHolder(4515, 1);
        DROPLIST = new int[][] { { 1540, 80, 10, 15 }, { 1538, 60, 5, 10 }, { 3936, 40, 5, 10 }, { 6387, 25, 5, 10 }, { 22025, 15, 5, 10 }, { 6622, 10, 1, 1 }, { 20034, 5, 1, 1 }, { 20004, 1, 1, 1 }, { 20004, 0, 1, 1 } };
    }
}
