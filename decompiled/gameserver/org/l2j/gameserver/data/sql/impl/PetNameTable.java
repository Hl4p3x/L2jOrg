// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.sql.impl;

import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import org.l2j.gameserver.Config;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.slf4j.Logger;

public class PetNameTable
{
    private static final Logger LOGGER;
    
    private PetNameTable() {
    }
    
    public boolean doesPetNameExist(final String name, final int petNpcId) {
        return ((PetDAO)DatabaseAccess.getDAO((Class)PetDAO.class)).existsPetName(name, PetDataTable.getInstance().getPetItemByNpc(petNpcId));
    }
    
    public boolean isValidPetName(final String name) {
        boolean result = true;
        if (!Util.isAlphaNumeric(name)) {
            return result;
        }
        Pattern pattern;
        try {
            pattern = Pattern.compile(Config.PET_NAME_TEMPLATE);
        }
        catch (PatternSyntaxException e) {
            PetNameTable.LOGGER.warn(": Pet name pattern of config is wrong!");
            pattern = Pattern.compile(".*");
        }
        final Matcher regexp = pattern.matcher(name);
        if (!regexp.matches()) {
            result = false;
        }
        return result;
    }
    
    public static PetNameTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PetNameTable.class);
    }
    
    private static class Singleton
    {
        private static final PetNameTable INSTANCE;
        
        static {
            INSTANCE = new PetNameTable();
        }
    }
}
