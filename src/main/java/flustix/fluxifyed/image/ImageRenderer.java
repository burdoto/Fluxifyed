package flustix.fluxifyed.image;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import flustix.fluxifyed.Main;
import flustix.fluxifyed.utils.color.ColorUtils;
import flustix.fluxifyed.utils.file.FileUtils;
import flustix.fluxifyed.utils.graphics.Alignment;
import flustix.fluxifyed.utils.graphics.GraphicsUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

public class ImageRenderer {
    public static boolean renderImage(RenderArgs args) {
        try {
            JsonObject data = JsonParser.parseString(FileUtils.getResourceString("/images/" + args.template + ".json")).getAsJsonObject();

            BufferedImage image = new BufferedImage(data.get("w").getAsInt(), data.get("h").getAsInt(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            InputStream stream = Main.class.getResourceAsStream("/fonts/Quicksand-SemiBold.ttf"); //TODO: make font configurable

            if (stream == null) {
                Main.LOGGER.error("Font not found!");
                return false;
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setFont(font.deriveFont(24f));

            for (JsonElement d : data.getAsJsonArray("d"))
                renderObject(d.getAsJsonObject(), args.data, graphics);

            graphics.dispose();
            ImageIO.write(image, "png", new File(args.output));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static void renderObject(JsonObject json, RenderData data, Graphics2D g2d) {
        switch (json.get("t").getAsString()) {
            case "rect" -> renderRect(json.getAsJsonObject("d"), data, g2d);
            case "xpProgress" -> renderXpProgress(json.getAsJsonObject("d"), data, g2d);
            case "image" -> renderImage(json.getAsJsonObject("d"), data, g2d);
            case "text" -> renderText(json.getAsJsonObject("d"), data, g2d);
        }
    }

    static void renderRect(JsonObject json, RenderData data, Graphics2D g2d) {
        String f = json.get("f") == null ? "#ffffff" : json.get("f").getAsString(); // fill
        int x = json.get("x") == null ? 0 : json.get("x").getAsInt(); // x
        int y = json.get("y") == null ? 0 : json.get("y").getAsInt(); // y
        int w = json.get("w") == null ? 0 : json.get("w").getAsInt(); // width
        int h = json.get("h") == null ? 0 : json.get("h").getAsInt(); // height
        int r = json.get("r") == null ? 0 : json.get("r").getAsInt(); // radius

        f = replaceVars(f, data);

        g2d.setColor(ColorUtils.hexToRGBA(f));
        g2d.fillRoundRect(x, y, w, h, r, r);
    }

    static void renderXpProgress(JsonObject json, RenderData data, Graphics2D g2d) {
        String f = json.get("f") == null ? "#ffffff" : json.get("f").getAsString(); // fill
        int x = json.get("x") == null ? 0 : json.get("x").getAsInt(); // x
        int y = json.get("y") == null ? 0 : json.get("y").getAsInt(); // y
        int w = json.get("w") == null ? 0 : json.get("w").getAsInt(); // max width
        int h = json.get("h") == null ? 0 : json.get("h").getAsInt(); // height
        int r = json.get("r") == null ? 0 : json.get("r").getAsInt(); // radius

        f = replaceVars(f, data);

        float progress = Float.parseFloat(replaceVars("{xp.progress}", data));
        w = (int) (w * (progress / 100f));

        g2d.setColor(ColorUtils.hexToRGBA(f));
        g2d.fillRoundRect(x, y, w, h, r, r);
    }

    static void renderImage(JsonObject json, RenderData data, Graphics2D g2d) {
        String i = json.get("i") == null ? "url" : json.get("i").getAsString(); // image
        int x = json.get("x") == null ? 0 : json.get("x").getAsInt(); // x
        int y = json.get("y") == null ? 0 : json.get("y").getAsInt(); // y
        int w = json.get("w") == null ? 0 : json.get("w").getAsInt(); // width
        int h = json.get("h") == null ? 0 : json.get("h").getAsInt(); // height
        int r = json.get("r") == null ? 0 : json.get("r").getAsInt(); // radius

        i = replaceVars(i, data);

        try {
            BufferedImage image = ImageIO.read(new URL(i));
            BufferedImage scaledImage = GraphicsUtils.scaleImage(image, w, h);
            BufferedImage roundedImage = GraphicsUtils.drawRoundedRect(scaledImage, r);
            g2d.drawImage(roundedImage, x, y, w, h, null);
        } catch (Exception ignored) {
        }
    }

    static void renderText(JsonObject json, RenderData data, Graphics2D g2d) {
        String t = json.get("t") == null ? "" : json.get("t").getAsString(); // text
        String a = json.get("a") == null ? "l" : json.get("a").getAsString(); // alignment
        String c = json.get("c") == null ? "#ffffff" : json.get("c").getAsString(); // color
        int x = json.get("x") == null ? 0 : json.get("x").getAsInt(); // x
        int y = json.get("y") == null ? 0 : json.get("y").getAsInt(); // y
        int w = json.get("w") == null ? 0 : json.get("w").getAsInt(); // max width
        int s = json.get("s") == null ? 0 : json.get("s").getAsInt(); // font size

        t = replaceVars(t, data);
        c = replaceVars(c, data);

        Alignment alignment = Alignment.LEFT;
        switch (a) {
            case "c" -> alignment = Alignment.CENTER;
            case "r" -> alignment = Alignment.RIGHT;
        }

        g2d.setColor(ColorUtils.hexToRGBA(c));
        GraphicsUtils.drawString(g2d, t, x, y, s, w, alignment);
    }

    static String replaceVars(String s, RenderData data) {
        while (!data.loaded) {
            try {
                Thread.sleep(100); // wait for data to load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (String key : data.getKeys().keySet()) {
            String replacement = data.getKeys().get(key);
            if (replacement == null) replacement = "";
            s = s.replace("{" + key + "}", replacement);
        }

        return s;
    }
}
