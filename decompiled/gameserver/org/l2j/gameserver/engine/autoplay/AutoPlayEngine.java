// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.HashSet;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.handler.PlayerActionHandler;
import java.util.Arrays;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import java.util.function.Consumer;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlaySettingResponse;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.data.database.data.Shortcut;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

public final class AutoPlayEngine
{
    private static final int AUTO_PLAY_INTERVAL = 1000;
    private static final int DEFAULT_ACTION = 2;
    private final ForkJoinPool autoPlayPool;
    private final Set<Player> players;
    private final Set<Player> autoPotionPlayers;
    private final DoAutoPlay doAutoPlayTask;
    private final Object autoPlayTaskLocker;
    private ScheduledFuture<?> autoPlayTask;
    private final DoAutoPotion doAutoPotion;
    private final Object autoPotionTaskLocker;
    private ScheduledFuture<?> autoPotionTask;
    private final AutoPlayTargetFinder tauntFinder;
    private final AutoPlayTargetFinder monsterFinder;
    private final AutoPlayTargetFinder playerFinder;
    private final AutoPlayTargetFinder friendlyFinder;
    
    private AutoPlayEngine() {
        this.autoPlayPool = new ForkJoinPool();
        this.players = (Set<Player>)ConcurrentHashMap.newKeySet();
        this.autoPotionPlayers = (Set<Player>)ConcurrentHashMap.newKeySet();
        this.doAutoPlayTask = new DoAutoPlay();
        this.autoPlayTaskLocker = new Object();
        this.doAutoPotion = new DoAutoPotion();
        this.autoPotionTaskLocker = new Object();
        this.tauntFinder = new TauntFinder();
        this.monsterFinder = new MonsterFinder();
        this.playerFinder = new PlayerFinder();
        this.friendlyFinder = new FriendlyMobFinder();
    }
    
    public boolean setActiveAutoShortcut(final Player player, final int room, final boolean activate) {
        final Shortcut shortcut = player.getShortcut(room);
        if (Objects.nonNull(shortcut) && this.handleShortcut(player, shortcut, activate)) {
            player.setActiveAutoShortcut(room, activate);
            return true;
        }
        return false;
    }
    
