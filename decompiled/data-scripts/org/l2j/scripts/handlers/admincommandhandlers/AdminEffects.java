// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.ExRedSky;
import org.l2j.gameserver.network.serverpackets.SunRise;
import org.l2j.gameserver.network.serverpackets.SunSet;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.model.html.PageResult;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;
import java.util.List;
import org.l2j.gameserver.model.holders.MovieHolder;
import org.l2j.gameserver.enums.Movie;
import java.util.Arrays;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.styles.ButtonsStyle;
import org.l2j.gameserver.model.html.PageBuilder;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.network.serverpackets.Earthquake;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ExUserInfoAbnormalVisualEffect;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminEffects implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();
        Label_2170: {
            if (command.equals("admin_invis_menu")) {
                if (!activeChar.isInvisible()) {
                    activeChar.setInvisible(true);
                    activeChar.broadcastUserInfo();
                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoAbnormalVisualEffect(activeChar) });
                    World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Creature.class, target -> {
                        if (target != null && target.getTarget() == activeChar) {
                            target.setTarget((WorldObject)null);
                            target.abortAttack();
                            target.abortCast();
                            target.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                        }
                        return;
                    });
                    BuilderUtil.sendSysMessage(activeChar, "Now, you cannot be seen.");
                }
                else {
                    activeChar.setInvisible(false);
                    activeChar.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.STEALTH });
                    activeChar.broadcastUserInfo();
                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoAbnormalVisualEffect(activeChar) });
                    BuilderUtil.sendSysMessage(activeChar, "Now, you can be seen.");
                }
                command = "";
                AdminHtml.showAdminHtml(activeChar, "gm_menu.htm");
            }
            else if (command.startsWith("admin_invis")) {
                activeChar.setInvisible(true);
                activeChar.broadcastUserInfo();
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoAbnormalVisualEffect(activeChar) });
                World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Creature.class, target -> {
                    if (target != null && target.getTarget() == activeChar) {
                        target.setTarget((WorldObject)null);
                        target.abortAttack();
                        target.abortCast();
                        target.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                    }
                    return;
                });
                BuilderUtil.sendSysMessage(activeChar, "Now, you cannot be seen.");
            }
            else if (command.startsWith("admin_vis")) {
                activeChar.setInvisible(false);
                activeChar.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.STEALTH });
                activeChar.broadcastUserInfo();
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoAbnormalVisualEffect(activeChar) });
                BuilderUtil.sendSysMessage(activeChar, "Now, you can be seen.");
            }
            else if (command.startsWith("admin_setinvis")) {
                if (!GameUtils.isCreature(activeChar.getTarget())) {
                    activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    return false;
                }
                final Creature target2 = (Creature)activeChar.getTarget();
                target2.setInvisible(!target2.isInvisible());
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, target2.getName(), target2.isInvisible() ? "invisible" : "visible"));
                if (GameUtils.isPlayer((WorldObject)target2)) {
                    ((Player)target2).broadcastUserInfo();
                }
            }
            else if (command.startsWith("admin_earthquake")) {
                try {
                    final String val1 = st.nextToken();
                    final int intensity = Integer.parseInt(val1);
                    final String val2 = st.nextToken();
                    final int duration = Integer.parseInt(val2);
                    final Earthquake eq = new Earthquake(activeChar.getX(), activeChar.getY(), activeChar.getZ(), intensity, duration);
                    activeChar.broadcastPacket((ServerPacket)eq);
                }
                catch (Exception e) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //earthquake <intensity> <duration>");
                }
            }
            else if (command.startsWith("admin_atmosphere")) {
                try {
                    final String type = st.nextToken();
                    final String state = st.nextToken();
                    final int duration2 = Integer.parseInt(st.nextToken());
                    this.adminAtmosphere(type, state, duration2, activeChar);
                }
                catch (Exception ex) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //atmosphere <signsky dawn|dusk>|<sky day|night|red> <duration>");
                }
            }
            else if (command.equals("admin_play_sounds")) {
                AdminHtml.showAdminHtml(activeChar, "songs/songs.htm");
            }
            else if (command.startsWith("admin_play_sounds")) {
                try {
                    AdminHtml.showAdminHtml(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command.substring(18)));
                }
                catch (StringIndexOutOfBoundsException e2) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //play_sounds <pagenumber>");
                }
            }
            else if (command.startsWith("admin_play_sound")) {
                try {
                    this.playAdminSound(activeChar, command.substring(17));
                }
                catch (StringIndexOutOfBoundsException e2) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //play_sound <soundname>");
                }
            }
            else if (command.equals("admin_para_all")) {
                World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Player.class, player -> {
                    if (!player.isGM()) {
                        player.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.PARALYZE });
                        player.setBlockActions(true);
                        player.startParalyze();
                        player.broadcastInfo();
                    }
                    return;
                });
            }
            else if (command.equals("admin_unpara_all")) {
                World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Player.class, player -> {
                    player.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.PARALYZE });
                    player.setBlockActions(false);
                    player.broadcastInfo();
                    return;
                });
            }
            else if (command.startsWith("admin_para")) {
                String type = "1";
                try {
                    type = st.nextToken();
                }
                catch (Exception ex2) {}
                try {
                    final WorldObject target3 = activeChar.getTarget();
                    if (GameUtils.isCreature(target3)) {
                        final Creature player2 = (Creature)target3;
                        if (type.equals("1")) {
                            player2.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.PARALYZE });
                        }
                        else {
                            player2.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.FLESH_STONE });
                        }
                        player2.setBlockActions(true);
                        player2.startParalyze();
                        player2.broadcastInfo();
                    }
                }
                catch (Exception ex3) {}
            }
            else if (command.startsWith("admin_unpara")) {
                String type = "1";
                try {
                    type = st.nextToken();
                }
                catch (Exception ex4) {}
                try {
                    final WorldObject target3 = activeChar.getTarget();
                    Creature player2 = null;
                    if (GameUtils.isCreature(target3)) {
                        player2 = (Creature)target3;
                        if (type.equals("1")) {
                            player2.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.PARALYZE });
                        }
                        else {
                            player2.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.FLESH_STONE });
                        }
                        player2.setBlockActions(false);
                        player2.broadcastInfo();
                    }
                }
                catch (Exception ex5) {}
            }
            else if (command.startsWith("admin_bighead")) {
                try {
                    final WorldObject target4 = activeChar.getTarget();
                    Creature player3 = null;
                    if (GameUtils.isCreature(target4)) {
                        player3 = (Creature)target4;
                        player3.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.BIG_HEAD });
                    }
                }
                catch (Exception ex6) {}
            }
            else if (command.startsWith("admin_shrinkhead")) {
                try {
                    final WorldObject target4 = activeChar.getTarget();
                    Creature player3 = null;
                    if (GameUtils.isCreature(target4)) {
                        player3 = (Creature)target4;
                        player3.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.BIG_HEAD });
                    }
                }
                catch (Exception ex7) {}
            }
            else if (command.equals("admin_clearteams")) {
                World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Player.class, player -> {
                    player.setTeam(Team.NONE);
                    player.broadcastUserInfo();
                    return;
                });
            }
            else if (command.startsWith("admin_setteam_close")) {
                try {
                    final String val3 = st.nextToken();
                    int radius = 400;
                    if (st.hasMoreTokens()) {
                        radius = Integer.parseInt(st.nextToken());
                    }
                    final Team team = Team.valueOf(val3.toUpperCase());
                    World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)Player.class, radius, player -> player.setTeam(team));
                }
                catch (Exception e) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //setteam_close <none|blue|red> [radius]");
                }
            }
            else if (command.startsWith("admin_setteam")) {
                try {
                    final Team team2 = Team.valueOf(st.nextToken().toUpperCase());
                    Creature target5 = null;
                    if (!GameUtils.isCreature(activeChar.getTarget())) {
                        return false;
                    }
                    target5 = (Creature)activeChar.getTarget();
                    target5.setTeam(team2);
                }
                catch (Exception e) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //setteam <none|blue|red>");
                }
            }
            else if (command.startsWith("admin_social")) {
                try {
                    String target6 = null;
                    WorldObject obj = activeChar.getTarget();
                    if (st.countTokens() == 2) {
                        final int social = Integer.parseInt(st.nextToken());
                        target6 = st.nextToken();
                        if (target6 != null) {
                            final Player player4 = World.getInstance().findPlayer(target6);
                            if (player4 != null) {
                                if (this.performSocial(social, (WorldObject)player4, activeChar)) {
                                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player4.getName()));
                                }
                            }
                            else {
                                try {
                                    final int radius2 = Integer.parseInt(target6);
                                    World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)WorldObject.class, radius2, object -> this.performSocial(social, object, activeChar));
                                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, radius2));
                                }
                                catch (NumberFormatException nbe) {
                                    BuilderUtil.sendSysMessage(activeChar, "Incorrect parameter");
                                }
                            }
                        }
                    }
                    else if (st.countTokens() == 1) {
                        final int social = Integer.parseInt(st.nextToken());
                        if (obj == null) {
                            obj = (WorldObject)activeChar;
                        }
                        if (this.performSocial(social, obj, activeChar)) {
                            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, obj.getName()));
                        }
                        else {
                            activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
                        }
                    }
                    else if (!command.contains("menu")) {
                        BuilderUtil.sendSysMessage(activeChar, "Usage: //social <social_id> [player_name|radius]");
                    }
                }
                catch (Exception ex8) {}
            }
            else if (command.startsWith("admin_ave_abnormal")) {
                String param1 = null;
                if (st.countTokens() > 0) {
                    param1 = st.nextToken();
                }
                if (param1 == null || Util.isDigit(param1)) {
                    int page = 0;
                    if (param1 != null) {
                        try {
                            page = Integer.parseInt(param1);
                        }
                        catch (NumberFormatException nfe) {
                            BuilderUtil.sendSysMessage(activeChar, "Incorrect page.");
                        }
                    }
                    final PageResult result = PageBuilder.newBuilder((Object[])AbnormalVisualEffect.values(), 50, "bypass -h admin_ave_abnormal").currentPage(page).style((IHtmlStyle)ButtonsStyle.INSTANCE).bodyHandler((pages, ave, sb) -> sb.append(String.format("<button action=\"bypass admin_ave_abnormal %s\" align=left icon=teleport>%s(%d)</button>", ave.name(), ave.name(), ave.getClientId()))).build();
                    final NpcHtmlMessage html = new NpcHtmlMessage(0, 1);
                    html.setFile(activeChar, "data/html/admin/ave_abnormal.htm");
                    if (result.getPages() > 0) {
                        html.replace("%pages%", invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, result.getPagerTemplate()));
                    }
                    else {
                        html.replace("%pages%", "");
                    }
                    html.replace("%abnormals%", result.getBodyTemplate().toString());
                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)html });
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, command.replace("admin_", "")));
                    return true;
                }
                AbnormalVisualEffect ave;
                try {
                    ave = AbnormalVisualEffect.valueOf(param1);
                }
                catch (Exception e3) {
                    return false;
                }
                int radius3 = 0;
                String param2 = null;
                if (st.countTokens() == 1) {
                    param2 = st.nextToken();
                    if (Util.isDigit(param2)) {
                        radius3 = Integer.parseInt(param2);
                    }
                }
                if (radius3 > 0) {
                    World.getInstance().forEachVisibleObjectInRange((WorldObject)activeChar, (Class)WorldObject.class, radius3, object -> this.performAbnormalVisualEffect(ave, object));
                    BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, param2, param1));
                }
                else {
                    final WorldObject obj2 = (WorldObject)((activeChar.getTarget() != null) ? activeChar.getTarget() : activeChar);
                    if (this.performAbnormalVisualEffect(ave, obj2)) {
                        activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, obj2.getName(), param1));
                    }
                    else {
                        activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
                    }
                }
            }
            else {
                if (!command.startsWith("admin_effect")) {
                    if (!command.startsWith("admin_npc_use_skill")) {
                        if (command.startsWith("admin_set_displayeffect")) {
                            final WorldObject target4 = activeChar.getTarget();
                            if (!(target4 instanceof Npc)) {
                                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                                return false;
                            }
                            final Npc npc = (Npc)target4;
                            try {
                                final String type2 = st.nextToken();
                                final int diplayeffect = Integer.parseInt(type2);
                                npc.setDisplayEffect(diplayeffect);
                            }
                            catch (Exception e3) {
                                BuilderUtil.sendSysMessage(activeChar, "Usage: //set_displayeffect <id>");
                            }
                            break Label_2170;
                        }
                        else {
                            if (command.startsWith("admin_playmovie")) {
                                try {
                                    final MovieHolder movieHolder = new MovieHolder((List)Arrays.asList(activeChar), Movie.findByClientId(Integer.parseInt(st.nextToken())));
                                }
                                catch (Exception e) {
                                    BuilderUtil.sendSysMessage(activeChar, "Usage: //playmovie <id>");
                                }
                                break Label_2170;
                            }
                            if (command.startsWith("admin_event_trigger")) {
                                try {
                                    final int triggerId = Integer.parseInt(st.nextToken());
                                    final boolean enable = Boolean.parseBoolean(st.nextToken());
                                    World.getInstance().forEachVisibleObject((WorldObject)activeChar, (Class)Player.class, player -> player.sendPacket(new ServerPacket[] { (ServerPacket)new OnEventTrigger(triggerId, enable) }));
                                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)new OnEventTrigger(triggerId, enable) });
                                }
                                catch (Exception e) {
                                    BuilderUtil.sendSysMessage(activeChar, "Usage: //event_trigger id [true | false]");
                                }
                                break Label_2170;
                            }
                            if (command.startsWith("admin_settargetable")) {
                                activeChar.setTargetable(!activeChar.isTargetable());
                            }
                            break Label_2170;
                        }
                    }
                }
                try {
                    WorldObject obj3 = activeChar.getTarget();
                    int level = 1;
                    int hittime = 1;
                    final int skill = Integer.parseInt(st.nextToken());
                    if (st.hasMoreTokens()) {
                        level = Integer.parseInt(st.nextToken());
                    }
                    if (st.hasMoreTokens()) {
                        hittime = Integer.parseInt(st.nextToken());
                    }
                    if (obj3 == null) {
                        obj3 = (WorldObject)activeChar;
                    }
                    if (!GameUtils.isCreature(obj3)) {
                        activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                    }
                    else {
                        final Creature target7 = (Creature)obj3;
                        target7.broadcastPacket((ServerPacket)new MagicSkillUse(target7, (WorldObject)activeChar, skill, level, hittime, 0));
                        activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, obj3.getName(), skill, level));
                    }
                }
                catch (Exception e) {
                    BuilderUtil.sendSysMessage(activeChar, "Usage: //effect skill [level | level hittime]");
                }
            }
        }
        if (command.contains("menu")) {
            this.showMainPage(activeChar, command);
        }
        return true;
    }
    
    private boolean performAbnormalVisualEffect(final AbnormalVisualEffect ave, final WorldObject target) {
        if (GameUtils.isCreature(target)) {
            final Creature character = (Creature)target;
            if (!character.getEffectList().hasAbnormalVisualEffect(ave)) {
                character.getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { ave });
            }
            else {
                character.getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { ave });
            }
            return true;
        }
        return false;
    }
    
    private boolean performSocial(final int action, final WorldObject target, final Player activeChar) {
        try {
            if (!GameUtils.isCreature(target)) {
                return false;
            }
            if (target instanceof Chest) {
                activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
                return false;
            }
            if (GameUtils.isNpc(target) && (action < 1 || action > 20)) {
                activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
                return false;
            }
            if (GameUtils.isPlayer(target) && (action < 2 || (action > 18 && action != 2122))) {
                activeChar.sendPacket(SystemMessageId.NOTHING_HAPPENED);
                return false;
            }
            final Creature character = (Creature)target;
            character.broadcastPacket((ServerPacket)new SocialAction(character.getObjectId(), action));
        }
        catch (Exception ex) {}
        return true;
    }
    
    private void adminAtmosphere(final String type, final String state, final int duration, final Player activeChar) {
        ServerPacket packet = null;
        if (type.equals("sky")) {
            if (state.equals("night")) {
                packet = (ServerPacket)SunSet.STATIC_PACKET;
            }
            else if (state.equals("day")) {
                packet = (ServerPacket)SunRise.STATIC_PACKET;
            }
            else if (state.equals("red")) {
                if (duration != 0) {
                    packet = (ServerPacket)new ExRedSky(duration);
                }
                else {
                    packet = (ServerPacket)new ExRedSky(10);
                }
            }
        }
        else {
            BuilderUtil.sendSysMessage(activeChar, "Usage: //atmosphere <signsky dawn|dusk>|<sky day|night|red> <duration>");
        }
        if (packet != null) {
            Broadcast.toAllOnlinePlayers(new ServerPacket[] { packet });
        }
    }
    
    private void playAdminSound(final Player activeChar, final String sound) {
        final PlaySound _snd = new PlaySound(1, sound, 0, 0, 0, 0, 0);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)_snd });
        activeChar.broadcastPacket((ServerPacket)_snd);
        BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, sound));
    }
    
    public String[] getAdminCommandList() {
        return AdminEffects.ADMIN_COMMANDS;
    }
    
    private void showMainPage(final Player activeChar, final String command) {
        String filename = "effects_menu";
        if (command.contains("social")) {
            filename = "social";
        }
        AdminHtml.showAdminHtml(activeChar, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, filename));
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_invis", "admin_invisible", "admin_setinvis", "admin_vis", "admin_visible", "admin_invis_menu", "admin_earthquake", "admin_earthquake_menu", "admin_bighead", "admin_shrinkhead", "admin_unpara_all", "admin_para_all", "admin_unpara", "admin_para", "admin_unpara_all_menu", "admin_para_all_menu", "admin_unpara_menu", "admin_para_menu", "admin_clearteams", "admin_setteam_close", "admin_setteam", "admin_social", "admin_effect", "admin_npc_use_skill", "admin_effect_menu", "admin_ave_abnormal", "admin_social_menu", "admin_play_sounds", "admin_play_sound", "admin_atmosphere", "admin_atmosphere_menu", "admin_set_displayeffect", "admin_set_displayeffect_menu", "admin_event_trigger", "admin_settargetable", "admin_playmovie" };
    }
}
