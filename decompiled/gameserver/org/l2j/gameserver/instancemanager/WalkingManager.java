// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.instancemanager;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.tasks.npc.walker.ArrivedTask;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.instancemanager.tasks.StartMovingTask;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.w3c.dom.NamedNodeMap;
import java.util.List;
import org.w3c.dom.Node;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.NpcWalkerNode;
import java.util.ArrayList;
import java.io.File;
import org.w3c.dom.Document;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.NpcRoutesHolder;
import org.l2j.gameserver.model.WalkInfo;
import org.l2j.gameserver.model.WalkRoute;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.util.GameXmlReader;

public final class WalkingManager extends GameXmlReader
{
    public static final byte NO_REPEAT = -1;
    public static final byte REPEAT_GO_BACK = 0;
    public static final byte REPEAT_GO_FIRST = 1;
    public static final byte REPEAT_TELE_FIRST = 2;
    public static final byte REPEAT_RANDOM = 3;
    private static final Logger LOGGER;
    private final Map<String, WalkRoute> _routes;
    private final Map<Integer, WalkInfo> _activeRoutes;
    private final Map<Integer, NpcRoutesHolder> _routesToAttach;
    private final Map<Npc, ScheduledFuture<?>> _startMoveTasks;
    private final Map<Npc, ScheduledFuture<?>> _repeatMoveTasks;
    private final Map<Npc, ScheduledFuture<?>> _arriveTasks;
    
    private WalkingManager() {
        this._routes = new HashMap<String, WalkRoute>();
        this._activeRoutes = new HashMap<Integer, WalkInfo>();
        this._routesToAttach = new HashMap<Integer, NpcRoutesHolder>();
        this._startMoveTasks = new ConcurrentHashMap<Npc, ScheduledFuture<?>>();
        this._repeatMoveTasks = new ConcurrentHashMap<Npc, ScheduledFuture<?>>();
        this._arriveTasks = new ConcurrentHashMap<Npc, ScheduledFuture<?>>();
        this.load();
    }
    
    protected Path getSchemaFilePath() {
        return ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("data/xsd/Routes.xsd");
    }
    
    public final void load() {
        this.parseDatapackFile("data/Routes.xml");
        WalkingManager.LOGGER.info("Loaded {} walking routes.", (Object)this._routes.size());
        this.releaseResources();
    }
    
