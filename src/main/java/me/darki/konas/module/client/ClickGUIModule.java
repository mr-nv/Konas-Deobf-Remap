package me.darki.konas.module.client;

import me.darki.konas.*;
import me.darki.konas.command.commands.fontCommand;
import me.darki.konas.module.Category;
import me.darki.konas.module.Module;
import me.darki.konas.setting.Setting;
import net.minecraft.client.gui.GuiScreen;

public class ClickGUIModule
extends Module {
    public static int Field1521 = -1;
    public static Setting<Boolean> binds = new Setting<>("Binds", false);
    public static Setting<ColorValue> color = new Setting<>("Color", new ColorValue(-4581958));
    public static Setting<ColorValue> fony = new Setting<>("Font", new ColorValue(-1));
    public static Setting<ColorValue> secondary = new Setting<>("Secondary", new ColorValue(-16777216));
    public static Setting<ColorValue> header = new Setting<>("Header", new ColorValue(-587202560));
    public static Setting<ColorValue> background = new Setting<>("Background", new ColorValue(-587202560));
    public static Setting<Float> maxHeight = new Setting<>("MaxHeight", Float.valueOf(500.0f), Float.valueOf(500.0f), Float.valueOf(200.0f), Float.valueOf(3.5f));
    public static Setting<Integer> scrollSpeed = new Setting<>("ScrollSpeed", 14, 28, 7, 1);
    public static Setting<Boolean> hover = new Setting<>("Hover", true);
    public static Setting<Boolean> animate = new Setting<>("Animate", true);
    public static Setting<Boolean> outline = new Setting<>("Outline", false).Method1191(ClickGUIModule::Method393);
    public static Setting<Integer> thickness = new Setting<>("Thickness", 1, 5, 1, 1).Method1191(ClickGUIModule::Method394);
    public static Setting<Integer> animationSpeed = new Setting<>("AnimationSpeed", 10, 20, 1, 1).Method1191(animate::getValue);
    public Setting<Boolean> customFont = new IdkWhatThisSettingThingDoes("CustomFont", true, new Class173(this));
    public static Class555 Field1536 = new Class555(fontCommand.Field1351, 17.0f);

    public ClickGUIModule() {
        super("ClickGUI", "Default Konas GUI", 21, Category.CLIENT, new String[0]);
    }

    public static boolean Method394() {
        return (Boolean) animate.getValue() == false && (Boolean) outline.getValue() != false;
    }

    public static boolean Method393() {
        return (Boolean) animate.getValue() == false;
    }

    @Override
    public void onEnable() {
        this.toggle();
        mc.displayGuiScreen((GuiScreen) NewGui.INSTANCE.Field1130);
    }
}
