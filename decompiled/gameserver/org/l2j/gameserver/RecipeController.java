// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.Recipe;
import java.util.ArrayList;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.RecipeStat;
import org.l2j.gameserver.enums.StatType;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.network.serverpackets.RecipeShopItemInfo;
import org.l2j.gameserver.network.serverpackets.RecipeItemMakeInfo;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.network.serverpackets.SetupGauge;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import java.util.Iterator;
import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.TempItem;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.slf4j.Logger;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import java.util.List;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.util.GameUtils;
import java.util.Arrays;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.RecipeBookItemList;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.Map;

public class RecipeController
{
    private static final Map<Integer, RecipeItemMaker> _activeMakers;
    
    private RecipeController() {
    }
    
    public void requestBookOpen(final Player player, final boolean isDwarvenCraft) {
        if (!RecipeController._activeMakers.containsKey(player.getObjectId())) {
            final RecipeBookItemList response = new RecipeBookItemList(isDwarvenCraft, player.getMaxMp());
            response.addRecipes(isDwarvenCraft ? player.getDwarvenRecipeBook() : player.getCommonRecipeBook());
            player.sendPacket(response);
            return;
        }
        player.sendPacket(SystemMessageId.YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING);
    }
    
    public void requestMakeItemAbort(final Player player) {
        RecipeController._activeMakers.remove(player.getObjectId());
    }
    