    private boolean handleShortcut(final Player player, final Shortcut shortcut, final boolean activate) {
        boolean b = false;
        switch (shortcut.getType()) {
            case ITEM: {
                b = this.handleAutoItem(player, shortcut, activate);
                break;
            }
            case SKILL: {
                b = this.handleAutoSkill(player, shortcut);
                break;
            }
            case ACTION: {
                b = this.handleAutoAction(shortcut);
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    private boolean handleAutoAction(final Shortcut shortcut) {
        return ActionManager.getInstance().isAutoUseAction(shortcut.getShortcutId());
    }
    
    private boolean handleAutoSkill(final Player player, final Shortcut shortcut) {
        final Skill skill = player.getKnownSkill(shortcut.getShortcutId());
        if (Objects.isNull(skill) || !skill.isAutoUse()) {
            player.deleteShortcut(shortcut.getClientId());
            return false;
        }
        return true;
    }
    
    private boolean handleAutoItem(final Player player, final Shortcut shortcut, final boolean activate) {
        final Item item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
        if (Objects.isNull(item) || (!item.isAutoSupply() && !item.isAutoPotion())) {
            player.deleteShortcut(shortcut.getClientId());
            return false;
        }
        if (item.isAutoPotion()) {
            if (activate) {
                this.startAutoPotion(player);
            }
            else {
                this.stopAutoPotion(player);
            }
        }
        return true;
    }
    
    public void startAutoPlay(final Player player) {
        this.players.add(player);
        synchronized (this.autoPlayTaskLocker) {
            if (Objects.isNull(this.autoPlayTask)) {
                this.autoPlayTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedDelay((Runnable)this.doAutoPlayTask, 1000L, 1000L);
            }
        }
        player.getAutoPlaySettings().setActive(true);
        player.sendPacket(new ExAutoPlaySettingResponse());
    }
    
    public void stopAutoPlay(final Player player) {
        if (this.players.remove(player)) {
            synchronized (this.autoPlayTaskLocker) {
                if (this.players.isEmpty() && Objects.nonNull(this.autoPlayTask)) {
                    this.autoPlayTask.cancel(false);
                    this.autoPlayTask = null;
                }
            }
        }
        player.getAutoPlaySettings().setActive(false);
        player.sendPacket(new ExAutoPlaySettingResponse());
        player.resetNextAutoShortcut();
    }
    
    public void startAutoPotion(final Player player) {
        this.autoPotionPlayers.add(player);
        synchronized (this.autoPotionTaskLocker) {
            if (Objects.isNull(this.autoPotionTask)) {
                this.autoPotionTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedDelay((Runnable)this.doAutoPotion, 1000L, 1000L);
            }
        }
    }
    
    public void stopAutoPotion(final Player player) {
        if (this.autoPotionPlayers.remove(player)) {
            synchronized (this.autoPotionTaskLocker) {
                if (this.autoPotionPlayers.isEmpty() && Objects.nonNull(this.autoPotionTask)) {
                    this.autoPotionTask.cancel(false);
                    this.autoPotionTask = null;
                }
            }
        }
    }
    
    public void stopTasks(final Player player) {
        if (Objects.nonNull(player.getAutoPlaySettings())) {
            this.stopAutoPlay(player);
            this.stopAutoPotion(player);
        }
    }
    
    public static AutoPlayEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    public void shutdown() {
        this.autoPlayPool.shutdown();
    }
    
    private void useItem(final Player player, final Item item) {
        final int reuseDelay = item.getReuseDelay();
        if (reuseDelay <= 0 || player.getItemRemainingReuseTime(item.getObjectId()) <= 0L) {
            final EtcItem etcItem = item.getEtcItem();
            final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
            if (Objects.nonNull(handler) && handler.useItem(player, item, false)) {
                player.onActionRequest();
                if (reuseDelay > 0) {
                    player.addTimeStampItem(item, reuseDelay);
                }
            }
        }
    }
    
    private boolean canUseAutoPlay(final Player player) {
        return !player.getAutoPlaySettings().isAutoPlaying() && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_PICK_UP && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST && !player.hasBlockActions() && !player.isControlBlocked() && !player.isAlikeDead() && !player.isInsideZone(ZoneType.PEACE) && !player.inObserverMode() && !player.isCastingNow();
    }
    
    private static final class Singleton
    {
        private static final AutoPlayEngine INSTANCE;
        
        static {
            INSTANCE = new AutoPlayEngine();
        }
    }
    
    private final class DoAutoPlay implements Runnable
    {
        @Override
        public void run() {
            AutoPlayEngine.this.autoPlayPool.submit(this::doAutoPlay);
        }
        
        private void doAutoPlay() {
            AutoPlayEngine.this.players.parallelStream().filter(x$0 -> AutoPlayEngine.this.canUseAutoPlay(x$0)).forEach((Consumer<? super Object>)this::doNextAction);
        }
        
        private void doNextAction(final Player player) {
            final AutoPlaySettings setting = player.getAutoPlaySettings();
            setting.setAutoPlaying(true);
            try {
                final int range = setting.isNearTarget() ? 600 : 1400;
                if (setting.isAutoPickUpOn()) {
                    final Item item = World.getInstance().findAnyVisibleObject(player, Item.class, 200, false, it -> Objects.nonNull(it.getDropProtection().getOwner()) && it.getDropProtection().tryPickUp(player) && player.getInventory().validateCapacity(it));
                    if (Objects.nonNull(item)) {
                        player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
                        return;
                    }
                }
                this.pickTargetAndAct(player, setting, range);
            }
            finally {
                setting.setAutoPlaying(false);
            }
        }
        
        private void pickTargetAndAct(final Player player, final AutoPlaySettings setting, final int range) {
            final AutoPlayTargetFinder targetFinder = this.targetFinderBySettings(setting);
            if (!targetFinder.canBeTarget(player, player.getTarget())) {
                player.setTarget(targetFinder.findNextTarget(player, range));
            }
            if (Objects.nonNull(player.getTarget())) {
                this.tryUseAutoShortcut(player);
            }
        }
        
        private AutoPlayTargetFinder targetFinderBySettings(final AutoPlaySettings setting) {
            AutoPlayTargetFinder autoPlayTargetFinder = null;
            switch (setting.getNextTargetMode()) {
                case 0: {
                    autoPlayTargetFinder = AutoPlayEngine.this.tauntFinder;
                    break;
                }
                case 2: {
                    autoPlayTargetFinder = AutoPlayEngine.this.playerFinder;
                    break;
                }
                case 3: {
                    autoPlayTargetFinder = AutoPlayEngine.this.friendlyFinder;
                    break;
                }
                default: {
                    autoPlayTargetFinder = AutoPlayEngine.this.monsterFinder;
                    break;
                }
            }
            return autoPlayTargetFinder;
        }
        
        private void tryUseAutoShortcut(final Player player) {
            final Shortcut nextShortcut = player.nextAutoShortcut();
            Label_0037: {
                if (Objects.nonNull(nextShortcut)) {
                    Shortcut shortcut = nextShortcut;
                    while (!this.useShortcut(player, shortcut)) {
                        shortcut = player.nextAutoShortcut();
                        if (nextShortcut.equals(shortcut)) {
                            break Label_0037;
                        }
                    }
                    return;
                }
            }
            this.autoUseAction(player, 2);
        }
        
        private boolean useShortcut(final Player player, final Shortcut shortcut) {
            boolean b = false;
            switch (shortcut.getType()) {
                case SKILL: {
                    b = this.autoUseSkill(player, shortcut);
                    break;
                }
                case ITEM: {
                    b = this.autoUseItem(player, shortcut);
                    break;
                }
                case ACTION: {
                    b = this.autoUseAction(player, shortcut.getShortcutId());
                    break;
                }
                default: {
                    b = false;
                    break;
                }
            }
            return b;
        }
        
        private boolean autoUseAction(final Player player, final int actionId) {
            final int[] allowedActions = player.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
            if (Arrays.binarySearch(allowedActions, actionId) < 0) {
                return false;
            }
            final ActionData action = ActionManager.getInstance().getActionData(actionId);
            if (Objects.nonNull(action)) {
                final IPlayerActionHandler handler = PlayerActionHandler.getInstance().getHandler(action.getHandler());
                if (Objects.nonNull(handler)) {
                    handler.useAction(player, action, false, false);
                    player.onActionRequest();
                    return true;
                }
            }
            return false;
        }
        
        private boolean autoUseItem(final Player player, final Shortcut shortcut) {
            final Item item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
            if (Objects.nonNull(item) && item.isAutoSupply() && item.getTemplate().checkAnySkill(ItemSkillType.NORMAL, s -> player.getBuffRemainTimeBySkillOrAbormalType(s.getSkill()) <= 3)) {
                AutoPlayEngine.this.useItem(player, item);
                return true;
            }
            return false;
        }
        
        private boolean autoUseSkill(final Player player, final Shortcut shortcut) {
            final Skill skill = player.getKnownSkill(shortcut.getShortcutId());
            if (skill.isBlockActionUseSkill()) {
                return false;
            }
            if (skill.isAutoTransformation() && player.isTransformed()) {
                return false;
            }
            if (skill.isAutoBuff() && player.getBuffRemainTimeBySkillOrAbormalType(skill) > 3) {
                return false;
            }
            player.onActionRequest();
            return player.useMagic(skill, null, false, false);
        }
    }
    
    private final class DoAutoPotion implements Runnable
    {
        @Override
        public void run() {
            AutoPlayEngine.this.autoPlayPool.submit(this::useAutoPotion);
        }
        
        private void useAutoPotion() {
            final HashSet<Player> toRemove = new HashSet<Player>();
            final Shortcut shortcut;
            Item item;
            final HashSet<Player> set;
            AutoPlayEngine.this.autoPotionPlayers.parallelStream().filter((Predicate<? super Object>)this::canUseAutoPotion).forEach(player -> {
                shortcut = player.getShortcut(Shortcut.AUTO_POTION_ROOM);
                if (Objects.nonNull(shortcut)) {
                    item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
                    if (Objects.nonNull(item)) {
                        AutoPlayEngine.this.useItem(player, item);
                    }
                }
                else {
                    set.add(player);
                }
                return;
            });
            if (!toRemove.isEmpty()) {
                AutoPlayEngine.this.autoPotionPlayers.removeAll(toRemove);
            }
        }
        
        private boolean canUseAutoPotion(final Player player) {
            return AutoPlayEngine.this.canUseAutoPlay(player) && player.getAutoPlaySettings().getUsableHpPotionPercent() >= player.getCurrentHpPercent();
        }
    }
}
