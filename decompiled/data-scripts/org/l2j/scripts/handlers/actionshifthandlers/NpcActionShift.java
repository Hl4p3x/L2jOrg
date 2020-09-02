// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionshifthandlers;

import org.l2j.gameserver.enums.InstanceType;
import java.util.Set;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionShiftHandler;

public class NpcActionShift implements IActionShiftHandler
{
    public boolean action(final Player player, final WorldObject target, final boolean interact) {
        if (player.isGM()) {
            player.setTarget(target);
            final Npc npc = (Npc)target;
            final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
            final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByNpcId(npc.getId());
            html.setFile(player, "data/html/admin/npcinfo.htm");
            html.replace("%objid%", String.valueOf(target.getObjectId()));
            html.replace("%class%", target.getClass().getSimpleName());
            html.replace("%race%", npc.getTemplate().getRace().toString());
            html.replace("%id%", String.valueOf(npc.getTemplate().getId()));
            html.replace("%lvl%", String.valueOf(npc.getTemplate().getLevel()));
            html.replace("%name%", npc.getTemplate().getName());
            html.replace("%tmplid%", String.valueOf(npc.getTemplate().getId()));
            html.replace("%aggro%", String.valueOf(GameUtils.isAttackable(target) ? ((Attackable)target).getAggroRange() : 0));
            html.replace("%hp%", String.valueOf((int)npc.getCurrentHp()));
            html.replace("%hpmax%", String.valueOf(npc.getMaxHp()));
            html.replace("%mp%", String.valueOf((int)npc.getCurrentMp()));
            html.replace("%mpmax%", String.valueOf(npc.getMaxMp()));
            html.replace("%patk%", String.valueOf(npc.getPAtk()));
            html.replace("%matk%", String.valueOf(npc.getMAtk()));
            html.replace("%pdef%", String.valueOf(npc.getPDef()));
            html.replace("%mdef%", String.valueOf(npc.getMDef()));
            html.replace("%accu%", String.valueOf(npc.getAccuracy()));
            html.replace("%evas%", String.valueOf(npc.getEvasionRate()));
            html.replace("%crit%", String.valueOf(npc.getCriticalHit()));
            html.replace("%rspd%", String.valueOf(npc.getRunSpeed()));
            html.replace("%aspd%", String.valueOf(npc.getPAtkSpd()));
            html.replace("%cspd%", String.valueOf(npc.getMAtkSpd()));
            html.replace("%atkType%", String.valueOf(npc.getTemplate().getBaseAttackType()));
            html.replace("%atkRng%", String.valueOf(npc.getTemplate().getBaseAttackRange()));
            html.replace("%str%", String.valueOf(npc.getSTR()));
            html.replace("%dex%", String.valueOf(npc.getDEX()));
            html.replace("%con%", String.valueOf(npc.getCON()));
            html.replace("%int%", String.valueOf(npc.getINT()));
            html.replace("%wit%", String.valueOf(npc.getWIT()));
            html.replace("%men%", String.valueOf(npc.getMEN()));
            html.replace("%loc%", invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, target.getX(), target.getY(), target.getZ()));
            html.replace("%heading%", String.valueOf(npc.getHeading()));
            html.replace("%collision_radius%", String.valueOf(npc.getTemplate().getfCollisionRadius()));
            html.replace("%collision_height%", String.valueOf(npc.getTemplate().getfCollisionHeight()));
            html.replace("%clanHall%", (clanHall != null) ? clanHall.getName() : "none");
            html.replace("%mpRewardValue%", npc.getTemplate().getMpRewardValue());
            html.replace("%mpRewardTicks%", npc.getTemplate().getMpRewardTicks());
            html.replace("%mpRewardType%", npc.getTemplate().getMpRewardType().name());
            html.replace("%mpRewardAffectType%", npc.getTemplate().getMpRewardAffectType().name());
            html.replace("%loc2d%", String.valueOf((int)MathUtil.calculateDistance2D((ILocational)player, (ILocational)npc)));
            html.replace("%loc3d%", String.valueOf((int)MathUtil.calculateDistance3D((ILocational)player, (ILocational)npc)));
            final AttributeType attackAttribute = npc.getAttackElement();
            html.replace("%ele_atk%", attackAttribute.name());
            html.replace("%ele_atk_value%", String.valueOf(npc.getAttackElementValue(attackAttribute)));
            html.replace("%ele_dfire%", String.valueOf(npc.getDefenseElementValue(AttributeType.FIRE)));
            html.replace("%ele_dwater%", String.valueOf(npc.getDefenseElementValue(AttributeType.WATER)));
            html.replace("%ele_dwind%", String.valueOf(npc.getDefenseElementValue(AttributeType.WIND)));
            html.replace("%ele_dearth%", String.valueOf(npc.getDefenseElementValue(AttributeType.EARTH)));
            html.replace("%ele_dholy%", String.valueOf(npc.getDefenseElementValue(AttributeType.HOLY)));
            html.replace("%ele_ddark%", String.valueOf(npc.getDefenseElementValue(AttributeType.DARK)));
            final Spawn spawn = npc.getSpawn();
            if (spawn != null) {
                final NpcSpawnTemplate template = spawn.getNpcSpawnTemplate();
                if (template != null) {
                    final String fileName = template.getSpawnTemplate().getFilePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/');
                    html.replace("%spawnfile%", fileName);
                    html.replace("%spawnname%", String.valueOf(template.getSpawnTemplate().getName()));
                    html.replace("%spawngroup%", String.valueOf(template.getGroup().getName()));
                    if (template.getSpawnTemplate().getAI() != null) {
                        final Quest script = QuestManager.getInstance().getQuest(template.getSpawnTemplate().getAI());
                        if (script != null) {
                            html.replace("%spawnai%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, script.getName(), script.getName()));
                        }
                    }
                    html.replace("%spawnai%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, template.getSpawnTemplate().getAI()));
                }
                html.replace("%spawn%", invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, npc.getSpawn().getX(), npc.getSpawn().getY(), npc.getSpawn().getZ()));
                if (npc.getSpawn().getRespawnMinDelay() == 0) {
                    html.replace("%resp%", "None");
                }
                else if (npc.getSpawn().hasRespawnRandom()) {
                    html.replace("%resp%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npc.getSpawn().getRespawnMinDelay() / 1000, npc.getSpawn().getRespawnMaxDelay() / 1000));
                }
                else {
                    html.replace("%resp%", invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getSpawn().getRespawnMinDelay() / 1000));
                }
            }
            else {
                html.replace("%spawn%", "<font color=FF0000>null</font>");
                html.replace("%resp%", "<font color=FF0000>--</font>");
            }
            html.replace("%spawnfile%", "<font color=FF0000>--</font>");
            html.replace("%spawnname%", "<font color=FF0000>--</font>");
            html.replace("%spawngroup%", "<font color=FF0000>--</font>");
            html.replace("%spawnai%", "<font color=FF0000>--</font>");
            if (npc.hasAI()) {
                final Set<String> clans = (Set<String>)NpcData.getInstance().getClansByIds(npc.getTemplate().getClans());
                final Set<Integer> ignoreClanNpcIds = (Set<Integer>)npc.getTemplate().getIgnoreClanNpcIds();
                final String clansString = clans.isEmpty() ? "" : CommonUtil.implode((Iterable)clans, ", ");
                final String ignoreClanNpcIdsString = (ignoreClanNpcIds != null) ? CommonUtil.implode((Iterable)ignoreClanNpcIds, ", ") : "";
                html.replace("%ai_intention%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getAI().getIntention().name()));
                html.replace("%ai%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, npc.getAI().getClass().getSimpleName()));
                html.replace("%ai_type%", invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/enums/AIType;)Ljava/lang/String;, npc.getAiType()));
                html.replace("%ai_clan%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, clansString, npc.getTemplate().getClanHelpRange()));
                html.replace("%ai_enemy_clan%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, ignoreClanNpcIdsString, npc.getTemplate().getAggroRange()));
            }
            else {
                html.replace("%ai_intention%", "");
                html.replace("%ai%", "");
                html.replace("%ai_type%", "");
                html.replace("%ai_clan%", "");
                html.replace("%ai_enemy_clan%", "");
            }
            final String routeName = WalkingManager.getInstance().getRouteName(npc);
            if (!routeName.isEmpty()) {
                html.replace("%route%", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, routeName));
            }
            else {
                html.replace("%route%", "");
            }
            player.sendPacket(new ServerPacket[] { (ServerPacket)html });
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2Npc;
    }
}
