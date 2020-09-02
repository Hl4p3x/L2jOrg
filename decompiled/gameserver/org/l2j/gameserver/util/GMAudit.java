// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.FileWriter;
import java.io.File;
import org.l2j.commons.util.CommonUtil;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.slf4j.Logger;

public class GMAudit
{
    private static final Logger LOGGER;
    
    public static void auditGMAction(final String gmName, final String action, final String target, final String params) {
        final SimpleDateFormat _formatter = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
        final String date = _formatter.format(new Date());
        String name = CommonUtil.replaceIllegalCharacters(gmName);
        if (!CommonUtil.isValidFileName(name)) {
            name = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, date);
        }
        final File file = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name));
        try {
            final FileWriter save = new FileWriter(file, true);
            try {
                save.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, date, gmName, action, target, params, System.lineSeparator()));
                save.close();
            }
            catch (Throwable t) {
                try {
                    save.close();
                }
                catch (Throwable exception) {
                    t.addSuppressed(exception);
                }
                throw t;
            }
        }
        catch (IOException e) {
            GMAudit.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, gmName), (Throwable)e);
        }
    }
    
    public static void auditGMAction(final String gmName, final String action, final String target) {
        auditGMAction(gmName, action, target, "");
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GMAudit.class);
        new File("log/GMAudit").mkdirs();
    }
}