    public void parseDocument(final Document doc, final File f) {
        final Node n = doc.getFirstChild();
        for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            if (d.getNodeName().equals("route")) {
                final String routeName = this.parseString(d.getAttributes(), "name");
                final boolean repeat = this.parseBoolean(d.getAttributes(), "repeat");
                final String lowerCase;
                final String repeatStyle = lowerCase = d.getAttributes().getNamedItem("repeatStyle").getNodeValue().toLowerCase();
                byte repeatType = 0;
                switch (lowerCase) {
                    case "back": {
                        repeatType = 0;
                        break;
                    }
                    case "cycle": {
                        repeatType = 1;
                        break;
                    }
                    case "conveyor": {
                        repeatType = 2;
                        break;
                    }
                    case "random": {
                        repeatType = 3;
                        break;
                    }
                    default: {
                        repeatType = -1;
                        break;
                    }
                }
                final List<NpcWalkerNode> list = new ArrayList<NpcWalkerNode>();
                for (Node r = d.getFirstChild(); r != null; r = r.getNextSibling()) {
                    if (r.getNodeName().equals("point")) {
                        final NamedNodeMap attrs = r.getAttributes();
                        final int x = this.parseInteger(attrs, "X");
                        final int y = this.parseInteger(attrs, "Y");
                        final int z = this.parseInteger(attrs, "Z");
                        final int delay = this.parseInteger(attrs, "delay");
                        final boolean run = this.parseBoolean(attrs, "run");
                        NpcStringId npcString = null;
                        String chatString = null;
                        Node node = attrs.getNamedItem("string");
                        if (node != null) {
                            chatString = node.getNodeValue();
                        }
                        else {
                            node = attrs.getNamedItem("npcString");
                            if (node != null) {
                                npcString = NpcStringId.getNpcStringId(node.getNodeValue());
                                if (npcString == null) {
                                    WalkingManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, node.getNodeValue(), routeName));
                                    continue;
                                }
                            }
                            else {
                                node = attrs.getNamedItem("npcStringId");
                                if (node != null) {
                                    npcString = NpcStringId.getNpcStringId(Integer.parseInt(node.getNodeValue()));
                                    if (npcString == null) {
                                        WalkingManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, node.getNodeValue(), routeName));
                                        continue;
                                    }
                                }
                            }
                        }
                        list.add(new NpcWalkerNode(x, y, z, delay, run, npcString, chatString));
                    }
                    else if (r.getNodeName().equals("target")) {
                        final NamedNodeMap attrs = r.getAttributes();
                        try {
                            final int npcId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                            final int x2 = Integer.parseInt(attrs.getNamedItem("spawnX").getNodeValue());
                            final int y2 = Integer.parseInt(attrs.getNamedItem("spawnY").getNodeValue());
                            final int z2 = Integer.parseInt(attrs.getNamedItem("spawnZ").getNodeValue());
                            if (NpcData.getInstance().getTemplate(npcId) != null) {
                                final NpcRoutesHolder holder = this._routesToAttach.containsKey(npcId) ? this._routesToAttach.get(npcId) : new NpcRoutesHolder();
                                holder.addRoute(routeName, new Location(x2, y2, z2));
                                this._routesToAttach.put(npcId, holder);
                            }
                            else {
                                WalkingManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, npcId, routeName));
                            }
                        }
                        catch (Exception e) {
                            WalkingManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, routeName));
                        }
                    }
                }
                this._routes.put(routeName, new WalkRoute(routeName, list, repeat, false, repeatType));
            }
        }
    }
    
    public boolean isOnWalk(final Npc npc) {
        Monster monster = null;
        if (GameUtils.isMonster(npc)) {
            if (((Monster)npc).getLeader() == null) {
                monster = (Monster)npc;
            }
            else {
                monster = ((Monster)npc).getLeader();
            }
        }
        if ((monster != null && !this.isRegistered(monster)) || !this.isRegistered(npc)) {
            return false;
        }
        final WalkInfo walk = (monster != null) ? this._activeRoutes.get(monster.getObjectId()) : this._activeRoutes.get(npc.getObjectId());
        return !walk.isStoppedByAttack() && !walk.isSuspended();
    }
    
    public WalkRoute getRoute(final String route) {
        return this._routes.get(route);
    }
    
    public boolean isRegistered(final Npc npc) {
        return this._activeRoutes.containsKey(npc.getObjectId());
    }
    
    public String getRouteName(final Npc npc) {
        return this._activeRoutes.containsKey(npc.getObjectId()) ? this._activeRoutes.get(npc.getObjectId()).getRoute().getName() : "";
    }
    
    public void startMoving(final Npc npc, final String routeName) {
        if (this._routes.containsKey(routeName) && npc != null && !npc.isDead()) {
            if (!this._activeRoutes.containsKey(npc.getObjectId())) {
                if (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                    final WalkInfo walk = new WalkInfo(routeName);
                    NpcWalkerNode node = walk.getCurrentNode();
                    if (npc.getX() == node.getX() && npc.getY() == node.getY()) {
                        walk.calculateNextNode(npc);
                        node = walk.getCurrentNode();
                    }
                    if (!MathUtil.isInsideRadius3D(npc, node, 3000)) {
                        WalkingManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IIIIIIID)Ljava/lang/String;, routeName, npc.getId(), npc.getX(), npc.getY(), npc.getZ(), node.getX(), node.getY(), node.getZ(), MathUtil.calculateDistance3D(npc, node)));
                        npc.teleToLocation(node);
                    }
                    if (node.runToLocation()) {
                        npc.setRunning();
                    }
                    else {
                        npc.setWalking();
                    }
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, node);
                    final ScheduledFuture<?> task = this._repeatMoveTasks.get(npc);
                    if (task == null || task.isCancelled() || task.isDone()) {
                        final ScheduledFuture<?> newTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)new StartMovingTask(npc, routeName), 60000L, 60000L);
                        this._repeatMoveTasks.put(npc, newTask);
                        walk.setWalkCheckTask(newTask);
                    }
                    this._activeRoutes.put(npc.getObjectId(), walk);
                }
                else {
                    final ScheduledFuture<?> task2 = this._startMoveTasks.get(npc);
                    if (task2 == null || task2.isCancelled() || task2.isDone()) {
                        this._startMoveTasks.put(npc, ThreadPool.schedule((Runnable)new StartMovingTask(npc, routeName), 60000L));
                    }
                }
            }
            else if (this._activeRoutes.containsKey(npc.getObjectId()) && (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE)) {
                final WalkInfo walk = this._activeRoutes.get(npc.getObjectId());
                if (walk == null) {
                    return;
                }
                if (walk.isBlocked() || walk.isSuspended()) {
                    return;
                }
                walk.setBlocked(true);
                final NpcWalkerNode node = walk.getCurrentNode();
                if (node.runToLocation()) {
                    npc.setRunning();
                }
                else {
                    npc.setWalking();
                }
                npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, node);
                walk.setBlocked(false);
                walk.setStoppedByAttack(false);
            }
        }
    }
    
    public synchronized void cancelMoving(final Npc npc) {
        final WalkInfo walk = this._activeRoutes.remove(npc.getObjectId());
        if (walk != null) {
            walk.getWalkCheckTask().cancel(true);
        }
    }
    
    public void resumeMoving(final Npc npc) {
        final WalkInfo walk = this._activeRoutes.get(npc.getObjectId());
        if (walk != null) {
            walk.setSuspended(false);
            walk.setStoppedByAttack(false);
            this.startMoving(npc, walk.getRoute().getName());
        }
    }
    
    public void stopMoving(final Npc npc, final boolean suspend, final boolean stoppedByAttack) {
        Monster monster = null;
        if (GameUtils.isMonster(npc)) {
            if (((Monster)npc).getLeader() == null) {
                monster = (Monster)npc;
            }
            else {
                monster = ((Monster)npc).getLeader();
            }
        }
        if ((monster != null && !this.isRegistered(monster)) || !this.isRegistered(npc)) {
            return;
        }
        final WalkInfo walk = (monster != null) ? this._activeRoutes.get(monster.getObjectId()) : this._activeRoutes.get(npc.getObjectId());
        walk.setSuspended(suspend);
        walk.setStoppedByAttack(stoppedByAttack);
        if (monster != null) {
            monster.stopMove(null);
            monster.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
        else {
            npc.stopMove(null);
            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
    
    public void onArrived(final Npc npc) {
        if (this._activeRoutes.containsKey(npc.getObjectId())) {
            final WalkInfo walk = this._activeRoutes.get(npc.getObjectId());
            if (walk.getCurrentNodeId() >= 0 && walk.getCurrentNodeId() < walk.getRoute().getNodesCount()) {
                final NpcWalkerNode node = walk.getRoute().getNodeList().get(walk.getCurrentNodeId());
                if (MathUtil.isInsideRadius2D(npc, node, 10)) {
                    walk.calculateNextNode(npc);
                    walk.setBlocked(true);
                    if (node.getNpcString() != null) {
                        npc.broadcastSay(ChatType.NPC_GENERAL, node.getNpcString(), new String[0]);
                    }
                    else if (!node.getChatText().isEmpty()) {
                        npc.broadcastSay(ChatType.NPC_GENERAL, node.getChatText());
                    }
                    final ScheduledFuture<?> task = this._arriveTasks.get(npc);
                    if (task == null || task.isCancelled() || task.isDone()) {
                        this._arriveTasks.put(npc, ThreadPool.schedule((Runnable)new ArrivedTask(npc, walk), (long)(100 + node.getDelay() * 1000)));
                    }
                }
            }
        }
    }
    
    public void onDeath(final Npc npc) {
        this.cancelMoving(npc);
    }
    
    public void onSpawn(final Npc npc) {
        if (this._routesToAttach.containsKey(npc.getId())) {
            final String routeName = this._routesToAttach.get(npc.getId()).getRouteName(npc);
            if (!routeName.isEmpty()) {
                this.startMoving(npc, routeName);
            }
        }
    }
    
    public static WalkingManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)WalkingManager.class);
    }
    
    private static class Singleton
    {
        private static final WalkingManager INSTANCE;
        
        static {
            INSTANCE = new WalkingManager();
        }
    }
}
