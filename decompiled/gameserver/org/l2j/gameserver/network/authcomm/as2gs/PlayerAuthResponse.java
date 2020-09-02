// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.network.serverpackets.html.TutorialShowHtml;
import org.l2j.gameserver.cache.HtmCache;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PlayerInGame;
import org.l2j.gameserver.network.serverpackets.ServerClose;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.LoginFail;
import org.l2j.gameserver.network.ConnectionState;
import org.l2j.commons.network.SessionKey;
import java.util.Objects;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;

public class PlayerAuthResponse extends ReceivablePacket
{
    private String account;
    private boolean authed;
    private int gameserverSession;
    private int gameserverAccountId;
    private int authAccountId;
    private int authKey;
    private int points;
    private String hwid;
    private long phoneNumber;
    
    public void readImpl() {
        this.account = this.readString();
        this.authed = this.readBoolean();
        if (this.authed) {
            this.gameserverSession = this.readInt();
            this.gameserverAccountId = this.readInt();
            this.authAccountId = this.readInt();
            this.authKey = this.readInt();
        }
    }
    
    @Override
    protected void runImpl() {
        final GameClient client = AuthServerCommunication.getInstance().removeWaitingClient(this.account);
        if (Objects.isNull(client)) {
            return;
        }
        final SessionKey skey = new SessionKey(this.authAccountId, this.authKey, this.gameserverSession, this.gameserverAccountId);
        if (this.authed && client.getSessionId().equals((Object)skey)) {
            client.setConnectionState(ConnectionState.AUTHENTICATED);
            client.sendPacket(LoginFail.LOGIN_SUCCESS);
            final GameClient oldClient = AuthServerCommunication.getInstance().addAuthedClient(client);
            if (Objects.nonNull(oldClient)) {
                oldClient.setConnectionState(ConnectionState.DISCONNECTED);
                final Player activeChar = oldClient.getPlayer();
                if (Objects.nonNull(activeChar)) {
                    activeChar.sendPacket(SystemMessageId.YOU_ARE_LOGGED_IN_TO_TWO_PLACES_IF_YOU_SUSPECT_ACCOUNT_THEFT_WE_RECOMMEND_CHANGING_YOUR_PASSWORD_SCANNING_YOUR_COMPUTER_FOR_VIRUSES_AND_USING_AN_ANTI_VIRUS_SOFTWARE);
                    Disconnection.of(activeChar).defaultSequence(false);
                }
                else {
                    oldClient.close(ServerClose.STATIC_PACKET);
                }
            }
            this.sendPacket(new PlayerInGame(new String[] { client.getAccountName() }));
            final CharSelectionInfo charSelectionInfo = new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId());
            client.sendPacket(charSelectionInfo);
            client.setCharSelection(charSelectionInfo.getCharInfo());
        }
        else {
            client.close(new LoginFail(4));
        }
    }
    
    private boolean hasMoreClientThanLimit(final GameClient client, final int limit, final List<GameClient> clients) {
        final int activeWindows = clients.size();
        if (activeWindows >= limit) {
            String html = HtmCache.getInstance().getHtm(null, "windows_limit_ip.htm");
            if (Objects.nonNull(html)) {
                html = html.replace("<?active_windows?>", String.valueOf(activeWindows));
                html = html.replace("<?windows_limit?>", String.valueOf(limit));
                client.close(new TutorialShowHtml(html));
            }
            else {
                client.close(new LoginFail(4));
            }
            return true;
        }
        return false;
    }
}
