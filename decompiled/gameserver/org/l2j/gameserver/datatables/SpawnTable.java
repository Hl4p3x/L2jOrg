// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.datatables;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Consumer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.l2j.gameserver.Config;
import java.io.File;
import java.util.Collections;
import org.l2j.gameserver.model.Spawn;
import java.util.Set;
import java.util.Map;
import org.slf4j.Logger;

public final class SpawnTable
{
    private static final Logger LOGGER;
    private static final Map<Integer, Set<Spawn>> _spawnTable;
    private static final String OTHER_XML_FOLDER = "data/spawns/Others";
    
    private SpawnTable() {
    }
    
    public Map<Integer, Set<Spawn>> getSpawnTable() {
        return SpawnTable._spawnTable;
    }
    
    public Set<Spawn> getSpawns(final int npcId) {
        return SpawnTable._spawnTable.getOrDefault(npcId, Collections.emptySet());
    }
    
    public int getSpawnCount(final int npcId) {
        return this.getSpawns(npcId).size();
    }
    
    public Spawn getAnySpawn(final int npcId) {
        return this.getSpawns(npcId).stream().findFirst().orElse(null);
    }
    
    public synchronized void addNewSpawn(final Spawn spawn, final boolean store) {
        this.addSpawn(spawn);
        if (store) {
            final File outputDirectory = new File("data/spawns/Others");
            if (!outputDirectory.exists()) {
                boolean result = false;
                try {
                    outputDirectory.mkdir();
                    result = true;
                }
                catch (SecurityException ex) {}
                if (result) {
                    SpawnTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName()));
                }
            }
            final int x = (spawn.getX() + 294912 >> 15) + 11;
            final int y = (spawn.getY() + 262144 >> 15) + 10;
            final File spawnFile = new File(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, x, y));
            final String spawnId = String.valueOf(spawn.getId());
            final String spawnCount = String.valueOf(spawn.getAmount());
            final String spawnX = String.valueOf(spawn.getX());
            final String spawnY = String.valueOf(spawn.getY());
            final String spawnZ = String.valueOf(spawn.getZ());
            final String spawnHeading = String.valueOf(spawn.getHeading());
            final String spawnDelay = String.valueOf(spawn.getRespawnDelay() / 1000);
            if (spawnFile.exists()) {
                final File tempFile = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, spawnFile.getAbsolutePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/')));
                try {
                    final BufferedReader reader = new BufferedReader(new FileReader(spawnFile));
                    try {
                        final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                        try {
                            String currentLine;
                            while ((currentLine = reader.readLine()) != null) {
                                if (currentLine.contains("</group>")) {
                                    writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, spawnId, (spawn.getAmount() > 1) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, spawnCount) : "", spawnX, spawnY, spawnZ, (spawn.getHeading() > 0) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, spawnHeading) : "", spawnDelay, NpcData.getInstance().getTemplate(spawn.getId()).getName(), System.lineSeparator()));
                                    writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currentLine, System.lineSeparator()));
                                }
                                else {
                                    writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currentLine, System.lineSeparator()));
                                }
                            }
                            spawnFile.delete();
                            tempFile.renameTo(spawnFile);
                            writer.close();
                        }
                        catch (Throwable t) {
                            try {
                                writer.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                            throw t;
                        }
                        reader.close();
                    }
                    catch (Throwable t2) {
                        try {
                            reader.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                        throw t2;
                    }
                }
                catch (Exception e) {
                    SpawnTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
                }
            }
            else {
                try {
                    final BufferedWriter writer2 = new BufferedWriter(new FileWriter(spawnFile));
                    writer2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    writer2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    writer2.write(invokedynamic(makeConcatWithConstants:(IILjava/lang/String;)Ljava/lang/String;, x, y, System.lineSeparator()));
                    writer2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    writer2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, spawnId, (spawn.getAmount() > 1) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, spawnCount) : "", spawnX, spawnY, spawnZ, (spawn.getHeading() > 0) ? invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, spawnHeading) : "", spawnDelay, NpcData.getInstance().getTemplate(spawn.getId()).getName(), System.lineSeparator()));
                    writer2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    writer2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    writer2.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator()));
                    writer2.close();
                    SpawnTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), x, y));
                }
                catch (Exception e2) {
                    SpawnTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/Spawn;Ljava/lang/Exception;)Ljava/lang/String;, spawn, e2));
                }
            }
        }
    }
    
    public synchronized void deleteSpawn(final Spawn spawn, final boolean update) {
        if (!this.removeSpawn(spawn)) {
            return;
        }
        if (update) {
            final int x = (spawn.getX() + 294912 >> 15) + 11;
            final int y = (spawn.getY() + 262144 >> 15) + 10;
            final File spawnFile = new File(Objects.nonNull(spawn.getNpcSpawnTemplate()) ? spawn.getNpcSpawnTemplate().getSpawnTemplate().getFilePath() : invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, x, y));
            final File tempFile = new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, spawnFile.getAbsolutePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/')));
            try {
                final BufferedReader reader = new BufferedReader(new FileReader(spawnFile));
                try {
                    final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                    try {
                        final String spawnId = String.valueOf(spawn.getId());
                        final String spawnX = String.valueOf(spawn.getX());
                        final String spawnY = String.valueOf(spawn.getY());
                        final String spawnZ = String.valueOf(spawn.getZ());
                        boolean found = false;
                        boolean isMultiLine = false;
                        boolean lastLineFound = false;
                        int lineCount = 0;
                        String currentLine;
                        while ((currentLine = reader.readLine()) != null) {
                            if (!found) {
                                if (isMultiLine) {
                                    if (currentLine.contains("</npc>")) {
                                        found = true;
                                        continue;
                                    }
                                    continue;
                                }
                                else if (currentLine.contains(spawnId) && currentLine.contains(spawnX) && currentLine.contains(spawnY) && currentLine.contains(spawnZ)) {
                                    if (!currentLine.contains("/>") && !currentLine.contains("</npc>")) {
                                        isMultiLine = true;
                                        continue;
                                    }
                                    found = true;
                                    continue;
                                }
                            }
                            writer.write(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, currentLine, System.lineSeparator()));
                            if (currentLine.contains("</list>")) {
                                lastLineFound = true;
                            }
                            if (!lastLineFound) {
                                ++lineCount;
                            }
                        }
                        spawnFile.delete();
                        tempFile.renameTo(spawnFile);
                        if (lineCount < 7) {
                            SpawnTable.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getClass().getSimpleName(), spawnFile.getAbsolutePath().substring(Config.DATAPACK_ROOT.getAbsolutePath().length() + 1).replace('\\', '/')));
                            spawnFile.delete();
                        }
                        writer.close();
                    }
                    catch (Throwable t) {
                        try {
                            writer.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                        throw t;
                    }
                    reader.close();
                }
                catch (Throwable t2) {
                    try {
                        reader.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                SpawnTable.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/Spawn;Ljava/lang/Exception;)Ljava/lang/String;, spawn, e));
            }
        }
    }
    
    private void addSpawn(final Spawn spawn) {
        SpawnTable._spawnTable.computeIfAbsent(Integer.valueOf(spawn.getId()), k -> ConcurrentHashMap.newKeySet(1)).add(spawn);
    }
    
    private boolean removeSpawn(final Spawn spawn) {
        final Set<Spawn> set = SpawnTable._spawnTable.get(spawn.getId());
        if (set != null) {
            final boolean removed = set.remove(spawn);
            if (set.isEmpty()) {
                SpawnTable._spawnTable.remove(spawn.getId());
            }
            set.forEach(this::notifyRemoved);
            return removed;
        }
        this.notifyRemoved(spawn);
        return false;
    }
    
    private void notifyRemoved(final Spawn spawn) {
        if (spawn != null && spawn.getLastSpawn() != null && spawn.getNpcSpawnTemplate() != null) {
            spawn.getNpcSpawnTemplate().notifyDespawnNpc(spawn.getLastSpawn());
        }
    }
    
    public boolean forEachSpawn(final Function<Spawn, Boolean> function) {
        for (final Set<Spawn> set : SpawnTable._spawnTable.values()) {
            for (final Spawn spawn : set) {
                if (!function.apply(spawn)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static SpawnTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SpawnTable.class);
        _spawnTable = new ConcurrentHashMap<Integer, Set<Spawn>>();
    }
    
    private static class Singleton
    {
        private static final SpawnTable INSTANCE;
        
        static {
            INSTANCE = new SpawnTable();
        }
    }
}
