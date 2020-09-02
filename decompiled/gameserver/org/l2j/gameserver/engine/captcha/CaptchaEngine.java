// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.captcha;

import io.github.joealisson.primitive.CHashIntMap;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.function.IntFunction;
import org.l2j.commons.util.Rnd;
import io.github.joealisson.primitive.IntMap;

public class CaptchaEngine
{
    private static final IntMap<Captcha> captchas;
    private static final DXT1ImageCompressor compressor;
    
    private CaptchaEngine() {
    }
    
    public Captcha next() {
        final int id = Rnd.get(CaptchaEngine.captchas.size() + 5);
        return (Captcha)CaptchaEngine.captchas.computeIfAbsent(id, (IntFunction)this::generateCaptcha);
    }
    
    public Captcha next(final int previousId) {
        int id = Rnd.get(CaptchaEngine.captchas.size() + 5);
        if (id == previousId) {
            ++id;
        }
        return (Captcha)CaptchaEngine.captchas.computeIfAbsent(id, (IntFunction)this::generateCaptcha);
    }
    
    private int generateCaptchaCode() {
        return Rnd.get(111111, 999999);
    }
    
    private Captcha generateCaptcha(final int id) {
        final int height = 32;
        final int width = 128;
        final BufferedImage image = new BufferedImage(width, height, 7);
        final Graphics2D graphics = this.createGraphics(height, width, image);
        graphics.setFont(new Font("SansSerif", 1, 22));
        final int code = this.generateCaptchaCode();
        this.writeCode(code, graphics);
        this.addNoise(graphics);
        graphics.dispose();
        return new Captcha(id, code, CaptchaEngine.compressor.compress(image));
    }
    
    private Graphics2D createGraphics(final int height, final int width, final BufferedImage image) {
        final Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, width, height);
        return graphics;
    }
    
    private void writeCode(final int code, final Graphics2D graphics) {
        final String text = String.valueOf(code);
        final FontMetrics metrics = graphics.getFontMetrics();
        final int textStart = 10;
        for (int i = 0; i < text.length(); ++i) {
            final char character = text.charAt(i);
            final int charWidth = metrics.charWidth(character) + 5;
            graphics.setColor(this.getColor());
            graphics.drawString(invokedynamic(makeConcatWithConstants:(C)Ljava/lang/String;, character), textStart + i * charWidth, Rnd.get(24, 32));
        }
    }
    
    private void addNoise(final Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        for (int i = 0; i < 20; ++i) {
            graphics.fillOval(Rnd.get(10, 122), Rnd.get(6, 20), 4, 4);
        }
        for (int i = 0; i < 6; ++i) {
            graphics.drawLine(Rnd.get(30, 90), Rnd.get(6, 28), Rnd.get(80, 120), Rnd.get(10, 26));
        }
    }
    
    private Color getColor() {
        Color color = null;
        switch (Rnd.get(5)) {
            case 1: {
                color = Color.WHITE;
                break;
            }
            case 2: {
                color = Color.RED;
                break;
            }
            case 3: {
                color = Color.YELLOW;
                break;
            }
            case 4: {
                color = Color.CYAN;
                break;
            }
            default: {
                color = Color.GREEN;
                break;
            }
        }
        return color;
    }
    
    public static CaptchaEngine getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        captchas = (IntMap)new CHashIntMap();
        compressor = new DXT1ImageCompressor();
    }
    
    private static class Singleton
    {
        private static final CaptchaEngine INSTANCE;
        
        static {
            INSTANCE = new CaptchaEngine();
        }
    }
}