    public void requestManufactureItem(final Player manufacturer, final int recipeListId, final Player player) {
        final RecipeList recipeList = RecipeData.getInstance().getValidRecipeList(player, recipeListId);
        if (recipeList == null) {
            return;
        }
        final List<RecipeList> dwarfRecipes = Arrays.asList(manufacturer.getDwarvenRecipeBook());
        final List<RecipeList> commonRecipes = Arrays.asList(manufacturer.getCommonRecipeBook());
        if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList)) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
            return;
        }
        if (Config.ALT_GAME_CREATION && RecipeController._activeMakers.containsKey(manufacturer.getObjectId())) {
            player.sendPacket(SystemMessageId.PLEASE_CLOSE_THE_SETUP_WINDOW_FOR_YOUR_PRIVATE_WORKSHOP_OR_PRIVATE_STORE_AND_TRY_AGAIN);
            return;
        }
        final RecipeItemMaker maker = new RecipeItemMaker(manufacturer, recipeList, player);
        if (maker._isValid) {
            if (Config.ALT_GAME_CREATION) {
                RecipeController._activeMakers.put(manufacturer.getObjectId(), maker);
                ThreadPool.schedule((Runnable)maker, 100L);
            }
            else {
                maker.run();
            }
        }
    }
    
    public void requestMakeItem(final Player player, final int recipeListId) {
        if (player.isInCombat() || player.isInDuel()) {
            player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            return;
        }
        final RecipeList recipeList = RecipeData.getInstance().getValidRecipeList(player, recipeListId);
        if (recipeList == null) {
            return;
        }
        final List<RecipeList> dwarfRecipes = Arrays.asList(player.getDwarvenRecipeBook());
        final List<RecipeList> commonRecipes = Arrays.asList(player.getCommonRecipeBook());
        if (!dwarfRecipes.contains(recipeList) && !commonRecipes.contains(recipeList)) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, player.getName(), player.getAccountName()));
            return;
        }
        if (Config.ALT_GAME_CREATION && RecipeController._activeMakers.containsKey(player.getObjectId())) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1);
            sm.addItemName(recipeList.getItemId());
            sm.addString("You are busy creating.");
            player.sendPacket(sm);
            return;
        }
        final RecipeItemMaker maker = new RecipeItemMaker(player, recipeList, player);
        if (maker._isValid) {
            if (Config.ALT_GAME_CREATION) {
                RecipeController._activeMakers.put(player.getObjectId(), maker);
                ThreadPool.schedule((Runnable)maker, 100L);
            }
            else {
                maker.run();
            }
        }
    }
    
    public static RecipeController getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        _activeMakers = new ConcurrentHashMap<Integer, RecipeItemMaker>();
    }
    
    private static class RecipeItemMaker implements Runnable
    {
        private static final Logger LOGGER;
        protected final RecipeList _recipeList;
        protected final Player _player;
        protected final Player _target;
        protected final Skill _skill;
        protected final int _skillId;
        protected final int _skillLevel;
        protected boolean _isValid;
        protected List<TempItem> _items;
        protected int _creationPasses;
        protected int _itemGrab;
        protected long _exp;
        protected long _sp;
        protected long _price;
        protected int _totalItems;
        protected int _delay;
        
        public RecipeItemMaker(final Player pPlayer, final RecipeList pRecipeList, final Player pTarget) {
            this._items = null;
            this._creationPasses = 1;
            this._exp = -1L;
            this._sp = -1L;
            this._player = pPlayer;
            this._target = pTarget;
            this._recipeList = pRecipeList;
            this._isValid = false;
            this._skillId = (this._recipeList.isDwarvenRecipe() ? CommonSkill.CREATE_DWARVEN.getId() : CommonSkill.CREATE_COMMON.getId());
            this._skillLevel = this._player.getSkillLevel(this._skillId);
            this._skill = this._player.getKnownSkill(this._skillId);
            this._player.setIsCrafting(true);
            if (this._player.isAlikeDead()) {
                this._player.sendPacket(ActionFailed.STATIC_PACKET);
                this.abort();
                return;
            }
            if (this._target.isAlikeDead()) {
                this._target.sendPacket(ActionFailed.STATIC_PACKET);
                this.abort();
                return;
            }
            if (this._target.isProcessingTransaction()) {
                this._target.sendPacket(ActionFailed.STATIC_PACKET);
                this.abort();
                return;
            }
            if (this._player.isProcessingTransaction()) {
                this._player.sendPacket(ActionFailed.STATIC_PACKET);
                this.abort();
                return;
            }
            if (this._recipeList.getRecipes().length == 0) {
                this._player.sendPacket(ActionFailed.STATIC_PACKET);
                this.abort();
                return;
            }
            if (this._recipeList.getLevel() > this._skillLevel) {
                this._player.sendPacket(ActionFailed.STATIC_PACKET);
                this.abort();
                return;
            }
            if (this._player != this._target) {
                final ManufactureItem item = this._player.getManufactureItems().get(this._recipeList.getId());
                if (item != null) {
                    this._price = item.getCost();
                    if (this._target.getAdena() < this._price) {
                        this._target.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                        this.abort();
                        return;
                    }
                }
            }
            this._items = this.listItems(false);
            if (this._items == null) {
                this.abort();
                return;
            }
            for (final TempItem i : this._items) {
                this._totalItems += i.getQuantity();
            }
            if (!this.calculateStatUse(false, false)) {
                this.abort();
                return;
            }
            if (Config.ALT_GAME_CREATION) {
                this.calculateAltStatChange();
            }
            this.updateMakeInfo(true);
            this.updateCurMp();
            this.updateCurLoad();
            this._player.setIsCrafting(false);
            this._isValid = true;
        }
        
        @Override
        public void run() {
            if (!Config.IS_CRAFTING_ENABLED) {
                this._target.sendMessage("Item creation is currently disabled.");
                this.abort();
                return;
            }
            if (this._player == null || this._target == null) {
                RecipeItemMaker.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this._target, this._player));
                this.abort();
                return;
            }
            if (!this._player.isOnline() || !this._target.isOnline()) {
                RecipeItemMaker.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this._target, this._player));
                this.abort();
                return;
            }
            if (Config.ALT_GAME_CREATION && !RecipeController._activeMakers.containsKey(this._player.getObjectId())) {
                if (this._target != this._player) {
                    this._target.sendMessage("Manufacture aborted");
                    this._player.sendMessage("Manufacture aborted");
                }
                else {
                    this._player.sendMessage("Item creation aborted");
                }
                this.abort();
                return;
            }
            if (Config.ALT_GAME_CREATION && !this._items.isEmpty()) {
                if (!this.calculateStatUse(true, true)) {
                    return;
                }
                this.updateCurMp();
                this.grabSomeItems();
                if (!this._items.isEmpty()) {
                    this._delay = (int)(Config.ALT_GAME_CREATION_SPEED * this._player.getStats().getReuseTime(this._skill) * 10.0 * 100.0);
                    final MagicSkillUse msk = new MagicSkillUse(this._player, this._skillId, this._skillLevel, this._delay, 0);
                    this._player.broadcastPacket(msk);
                    this._player.sendPacket(new SetupGauge(this._player.getObjectId(), 0, this._delay));
                    ThreadPool.schedule((Runnable)this, (long)(100 + this._delay));
                }
                else {
                    this._player.sendPacket(new SetupGauge(this._player.getObjectId(), 0, this._delay));
                    try {
                        Thread.sleep(this._delay);
                    }
                    catch (InterruptedException ex) {}
                    finally {
                        this.finishCrafting();
                    }
                }
            }
            else {
                this.finishCrafting();
            }
        }
        
        private void finishCrafting() {
            if (!Config.ALT_GAME_CREATION) {
                this.calculateStatUse(false, true);
            }
            if (this._target != this._player && this._price > 0L) {
                final Item adenatransfer = this._target.transferItem("PayManufacture", this._target.getInventory().getAdenaInstance().getObjectId(), this._price, this._player.getInventory(), this._player);
                if (adenatransfer == null) {
                    this._target.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
                    this.abort();
                    return;
                }
            }
            this._items = this.listItems(true);
            if (this._items != null) {
                if (Rnd.get(100) < this.getCraftChanceRate()) {
                    this.rewardPlayer();
                    this.updateMakeInfo(true);
                }
                else {
                    if (this._target != this._player) {
                        SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_FAILED_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA);
                        msg.addString(this._target.getName());
                        msg.addItemName(this._recipeList.getItemId());
                        msg.addLong(this._price);
                        this._player.sendPacket(msg);
                        msg = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA);
                        msg.addString(this._player.getName());
                        msg.addItemName(this._recipeList.getItemId());
                        msg.addLong(this._price);
                        this._target.sendPacket(msg);
                    }
                    else {
                        this._target.sendPacket(SystemMessageId.YOU_FAILED_AT_MIXING_THE_ITEM);
                    }
                    this.updateMakeInfo(false);
                }
            }
            this.updateCurMp();
            RecipeController._activeMakers.remove(this._player.getObjectId());
            this._player.setIsCrafting(false);
            this._target.sendItemList();
        }
        
        private double getCraftChanceRate() {
            return this._player.getStats().getValue(Stat.CRAFT_RATE_MASTER, this._recipeList.getSuccessRate());
        }
        
        private double getCraftCriticalRate() {
            return this._player.getStats().getValue(Stat.CRAFT_RATE_CRITICAL, Config.BASE_CRITICAL_CRAFT_RATE);
        }
        
        private void updateMakeInfo(final boolean success) {
            if (this._target == this._player) {
                this._target.sendPacket(new RecipeItemMakeInfo(this._recipeList.getId(), this._target, success));
            }
            else {
                this._target.sendPacket(new RecipeShopItemInfo(this._player, this._recipeList.getId()));
            }
        }
        
        private void updateCurLoad() {
            this._target.sendPacket(new ExUserInfoInvenWeight(this._target));
        }
        
        private void updateCurMp() {
            final StatusUpdate su = new StatusUpdate(this._target);
            su.addUpdate(StatusUpdateType.CUR_MP, (int)this._target.getCurrentMp());
            this._target.sendPacket(su);
        }
        
        private void grabSomeItems() {
            int grabItems = this._itemGrab;
            while (grabItems > 0 && !this._items.isEmpty()) {
                final TempItem item = this._items.get(0);
                int count = item.getQuantity();
                if (count >= grabItems) {
                    count = grabItems;
                }
                item.setQuantity(item.getQuantity() - count);
                if (item.getQuantity() <= 0) {
                    this._items.remove(0);
                }
                else {
                    this._items.set(0, item);
                }
                grabItems -= count;
                if (this._target == this._player) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2);
                    sm.addLong(count);
                    sm.addItemName(item.getItemId());
                    this._player.sendPacket(sm);
                }
                else {
                    this._target.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this._player.getName(), count, item.getItemName()));
                }
            }
        }
        
        private void calculateAltStatChange() {
            this._itemGrab = this._skillLevel;
            for (final RecipeStat altStatChange : this._recipeList.getAltStatChange()) {
                if (altStatChange.getType() == StatType.XP) {
                    this._exp = altStatChange.getValue();
                }
                else if (altStatChange.getType() == StatType.SP) {
                    this._sp = altStatChange.getValue();
                }
                else if (altStatChange.getType() == StatType.GIM) {
                    this._itemGrab *= altStatChange.getValue();
                }
            }
            this._creationPasses = this._totalItems / this._itemGrab + ((this._totalItems % this._itemGrab != 0) ? 1 : 0);
            if (this._creationPasses < 1) {
                this._creationPasses = 1;
            }
        }
        
        private boolean calculateStatUse(final boolean isWait, final boolean isReduce) {
            boolean ret = true;
            for (final RecipeStat statUse : this._recipeList.getStatUse()) {
                final double modifiedValue = statUse.getValue() / this._creationPasses;
                if (statUse.getType() == StatType.HP) {
                    if (this._player.getCurrentHp() <= modifiedValue) {
                        if (Config.ALT_GAME_CREATION && isWait) {
                            this._player.sendPacket(new SetupGauge(this._player.getObjectId(), 0, this._delay));
                            ThreadPool.schedule((Runnable)this, (long)(100 + this._delay));
                        }
                        else {
                            this._target.sendPacket(SystemMessageId.NOT_ENOUGH_HP);
                            this.abort();
                        }
                        ret = false;
                    }
                    else if (isReduce) {
                        this._player.reduceCurrentHp(modifiedValue, this._player, this._skill, DamageInfo.DamageType.OTHER);
                    }
                }
                else if (statUse.getType() == StatType.MP) {
                    if (this._player.getCurrentMp() < modifiedValue) {
                        if (Config.ALT_GAME_CREATION && isWait) {
                            this._player.sendPacket(new SetupGauge(this._player.getObjectId(), 0, this._delay));
                            ThreadPool.schedule((Runnable)this, (long)(100 + this._delay));
                        }
                        else {
                            this._target.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
                            this.abort();
                        }
                        ret = false;
                    }
                    else if (isReduce) {
                        this._player.reduceCurrentMp(modifiedValue);
                    }
                }
                else {
                    this._target.sendMessage("Recipe error!!!, please tell this to your GM.");
                    ret = false;
                    this.abort();
                }
            }
            return ret;
        }
        
        private List<TempItem> listItems(final boolean remove) {
            final Recipe[] recipes = this._recipeList.getRecipes();
            final Inventory inv = this._target.getInventory();
            final List<TempItem> materials = new ArrayList<TempItem>();
            for (final Recipe recipe : recipes) {
                if (recipe.getQuantity() > 0) {
                    final Item item = inv.getItemByItemId(recipe.getItemId());
                    final long itemQuantityAmount = (item == null) ? 0L : item.getCount();
                    if (itemQuantityAmount < recipe.getQuantity()) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_NEED_S2_MORE_S1_S);
                        sm.addItemName(recipe.getItemId());
                        sm.addLong(recipe.getQuantity() - itemQuantityAmount);
                        this._target.sendPacket(sm);
                        this.abort();
                        return null;
                    }
                    materials.add(new TempItem(item, recipe.getQuantity()));
                }
            }
            if (remove) {
                for (final TempItem tmp : materials) {
                    inv.destroyItemByItemId("Manufacture", tmp.getItemId(), tmp.getQuantity(), this._target, this._player);
                    if (tmp.getQuantity() > 1) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                        sm.addItemName(tmp.getItemId());
                        sm.addLong(tmp.getQuantity());
                        this._target.sendPacket(sm);
                    }
                    else {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                        sm.addItemName(tmp.getItemId());
                        this._target.sendPacket(sm);
                    }
                }
            }
            return materials;
        }
        
        private void abort() {
            this.updateMakeInfo(false);
            this._player.setIsCrafting(false);
            RecipeController._activeMakers.remove(this._player.getObjectId());
        }
        
        private void rewardPlayer() {
            final int rareProdId = this._recipeList.getRareItemId();
            int itemId = this._recipeList.getItemId();
            int itemCount = this._recipeList.getCount();
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
            if (Rnd.get(100) < this.getCraftCriticalRate()) {
                itemCount *= 2;
            }
            if (rareProdId != -1 && (rareProdId == itemId || Config.CRAFT_MASTERWORK) && Rnd.get(100) < this._recipeList.getRarity()) {
                itemId = rareProdId;
                itemCount = this._recipeList.getRareCount();
            }
            this._target.getInventory().addItem("Manufacture", itemId, itemCount, this._target, this._player);
            SystemMessage sm = null;
            if (this._target != this._player) {
                if (itemCount == 1) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S2_HAS_BEEN_CREATED_FOR_C1_AFTER_THE_PAYMENT_OF_S3_ADENA_WAS_RECEIVED);
                    sm.addString(this._target.getName());
                    sm.addItemName(itemId);
                    sm.addLong(this._price);
                    this._player.sendPacket(sm);
                    sm = SystemMessage.getSystemMessage(SystemMessageId.C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA);
                    sm.addString(this._player.getName());
                    sm.addItemName(itemId);
                    sm.addLong(this._price);
                    this._target.sendPacket(sm);
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S3_S2_S_HAVE_BEEN_CREATED_FOR_C1_AT_THE_PRICE_OF_S4_ADENA);
                    sm.addString(this._target.getName());
                    sm.addInt(itemCount);
                    sm.addItemName(itemId);
                    sm.addLong(this._price);
                    this._player.sendPacket(sm);
                    sm = SystemMessage.getSystemMessage(SystemMessageId.C1_CREATED_S3_S2_S_AT_THE_PRICE_OF_S4_ADENA);
                    sm.addString(this._player.getName());
                    sm.addInt(itemCount);
                    sm.addItemName(itemId);
                    sm.addLong(this._price);
                    this._target.sendPacket(sm);
                }
            }
            if (itemCount > 1) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                sm.addItemName(itemId);
                sm.addLong(itemCount);
                this._target.sendPacket(sm);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
                sm.addItemName(itemId);
                this._target.sendPacket(sm);
            }
            if (Config.ALT_GAME_CREATION) {
                final int recipeLevel = this._recipeList.getLevel();
                if (this._exp < 0L) {
                    this._exp = template.getReferencePrice() * itemCount;
                    this._exp /= recipeLevel;
                }
                if (this._sp < 0L) {
                    this._sp = this._exp / 10L;
                }
                if (itemId == rareProdId) {
                    this._exp *= (long)Config.ALT_GAME_CREATION_RARE_XPSP_RATE;
                    this._sp *= (long)Config.ALT_GAME_CREATION_RARE_XPSP_RATE;
                }
                if (this._exp < 0L) {
                    this._exp = 0L;
                }
                if (this._sp < 0L) {
                    this._sp = 0L;
                }
                for (int i = this._skillLevel; i > recipeLevel; --i) {
                    this._exp /= 4L;
                    this._sp /= 4L;
                }
                this._player.addExpAndSp((int)this._player.getStats().getValue(Stat.EXPSP_RATE, this._exp * Config.ALT_GAME_CREATION_XP_RATE * Config.ALT_GAME_CREATION_SPEED), (int)this._player.getStats().getValue(Stat.EXPSP_RATE, this._sp * Config.ALT_GAME_CREATION_SP_RATE * Config.ALT_GAME_CREATION_SPEED));
            }
            this.updateMakeInfo(true);
        }
        
        static {
            LOGGER = LoggerFactory.getLogger((Class)RecipeItemMaker.class);
        }
    }
    
    private static class Singleton
    {
        private static final RecipeController INSTANCE;
        
        static {
            INSTANCE = new RecipeController();
        }
    }
}
