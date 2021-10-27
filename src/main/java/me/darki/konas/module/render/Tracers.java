package me.darki.konas.module.render;

import com.google.common.primitives.Ints;
import cookiedragon.eventsystem.Subscriber;
import java.awt.Color;
import me.darki.konas.module.Category;
import me.darki.konas.ColorValue;
import me.darki.konas.Class460;
import me.darki.konas.Class492;
import me.darki.konas.Class502;
import me.darki.konas.Class516;
import me.darki.konas.module.client.NewGui;
import me.darki.konas.Class89;
import me.darki.konas.Class91;
import me.darki.konas.mixin.mixins.IEntityRenderer;
import me.darki.konas.mixin.mixins.IRenderManager;
import me.darki.konas.module.Module;
import me.darki.konas.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class Tracers
extends Module {
    public static Setting<Class460> Field296 = new Setting<>("Mode", Class460.LINES);
    public static Setting<Boolean> Field297 = new Setting<>("ShowTargets", true);
    public static Setting<Boolean> Field298 = new Setting<>("ShowDistanceColor", true);
    public static Setting<Boolean> Field299 = new Setting<>("ShowFriends", true);
    public static Setting<ColorValue> Field300 = new Setting<>("Color", new ColorValue(-1));
    public static Setting<Boolean> Field301 = new Setting<>("Visible", false).Method1191(Tracers::Method393);
    public static Setting<Boolean> Field302 = new Setting<>("Fade", false).Method1191(Tracers::Method394);
    public static Setting<Integer> Field303 = new Setting<>("Distance", 100, 200, 50, 1).Method1191(Tracers::Method388);
    public static Setting<Integer> Field304 = new Setting<>("Radius", 30, 200, 10, 1);
    public static Setting<Float> Field305 = new Setting<>("Width", Float.valueOf(2.0f), Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(0.5f));
    public static Setting<Float> Field306 = new Setting<>("Range", Float.valueOf(220.0f), Float.valueOf(500.0f), Float.valueOf(1.0f), Float.valueOf(1.0f));

    public static boolean Method388() {
        return Field296.getValue() == Class460.ARROWS && (Boolean)Field302.getValue() != false;
    }

    public static boolean Method394() {
        return Field296.getValue() == Class460.ARROWS;
    }

    public static boolean Method393() {
        return Field296.getValue() == Class460.ARROWS;
    }

    @Subscriber
    public void Method466(Class91 class91) {
        if (Tracers.mc.world == null || Tracers.mc.player == null) {
            return;
        }
        if (Field296.getValue() == Class460.LINES) {
            return;
        }
        for (Entity entity : Tracers.mc.world.loadedEntityList) {
            int n;
            Vec3d vec3d;
            Vec3d vec3d2;
            if (!(entity instanceof EntityPlayer) || entity == Tracers.mc.player || !(Tracers.mc.player.getDistance(entity) <= ((Float)Field306.getValue()).floatValue()) || (vec3d2 = NewGui.INSTANCE.Field1137.Method2026(vec3d = new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)mc.getRenderPartialTicks(), entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)mc.getRenderPartialTicks(), entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)mc.getRenderPartialTicks()).add(0.0, (double)entity.getEyeHeight(), 0.0))) == null || this.Method467(vec3d2) || Class516.Method1288(entity) && !((Boolean)Field301.getValue()).booleanValue()) continue;
            GL11.glPushMatrix();
            int n2 = -1;
            if (((Boolean)Field297.getValue()).booleanValue() && NewGui.INSTANCE.Field1133.Method423(entity)) {
                n = NewGui.INSTANCE.Field1133.Method428(entity);
                n2 = new Color(255, n, n).hashCode();
            } else {
                n2 = Class492.Method1989(entity.getName()) && (Boolean)Field299.getValue() != false ? Color.CYAN.hashCode() : ((Boolean)Field298.getValue() != false ? this.Method468(entity.getDistance((Entity)Tracers.mc.player)) : ((ColorValue)Field300.getValue()).Method774());
            }
            n = n2 >> 24 & 0xFF;
            int n3 = n2 >> 16 & 0xFF;
            int n4 = n2 >> 8 & 0xFF;
            int n5 = n2 & 0xFF;
            Color color = new Color(n3, n4, n5, (int)((Boolean)Field302.getValue() != false ? MathHelper.clamp((float)(255.0f - 255.0f / (float)((Integer)Field303.getValue()).intValue() * Tracers.mc.player.getDistance(entity)), (float)100.0f, (float)255.0f) : (float)n));
            int n6 = Display.getWidth() / 2 / (Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale);
            int n7 = Display.getHeight() / 2 / (Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale);
            float f = this.Method469(entity) - Tracers.mc.player.rotationYaw;
            GL11.glTranslatef((float)n6, (float)n7, (float)0.0f);
            GL11.glRotatef((float)f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-n6), (float)(-n7), (float)0.0f);
            Class516.Method1270(n6, n7 - (Integer)Field304.getValue(), ((Float)Field305.getValue()).floatValue() * 5.0f, 2.0f, 1.0f, false, 1.0f, color.getRGB());
            GL11.glTranslatef((float)n6, (float)n7, (float)0.0f);
            GL11.glRotatef((float)(-f), (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glTranslatef((float)(-n6), (float)(-n7), (float)0.0f);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glPopMatrix();
        }
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean Method467(Vec3d vec3d) {
        if (!(vec3d.x > -1.0)) return false;
        if (!(vec3d.y < 1.0)) return false;
        int n = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        if (!(vec3d.x / (double)n >= 0.0)) return false;
        int n2 = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        if (!(vec3d.x / (double)n2 <= (double)Display.getWidth())) return false;
        int n3 = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        if (!(vec3d.y / (double)n3 >= 0.0)) return false;
        int n4 = Tracers.mc.gameSettings.guiScale == 0 ? 1 : Tracers.mc.gameSettings.guiScale;
        if (!(vec3d.y / (double)n4 <= (double)Display.getHeight())) return false;
        return true;
    }

    public int Method468(float f) {
        int n = Ints.constrainToRange((int)f * 4, 0, 255);
        return new Color(Ints.constrainToRange(255 - n, 0, 255), Ints.constrainToRange(n, 0, 255), 0).hashCode();
    }

    public float Method469(Entity entity) {
        double d = entity.posX - Tracers.mc.player.posX;
        double d2 = entity.posZ - Tracers.mc.player.posZ;
        return (float)(-(Math.atan2(d, d2) * 57.29577951308232));
    }

    public Tracers() {
        super("Tracers", Category.RENDER, new String[0]);
    }

    @Subscriber
    public void Method139(Class89 class89) {
        if (Tracers.mc.world == null || Tracers.mc.player == null) {
            return;
        }
        if (Field296.getValue() == Class460.ARROWS) {
            return;
        }
        for (Entity entity : Tracers.mc.world.loadedEntityList) {
            int n;
            if (!(entity instanceof EntityPlayer) || entity == Tracers.mc.player || !(Tracers.mc.player.getDistance(entity) <= ((Float)Field306.getValue()).floatValue())) continue;
            Vec3d vec3d = Class502.Method1393(entity, class89.Method436()).subtract(((IRenderManager)mc.getRenderManager()).Method69(), ((IRenderManager)mc.getRenderManager()).Method70(), ((IRenderManager)mc.getRenderManager()).Method71());
            GL11.glBlendFunc((int)770, (int)771);
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth((float)((Float)Field305.getValue()).floatValue());
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask((boolean)false);
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f);
            boolean bl = Tracers.mc.gameSettings.viewBobbing;
            Tracers.mc.gameSettings.viewBobbing = false;
            ((IEntityRenderer)Tracers.mc.entityRenderer).Method1908(class89.Method436(), 0);
            Vec3d vec3d2 = new Vec3d(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(Tracers.mc.player.rotationPitch))).rotateYaw(-((float)Math.toRadians(Tracers.mc.player.rotationYaw)));
            if (((Boolean)Field297.getValue()).booleanValue() && NewGui.INSTANCE.Field1133.Method423(entity)) {
                int n2 = NewGui.INSTANCE.Field1133.Method428(entity);
                n = new Color(255, n2, n2).hashCode();
            } else {
                n = Class492.Method1989(entity.getName()) && (Boolean)Field299.getValue() != false ? Color.CYAN.hashCode() : ((Boolean)Field298.getValue() != false ? this.Method468(entity.getDistance((Entity)Tracers.mc.player)) : ((ColorValue)Field300.getValue()).Method774());
            }
            Class502.Method1408((float)vec3d2.x, (float)vec3d2.y + Tracers.mc.player.getEyeHeight(), (float)vec3d2.z, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z, ((Float)Field305.getValue()).floatValue(), n);
            Class502.Method1408((float)vec3d.x, (float)vec3d.y, (float)vec3d.z, (float)vec3d.x, (float)vec3d.y + entity.getEyeHeight(), (float)vec3d.z, ((Float)Field305.getValue()).floatValue(), n);
            Tracers.mc.gameSettings.viewBobbing = bl;
            ((IEntityRenderer)Tracers.mc.entityRenderer).Method1908(class89.Method436(), 0);
            GlStateManager.enableCull();
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.enableDepth();
        }
    }
}
