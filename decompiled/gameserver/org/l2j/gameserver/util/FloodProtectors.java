// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.GameClient;

public final class FloodProtectors
{
    private final FloodProtectorAction _useItem;
    private final FloodProtectorAction _rollDice;
    private final FloodProtectorAction _firework;
    private final FloodProtectorAction _itemPetSummon;
    private final FloodProtectorAction _heroVoice;
    private final FloodProtectorAction _globalChat;
    private final FloodProtectorAction _subclass;
    private final FloodProtectorAction _dropItem;
    private final FloodProtectorAction _serverBypass;
    private final FloodProtectorAction _multiSell;
    private final FloodProtectorAction _transaction;
    private final FloodProtectorAction _manufacture;
    private final FloodProtectorAction _manor;
    private final FloodProtectorAction _sendMail;
    private final FloodProtectorAction _characterSelect;
    private final FloodProtectorAction _itemAuction;
    
    public FloodProtectors(final GameClient client) {
        this._useItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_USE_ITEM);
        this._rollDice = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ROLL_DICE);
        this._firework = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_FIREWORK);
        this._itemPetSummon = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ITEM_PET_SUMMON);
        this._heroVoice = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_HERO_VOICE);
        this._globalChat = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_GLOBAL_CHAT);
        this._subclass = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SUBCLASS);
        this._dropItem = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_DROP_ITEM);
        this._serverBypass = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SERVER_BYPASS);
        this._multiSell = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_MULTISELL);
        this._transaction = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_TRANSACTION);
        this._manufacture = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_MANUFACTURE);
        this._manor = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_MANOR);
        this._sendMail = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_SENDMAIL);
        this._characterSelect = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_CHARACTER_SELECT);
        this._itemAuction = new FloodProtectorAction(client, Config.FLOOD_PROTECTOR_ITEM_AUCTION);
    }
    
    public FloodProtectorAction getUseItem() {
        return this._useItem;
    }
    
    public FloodProtectorAction getRollDice() {
        return this._rollDice;
    }
    
    public FloodProtectorAction getFirework() {
        return this._firework;
    }
    
    public FloodProtectorAction getItemPetSummon() {
        return this._itemPetSummon;
    }
    
    public FloodProtectorAction getHeroVoice() {
        return this._heroVoice;
    }
    
    public FloodProtectorAction getGlobalChat() {
        return this._globalChat;
    }
    
    public FloodProtectorAction getSubclass() {
        return this._subclass;
    }
    
    public FloodProtectorAction getDropItem() {
        return this._dropItem;
    }
    
    public FloodProtectorAction getServerBypass() {
        return this._serverBypass;
    }
    
    public FloodProtectorAction getMultiSell() {
        return this._multiSell;
    }
    
    public FloodProtectorAction getTransaction() {
        return this._transaction;
    }
    
    public FloodProtectorAction getManufacture() {
        return this._manufacture;
    }
    
    public FloodProtectorAction getManor() {
        return this._manor;
    }
    
    public FloodProtectorAction getSendMail() {
        return this._sendMail;
    }
    
    public FloodProtectorAction getCharacterSelect() {
        return this._characterSelect;
    }
    
    public FloodProtectorAction getItemAuction() {
        return this._itemAuction;
    }
}
