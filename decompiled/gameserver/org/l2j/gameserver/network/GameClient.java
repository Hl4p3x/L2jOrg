// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordCheck;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordVerify;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;
import java.security.NoSuchAlgorithmException;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAck;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.vip.ReceiveVipInfo;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.actor.instance.PlayerFactory;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.enums.CharacterDeleteFailType;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.data.database.dao.AccountDAO;
import org.l2j.gameserver.network.serverpackets.LeaveWorld;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerLogout;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import java.util.Objects;
import io.github.joealisson.mmocore.Buffer;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.database.dao.ItemDAO;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.database.data.AccountData;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.network.SessionKey;
import org.l2j.gameserver.util.FloodProtectors;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import io.github.joealisson.mmocore.Connection;
import io.github.joealisson.mmocore.Client;

public final class GameClient extends Client<Connection<GameClient>>
{
    protected static final Logger LOGGER;
    protected static final Logger LOGGER_ACCOUNTING;
    private final ReentrantLock activeCharLock;
    private final FloodProtectors floodProtectors;
    private final Crypt crypt;
    private String accountName;
    private SessionKey sessionId;
    private Player player;
    private ClientHardwareInfoHolder hardwareInfo;
    private boolean isAuthedGG;
    private CharSelectInfoPackage[] charSlotMapping;
    private volatile boolean isDetached;
    private boolean _protocol;
    private int[][] trace;
    private ConnectionState state;
    private AccountData account;
    private boolean secondaryAuthed;
    
    public GameClient(final Connection<GameClient> connection) {
        super((Connection)connection);
        this.activeCharLock = new ReentrantLock();
        this.floodProtectors = new FloodProtectors(this);
        this.charSlotMapping = null;
        this.isDetached = false;
        this.crypt = new Crypt();
    }
    
