// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordCheck;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordVerify;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import java.security.NoSuchAlgorithmException;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAck;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import java.time.Duration;
import org.l2j.gameserver.model.actor.instance.PlayerFactory;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.AccountDAO;
import org.l2j.gameserver.network.serverpackets.LeaveWorld;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import java.util.Objects;
import io.github.joealisson.mmocore.Buffer;
import org.l2j.gameserver.model.PlayerSelectInfo;
import java.util.List;
import org.l2j.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.database.data.AccountData;
import org.l2j.commons.network.SessionKey;
import org.l2j.gameserver.util.FloodProtectors;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import io.github.joealisson.mmocore.Connection;
import io.github.joealisson.mmocore.Client;

public final class GameClient extends Client<Connection<GameClient>>
{
    private static final Logger LOGGER;
    private static final Logger LOGGER_ACCOUNTING;
    private final ReentrantLock activeCharLock;
    private final FloodProtectors floodProtectors;
    private final Crypt crypt;
    private SessionKey sessionId;
    private ConnectionState state;
    private AccountData account;
    private Player player;
    private ClientHardwareInfoHolder hardwareInfo;
    private List<PlayerSelectInfo> playersInfo;
    private boolean isAuthedGG;
    private boolean _protocol;
    private int[][] trace;
    private boolean secondaryAuthed;
    private int activeSlot;
    
    public GameClient(final Connection<GameClient> connection) {
        super((Connection)connection);
        this.activeCharLock = new ReentrantLock();
        this.floodProtectors = new FloodProtectors(this);
        this.activeSlot = -1;
        this.crypt = new Crypt();
    }
    
    public boolean encrypt(final Buffer data, final int offset, final int size) {
        return this.crypt.encrypt(data, offset, size);
    }
    
    public boolean decrypt(final Buffer data, final int offset, final int size) {
        return this.crypt.decrypt(data, offset, size);
    }
    
