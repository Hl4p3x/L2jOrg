// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.beautyshop.BeautyData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ExResponseBeautyList;
import org.l2j.gameserver.network.serverpackets.ExResponseBeautyRegistReset;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.network.GameClient;

public class RequestRegistBeauty extends ClientPacket
{
    private int _hairId;
    private int _faceId;
    private int _colorId;
    
    public void readImpl() {
        this._hairId = this.readInt();
        this._faceId = this.readInt();
        this._colorId = this.readInt();
    }
    
    public void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        if (player == null) {
            return;
        }
        final BeautyData beautyData = BeautyShopData.getInstance().getBeautyData(player.getRace(), player.getAppearance().getSexType());
        int requiredAdena = 0;
        int requiredBeautyShopTicket = 0;
        if (this._hairId > 0) {
            final BeautyItem hair = beautyData.getHairList().get(this._hairId);
            if (hair == null) {
                player.sendPacket(new ExResponseBeautyRegistReset(player, 0, 0));
                player.sendPacket(new ExResponseBeautyList(player, 1));
                return;
            }
            if (hair.getId() != player.getVisualHair()) {
                requiredAdena += hair.getAdena();
                requiredBeautyShopTicket += hair.getBeautyShopTicket();
            }
            if (this._colorId > 0) {
                final BeautyItem color = hair.getColors().get(this._colorId);
                if (color == null) {
                    player.sendPacket(new ExResponseBeautyRegistReset(player, 0, 0));
                    player.sendPacket(new ExResponseBeautyList(player, 1));
                    return;
                }
                requiredAdena += color.getAdena();
                requiredBeautyShopTicket += color.getBeautyShopTicket();
            }
        }
        if (this._faceId > 0 && this._faceId != player.getVisualFace()) {
            final BeautyItem face = beautyData.getFaceList().get(this._faceId);
            if (face == null) {
                player.sendPacket(new ExResponseBeautyRegistReset(player, 0, 0));
                player.sendPacket(new ExResponseBeautyList(player, 1));
                return;
            }
            requiredAdena += face.getAdena();
            requiredBeautyShopTicket += face.getBeautyShopTicket();
        }
        if (player.getAdena() < requiredAdena || player.getBeautyTickets() < requiredBeautyShopTicket) {
            player.sendPacket(new ExResponseBeautyRegistReset(player, 0, 0));
            player.sendPacket(new ExResponseBeautyList(player, 1));
            return;
        }
        if (requiredAdena > 0 && !player.reduceAdena(this.getClass().getSimpleName(), requiredAdena, null, true)) {
            player.sendPacket(new ExResponseBeautyRegistReset(player, 0, 0));
            player.sendPacket(new ExResponseBeautyList(player, 1));
            return;
        }
        if (requiredBeautyShopTicket > 0 && !player.reduceBeautyTickets(this.getClass().getSimpleName(), requiredBeautyShopTicket, null, true)) {
            player.sendPacket(new ExResponseBeautyRegistReset(player, 0, 0));
            player.sendPacket(new ExResponseBeautyList(player, 1));
            return;
        }
        if (this._hairId > 0) {
            player.setVisualHair(this._hairId);
        }
        if (this._colorId > 0) {
            player.setVisualHairColor(this._colorId);
        }
        if (this._faceId > 0) {
            player.setVisualFace(this._faceId);
        }
        player.sendPacket(new ExResponseBeautyRegistReset(player, 0, 1));
    }
}
