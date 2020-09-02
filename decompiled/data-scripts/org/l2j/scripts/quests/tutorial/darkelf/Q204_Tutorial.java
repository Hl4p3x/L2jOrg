// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.quests.tutorial.darkelf;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.quests.tutorial.Tutorial;

public class Q204_Tutorial extends Tutorial
{
    private static final int SUPERVISOR = 30129;
    private static final int NEWBIE_HELPER = 30131;
    private static final Location HELPER_LOCATION;
    private static final Location VILLAGE_LOCATION;
    private static final QuestSoundHtmlHolder STARTING_VOICE_HTML;
    
    public Q204_Tutorial() {
        super(204, new ClassId[] { ClassId.DARK_FIGHTER, ClassId.DARK_MAGE });
        this.addFirstTalkId(30129);
    }
    
    @Override
    protected int newbieHelperId() {
        return 30131;
    }
    
    @Override
    protected ILocational villageLocation() {
        return (ILocational)Q204_Tutorial.VILLAGE_LOCATION;
    }
    
    @Override
    protected QuestSoundHtmlHolder startingVoiceHtml() {
        return Q204_Tutorial.STARTING_VOICE_HTML;
    }
    
    @Override
    protected Location helperLocation() {
        return Q204_Tutorial.HELPER_LOCATION;
    }
    
    static {
        HELPER_LOCATION = new Location(28384, 11056, -4233);
        VILLAGE_LOCATION = new Location(12161, 16674, -4584, 60030);
        STARTING_VOICE_HTML = new QuestSoundHtmlHolder("tutorial_voice_001e", "main.html");
    }
}
