// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.beautyshop.BeautyData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.ExResponseBeautyRegistReset;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.network.GameClient;

public class RequestShowResetShopList extends ClientPacket
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
        if (this._hairId > 0) {
            final BeautyItem hair = beautyData.getHairList().get(this._hairId);
            if (hair == null) {
                player.sendPacket(new ExResponseBeautyRegistReset(player, 1, 0));
                return;
            }
            requiredAdena += hair.getResetAdena();
            if (this._colorId > 0) {
                final BeautyItem color = hair.getColors().get(this._colorId);
                if (color == null) {
                    player.sendPacket(new ExResponseBeautyRegistReset(player, 1, 0));
                    return;
                }
                requiredAdena += color.getResetAdena();
            }
        }
        if (this._faceId > 0) {
            final BeautyItem face = beautyData.getFaceList().get(this._faceId);
            if (face == null) {
                player.sendPacket(new ExResponseBeautyRegistReset(player, 1, 0));
                return;
            }
            requiredAdena += face.getResetAdena();
        }
        if (player.getAdena() < requiredAdena) {
            player.sendPacket(new ExResponseBeautyRegistReset(player, 1, 0));
            return;
        }
        if (requiredAdena > 0 && !player.reduceAdena(this.getClass().getSimpleName(), requiredAdena, null, true)) {
            player.sendPacket(new ExResponseBeautyRegistReset(player, 1, 0));
            return;
        }
        player.setVisualHair(0);
        player.setVisualHairColor(0);
        player.setVisualFace(0);
        player.sendPacket(new ExResponseBeautyRegistReset(player, 1, 1));
    }
}
