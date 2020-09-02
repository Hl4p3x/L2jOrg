// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import java.util.ArrayList;
import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.html.PageResult;
import java.util.Collection;
import org.l2j.gameserver.model.html.PageBuilder;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.Objects;
import org.l2j.gameserver.network.serverpackets.ExShowTerritory;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.annotations.Priority;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMoveRequest;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.File;
import java.util.StringJoiner;
import org.l2j.gameserver.model.Location;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.geo.GeoEngine;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.world.zone.form.ZonePolygonArea;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.util.BuilderUtil;
import java.awt.Color;
import org.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.scripts.ai.AbstractNpcAI;

public class AdminZones extends AbstractNpcAI implements IAdminCommandHandler
{
    private static final Logger LOGGER;
    private final Map<Integer, ZoneNodeHolder> _zones;
    private static final String[] COMMANDS;
    
    public AdminZones() {
        this._zones = new ConcurrentHashMap<Integer, ZoneNodeHolder>();
    }
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command);
        st.nextToken();
        if (!st.hasMoreTokens()) {
            this.buildZonesEditorWindow(activeChar);
            return false;
        }
        final String nextToken;
        final String subCmd = nextToken = st.nextToken();
        switch (nextToken) {
            case "load": {
                if (st.hasMoreTokens()) {
                    final StringBuilder name = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        name.append(st.nextToken()).append(" ");
                    }
                    this.loadZone(activeChar, name.toString().trim());
                    break;
                }
                break;
            }
            case "create": {
                this.buildHtmlWindow(activeChar, 0);
                break;
            }
            case "setname": {
                final StringBuilder name = new StringBuilder();
                while (st.hasMoreTokens()) {
                    name.append(st.nextToken()).append(" ");
                }
                this.setName(activeChar, name.toString().trim());
                break;
            }
            case "start": {
                this.enablePicking(activeChar);
                break;
            }
            case "finish": {
                this.disablePicking(activeChar);
                break;
            }
            case "setMinZ": {
                if (st.hasMoreTokens()) {
                    final int minZ = Integer.parseInt(st.nextToken());
                    this.setMinZ(activeChar, minZ);
                    break;
                }
                break;
            }
            case "setMaxZ": {
                if (st.hasMoreTokens()) {
                    final int maxZ = Integer.parseInt(st.nextToken());
                    this.setMaxZ(activeChar, maxZ);
                    break;
                }
                break;
            }
            case "show": {
                this.showPoints(activeChar);
                final ConfirmDlg dlg = new ConfirmDlg("When enable show territory you must restart client to remove it, are you sure about that?");
                dlg.addTime(15000);
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)dlg });
                activeChar.addAction(PlayerAction.ADMIN_SHOW_TERRITORY);
                break;
            }
            case "hide": {
                final ZoneNodeHolder holder = this._zones.get(activeChar.getObjectId());
                if (holder != null) {
                    final ExServerPrimitive exsp = new ExServerPrimitive(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, activeChar.getObjectId()), activeChar.getX(), activeChar.getY(), activeChar.getZ());
                    exsp.addPoint(Color.BLACK, 0, 0, 0);
                    activeChar.sendPacket(new ServerPacket[] { (ServerPacket)exsp });
                }
                break;
            }
            case "change": {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Missing node index!");
                    break;
                }
                final String indexToken = st.nextToken();
                if (!Util.isInteger(indexToken)) {
                    BuilderUtil.sendSysMessage(activeChar, "Node index should be int!");
                    break;
                }
                final int index = Integer.parseInt(indexToken);
                this.changePoint(activeChar, index);
                break;
            }
            case "delete": {
                if (!st.hasMoreTokens()) {
                    BuilderUtil.sendSysMessage(activeChar, "Missing node index!");
                    break;
                }
                final String indexToken = st.nextToken();
                if (!Util.isDigit(indexToken)) {
                    BuilderUtil.sendSysMessage(activeChar, "Node index should be int!");
                    break;
                }
                final int index = Integer.parseInt(indexToken);
                this.deletePoint(activeChar, index);
                this.showPoints(activeChar);
                break;
            }
            case "clear": {
                this._zones.remove(activeChar.getObjectId());
                break;
            }
            case "dump": {
                this.dumpPoints(activeChar);
                break;
            }
            case "list": {
                final int page = Util.parseNextInt(st, 0);
                this.buildHtmlWindow(activeChar, page);
                return false;
            }
        }
        this.buildHtmlWindow(activeChar, 0);
        return false;
    }
    
    private void setMinZ(final Player activeChar, final int minZ) {
        this._zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar)).setMinZ(minZ);
    }
    
    private void setMaxZ(final Player activeChar, final int maxZ) {
        this._zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar)).setMaxZ(maxZ);
    }
    
    private void buildZonesEditorWindow(final Player activeChar) {
        final StringBuilder sb = new StringBuilder();
        final List<Zone> zones = (List<Zone>)ZoneManager.getInstance().getZones((ILocational)activeChar);
        for (final Zone zone : zones) {
            if (zone.getArea() instanceof ZonePolygonArea) {
                sb.append("<tr>");
                sb.append("<td fixwidth=200><a action=\"bypass -h admin_zones load ").append(zone.getName()).append("\">").append(zone.getName()).append("</a></td>");
                sb.append("</tr>");
            }
        }
        final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
        msg.setFile(activeChar, "data/html/admin/zone_editor.htm");
        msg.replace("%zones%", sb.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
    }
    
    private void loadZone(final Player activeChar, final String zoneName) {
        BuilderUtil.sendSystemMessage(activeChar, "Searching for zone: %s", new Object[] { zoneName });
        final Zone zone = ZoneManager.getInstance().getZoneByName(zoneName);
        if (zone != null && zone.getArea() instanceof ZonePolygonArea) {
            final ZonePolygonArea zoneArea = (ZonePolygonArea)zone.getArea();
            final ZoneNodeHolder holder = this._zones.computeIfAbsent(activeChar.getObjectId(), val -> new ZoneNodeHolder(activeChar));
            holder.getNodes().clear();
            holder.setName(zone.getName());
            holder.setMinZ(zoneArea.getLowZ());
            holder.setMaxZ(zoneArea.getHighZ());
            for (int i = 0; i < zoneArea.getX().length; ++i) {
                final int x = zoneArea.getX()[i];
                final int y = zoneArea.getY()[i];
                holder.addNode(new Location(x, y, (int)GeoEngine.getInstance().getHeight(x, y, Rnd.get(zoneArea.getLowZ(), zoneArea.getHighZ()))));
            }
            this.showPoints(activeChar);
        }
    }
    
    private void setName(final Player player, final String name) {
        if (name.contains("<") || name.contains(">") || name.contains("&") || name.contains("\\") || name.contains("\"") || name.contains("$")) {
            BuilderUtil.sendSysMessage(player, "You cannot use symbols like: < > & \" $ \\");
            return;
        }
        this._zones.computeIfAbsent(player.getObjectId(), key -> new ZoneNodeHolder(player)).setName(name);
    }
    
    private void enablePicking(final Player player) {
        if (!player.hasAction(PlayerAction.ADMIN_POINT_PICKING)) {
            player.addAction(PlayerAction.ADMIN_POINT_PICKING);
            BuilderUtil.sendSysMessage(player, "Point picking mode activated!");
        }
        else {
            BuilderUtil.sendSysMessage(player, "Point picking mode is already activated!");
        }
    }
    
    private void disablePicking(final Player player) {
        if (player.removeAction(PlayerAction.ADMIN_POINT_PICKING)) {
            BuilderUtil.sendSysMessage(player, "Point picking mode deactivated!");
        }
        else {
            BuilderUtil.sendSysMessage(player, "Point picking mode was not activated!");
        }
    }
    
    private void showPoints(final Player player) {
        final ZoneNodeHolder holder = this._zones.get(player.getObjectId());
        if (holder != null) {
            if (holder.getNodes().size() < 3) {
                BuilderUtil.sendSysMessage(player, "In order to visualize this zone you must have at least 3 points.");
                return;
            }
            final ExServerPrimitive exsp = new ExServerPrimitive(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, player.getObjectId()), (ILocational)player);
            final List<Location> list = holder.getNodes();
            for (int i = 1; i < list.size(); ++i) {
                final Location prevLoc = list.get(i - 1);
                final Location nextLoc = list.get(i);
                if (holder.getMinZ() != 0) {
                    exsp.addLine(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, i, i + 1), Color.CYAN, true, prevLoc.getX(), prevLoc.getY(), holder.getMinZ(), nextLoc.getX(), nextLoc.getY(), holder.getMinZ());
                }
                exsp.addLine(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, i, i + 1), Color.WHITE, true, (ILocational)prevLoc, (ILocational)nextLoc);
                if (holder.getMaxZ() != 0) {
                    exsp.addLine(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, i, i + 1), Color.RED, true, prevLoc.getX(), prevLoc.getY(), holder.getMaxZ(), nextLoc.getX(), nextLoc.getY(), holder.getMaxZ());
                }
            }
            final Location prevLoc2 = list.get(list.size() - 1);
            final Location nextLoc2 = list.get(0);
            if (holder.getMinZ() != 0) {
                exsp.addLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, list.size()), Color.CYAN, true, prevLoc2.getX(), prevLoc2.getY(), holder.getMinZ(), nextLoc2.getX(), nextLoc2.getY(), holder.getMinZ());
            }
            exsp.addLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, list.size()), Color.WHITE, true, (ILocational)prevLoc2, (ILocational)nextLoc2);
            if (holder.getMaxZ() != 0) {
                exsp.addLine(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, list.size()), Color.RED, true, prevLoc2.getX(), prevLoc2.getY(), holder.getMaxZ(), nextLoc2.getX(), nextLoc2.getY(), holder.getMaxZ());
            }
            player.sendPacket(new ServerPacket[] { (ServerPacket)exsp });
        }
    }
    
    private void changePoint(final Player player, final int index) {
        final ZoneNodeHolder holder = this._zones.get(player.getObjectId());
        if (holder != null) {
            final Location loc = holder.getNodes().get(index);
            if (loc != null) {
                this.enablePicking(player);
                holder.setChangingLoc(loc);
            }
        }
    }
    
    private void deletePoint(final Player player, final int index) {
        final ZoneNodeHolder holder = this._zones.get(player.getObjectId());
        if (holder != null) {
            final Location loc = holder.getNodes().get(index);
            if (loc != null) {
                holder.getNodes().remove(loc);
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, index));
                if (holder.getNodes().isEmpty()) {
                    BuilderUtil.sendSysMessage(player, "Since node list is empty destroying session!");
                    this._zones.remove(player.getObjectId());
                }
            }
        }
    }
    
    private void dumpPoints(final Player player) {
        final ZoneNodeHolder holder = this._zones.get(player.getObjectId());
        if (holder != null && !holder.getNodes().isEmpty()) {
            if (holder.getName().isEmpty()) {
                BuilderUtil.sendSysMessage(player, "Set name first!");
                return;
            }
            final Location firstNode = holder.getNodes().get(0);
            final StringJoiner sj = new StringJoiner(System.lineSeparator());
            sj.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sj.add("<list xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://l2j.org\" xsi:schemaLocation=\"http://l2j.org zones.xsd\">");
            sj.add(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, holder.getName(), (holder.getMinZ() != 0) ? holder.getMinZ() : (firstNode.getZ() - 100), (holder.getMaxZ() != 0) ? holder.getMaxZ() : (firstNode.getZ() + 100)));
            for (final Location loc : holder.getNodes()) {
                sj.add(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, loc.getX(), loc.getY()));
            }
            sj.add("\t</zone>");
            sj.add("</list>");
            sj.add("");
            try {
                File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getAccountName(), holder.getName()));
                if (file.exists()) {
                    for (int i = 0; (file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/String;, player.getAccountName(), holder.getName(), i))).exists(); ++i) {}
                }
                if (!file.getParentFile().isDirectory()) {
                    file.getParentFile().mkdirs();
                }
                Files.writeString(file.toPath(), sj.toString(), new OpenOption[0]);
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, file.getAbsolutePath().replace(new File(".").getCanonicalFile().getAbsolutePath(), "")));
            }
            catch (Exception e) {
                BuilderUtil.sendSysMessage(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
                AdminZones.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), e.getMessage()), (Throwable)e);
            }
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_MOVE_REQUEST)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    @Priority(Integer.MAX_VALUE)
    public TerminateReturn onPlayerPointPicking(final OnPlayerMoveRequest event) {
        final Player activeChar = event.getActiveChar();
        if (activeChar.hasAction(PlayerAction.ADMIN_POINT_PICKING)) {
            final Location newLocation = event.getLocation();
            final ZoneNodeHolder holder = this._zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar));
            final Location changeLog = holder.getChangingLoc();
            if (changeLog != null) {
                changeLog.setXYZ((ILocational)newLocation);
                holder.setChangingLoc(null);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, holder.indexOf(changeLog) + 1));
                this.disablePicking(activeChar);
            }
            else {
                holder.addNode(newLocation);
                BuilderUtil.sendSysMessage(activeChar, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, holder.indexOf(changeLog) + 1));
            }
            if (holder.getNodes().size() >= 3) {
                this.showPoints(activeChar);
            }
            this.buildHtmlWindow(activeChar, 0);
            return new TerminateReturn(true, true, false);
        }
        return null;
    }
    
    @RegisterEvent(EventType.ON_PLAYER_DLG_ANSWER)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerDlgAnswer(final OnPlayerDlgAnswer event) {
        final Player activeChar = event.getActiveChar();
        if (activeChar.removeAction(PlayerAction.ADMIN_SHOW_TERRITORY) && event.getAnswer() == 1) {
            final ZoneNodeHolder holder = this._zones.get(activeChar.getObjectId());
            if (holder != null) {
                final List<Location> list = holder.getNodes();
                if (list.size() < 3) {
                    BuilderUtil.sendSysMessage(activeChar, "You must have at least 3 nodes to use this option!");
                    return;
                }
                final Location firstLoc = list.get(0);
                final int minZ = (holder.getMinZ() != 0) ? holder.getMinZ() : (firstLoc.getZ() - 100);
                final int maxZ = (holder.getMaxZ() != 0) ? holder.getMaxZ() : (firstLoc.getZ() + 100);
                final ExShowTerritory exst = new ExShowTerritory(minZ, maxZ);
                final List<Location> list2 = list;
                final ExShowTerritory obj = exst;
                Objects.requireNonNull(obj);
                list2.forEach((Consumer<? super Object>)obj::addVertice);
                activeChar.sendPacket(new ServerPacket[] { (ServerPacket)exst });
                BuilderUtil.sendSysMessage(activeChar, "In order to remove the debug you must restart your game client!");
            }
        }
    }
    
    public String[] getAdminCommandList() {
        return AdminZones.COMMANDS;
    }
    
    private void buildHtmlWindow(final Player activeChar, final int page) {
        final NpcHtmlMessage msg = new NpcHtmlMessage(0, 1);
        msg.setFile(activeChar, "data/html/admin/zone_editor_create.htm");
        final ZoneNodeHolder holder = this._zones.computeIfAbsent(activeChar.getObjectId(), key -> new ZoneNodeHolder(activeChar));
        final AtomicInteger position = new AtomicInteger(page * 20);
        final PageResult result = PageBuilder.newBuilder((Collection)holder.getNodes(), 20, "bypass -h admin_zones list").currentPage(page).bodyHandler((pages, loc, sb) -> {
            sb.append("<tr>");
            sb.append("<td fixwidth=5></td>");
            sb.append("<td fixwidth=20>").append(position.getAndIncrement()).append("</td>");
            sb.append("<td fixwidth=60>").append(loc.getX()).append("</td>");
            sb.append("<td fixwidth=60>").append(loc.getY()).append("</td>");
            sb.append("<td fixwidth=60>").append(loc.getZ()).append("</td>");
            sb.append("<td fixwidth=30><a action=\"bypass -h admin_zones change ").append(holder.indexOf(loc)).append("\">[E]</a></td>");
            sb.append("<td fixwidth=30><a action=\"bypass -h admin_move_to ").append(loc.getX()).append(" ").append(loc.getY()).append(" ").append(loc.getZ()).append("\">[T]</a></td>");
            sb.append("<td fixwidth=30><a action=\"bypass -h admin_zones delete ").append(holder.indexOf(loc)).append("\">[D]</a></td>");
            sb.append("<td fixwidth=5></td>");
            sb.append("</tr>");
        }).build();
        msg.replace("%name%", holder.getName());
        msg.replace("%minZ%", holder.getMinZ());
        msg.replace("%maxZ%", holder.getMaxZ());
        msg.replace("%pages%", (CharSequence)result.getPagerTemplate());
        msg.replace("%nodes%", (CharSequence)result.getBodyTemplate());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)msg });
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AdminZones.class);
        COMMANDS = new String[] { "admin_zones" };
    }
    
    protected static class ZoneNodeHolder
    {
        private String _name;
        private Location _changingLoc;
        private int _minZ;
        private int _maxZ;
        private final List<Location> _nodes;
        
        ZoneNodeHolder(final Player player) {
            this._name = "";
            this._changingLoc = null;
            this._nodes = new ArrayList<Location>();
            this._minZ = player.getZ() - 200;
            this._maxZ = player.getZ() + 200;
        }
        
        public void setName(final String name) {
            this._name = name;
        }
        
        public String getName() {
            return this._name;
        }
        
        void setChangingLoc(final Location loc) {
            this._changingLoc = loc;
        }
        
        Location getChangingLoc() {
            return this._changingLoc;
        }
        
        void addNode(final Location loc) {
            this._nodes.add(loc);
        }
        
        public List<Location> getNodes() {
            return this._nodes;
        }
        
        int indexOf(final Location loc) {
            return this._nodes.indexOf(loc);
        }
        
        public int getMinZ() {
            return this._minZ;
        }
        
        public int getMaxZ() {
            return this._maxZ;
        }
        
        public void setMinZ(final int minZ) {
            this._minZ = minZ;
        }
        
        public void setMaxZ(final int maxZ) {
            this._maxZ = maxZ;
        }
    }
}
