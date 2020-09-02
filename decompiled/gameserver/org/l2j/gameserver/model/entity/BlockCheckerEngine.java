// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.network.serverpackets.ExCubeGameEnd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.instance.Block;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import org.l2j.gameserver.network.serverpackets.ExCubeGameChangePoints;
import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.serverpackets.ExCubeGameCloseUI;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.slf4j.LoggerFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.network.serverpackets.RelationChanged;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.Spawn;
import java.util.Set;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Map;
import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.slf4j.Logger;

public final class BlockCheckerEngine
{
    protected static final Logger LOGGER;
    protected static final int[][] _arenaCoordinates;
    private static final int _zCoord = -2405;
    private static final byte DEFAULT_ARENA = -1;
    protected ArenaParticipantsHolder _holder;
    protected Map<Player, Integer> _redTeamPoints;
    protected Map<Player, Integer> _blueTeamPoints;
    protected int _redPoints;
    protected int _bluePoints;
    protected int _arena;
    protected Set<Spawn> _spawns;
    protected boolean _isRedWinner;
    protected long _startedTime;
    protected Set<Item> _drops;
    protected boolean _isStarted;
    protected ScheduledFuture<?> _task;
    protected boolean _abnormalEnd;
    
    public BlockCheckerEngine(final ArenaParticipantsHolder holder, final int arena) {
        this._redTeamPoints = new ConcurrentHashMap<Player, Integer>();
        this._blueTeamPoints = new ConcurrentHashMap<Player, Integer>();
        this._redPoints = 15;
        this._bluePoints = 15;
        this._arena = -1;
        this._spawns = (Set<Spawn>)ConcurrentHashMap.newKeySet();
        this._drops = (Set<Item>)ConcurrentHashMap.newKeySet();
        this._isStarted = false;
        this._abnormalEnd = false;
        this._holder = holder;
        if (arena > -1 && arena < 4) {
            this._arena = arena;
        }
        for (final Player player : holder.getRedPlayers()) {
            this._redTeamPoints.put(player, 0);
        }
        for (final Player player : holder.getBluePlayers()) {
            this._blueTeamPoints.put(player, 0);
        }
    }
    
    public void updatePlayersOnStart(final ArenaParticipantsHolder holder) {
        this._holder = holder;
    }
    
    public ArenaParticipantsHolder getHolder() {
        return this._holder;
    }
    
    public int getArena() {
        return this._arena;
    }
    
    public long getStarterTime() {
        return this._startedTime;
    }
    
    public int getRedPoints() {
        synchronized (this) {
            return this._redPoints;
        }
    }
    
    public int getBluePoints() {
        synchronized (this) {
            return this._bluePoints;
        }
    }
    
    public int getPlayerPoints(final Player player, final boolean isRed) {
        if (!this._redTeamPoints.containsKey(player) && !this._blueTeamPoints.containsKey(player)) {
            return 0;
        }
        if (isRed) {
            return this._redTeamPoints.get(player);
        }
        return this._blueTeamPoints.get(player);
    }
    
    public synchronized void increasePlayerPoints(final Player player, final int team) {
        if (player == null) {
            return;
        }
        if (team == 0) {
            final int points = this._redTeamPoints.get(player) + 1;
            this._redTeamPoints.put(player, points);
            ++this._redPoints;
            --this._bluePoints;
        }
        else {
            final int points = this._blueTeamPoints.get(player) + 1;
            this._blueTeamPoints.put(player, points);
            ++this._bluePoints;
            --this._redPoints;
        }
    }
    
    public void addNewDrop(final Item item) {
        if (item != null) {
            this._drops.add(item);
        }
    }
    
    public boolean isStarted() {
        return this._isStarted;
    }
    
    protected void broadcastRelationChanged(final Player plr) {
        for (final Player p : this._holder.getAllPlayers()) {
            p.sendPacket(new RelationChanged(plr, plr.getRelation(p), plr.isAutoAttackable(p)));
        }
    }
    
