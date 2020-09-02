// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util.cron4j;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

class FileTaskCollector implements TaskCollector
{
    private final ArrayList<File> files;
    
    FileTaskCollector() {
        this.files = new ArrayList<File>();
    }
    
    public synchronized void addFile(final File file) {
        this.files.add(file);
    }
    
    public synchronized void removeFile(final File file) {
        this.files.remove(file);
    }
    
    public synchronized File[] getFiles() {
        final int size = this.files.size();
        final File[] ret = new File[size];
        for (int i = 0; i < size; ++i) {
            ret[i] = this.files.get(i);
        }
        return ret;
    }
    
    @Override
    public synchronized TaskTable getTasks() {
        final TaskTable ret = new TaskTable();
        for (int size = this.files.size(), i = 0; i < size; ++i) {
            final File f = this.files.get(i);
            TaskTable aux = null;
            try {
                aux = CronParser.parse(f);
            }
            catch (IOException e2) {
                final Exception e1 = new Exception(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, f.getAbsolutePath()), (Throwable)e2);
                e1.printStackTrace();
            }
            if (aux != null) {
                for (int auxSize = aux.size(), j = 0; j < auxSize; ++j) {
                    ret.add(aux.getSchedulingPattern(j), aux.getTask(j));
                }
            }
        }
        return ret;
    }
}
