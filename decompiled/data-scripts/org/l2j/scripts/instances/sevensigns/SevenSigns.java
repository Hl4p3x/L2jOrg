// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.instances.sevensigns;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.List;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.instances.AbstractInstance;

public class SevenSigns extends AbstractInstance
{
    private static final int ANAKIM_GATEKEEPER_SPIRIT = 31089;
    private static final int LILITH_GATEKEEPER_SPIRIT = 31087;
    private static final int GATEKEEPER_SPIRIT_OUT_TELEPORT = 31088;
    private static final int ANAKIM = 25286;
    private static final int LILITH = 25283;
    private static final int ANAKIM_INSTANCE_TEMPLATE_ID = 200;
    private static final int LILITH_INSTANCE_TEMPLATE_ID = 199;
    private static final int ANAKIM_ZONE_ID = 70052;
    private static final int LILITH_ZONE_ID = 70053;
    private static final int MAX_PLAYERS_IN_ZONE = 300;
    
    public SevenSigns() {
        super(new int[] { 200, 199 });
        this.addStartNpc(new int[] { 31089, 31087 });
        this.addTalkId(new int[] { 31089, 31087, 31088 });
        this.addKillId(new int[] { 25286, 25283 });
        this.addInstanceLeaveId(new int[] { 200, 199 });
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "ANAKIM_ENTER": {
                this.onRaidEnter(npc, player, 70052, 200);
                break;
            }
            case "LILITH_ENTER": {
                this.onRaidEnter(npc, player, 70053, 199);
                break;
            }
            case "CLEAR_ZONE_ANAKIM": {
                this.teleportFromZone(70052);
                break;
            }
            case "CLEAR_ZONE_LILITH": {
                this.teleportFromZone(70053);
                break;
            }
            case "TELEPORT_OUT": {
                player.teleToLocation(TeleportWhereType.TOWN);
                break;
            }
            case "ANAKIM_DEATH": {
                this.castInvincibility(199, 25283);
                break;
            }
            case "LILITH_DEATH": {
                this.castInvincibility(200, 25286);
                break;
            }
        }
        return null;
    }
    
    protected void teleportFromZone(final int lilithZoneId) {
        final Zone zone = ZoneManager.getInstance().getZoneById(lilithZoneId);
        zone.forEachPlayer(p -> p.teleToLocation(TeleportWhereType.TOWN));
    }
    
    protected void castInvincibility(final int lilithInstanceTemplateId, final int lilith2) {
        final List<Instance> instances = (List<Instance>)InstanceManager.getInstance().getInstances(lilithInstanceTemplateId);
        final Skill skill;
        instances.stream().map(instance -> instance.getNpc(lilith2)).filter(Objects::nonNull).forEach(lilith -> {
            skill = SkillEngine.getInstance().getSkill(15394, 1);
            SkillCaster.triggerCast(lilith, lilith, skill);
        });
    }
    
    protected void onRaidEnter(final Npc npc, final Player player, final int zoneId, final int raidTemplateId) {
        if (MathUtil.isInsideRadius3D((ILocational)player, (ILocational)npc, 1000)) {
            final Zone zone = ZoneManager.getInstance().getZoneById(zoneId);
            if (zone.getPlayersInsideCount() < 300L) {
                this.enterInstance(player, npc, raidTemplateId);
            }
            else {
                player.sendMessage(this.getZoneIsFullMessage(zoneId));
            }
        }
    }
    
    private String getZoneIsFullMessage(final int zoneId) {
        return String.format("%s reached %d players. You cannot enter now", (zoneId == 70052) ? "Anakim Sanctum" : "Lilith Sanctum", 300);
    }
    
    public String onKill(final Npc npc, final Player player, final boolean isSummon) {
        switch (npc.getId()) {
            case 25286: {
                this.startQuestTimer("ANAKIM_DEATH", 1000L, (Npc)null, (Player)null);
                this.startQuestTimer("CLEAR_ZONE_ANAKIM", 600000L, (Npc)null, player);
                addSpawn(31088, -6664, 18501, -5495, 0, false, 600000L, false, npc.getInstanceId());
                break;
            }
            case 25283: {
                this.startQuestTimer("LILITH_DEATH", 1000L, (Npc)null, (Player)null);
                this.startQuestTimer("CLEAR_ZONE_LILITH", 600000L, (Npc)null, player);
                addSpawn(31088, 185062, -9612, -5493, 0, false, 600000L, false, npc.getInstanceId());
                break;
            }
        }
        Util.doIfNonNull((Object)npc.getInstanceWorld(), Instance::finishInstance);
        return super.onKill(npc, player, isSummon);
    }
    
    public static SevenSigns provider() {
        return new SevenSigns();
    }
}