    public void endEventAbnormally() {
        try {
            synchronized (this) {
                this._isStarted = false;
                if (this._task != null) {
                    this._task.cancel(true);
                }
                this._abnormalEnd = true;
                ThreadPool.execute((Runnable)new EndEvent());
            }
        }
        catch (Exception e) {
            BlockCheckerEngine.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this._arena), (Throwable)e);
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)BlockCheckerEngine.class);
        _arenaCoordinates = new int[][] { { -58368, -62745, -57751, -62131, -58053, -62417 }, { -58350, -63853, -57756, -63266, -58053, -63551 }, { -57194, -63861, -56580, -63249, -56886, -63551 }, { -57200, -62727, -56584, -62115, -56850, -62391 } };
    }
    
    public class StartEvent implements Runnable
    {
        private final Skill _freeze;
        private final Skill _transformationRed;
        private final Skill _transformationBlue;
        private final ExCubeGameCloseUI _closeUserInterface;
        
        public StartEvent() {
            this._closeUserInterface = ExCubeGameCloseUI.STATIC_PACKET;
            this._freeze = SkillEngine.getInstance().getSkill(6034, 1);
            this._transformationRed = SkillEngine.getInstance().getSkill(6035, 1);
            this._transformationBlue = SkillEngine.getInstance().getSkill(6036, 1);
        }
        
        private void setUpPlayers() {
            HandysBlockCheckerManager.getInstance().setArenaBeingUsed(BlockCheckerEngine.this._arena);
            BlockCheckerEngine.this._redPoints = BlockCheckerEngine.this._spawns.size() / 2;
            BlockCheckerEngine.this._bluePoints = BlockCheckerEngine.this._spawns.size() / 2;
            final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(300, BlockCheckerEngine.this._bluePoints, BlockCheckerEngine.this._redPoints);
            for (final Player player : BlockCheckerEngine.this._holder.getAllPlayers()) {
                if (player == null) {
                    continue;
                }
                final boolean isRed = BlockCheckerEngine.this._holder.getRedPlayers().contains(player);
                final ExCubeGameExtendedChangePoints clientSetUp = new ExCubeGameExtendedChangePoints(300, BlockCheckerEngine.this._bluePoints, BlockCheckerEngine.this._redPoints, isRed, player, 0);
                player.sendPacket(clientSetUp);
                player.sendPacket(ActionFailed.STATIC_PACKET);
                final int tc = BlockCheckerEngine.this._holder.getPlayerTeam(player) * 2;
                final int x = BlockCheckerEngine._arenaCoordinates[BlockCheckerEngine.this._arena][tc];
                final int y = BlockCheckerEngine._arenaCoordinates[BlockCheckerEngine.this._arena][tc + 1];
                player.teleToLocation(x, y, -2405);
                if (isRed) {
                    BlockCheckerEngine.this._redTeamPoints.put(player, 0);
                    player.setTeam(Team.RED);
                }
                else {
                    BlockCheckerEngine.this._blueTeamPoints.put(player, 0);
                    player.setTeam(Team.BLUE);
                }
                player.stopAllEffects();
                final Summon pet = player.getPet();
                if (pet != null) {
                    pet.unSummon(player);
                }
                player.getServitors().values().forEach(s -> s.unSummon(player));
                this._freeze.applyEffects(player, player);
                if (BlockCheckerEngine.this._holder.getPlayerTeam(player) == 0) {
                    this._transformationRed.applyEffects(player, player);
                }
                else {
                    this._transformationBlue.applyEffects(player, player);
                }
                player.setBlockCheckerArena((byte)BlockCheckerEngine.this._arena);
                player.setInsideZone(ZoneType.PVP, true);
                player.sendPacket(initialPoints);
                player.sendPacket(this._closeUserInterface);
                player.sendPacket(ExBasicActionList.STATIC_PACKET);
                BlockCheckerEngine.this.broadcastRelationChanged(player);
            }
        }
        
        @Override
        public void run() {
            if (BlockCheckerEngine.this._arena == -1) {
                BlockCheckerEngine.LOGGER.error("Couldnt set up the arena Id for the Block Checker event, cancelling event...");
                return;
            }
            BlockCheckerEngine.this._isStarted = true;
            ThreadPool.execute((Runnable)new SpawnRound(16, 1));
            this.setUpPlayers();
            BlockCheckerEngine.this._startedTime = System.currentTimeMillis() + 300000L;
        }
    }
    
    private class SpawnRound implements Runnable
    {
        int _numOfBoxes;
        int _round;
        
        SpawnRound(final int numberOfBoxes, final int round) {
            this._numOfBoxes = numberOfBoxes;
            this._round = round;
        }
        
        @Override
        public void run() {
            if (!BlockCheckerEngine.this._isStarted) {
                return;
            }
            switch (this._round) {
                case 1: {
                    BlockCheckerEngine.this._task = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new SpawnRound(20, 2), 60000L);
                    break;
                }
                case 2: {
                    BlockCheckerEngine.this._task = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new SpawnRound(14, 3), 60000L);
                    break;
                }
                case 3: {
                    BlockCheckerEngine.this._task = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new EndEvent(), 180000L);
                    break;
                }
            }
            byte random = 2;
            final NpcTemplate template = NpcData.getInstance().getTemplate(18672);
            try {
                for (int i = 0; i < this._numOfBoxes; ++i) {
                    final Spawn spawn = new Spawn(template);
                    spawn.setXYZ(BlockCheckerEngine._arenaCoordinates[BlockCheckerEngine.this._arena][4] + Rnd.get(-400, 400), BlockCheckerEngine._arenaCoordinates[BlockCheckerEngine.this._arena][5] + Rnd.get(-400, 400), -2405);
                    spawn.setAmount(1);
                    spawn.setHeading(1);
                    spawn.setRespawnDelay(1);
                    SpawnTable.getInstance().addNewSpawn(spawn, false);
                    spawn.init();
                    final Block block = (Block)spawn.getLastSpawn();
                    if (random % 2 == 0) {
                        block.setRed(true);
                    }
                    else {
                        block.setRed(false);
                    }
                    block.disableCoreAI(true);
                    BlockCheckerEngine.this._spawns.add(spawn);
                    ++random;
                }
            }
            catch (Exception e) {
                BlockCheckerEngine.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
            }
            Label_0485: {
                if (this._round != 1) {
                    if (this._round != 2) {
                        break Label_0485;
                    }
                }
                try {
                    final Spawn girlSpawn = new Spawn(18676);
                    girlSpawn.setXYZ(BlockCheckerEngine._arenaCoordinates[BlockCheckerEngine.this._arena][4] + Rnd.get(-400, 400), BlockCheckerEngine._arenaCoordinates[BlockCheckerEngine.this._arena][5] + Rnd.get(-400, 400), -2405);
                    girlSpawn.setAmount(1);
                    girlSpawn.setHeading(1);
                    girlSpawn.setRespawnDelay(1);
                    SpawnTable.getInstance().addNewSpawn(girlSpawn, false);
                    girlSpawn.init();
                    ThreadPool.schedule((Runnable)new CarryingGirlUnspawn(girlSpawn), 9000L);
                }
                catch (Exception e) {
                    BlockCheckerEngine.LOGGER.warn("Couldnt Spawn Block Checker NPCs! Wrong instance type at npc table?");
                    BlockCheckerEngine.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()));
                }
            }
            final BlockCheckerEngine this$0 = BlockCheckerEngine.this;
            this$0._redPoints += this._numOfBoxes / 2;
            final BlockCheckerEngine this$2 = BlockCheckerEngine.this;
            this$2._bluePoints += this._numOfBoxes / 2;
            final int timeLeft = (int)((BlockCheckerEngine.this._startedTime - System.currentTimeMillis()) / 1000L);
            final ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, BlockCheckerEngine.this.getBluePoints(), BlockCheckerEngine.this.getRedPoints());
            BlockCheckerEngine.this._holder.broadCastPacketToTeam(changePoints);
        }
    }
    
    private class CarryingGirlUnspawn implements Runnable
    {
        private final Spawn _spawn;
        
        protected CarryingGirlUnspawn(final Spawn spawn) {
            this._spawn = spawn;
        }
        
        @Override
        public void run() {
            if (this._spawn == null) {
                BlockCheckerEngine.LOGGER.warn("HBCE: Block Carrying Girl is null");
                return;
            }
            SpawnTable.getInstance().deleteSpawn(this._spawn, false);
            this._spawn.stopRespawn();
            this._spawn.getLastSpawn().deleteMe();
        }
    }
    
    protected class EndEvent implements Runnable
    {
        private void clearMe() {
            HandysBlockCheckerManager.getInstance().clearPaticipantQueueByArenaId(BlockCheckerEngine.this._arena);
            BlockCheckerEngine.this._holder.clearPlayers();
            BlockCheckerEngine.this._blueTeamPoints.clear();
            BlockCheckerEngine.this._redTeamPoints.clear();
            HandysBlockCheckerManager.getInstance().setArenaFree(BlockCheckerEngine.this._arena);
            for (final Spawn spawn : BlockCheckerEngine.this._spawns) {
                spawn.stopRespawn();
                spawn.getLastSpawn().deleteMe();
                SpawnTable.getInstance().deleteSpawn(spawn, false);
            }
            BlockCheckerEngine.this._spawns.clear();
            for (final Item item : BlockCheckerEngine.this._drops) {
                if (item == null) {
                    continue;
                }
                if (!item.isSpawned()) {
                    continue;
                }
                if (item.getOwnerId() != 0) {
                    continue;
                }
                item.decayMe();
            }
            BlockCheckerEngine.this._drops.clear();
        }
        
        private void rewardPlayers() {
            if (BlockCheckerEngine.this._redPoints == BlockCheckerEngine.this._bluePoints) {
                return;
            }
            BlockCheckerEngine.this._isRedWinner = (BlockCheckerEngine.this._redPoints > BlockCheckerEngine.this._bluePoints);
            if (BlockCheckerEngine.this._isRedWinner) {
                this.rewardAsWinner(true);
                this.rewardAsLooser(false);
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.THE_C1_TEAM_HAS_WON);
                msg.addString("Red Team");
                BlockCheckerEngine.this._holder.broadCastPacketToTeam(msg);
            }
            else if (BlockCheckerEngine.this._bluePoints > BlockCheckerEngine.this._redPoints) {
                this.rewardAsWinner(false);
                this.rewardAsLooser(true);
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.THE_C1_TEAM_HAS_WON);
                msg.addString("Blue Team");
                BlockCheckerEngine.this._holder.broadCastPacketToTeam(msg);
            }
            else {
                this.rewardAsLooser(true);
                this.rewardAsLooser(false);
            }
        }
        
        private void rewardAsWinner(final boolean isRed) {
            final Map<Player, Integer> tempPoints = isRed ? BlockCheckerEngine.this._redTeamPoints : BlockCheckerEngine.this._blueTeamPoints;
            for (final Map.Entry<Player, Integer> points : tempPoints.entrySet()) {
                if (points.getKey() == null) {
                    continue;
                }
                if (points.getValue() >= 10) {
                    points.getKey().addItem("Block Checker", 13067, 2L, points.getKey(), true);
                }
                else {
                    tempPoints.remove(points.getKey());
                }
            }
            int first = 0;
            int second = 0;
            Player winner1 = null;
            Player winner2 = null;
            for (final Map.Entry<Player, Integer> entry : tempPoints.entrySet()) {
                final Player pc = entry.getKey();
                final int pcPoints = entry.getValue();
                if (pcPoints > first) {
                    second = first;
                    winner2 = winner1;
                    first = pcPoints;
                    winner1 = pc;
                }
                else {
                    if (pcPoints <= second) {
                        continue;
                    }
                    second = pcPoints;
                    winner2 = pc;
                }
            }
            if (winner1 != null) {
                winner1.addItem("Block Checker", 13067, 8L, winner1, true);
            }
            if (winner2 != null) {
                winner2.addItem("Block Checker", 13067, 5L, winner2, true);
            }
        }
        
        private void rewardAsLooser(final boolean isRed) {
            for (final Map.Entry<Player, Integer> entry : (isRed ? BlockCheckerEngine.this._redTeamPoints : BlockCheckerEngine.this._blueTeamPoints).entrySet()) {
                final Player player = entry.getKey();
                if (player != null && entry.getValue() >= 10) {
                    player.addItem("Block Checker", 13067, 2L, player, true);
                }
            }
        }
        
        private void setPlayersBack() {
            final ExCubeGameEnd end = new ExCubeGameEnd(BlockCheckerEngine.this._isRedWinner);
            for (final Player player : BlockCheckerEngine.this._holder.getAllPlayers()) {
                if (player == null) {
                    continue;
                }
                player.stopAllEffects();
                player.setTeam(Team.NONE);
                player.setBlockCheckerArena((byte)(-1));
                final PlayerInventory inv = player.getInventory();
                if (inv.getItemByItemId(13787) != null) {
                    inv.destroyItemByItemId("Handys Block Checker", 13787, inv.getInventoryItemCount(13787, 0), player, player);
                }
                if (inv.getItemByItemId(13788) != null) {
                    inv.destroyItemByItemId("Handys Block Checker", 13788, inv.getInventoryItemCount(13788, 0), player, player);
                }
                BlockCheckerEngine.this.broadcastRelationChanged(player);
                player.teleToLocation(-57478, -60367, -2370);
                player.setInsideZone(ZoneType.PVP, false);
                player.sendPacket(end);
                player.broadcastUserInfo();
            }
        }
        
        @Override
        public void run() {
            if (!BlockCheckerEngine.this._abnormalEnd) {
                this.rewardPlayers();
            }
            this.setPlayersBack();
            this.clearMe();
            BlockCheckerEngine.this._isStarted = false;
            BlockCheckerEngine.this._abnormalEnd = false;
        }
    }
}
