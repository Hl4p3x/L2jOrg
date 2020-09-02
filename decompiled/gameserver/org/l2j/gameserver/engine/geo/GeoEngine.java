// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.geo;

import org.slf4j.LoggerFactory;
import java.util.List;
import org.l2j.gameserver.engine.geo.geodata.GeoLocation;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.data.xml.FenceDataManager;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import java.nio.MappedByteBuffer;
import org.l2j.gameserver.engine.geo.geodata.BlockComplex;
import java.nio.ByteBuffer;
import org.l2j.gameserver.engine.geo.geodata.BlockFlat;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;
import org.l2j.gameserver.util.MathUtil;
import java.nio.file.Path;
import org.l2j.gameserver.engine.geo.settings.GeoEngineSettings;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import org.l2j.gameserver.engine.geo.geodata.GeoFormat;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.engine.geo.geodata.BlockMultilayer;
import org.l2j.gameserver.engine.geo.geodata.BlockNull;
import org.l2j.gameserver.engine.geo.geodata.ABlock;
import org.slf4j.Logger;

public class GeoEngine
{
    private static final Logger LOGGER;
    private static final double SIGHT_LINE_PERCENT = 0.75;
    private static final int MAX_OBSTACLE_HEIGHT = 32;
    private final ABlock[][] blocks;
    private final BlockNull nullBlock;
    
    protected GeoEngine() {
        this.blocks = new ABlock[4608][4352];
        this.nullBlock = new BlockNull();
    }
    
    protected void load() {
        BlockMultilayer.initialize();
        this.loadGeodataFiles();
        BlockMultilayer.release();
    }
    