    public static void deleteCharByObjId(final int objId) {
        if (objId < 0) {
            return;
        }
        PlayerNameTable.getInstance().removeName(objId);
        ((PetDAO)DatabaseAccess.getDAO((Class)PetDAO.class)).deleteByOwner(objId);
        final ItemDAO itemDAO = (ItemDAO)DatabaseAccess.getDAO((Class)ItemDAO.class);
        itemDAO.deleteVariationsByOwner(objId);
        itemDAO.deleteSpecialAbilitiesByOwner(objId);
        itemDAO.deleteByOwner(objId);
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).deleteById(objId);
    }
    
    public boolean encrypt(final Buffer data, final int offset, final int size) {
        return this.crypt.encrypt(data, offset, size);
    }
    
    public boolean decrypt(final Buffer data, final int offset, final int size) {
        return this.crypt.decrypt(data, offset, size);
    }
    
    protected void onDisconnection() {
        GameClient.LOGGER_ACCOUNTING.debug("Client Disconnected: {}", (Object)this);
        if (Objects.nonNull(this.getAccountName())) {
            if (this.state == ConnectionState.AUTHENTICATED) {
                AuthServerCommunication.getInstance().removeAuthedClient(this.getAccountName());
            }
            else {
                AuthServerCommunication.getInstance().removeWaitingClient(this.getAccountName());
            }
            AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(this.getAccountName()));
        }
        if (this.player == null || !this.player.isInOfflineMode()) {
            Disconnection.of(this).onDisconnection();
        }
    }
    
    public void onConnected() {
        this.setConnectionState(ConnectionState.CONNECTED);
        GameClient.LOGGER_ACCOUNTING.debug("Client Connected: {}", (Object)this);
    }
    
    public void closeNow() {
        super.close((WritablePacket)null);
    }
    
    public void close(final ServerPacket packet) {
        super.close((WritablePacket)packet);
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
        return this.accountName;
    }
    
    public synchronized void setAccountName(final String accountName) {
        this.accountName = accountName;
        this.account = ((AccountDAO)DatabaseAccess.getDAO((Class)AccountDAO.class)).findById(this.accountName);
        if (Objects.isNull(this.account)) {
            this.createNewAccountData();
        }
    }
    
    public SessionKey getSessionId() {
        return this.sessionId;
    }
    
    public void setSessionId(final SessionKey sk) {
        this.sessionId = sk;
    }
    
    public void sendPacket(final ServerPacket packet) {
        if (this.isDetached || Objects.isNull(packet)) {
            return;
        }
        this.writePacket((WritablePacket)packet);
        packet.runImpl(this.player);
    }
    
    public void sendPacket(final SystemMessageId smId) {
        this.sendPacket(SystemMessage.getSystemMessage(smId));
    }
    
    public boolean isDetached() {
        return this.isDetached;
    }
    
    public void setDetached(final boolean b) {
        this.isDetached = b;
    }
    
    public CharacterDeleteFailType markToDeleteChar(final int characterSlot) {
        final int objectId = this.getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return CharacterDeleteFailType.UNKNOWN;
        }
        if (MentorManager.getInstance().isMentor(objectId)) {
            return CharacterDeleteFailType.MENTOR;
        }
        if (MentorManager.getInstance().isMentee(objectId)) {
            return CharacterDeleteFailType.MENTEE;
        }
        if (CommissionManager.getInstance().hasCommissionItems(objectId)) {
            return CharacterDeleteFailType.COMMISSION;
        }
        if (MailEngine.getInstance().hasMailInProgress(objectId)) {
            return CharacterDeleteFailType.MAIL;
        }
        final int clanId = PlayerNameTable.getInstance().getClassIdById(objectId);
        if (clanId > 0) {
            final Clan clan = ClanTable.getInstance().getClan(clanId);
            if (clan != null) {
                if (clan.getLeaderId() == objectId) {
                    return CharacterDeleteFailType.PLEDGE_MASTER;
                }
                return CharacterDeleteFailType.PLEDGE_MEMBER;
            }
        }
        if (Config.DELETE_DAYS == 0) {
            deleteCharByObjId(objectId);
        }
        else {
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateDeleteTime(objectId, System.currentTimeMillis() + Config.DELETE_DAYS * 86400000);
        }
        GameClient.LOGGER_ACCOUNTING.info(invokedynamic(makeConcatWithConstants:(ILorg/l2j/gameserver/network/GameClient;)Ljava/lang/String;, objectId, this));
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
    
    public Player load(final int characterSlot) {
        final int objectId = this.getObjectIdForSlot(characterSlot);
        if (objectId < 0) {
            return null;
        }
        Player player = World.getInstance().findPlayer(objectId);
        if (player != null) {
            if (player.isOnlineInt() == 1) {
                GameClient.LOGGER.error("Attempt of double login: {} ({}) {}", new Object[] { player.getName(), objectId, this.accountName });
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
            GameClient.LOGGER.error("Could not restore in slot: {}", (Object)characterSlot);
        }
        return player;
    }
    
    public void setCharSelection(final CharSelectInfoPackage[] chars) {
        this.charSlotMapping = chars;
    }
    
    public CharSelectInfoPackage getCharSelection(final int charslot) {
        if (Objects.isNull(this.charSlotMapping) || charslot < 0 || charslot >= this.charSlotMapping.length) {
            return null;
        }
        return this.charSlotMapping[charslot];
    }
    
    private int getObjectIdForSlot(final int characterSlot) {
        final CharSelectInfoPackage info = this.getCharSelection(characterSlot);
        if (info == null) {
            GameClient.LOGGER.warn("{} tried to delete Character in slot {} but no characters exits at that slot.", (Object)this, (Object)characterSlot);
            return -1;
        }
        return info.getObjectId();
    }
    
    private AccountData getAccountData() {
        return this.account;
    }
    
    private void createNewAccountData() {
        (this.account = new AccountData()).setAccount(this.accountName);
    }
    
    public void sendActionFailed() {
        this.sendPacket(ActionFailed.STATIC_PACKET);
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
    
    public long getVipPoints() {
        return this.getAccountData().getVipPoints();
    }
    
    public long getVipTierExpiration() {
        return this.getAccountData().getVipTierExpiration();
    }
    
    public void storeAccountData() {
        ((AccountDAO)DatabaseAccess.getDAO((Class)AccountDAO.class)).save((Object)this.getAccountData());
    }
    
    public void updateVipPoints(final long points) {
        if (points == 0L) {
            return;
        }
        final byte currentVipTier = VipEngine.getInstance().getVipTier(this.getVipPoints());
        this.getAccountData().updateVipPoints(points);
        final byte newTier = VipEngine.getInstance().getVipTier(this.getVipPoints());
        if (newTier != currentVipTier && Objects.nonNull(this.player)) {
            this.player.setVipTier(newTier);
            if (newTier > 0) {
                this.getAccountData().setVipTierExpiration(Instant.now().plus(30L, (TemporalUnit)ChronoUnit.DAYS).toEpochMilli());
                VipEngine.getInstance().manageTier(this.player);
            }
            else {
                this.getAccountData().setVipTierExpiration(0L);
            }
        }
        this.sendPacket(new ReceiveVipInfo());
    }
    
    public int getCoin() {
        return this.getAccountData().getCoin();
    }
    
    public void updateCoin(final int coins) {
        this.getAccountData().updateCoins(coins);
    }
    
    public void setCoin(final int coins) {
        this.getAccountData().setCoins(coins);
    }
    
    public void setVipTierExpiration(final long expiration) {
        this.getAccountData().setVipTierExpiration(expiration);
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
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.accountName, (address == null) ? "disconnected" : address);
                    break;
                }
                case IN_GAME:
                case JOINING_GAME: {
                    s = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, (this.player == null) ? "disconnected" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.player.getName(), this.player.getObjectId()), this.accountName, (address == null) ? "disconnected" : address);
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
                    this.close(new Ex2ndPasswordVerify(2, SecondaryAuthManager.getInstance().getMaxAttempts()));
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
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GameClient.class);
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
    }
}
