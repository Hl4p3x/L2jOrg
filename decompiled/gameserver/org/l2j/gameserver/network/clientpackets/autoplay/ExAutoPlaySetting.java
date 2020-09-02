// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.autoplay;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import java.util.Objects;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExAutoPlaySetting extends ClientPacket
{
    private short size;
    private boolean active;
    private boolean pickUp;
    private short nextTargetMode;
    private boolean isNearTarget;
    private int usableHpPotionPercent;
    private boolean respectfulHunt;
    private int usableHpPetPotionPercent;
    
    @Override
    protected void readImpl() throws Exception {
        this.size = this.readShort();
        this.active = this.readBoolean();
        this.pickUp = this.readBoolean();
        this.nextTargetMode = this.readShort();
        this.isNearTarget = this.readBoolean();
        this.usableHpPotionPercent = this.readInt();
        this.usableHpPetPotionPercent = this.readInt();
        this.respectfulHunt = this.readBoolean();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        AutoPlaySettings settings = player.getAutoPlaySettings();
        if (Objects.isNull(settings)) {
            settings = new AutoPlaySettings(this.size, this.active, this.pickUp, this.nextTargetMode, this.isNearTarget, this.usableHpPotionPercent, this.usableHpPetPotionPercent, this.respectfulHunt);
            player.setAutoPlaySettings(settings);
        }
        else {
            settings.setSize(this.size);
            settings.setActive(this.active);
            settings.setAutoPickUpOn(this.pickUp);
            settings.setNextTargetMode(this.nextTargetMode);
            settings.setNearTarget(this.isNearTarget);
            settings.setUsableHpPotionPercent(this.usableHpPotionPercent);
            settings.setUsableHpPetPotionPercent(this.usableHpPetPotionPercent);
            settings.setRespectfulHunt(this.respectfulHunt);
        }
        if (this.active) {
            AutoPlayEngine.getInstance().startAutoPlay(((GameClient)this.client).getPlayer());
        }
        else {
            AutoPlayEngine.getInstance().stopAutoPlay(((GameClient)this.client).getPlayer());
        }
    }
}