    private void loadGeodataFiles() {
        int loaded = 0;
        final Path geodataPath = ((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).dataPackDirectory().resolve("geodata");
        for (int rx = 11; rx <= 28; ++rx) {
            for (int ry = 10; ry <= 26; ++ry) {
                final Path filePath = geodataPath.resolve(String.format(GeoFormat.L2D.getFilename(), rx, ry));
                if (Files.exists(filePath, new LinkOption[0]) && !Files.isDirectory(filePath, new LinkOption[0])) {
                    if (this.loadGeoBlocks(filePath, rx, ry)) {
                        ++loaded;
                    }
                }
                else {
                    this.loadNullBlocks(rx, ry);
                }
            }
        }
        GeoEngine.LOGGER.info("Loaded {} geodata files.", (Object)loaded);
        if (loaded == 0) {
            final GeoEngineSettings geoSettings = (GeoEngineSettings)Configurator.getSettings((Class)GeoEngineSettings.class);
            if (geoSettings.isEnabledPathFinding()) {
                geoSettings.setEnabledPathFinding(false);
                GeoEngine.LOGGER.warn("Disabling  Path Finding.");
            }
            if (geoSettings.isSyncMode(SyncMode.SERVER)) {
                geoSettings.setSyncMode(SyncMode.Z_ONLY);
                GeoEngine.LOGGER.warn("Forcing Sync Mode setting to {}", (Object)SyncMode.Z_ONLY);
            }
        }
    }
    
    public static int getGeoX(final int worldX) {
        return MathUtil.limit(worldX, -294912, 294912) + 294912 >> 4;
    }
    
    public static int getGeoY(final int worldY) {
        return MathUtil.limit(worldY, -262144, 294912) + 262144 >> 4;
    }
    
    public static int getWorldX(final int geoX) {
        return (MathUtil.limit(geoX, 0, 36864) << 4) - 294912 + 8;
    }
    
    public static int getWorldY(final int geoY) {
        return (MathUtil.limit(geoY, 0, 34816) << 4) - 262144 + 8;
    }
    
    private static byte getDirXY(final byte dirX, final byte dirY) {
        if (dirY == 8) {
            if (dirX == 2) {
                return -128;
            }
            return 64;
        }
        else {
            if (dirX == 2) {
                return 32;
            }
            return 16;
        }
    }
    
    private boolean loadGeoBlocks(final Path filePath, final int regionX, final int regionY) {
        try {
            final RandomAccessFile raf = new RandomAccessFile(filePath.toAbsolutePath().toString(), "r");
            try {
                final FileChannel fc = raf.getChannel();
                try {
                    final MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0L, fc.size()).load();
                    buffer.order(ByteOrder.LITTLE_ENDIAN);
                    final int blockX = (regionX - 11) * 256;
                    final int blockY = (regionY - 10) * 256;
                    for (int ix = 0; ix < 256; ++ix) {
                        for (int iy = 0; iy < 256; ++iy) {
                            final byte type = buffer.get();
                            final ABlock[] array = this.blocks[blockX + ix];
                            final int n = blockY + iy;
                            ABlock aBlock = null;
                            switch (type) {
                                case -48: {
                                    aBlock = new BlockFlat(buffer, GeoFormat.L2D);
                                    break;
                                }
                                case -47: {
                                    aBlock = new BlockComplex(buffer, GeoFormat.L2D);
                                    break;
                                }
                                case -46: {
                                    aBlock = new BlockMultilayer(buffer, GeoFormat.L2D);
                                    break;
                                }
                                default: {
                                    throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(B)Ljava/lang/String;, type));
                                }
                            }
                            array[n] = aBlock;
                        }
                    }
                    if (buffer.remaining() > 0) {
                        GeoEngine.LOGGER.warn("GeoEngine: Region file {} can be corrupted, remaining {} bytes to read.", (Object)filePath, (Object)buffer.remaining());
                    }
                    final boolean b = true;
                    if (fc != null) {
                        fc.close();
                    }
                    raf.close();
                    return b;
                }
                catch (Throwable t) {
                    if (fc != null) {
                        try {
                            fc.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
            }
            catch (Throwable t2) {
                try {
                    raf.close();
                }
                catch (Throwable exception2) {
                    t2.addSuppressed(exception2);
                }
                throw t2;
            }
        }
        catch (Exception e) {
            GeoEngine.LOGGER.error("Error while loading {} region file.", (Object)filePath);
            GeoEngine.LOGGER.error(e.getMessage());
            this.loadNullBlocks(regionX, regionY);
            return false;
        }
    }
    
    private void loadNullBlocks(final int regionX, final int regionY) {
        final int blockX = (regionX - 11) * 256;
        final int blockY = (regionY - 10) * 256;
        for (int ix = 0; ix < 256; ++ix) {
            for (int iy = 0; iy < 256; ++iy) {
                this.blocks[blockX + ix][blockY + iy] = this.nullBlock;
            }
        }
    }
    
    private ABlock getBlock(final int geoX, final int geoY) {
        final int x = geoX / 8;
        final int y = geoY / 8;
        if (x > -1 && y > -1 && x < 4608 && y < 4352) {
            return this.blocks[x][y];
        }
        return null;
    }
    
    public final boolean hasGeoPos(final int geoX, final int geoY) {
        final ABlock block = this.getBlock(geoX, geoY);
        return block != null && block.hasGeoPos();
    }
    
    public final short getHeightNearest(final int geoX, final int geoY, final int worldZ) {
        final ABlock block = this.getBlock(geoX, geoY);
        return (block != null) ? block.getHeightNearest(geoX, geoY, worldZ) : ((short)worldZ);
    }
    
    public final byte getNsweNearest(final int geoX, final int geoY, final int worldZ) {
        final ABlock block = this.getBlock(geoX, geoY);
        return (byte)((block != null) ? block.getNsweNearest(geoX, geoY, worldZ) : -1);
    }
    
    public final boolean hasGeo(final int worldX, final int worldY) {
        return this.hasGeoPos(getGeoX(worldX), getGeoY(worldY));
    }
    
    public final short getHeight(final int worldX, final int worldY, final int worldZ) {
        return this.getHeightNearest(getGeoX(worldX), getGeoY(worldY), worldZ);
    }
    
    public final boolean canSeeTarget(final WorldObject origin, final WorldObject target) {
        if (GameUtils.isDoor(target) || GameUtils.isArtifact(target) || (GameUtils.isCreature(target) && ((Creature)target).isFlying())) {
            return true;
        }
        final double theight = GameUtils.isCreature(target) ? (((Creature)target).getCollisionHeight() * 2.0) : 0.0;
        return this.canSeeTarget(origin, target.getLocation(), theight);
    }
    
    public final boolean canSeeTarget(final WorldObject origin, final Location position) {
        return this.canSeeTarget(origin, position, 0.0);
    }
    
    private boolean canSeeTarget(final WorldObject origin, final Location position, final double tHeight) {
        final int ox = origin.getX();
        final int oy = origin.getY();
        final int oz = origin.getZ();
        final int tx = position.getX();
        final int ty = position.getY();
        final int tz = position.getZ();
        if (DoorDataManager.getInstance().checkIfDoorsBetween(ox, oy, oz, tx, ty, tz, origin.getInstanceWorld(), true)) {
            return false;
        }
        if (FenceDataManager.getInstance().checkIfFenceBetween(ox, oy, oz, tx, ty, tz, origin.getInstanceWorld())) {
            return false;
        }
        final int gox = getGeoX(ox);
        final int goy = getGeoY(oy);
        if (!this.hasGeoPos(gox, goy)) {
            return true;
        }
        final short goz = this.getHeightNearest(gox, goy, oz);
        final int gtx = getGeoX(tx);
        final int gty = getGeoY(ty);
        if (!this.hasGeoPos(gtx, gty)) {
            return true;
        }
        final short gtz = this.getHeightNearest(gtx, gty, tz);
        if (gox == gtx && goy == gty) {
            return goz == gtz;
        }
        double oheight = 0.0;
        if (GameUtils.isCreature(origin)) {
            oheight = ((Creature)origin).getTemplate().getCollisionHeight();
        }
        return this.checkSee(gox, goy, goz, oheight, gtx, gty, gtz, tHeight, origin.getInstanceWorld());
    }
    
    private boolean checkSee(int gox, int goy, int goz, final double oheight, int gtx, int gty, int gtz, final double theight, final Instance instance) {
        double losoz = goz + oheight * 0.75;
        double lostz = gtz + theight * 0.75;
        final int dx = Math.abs(gtx - gox);
        final int sx = (gox < gtx) ? 1 : -1;
        final byte dirox = (byte)((sx > 0) ? 1 : 2);
        final byte dirtx = (byte)((sx > 0) ? 2 : 1);
        final int dy = Math.abs(gty - goy);
        final int sy = (goy < gty) ? 1 : -1;
        final byte diroy = (byte)((sy > 0) ? 4 : 8);
        final byte dirty = (byte)((sy > 0) ? 8 : 4);
        final int dm = Math.max(dx, dy);
        final double dz = (lostz - losoz) / dm;
        final byte diroxy = getDirXY(dirox, diroy);
        final byte dirtxy = getDirXY(dirtx, dirty);
        int d = dx - dy;
        int nox = gox;
        int noy = goy;
        int ntx = gtx;
        int nty = gty;
        byte nsweo = this.getNsweNearest(gox, goy, goz);
        byte nswet = this.getNsweNearest(gtx, gty, gtz);
        for (int i = 0; i < (dm + 1) / 2; ++i) {
            byte diro = 0;
            byte dirt = 0;
            final int e2 = 2 * d;
            if (e2 > -dy && e2 < dx) {
                d -= dy;
                d += dx;
                nox += sx;
                ntx -= sx;
                noy += sy;
                nty -= sy;
                diro |= diroxy;
                dirt |= dirtxy;
            }
            else if (e2 > -dy) {
                d -= dy;
                nox += sx;
                ntx -= sx;
                diro |= dirox;
                dirt |= dirtx;
            }
            else if (e2 < dx) {
                d += dx;
                noy += sy;
                nty -= sy;
                diro |= diroy;
                dirt |= dirty;
            }
            ABlock block = this.getBlock(nox, noy);
            int index;
            if ((nsweo & diro) == 0x0) {
                index = block.getIndexAbove(nox, noy, goz - 48);
            }
            else {
                index = block.getIndexBelow(nox, noy, goz + 48);
            }
            if (index == -1) {
                return false;
            }
            goz = block.getHeight(index);
            losoz += dz;
            if (goz - losoz > 32.0) {
                return false;
            }
            nsweo = block.getNswe(index);
            block = this.getBlock(ntx, nty);
            if ((nswet & dirt) == 0x0) {
                index = block.getIndexAbove(ntx, nty, gtz - 48);
            }
            else {
                index = block.getIndexBelow(ntx, nty, gtz + 48);
            }
            if (index == -1) {
                return false;
            }
            gtz = block.getHeight(index);
            lostz -= dz;
            if (gtz - lostz > 32.0) {
                return false;
            }
            nswet = block.getNswe(index);
            gox = nox;
            goy = noy;
            gtx = ntx;
            gty = nty;
        }
        return Math.abs(goz - gtz) < 32;
    }
    
    public boolean canMoveToTarget(final Creature creature, final ILocational location) {
        return this.canMoveToTarget(creature.getX(), creature.getY(), creature.getZ(), location.getX(), location.getY(), location.getZ(), creature.getInstanceWorld());
    }
    
    public final boolean canMoveToTarget(final int ox, final int oy, final int oz, final int tx, final int ty, final int tz, final Instance instance) {
        final int gox = getGeoX(ox);
        final int goy = getGeoY(oy);
        if (!this.hasGeoPos(gox, goy)) {
            return true;
        }
        final short goz = this.getHeightNearest(gox, goy, oz);
        final int gtx = getGeoX(tx);
        final int gty = getGeoY(ty);
        if (!this.hasGeoPos(gtx, gty)) {
            return true;
        }
        final short gtz = this.getHeightNearest(gtx, gty, tz);
        if (gox == gtx && goy == gty && goz == gtz) {
            return true;
        }
        final GeoLocation loc = this.checkMove(gox, goy, goz, gtx, gty, gtz, instance);
        return loc.getGeoX() == gtx && loc.getGeoY() == gty;
    }
    
    public final Location canMoveToTargetLoc(final int ox, final int oy, final int oz, final int tx, final int ty, final int tz, final Instance instance) {
        if (DoorDataManager.getInstance().checkIfDoorsBetween(ox, oy, oz, tx, ty, tz, instance, false)) {
            return new GeoLocation(ox, oy, oz);
        }
        if (FenceDataManager.getInstance().checkIfFenceBetween(ox, oy, oz, tx, ty, tz, instance)) {
            return new Location(ox, oy, oz);
        }
        final int gox = getGeoX(ox);
        final int goy = getGeoY(oy);
        if (!this.hasGeoPos(gox, goy)) {
            return new Location(tx, ty, tz);
        }
        final short goz = this.getHeightNearest(gox, goy, oz);
        final int gtx = getGeoX(tx);
        final int gty = getGeoY(ty);
        if (!this.hasGeoPos(gtx, gty)) {
            return new Location(tx, ty, tz);
        }
        final short gtz = this.getHeightNearest(gtx, gty, tz);
        if (gox == gtx && goy == gty && goz == gtz) {
            return new Location(tx, ty, tz);
        }
        return this.checkMove(gox, goy, goz, gtx, gty, gtz, instance);
    }
    
    protected final GeoLocation checkMove(final int gox, final int goy, final int goz, final int gtx, final int gty, final int gtz, final Instance instance) {
        if (DoorDataManager.getInstance().checkIfDoorsBetween(gox, goy, goz, gtx, gty, gtz, instance, false)) {
            return new GeoLocation(gox, goy, goz);
        }
        if (FenceDataManager.getInstance().checkIfFenceBetween(gox, goy, goz, gtx, gty, gtz, instance)) {
            return new GeoLocation(gox, goy, goz);
        }
        final int dx = Math.abs(gtx - gox);
        final int sx = (gox < gtx) ? 1 : -1;
        final byte dirX = (byte)((sx > 0) ? 1 : 2);
        final int dy = Math.abs(gty - goy);
        final int sy = (goy < gty) ? 1 : -1;
        final byte dirY = (byte)((sy > 0) ? 4 : 8);
        final byte dirXY = getDirXY(dirX, dirY);
        int d = dx - dy;
        int gpx = gox;
        int gpy = goy;
        int gpz = goz;
        int nx = gpx;
        int ny = gpy;
        while (true) {
            byte direction = 0;
            final int e2 = 2 * d;
            if (e2 > -dy && e2 < dx) {
                d -= dy;
                d += dx;
                nx += sx;
                ny += sy;
                direction |= dirXY;
            }
            else if (e2 > -dy) {
                d -= dy;
                nx += sx;
                direction |= dirX;
            }
            else if (e2 < dx) {
                d += dx;
                ny += sy;
                direction |= dirY;
            }
            if ((this.getNsweNearest(gpx, gpy, gpz) & direction) == 0x0) {
                return new GeoLocation(gpx, gpy, gpz);
            }
            gpx = nx;
            gpy = ny;
            gpz = this.getHeightNearest(nx, ny, gpz);
            if (gpx != gtx || gpy != gty) {
                continue;
            }
            if (gpz == gtz) {
                return new GeoLocation(gtx, gty, gtz);
            }
            return new GeoLocation(gox, goy, goz);
        }
    }
    
    public List<Location> findPath(final int ox, final int oy, final int oz, final int tx, final int ty, final int tz, final Instance instance) {
        return null;
    }
    
    public static void init() {
        getInstance().load();
    }
    
    public static GeoEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)GeoEngine.class);
    }
    
    private static class Singleton
    {
        private static final GeoEngine INSTANCE;
        
        static {
            INSTANCE = (((GeoEngineSettings)Configurator.getSettings((Class)GeoEngineSettings.class)).isEnabledPathFinding() ? new GeoEnginePathFinding() : new GeoEngine());
        }
    }
}
