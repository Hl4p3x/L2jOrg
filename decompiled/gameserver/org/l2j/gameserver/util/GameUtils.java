// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.actor.instance.Warehouse;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.FriendlyNpc;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.actor.instance.Artefact;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Monster;
import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Comparator;
import java.util.Collections;
import java.util.Map;
import org.l2j.gameserver.world.World;
import java.util.Arrays;
import org.l2j.gameserver.network.serverpackets.ShowBoard;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.Config;
import java.util.Locale;
import org.l2j.gameserver.enums.HtmlActionScope;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.tasks.player.IllegalPlayerActionTask;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.model.actor.instance.Player;
import java.text.NumberFormat;
import org.slf4j.Logger;

public final class GameUtils
{
    private static final Logger LOGGER;
    private static final NumberFormat ADENA_FORMATTER;
    
    public static void handleIllegalPlayerAction(final Player actor, final String message) {
        handleIllegalPlayerAction(actor, message, ((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).defaultPunishment());
    }
    
    public static void handleIllegalPlayerAction(final Player actor, final String message, final IllegalActionPunishmentType punishment) {
        ThreadPool.schedule((Runnable)new IllegalPlayerActionTask(actor, message, punishment), 5000L);
    }
    
    public static Location getRandomPosition(final ILocational loc, final int minRange, final int maxRange) {
        final int randomX = Rnd.get(minRange, maxRange);
        final int randomY = Rnd.get(minRange, maxRange);
        final double rndAngle = Math.toRadians(Rnd.get(360));
        final int newX = (int)(loc.getX() + randomX * Math.cos(rndAngle));
        final int newY = (int)(loc.getY() + randomY * Math.sin(rndAngle));
        return new Location(newX, newY, loc.getZ());
    }
    
    public static boolean checkIfInRange(final int range, final WorldObject obj1, final WorldObject obj2, final boolean includeZAxis) {
        if (Util.isAnyNull(new Object[] { obj1, obj2 }) || obj1.getInstanceWorld() != obj2.getInstanceWorld()) {
            return false;
        }
        if (range == -1) {
            return true;
        }
        int radius = 0;
        if (isCreature(obj1)) {
            radius += ((Creature)obj1).getTemplate().getCollisionRadius();
        }
        if (isCreature(obj2)) {
            radius += ((Creature)obj2).getTemplate().getCollisionRadius();
        }
        return includeZAxis ? MathUtil.isInsideRadius3D(obj1, obj2, range + radius) : MathUtil.isInsideRadius2D(obj1, obj2, range + radius);
    }
    
    public static boolean checkIfInShortRange(final int range, final WorldObject obj1, final WorldObject obj2, final boolean includeZAxis) {
        return !Util.isAnyNull(new Object[] { obj1, obj2 }) && (range == -1 || (includeZAxis ? MathUtil.isInsideRadius3D(obj1, obj2, range) : MathUtil.isInsideRadius2D(obj1, obj2, range)));
    }
    
    public static <T extends Enum<T>> boolean isEnum(final String name, final Class<T> enumType) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        try {
            return Enum.valueOf(enumType, name) != null;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static String formatAdena(final long amount) {
        synchronized (GameUtils.ADENA_FORMATTER) {
            return GameUtils.ADENA_FORMATTER.format(amount);
        }
    }
    
    public static String formatDate(final Date date, final String format) {
        if (date == null) {
            return null;
        }
        final DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    
    public static String getDateString(final Date date) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date.getTime());
    }
    
    private static void buildHtmlBypassCache(final Player player, final HtmlActionScope scope, final String html) {
        final String htmlLower = html.toLowerCase(Locale.ENGLISH);
        for (int bypassEnd = 0, bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd); bypassStart != -1; bypassStart = htmlLower.indexOf("=\"bypass ", bypassEnd)) {
            final int bypassStartEnd = bypassStart + 9;
            bypassEnd = htmlLower.indexOf("\"", bypassStartEnd);
            if (bypassEnd == -1) {
                break;
            }
            final int hParamPos = htmlLower.indexOf("-h ", bypassStartEnd);
            String bypass;
            if (hParamPos != -1 && hParamPos < bypassEnd) {
                bypass = html.substring(hParamPos + 3, bypassEnd).trim();
            }
            else {
                bypass = html.substring(bypassStartEnd, bypassEnd).trim();
            }
            final int firstParameterStart = bypass.indexOf(36);
            if (firstParameterStart != -1) {
                bypass = bypass.substring(0, firstParameterStart + 1);
            }
            if (Config.HTML_ACTION_CACHE_DEBUG) {
                GameUtils.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/enums/HtmlActionScope;Ljava/lang/String;)Ljava/lang/String;, scope, bypass));
            }
            player.addHtmlAction(scope, bypass);
        }
    }
    
    private static void buildHtmlLinkCache(final Player player, final HtmlActionScope scope, final String html) {
        final String htmlLower = html.toLowerCase(Locale.ENGLISH);
        int linkEnd = 0;
        int linkStart = htmlLower.indexOf("=\"link ", linkEnd);
        while (linkStart != -1) {
            final int linkStartEnd = linkStart + 7;
            linkEnd = htmlLower.indexOf("\"", linkStartEnd);
            if (linkEnd == -1) {
                break;
            }
            final String htmlLink = html.substring(linkStartEnd, linkEnd).trim();
            if (htmlLink.isEmpty()) {
                GameUtils.LOGGER.warn("Html link path is empty!");
            }
            else if (htmlLink.contains("..")) {
                GameUtils.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmlLink));
            }
            else {
                if (Config.HTML_ACTION_CACHE_DEBUG) {
                    GameUtils.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/enums/HtmlActionScope;Ljava/lang/String;)Ljava/lang/String;, scope, htmlLink));
                }
                player.addHtmlAction(scope, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmlLink));
                linkStart = htmlLower.indexOf("=\"link ", linkEnd);
            }
        }
    }
    
    public static void buildHtmlActionCache(final Player player, final HtmlActionScope scope, final int npcObjId, final String html) {
        if (player == null || scope == null || npcObjId < 0 || html == null) {
            throw new IllegalArgumentException();
        }
        if (Config.HTML_ACTION_CACHE_DEBUG) {
            GameUtils.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/enums/HtmlActionScope;I)Ljava/lang/String;, scope, npcObjId));
        }
        player.setHtmlActionOriginObjectId(scope, npcObjId);
        buildHtmlBypassCache(player, scope, html);
        buildHtmlLinkCache(player, scope, html);
    }
    
    public static void sendCBHtml(final Player activeChar, final String html) {
        sendCBHtml(activeChar, html, 0);
    }
    
    public static void sendCBHtml(final Player activeChar, final String html, final int npcObjId) {
        sendCBHtml(activeChar, html, null, npcObjId);
    }
    
    public static void sendCBHtml(final Player activeChar, final String html, final String fillMultiEdit) {
        sendCBHtml(activeChar, html, fillMultiEdit, 0);
    }
    
    public static void sendCBHtml(final Player player, final String html, final String fillMultiEdit, final int npcObjId) {
        if (Objects.isNull(player) || Objects.isNull(html)) {
            return;
        }
        player.clearHtmlActions(HtmlActionScope.COMM_BOARD_HTML);
        if (npcObjId > -1) {
            buildHtmlActionCache(player, HtmlActionScope.COMM_BOARD_HTML, npcObjId, html);
        }
        if (fillMultiEdit != null) {
            player.sendPacket(new ShowBoard(html, "1001"));
            fillMultiEditContent(player, fillMultiEdit);
        }
        else if (html.length() < 16250) {
            player.sendPacket(new ShowBoard(html, "101"));
            player.sendPacket(new ShowBoard(null, "102"));
            player.sendPacket(new ShowBoard(null, "103"));
        }
        else if (html.length() < 32500) {
            player.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            player.sendPacket(new ShowBoard(html.substring(16250), "102"));
            player.sendPacket(new ShowBoard(null, "103"));
        }
        else if (html.length() < 48750) {
            player.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            player.sendPacket(new ShowBoard(html.substring(16250, 32500), "102"));
            player.sendPacket(new ShowBoard(html.substring(32500), "103"));
        }
        else {
            player.sendPacket(new ShowBoard("<html><body><br><center>Error: HTML was too long!</center></body></html>", "101"));
            player.sendPacket(new ShowBoard(null, "102"));
            player.sendPacket(new ShowBoard(null, "103"));
        }
    }
    
    public static void fillMultiEditContent(final Player player, final String text) {
        player.sendPacket(new ShowBoard(Arrays.asList("0", "0", "0", "0", "0", "0", player.getName(), Integer.toString(player.getObjectId()), player.getAccountName(), "9", " ", " ", text.replaceAll("<br>", System.lineSeparator()), "0", "0", "0", "0")));
    }
    
    public static boolean isInsideRangeOfObjectId(final WorldObject obj, final int targetObjId, final int radius) {
        final WorldObject target = World.getInstance().findObject(targetObjId);
        return target != null && MathUtil.isInsideRadius3D(obj, target, radius);
    }
    
    public static int map(int input, final int inputMin, final int inputMax, final int outputMin, final int outputMax) {
        input = constrain(input, inputMin, inputMax);
        return (input - inputMin) * (outputMax - outputMin) / (inputMax - inputMin) + outputMin;
    }
    
    public static long map(long input, final long inputMin, final long inputMax, final long outputMin, final long outputMax) {
        input = constrain(input, inputMin, inputMax);
        return (input - inputMin) * (outputMax - outputMin) / (inputMax - inputMin) + outputMin;
    }
    
    public static double map(double input, final double inputMin, final double inputMax, final double outputMin, final double outputMax) {
        input = constrain(input, inputMin, inputMax);
        return (input - inputMin) * (outputMax - outputMin) / (inputMax - inputMin) + outputMin;
    }
    
    public static int constrain(final int input, final int min, final int max) {
        return (input < min) ? min : ((input > max) ? max : input);
    }
    
    public static long constrain(final long input, final long min, final long max) {
        return (input < min) ? min : ((input > max) ? max : input);
    }
    
    public static double constrain(final double input, final double min, final double max) {
        return (input < min) ? min : ((input > max) ? max : input);
    }
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map, final boolean descending) {
        if (descending) {
            return map.entrySet().stream().sorted((Comparator<? super Object>)Map.Entry.comparingByValue(Collections.reverseOrder())).collect((Collector<? super Object, ?, Map<K, V>>)Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, (Function<? super Object, ?>)Map.Entry::getValue, (e1, e2) -> e1, (Supplier<R>)LinkedHashMap::new));
        }
        return map.entrySet().stream().sorted((Comparator<? super Object>)Map.Entry.comparingByValue()).collect((Collector<? super Object, ?, Map<K, V>>)Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, (Function<? super Object, ?>)Map.Entry::getValue, (e1, e2) -> e1, (Supplier<R>)LinkedHashMap::new));
    }
    
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {
        return map.entrySet().stream().sorted((Comparator<? super Object>)Map.Entry.comparingByValue()).collect((Collector<? super Object, ?, Map<K, V>>)Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, (Function<? super Object, ?>)Map.Entry::getValue, (e1, e2) -> e1, (Supplier<R>)LinkedHashMap::new));
    }
    
    public static int hashIp(final Player player) {
        return Util.hashIp(player.getIPAddress());
    }
    
    public static boolean isPlayer(final WorldObject object) {
        return object instanceof Player;
    }
    
    public static double calcIfIsPlayer(final WorldObject object, final ToDoubleFunction<Player> function) {
        final Player player;
        return (object instanceof Player && (player = (Player)object) == object) ? function.applyAsDouble(player) : 0.0;
    }
    
    public static boolean isCreature(final WorldObject object) {
        return object instanceof Creature;
    }
    
    public static void doIfIsCreature(final WorldObject object, final Consumer<Creature> action) {
        final Creature creature;
        if (object instanceof Creature && (creature = (Creature)object) == object) {
            action.accept(creature);
        }
    }
    
    public static boolean isMonster(final WorldObject object) {
        return object instanceof Monster;
    }
    
    public static boolean isNpc(final WorldObject object) {
        return object instanceof Npc;
    }
    
    public static void doIfIsNpc(final WorldObject object, final Consumer<Npc> action) {
        final Npc npc;
        if (object instanceof Npc && (npc = (Npc)object) == object) {
            action.accept(npc);
        }
    }
    
    public static boolean isPlayable(final WorldObject object) {
        return object instanceof Playable;
    }
    
    public static boolean isArtifact(final WorldObject object) {
        return object instanceof Artefact;
    }
    
    public static boolean isGM(final WorldObject object) {
        final Player player;
        return object instanceof Player && (player = (Player)object) == object && player.isGM();
    }
    
    public static boolean isWalker(final WorldObject object) {
        if (isMonster(object)) {
            final Monster monster = (Monster)object;
            final Monster leader;
            return Objects.nonNull(leader = monster.getLeader()) ? isWalker(leader) : WalkingManager.getInstance().isRegistered(monster);
        }
        return isNpc(object) && WalkingManager.getInstance().isRegistered((Npc)object);
    }
    
    public static boolean isSummon(final WorldObject object) {
        return object instanceof Summon;
    }
    
    public static boolean isServitor(final WorldObject object) {
        return object instanceof Servitor;
    }
    
    public static boolean isAttackable(final WorldObject object) {
        return object instanceof Attackable && !(object instanceof FriendlyNpc);
    }
    
    public static boolean isPet(final WorldObject object) {
        return object instanceof Pet;
    }
    
    public static boolean isDoor(final WorldObject object) {
        return object instanceof Door;
    }
    
    public static boolean isTrap(final WorldObject object) {
        return object instanceof Trap;
    }
    
    public static boolean isItem(final WorldObject object) {
        return object instanceof Item;
    }
    
    public static boolean isWarehouseManager(final WorldObject object) {
        return object instanceof Warehouse;
    }
    
    public static boolean isWeapon(final Item item) {
        return Objects.nonNull(item) && isWeapon(item.getTemplate());
    }
    
    public static boolean isWeapon(final ItemTemplate item) {
        return item instanceof Weapon;
    }
    
    public static boolean isArmor(final ItemTemplate item) {
        return item instanceof Armor;
    }
    
    public static boolean canTeleport(final Player player) {
        return !Objects.isNull(player) && !player.isInDuel() && !player.isControlBlocked() && !player.isConfused() && !player.isFlying() && !player.isFlyingMounted() && !player.isInOlympiadMode() && !player.isAlikeDead() && !player.isOnCustomEvent() && player.getPvpFlag() <= 0 && !player.isInsideZone(ZoneType.JAIL) && !player.isInTimedHuntingZone();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GameUtils.class);
        ADENA_FORMATTER = NumberFormat.getIntegerInstance(Locale.ENGLISH);
    }
}
