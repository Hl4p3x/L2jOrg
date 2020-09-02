// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial.mage;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.quests.tutorial.Tutorial;

public class Q202_Tutorial extends Tutorial
{
    private static final int SUPERVISOR = 30017;
    private static final int NEWBIE_HELPER = 30019;
    private static final Location HELPER_LOCATION;
    private static final Location VILLAGE_LOCATION;
    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML;
    
    public Q202_Tutorial() {
        super(202, new ClassId[] { ClassId.MAGE });
        this.addFirstTalkId(30017);
    }
    
    @Override
    protected int newbieHelperId() {
        return 30019;
    }
    
    @Override
    protected ILocational villageLocation() {
        return (ILocational)Q202_Tutorial.VILLAGE_LOCATION;
    }
    
    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return Q202_Tutorial.STARTING_VOICE_HTML;
    }
    
    @Override
    protected Location helperLocation() {
        return Q202_Tutorial.HELPER_LOCATION;
    }
    
    static {
        HELPER_LOCATION = new Location(-91036, 248044, -3568);
        VILLAGE_LOCATION = new Location(-84046, 243283, -3728, 18316);
        STARTING_VOICE_HTML = new QuestSoundHtmlHolder("tutorial_voice_001b", "main.html");
    }
}
