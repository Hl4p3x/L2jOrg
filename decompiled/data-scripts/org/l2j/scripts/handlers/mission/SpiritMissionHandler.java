// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.mission;

import org.l2j.gameserver.engine.mission.MissionHandlerFactory;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.events.impl.character.OnElementalSpiritUpgrade;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.events.impl.character.player.OnElementalSpiritLearn;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.engine.mission.AbstractMissionHandler;

public class SpiritMissionHandler extends AbstractMissionHandler
{
    private final ElementalType type;
    
    private SpiritMissionHandler(final MissionDataHolder holder) {
        super(holder);
        this.type = (ElementalType)this.getHolder().getParams().getEnum("element", (Class)ElementalType.class, (Enum)ElementalType.NONE);
    }
    
    public void init() {
        final MissionKind kind = (MissionKind)this.getHolder().getParams().getEnum("kind", (Class)MissionKind.class, (Enum)null);
        if (MissionKind.EVOLVE == kind) {
            Listeners.players().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_ELEMENTAL_SPIRIT_UPGRADE, (Consumer)this::onElementalSpiritUpgrade, (Object)this));
        }
        else if (MissionKind.LEARN == kind) {
            Listeners.players().addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)this, EventType.ON_ELEMENTAL_SPIRIT_LEARN, (Consumer)this::onElementalSpiritLearn, (Object)this));
        }
    }
    
    private void onElementalSpiritLearn(final OnElementalSpiritLearn event) {
        final MissionPlayerData missionData = this.getPlayerEntry(event.getPlayer(), true);
        missionData.setProgress(1);
        missionData.setStatus(MissionStatus.AVAILABLE);
        this.notifyAvailablesReward(event.getPlayer());
        this.storePlayerEntry(missionData);
    }
    
    private void onElementalSpiritUpgrade(final OnElementalSpiritUpgrade event) {
        final ElementalSpirit spirit = event.getSpirit();
        if (ElementalType.of(spirit.getType()) != this.type) {
            return;
        }
        final MissionPlayerData missionData = this.getPlayerEntry(event.getPlayer(), true);
        missionData.setProgress((int)spirit.getStage());
        if (missionData.getProgress() >= this.getRequiredCompletion()) {
            missionData.setStatus(MissionStatus.AVAILABLE);
            this.notifyAvailablesReward(event.getPlayer());
        }
        this.storePlayerEntry(missionData);
    }
    
    private enum MissionKind
    {
        LEARN, 
        EVOLVE;
    }
    
    public static class Factory implements MissionHandlerFactory
    {
        public AbstractMissionHandler create(final MissionDataHolder data) {
            return new SpiritMissionHandler(data);
        }
        
        public String handlerName() {
            return "spirit";
        }
    }
}