    protected void onDisconnection() {
        GameClient.LOGGER_ACCOUNTING.debug("Client Disconnected: {}", (Object)this);
        if (Objects.nonNull(this.account)) {
            if (this.state == ConnectionState.AUTHENTICATED) {
                AuthServerCommunication.getInstance().removeAuthedClient(this.account.getAccountName());
            }
            else {
                AuthServerCommunication.getInstance().removeWaitingClient(this.account.getAccountName());
            }
            AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(this.account.getAccountName()));
        }
        Disconnection.of(this).onDisconnection();
    }
    
    public void onConnected() {
        this.setConnectionState(ConnectionState.CONNECTED);
        GameClient.LOGGER_ACCOUNTING.debug("Client Connected: {}", (Object)this);
    }
    
    public void close(final boolean toLoginScreen) {
        this.sendPacket(toLoginScreen ? ServerClose.STATIC_PACKET : LeaveWorld.STATIC_PACKET);
    }
    
    public byte[] enableCrypt() {
        final byte[] key = BlowFishKeygen.getRandomKey();
        this.crypt.setKey(key);
        return key;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void setPlayer(final Player player) {
        this.player = player;
    }
    
    public ReentrantLock getActivePlayerLock() {
        return this.activeCharLock;
    }
    
    public FloodProtectors getFloodProtectors() {
        return this.floodProtectors;
    }
    
    public void setGameGuardOk(final boolean val) {
        this.isAuthedGG = val;
    }
    
    public String getAccountName() {
        return this.account.getAccountName();
    }
    
    public synchronized void loadAccount(final String accountName) {
        this.account = ((AccountDAO)DatabaseAccess.getDAO((Class)AccountDAO.class)).findById(accountName);
        if (Objects.isNull(this.account)) {
            this.account = AccountData.of(accountName);
        }
    }
    
    public SessionKey getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final SessionKey sk) {
        this.sessionId = sk;
    }
    
    public void sendPacket(final ServerPacket packet) {
        if (Objects.isNull(packet)) {
            return;
        }
        this.writePacket((WritablePacket)packet);
        packet.runImpl(this.player);
    }
    
    public void sendPacket(final SystemMessageId smId) {
        this.sendPacket(SystemMessage.getSystemMessage(smId));
    }
    
    public CharacterDeleteFailType markToDeleteChar(final int slot) {
        final PlayerSelectInfo info = this.getPlayerSelection(slot);
        if (Objects.isNull(info)) {
            return CharacterDeleteFailType.UNKNOWN;
        }
        if (CommissionManager.getInstance().hasCommissionItems(info.getObjectId())) {
            return CharacterDeleteFailType.COMMISSION;
        }
        if (MailEngine.getInstance().hasMailInProgress(info.getObjectId())) {
            return CharacterDeleteFailType.MAIL;
        }
        final int clanId = PlayerNameTable.getInstance().getClassIdById(info.getObjectId());
        if (clanId > 0) {
            final Clan clan = ClanTable.getInstance().getClan(clanId);
            if (clan != null) {
                if (clan.getLeaderId() == info.getObjectId()) {
                    return CharacterDeleteFailType.PLEDGE_MASTER;
                }
                return CharacterDeleteFailType.PLEDGE_MEMBER;
            }
        }
        if (Config.DELETE_DAYS == 0) {
            PlayerFactory.deleteCharByObjId(info.getObjectId());
            this.playersInfo.remove(slot);
        }
        else {
            final long deleteTime = Duration.ofDays(Config.DELETE_DAYS).toMillis() + System.currentTimeMillis();
            info.setDeleteTime(deleteTime);
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateDeleteTime(info.getObjectId(), deleteTime);
        }
        GameClient.LOGGER_ACCOUNTING.info("{} deleted {}", (Object)this, (Object)info.getObjectId());
        return CharacterDeleteFailType.NONE;
    }
    
    public void restore(final int characterSlot) {
        final int objectId = this.getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return;
        }
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateDeleteTime(objectId, 0L);
        GameClient.LOGGER_ACCOUNTING.info("Restore {} [{}]", (Object)objectId, (Object)this);
    }
    
    public Player load(final int slot) {
        final int objectId = this.getObjectIdForSlot(slot);
        if (objectId < 0) {
            return null;
        }
        Player player = World.getInstance().findPlayer(objectId);
        if (player != null) {
            if (player.isOnline()) {
                GameClient.LOGGER.error("Attempt of double login: {}", (Object)this);
            }
            if (player.getClient() != null) {
                Disconnection.of(player).defaultSequence(false);
            }
            else {
                player.storeMe();
                player.deleteMe();
            }
            return null;
        }
        player = PlayerFactory.loadPlayer(this, objectId);
        if (player == null) {
            GameClient.LOGGER.error("Could not restore in slot: {}", (Object)slot);
        }
        this.activeSlot = slot;
        return player;
    }
    
    public PlayerSelectInfo getPlayerSelection(final int slot) {
        if (Objects.isNull(this.playersInfo) || slot < 0 || slot >= this.playersInfo.size()) {
            return null;
        }
        return this.playersInfo.get(slot);
    }
    
    private int getObjectIdForSlot(final int slot) {
        final PlayerSelectInfo info = this.getPlayerSelection(slot);
        if (info == null) {
            GameClient.LOGGER.warn("{} tried select in slot {} but no characters exits at that slot.", (Object)this, (Object)slot);
            return -1;
        }
        return info.getObjectId();
    }
    
    public AccountData getAccount() {
        return this.account;
    }
    
    public boolean isProtocolOk() {
        return this._protocol;
    }
    
    public void setProtocolOk(final boolean b) {
        this._protocol = b;
    }
    
    public void setClientTracert(final int[][] tracert) {
        this.trace = tracert;
    }
    
    public int[][] getTrace() {
        return this.trace;
    }
    
    public ClientHardwareInfoHolder getHardwareInfo() {
        return this.hardwareInfo;
    }
    
    public void setHardwareInfo(final ClientHardwareInfoHolder hardwareInfo) {
        this.hardwareInfo = hardwareInfo;
    }
    
    public ConnectionState getConnectionState() {
        return this.state;
    }
    
    public void setConnectionState(final ConnectionState state) {
        this.state = state;
    }
    
    public void storeAccountData() {
        ((AccountDAO)DatabaseAccess.getDAO((Class)AccountDAO.class)).save((Object)this.account);
    }
    
    public String toString() {
        try {
            final String address = this.getHostAddress();
            final ConnectionState state = this.getConnectionState();
            String s = null;
            switch (state) {
                case CONNECTED:
                case CLOSING:
                case DISCONNECTED: {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, (address == null) ? "disconnected" : address);
                    break;
                }
                case AUTHENTICATED: {
                    s = invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/data/database/data/AccountData;Ljava/lang/String;)Ljava/lang/String;, this.account, (address == null) ? "disconnected" : address);
                    break;
                }
                case IN_GAME:
                case JOINING_GAME: {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Lorg/l2j/gameserver/data/database/data/AccountData;Ljava/lang/String;)Ljava/lang/String;, (this.player == null) ? "disconnected" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.player.getName(), this.player.getObjectId()), this.account, (address == null) ? "disconnected" : address);
                    break;
                }
                default: {
                    throw new IncompatibleClassChangeError();
                }
            }
            return s;
        }
        catch (NullPointerException e) {
            return "[Character read failed due to disconnect]";
        }
    }
    
    public boolean hasSecondPassword() {
        return Util.isNotEmpty(this.account.getSecAuthPassword());
    }
    
    public boolean saveSecondPassword(final String password) {
        if (this.hasSecondPassword()) {
            GameClient.LOGGER.warn("{} forced savePassword", (Object)this);
            Disconnection.of(this).defaultSequence(false);
            return false;
        }
        if (!this.validatePassword(password)) {
            this.sendPacket(new Ex2ndPasswordAck(0, Ex2ndPasswordAck.WRONG_PATTERN));
            return false;
        }
        try {
            final String cripted = Util.hash(password);
            this.account.setSecAuthPassword(cripted);
            this.account.setSecAuthAttempts(0);
            return true;
        }
        catch (NoSuchAlgorithmException e) {
            GameClient.LOGGER.error("Unsupported Algorithm", (Throwable)e);
            return false;
        }
    }
    
    private boolean validatePassword(final String password) {
        return Util.isInteger(password) && password.length() >= 6 && password.length() <= 8 && !SecondaryAuthManager.getInstance().isForbiddenPassword(Integer.parseInt(password));
    }
    
    public boolean changeSecondPassword(final String password, final String newPassword) {
        if (!this.hasSecondPassword()) {
            GameClient.LOGGER.warn("{} forced changePassword", (Object)this);
            Disconnection.of(this).defaultSequence(false);
            return false;
        }
        if (!this.checkPassword(password, true)) {
            return false;
        }
        if (!this.validatePassword(newPassword)) {
            this.sendPacket(new Ex2ndPasswordAck(2, Ex2ndPasswordAck.WRONG_PATTERN));
            return false;
        }
        try {
            this.account.setSecAuthPassword(Util.hash(newPassword));
            this.secondaryAuthed = false;
            return true;
        }
        catch (NoSuchAlgorithmException e) {
            GameClient.LOGGER.error("Unsupported Algorithm", (Throwable)e);
            return false;
        }
    }
    
    public boolean checkPassword(String password, final boolean skipAuth) {
        try {
            password = Util.hash(password);
            if (!Objects.equals(password, this.account.getSecAuthPassword())) {
                final int attempts = this.account.increaseSecAuthAttempts();
                if (attempts < SecondaryAuthManager.getInstance().getMaxAttempts()) {
                    this.sendPacket(new Ex2ndPasswordVerify(1, attempts));
                }
                else {
                    this.close((WritablePacket)new Ex2ndPasswordVerify(2, SecondaryAuthManager.getInstance().getMaxAttempts()));
                    GameClient.LOGGER.warn("{}  has inputted the wrong password {} times in row.", (Object)this, (Object)attempts);
                }
                return false;
            }
        }
        catch (NoSuchAlgorithmException e) {
            GameClient.LOGGER.error("Unsupported Algorithm", (Throwable)e);
            return false;
        }
        if (!skipAuth) {
            this.secondaryAuthed = true;
            this.sendPacket(new Ex2ndPasswordVerify(0, this.account.getSecAuthAttempts()));
        }
        this.account.setSecAuthAttempts(0);
        return true;
    }
    
    public boolean isSecondaryAuthed() {
        return this.secondaryAuthed;
    }
    
    public void openSecondaryAuthDialog() {
        if (this.hasSecondPassword()) {
            this.sendPacket(new Ex2ndPasswordCheck(1));
        }
        else {
            this.sendPacket(new Ex2ndPasswordCheck(0));
        }
    }
    
    public int getPlayerCount() {
        return Objects.nonNull(this.playersInfo) ? this.playersInfo.size() : 0;
    }
    
    public List<PlayerSelectInfo> getPlayersInfo() {
        if (Objects.isNull(this.playersInfo)) {
            synchronized (this) {
                if (Objects.isNull(this.playersInfo)) {
                    this.playersInfo = PlayerFactory.loadPlayersInfo(this);
                }
            }
        }
        return this.playersInfo;
    }
    
    public void addPlayerInfo(final PlayerSelectInfo playerInfo) {
        this.activeSlot = this.playersInfo.size();
        this.playersInfo.add(playerInfo);
    }
    
    public int getActiveSlot() {
        return this.activeSlot;
    }
    
    public int getPlayerInfoAccessLevel(final int playerId) {
        for (final PlayerSelectInfo info : this.playersInfo) {
            if (info.getObjectId() == playerId) {
                return info.getAccessLevel();
            }
        }
        throw new IllegalStateException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, playerId));
    }
    
    public void detachPlayersInfo() {
        this.playersInfo = null;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GameClient.class);
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
