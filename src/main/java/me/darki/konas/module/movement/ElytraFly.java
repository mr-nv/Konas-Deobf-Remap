package me.darki.konas.module.movement;

import cookiedragon.eventsystem.Subscriber;
import java.util.Random;

import me.darki.konas.*;
import me.darki.konas.mixin.mixins.ICPacketPlayer;
import me.darki.konas.module.Category;
import me.darki.konas.module.Module;
import me.darki.konas.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.input.Keyboard;

public class ElytraFly
extends Module {
    public static Setting<Class403> mode = new Setting<>("Mode", Class403.CONTROL);
    public static Setting<Float> limit = new Setting<>("Limit", Float.valueOf(1.0f), Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(0.1f)).Method1191(ElytraFly::Method987);
    public static Setting<Float> delay = new Setting<>("Delay", Float.valueOf(5.0f), Float.valueOf(20.0f), Float.valueOf(0.1f), Float.valueOf(0.1f)).Method1191(ElytraFly::Method990);
    public static Setting<Float> timeout = new Setting<>("Timeout", Float.valueOf(0.5f), Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(0.1f)).Method1191(ElytraFly::Method979);
    public static Setting<Boolean> stopMotion = new Setting<>("StopMotion", true).Method1191(ElytraFly::Method981);
    public static Setting<Boolean> freeze = new Setting<>("Freeze", false).Method1191(ElytraFly::Method980);
    public static Setting<Boolean> better = new Setting<>("Better", true).Method1191(ElytraFly::Method982);
    public static Setting<Boolean> cruiseControl = new Setting<>("CruiseControl", false).Method1191(ElytraFly::Method535);
    public static Setting<Double> minUpSpeed = new Setting<>("MinUpSpeed", 0.5, 5.0, 0.1, 0.05).Method1191(ElytraFly::Method973);
    public static Setting<Boolean> autoSwitch = new Setting<>("AutoSwitch", false).Method1191(ElytraFly::Method983);
    public static Setting<Float> factor = new Setting<>("Factor", Float.valueOf(1.5f), Float.valueOf(50.0f), Float.valueOf(0.1f), Float.valueOf(0.1f));
    public static Setting<Integer> minSpeed = new Setting<>("MinSpeed", 20, 50, 1, 1).Method1191(ElytraFly::Method539);
    public static Setting<Float> upFactor = new Setting<>("UpFactor", Float.valueOf(1.0f), Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(0.1f));
    public static Setting<Float> downFactor = new Setting<>("DownFactor", Float.valueOf(1.0f), Float.valueOf(10.0f), Float.valueOf(0.0f), Float.valueOf(0.1f));
    public static Setting<Boolean> forceHeight = new Setting<>("ForceHeight", false).Method1191(ElytraFly::Method986);
    public static Setting<Integer> height = new Setting<>("Height", 121, 256, 1, 1).Method1191(ElytraFly::Method984);
    public static Setting<Boolean> groundSafety = new Setting<>("GroundSafety", false).Method1191(ElytraFly::Method972);
    public static Setting<Float> triggerHeight = new Setting<>("TriggerHeight", Float.valueOf(0.3f), Float.valueOf(1.0f), Float.valueOf(0.05f), Float.valueOf(0.05f)).Method1191(ElytraFly::Method991);
    public static Setting<Float> speed = new Setting<>("Speed", Float.valueOf(1.0f), Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(0.1f)).Method1191(ElytraFly::Method977);
    public static Setting<Float> downSpeed = new Setting<>("DownSpeed", Float.valueOf(1.0f), Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(0.1f)).Method1191(ElytraFly::Method988);
    public static Setting<Boolean> instantFly = new Setting<>("InstantFly", true).Method1191(ElytraFly::Method538);
    public static Setting<Boolean> timer = new Setting<>("Timer", true).Method1191(ElytraFly::Method388);
    public static Setting<Boolean> speedLimit = new Setting<>("SpeedLimit", true).Method1191(ElytraFly::Method393);
    public static Setting<Float> maxSpeed = new Setting<>("MaxSpeed", Float.valueOf(2.5f), Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(0.1f)).Method1191(ElytraFly::Method993);
    public static Setting<Boolean> noDrag = new Setting<>("NoDrag", false).Method1191(ElytraFly::Method989);
    public static Setting<Boolean> accelerate = new Setting<>("Accelerate", true).Method1191(ElytraFly::Method975);
    public static Setting<Float> acceleration = new Setting<>("Acceleration", Float.valueOf(1.0f), Float.valueOf(5.0f), Float.valueOf(0.1f), Float.valueOf(0.1f)).Method1191(ElytraFly::Method519);
    public static Setting<Class426> strict = new Setting<>("Strict", Class426.NONE).Method1191(ElytraFly::Method994);
    public static Setting<Boolean> antiKick = new Setting<>("AntiKick", true).Method1191(ElytraFly::Method394);
    public static Setting<Boolean> infDurability = new Setting<>("InfDurability", true).Method1191(ElytraFly::Method992);
    public static boolean Field1000 = false;
    public boolean Field1001;
    public double Field1002;
    public double Field1003;
    public double Field1004;
    public Random Field1005 = new Random();
    public Class566 Field1006 = new Class566();
    public Class566 Field1007 = new Class566();
    public Class566 Field1008 = new Class566();
    public Class566 Field1009 = new Class566();
    public boolean Field1010 = false;
    public boolean Field1011 = false;

    @Subscriber
    public void Method462(TickEvent tickEvent) {
        if (ElytraFly.mc.world == null || ElytraFly.mc.player == null) {
            return;
        }
        if (tickEvent.Method324() != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.START) {
            return;
        }
        if (ElytraFly.mc.player.onGround) {
            this.Field1011 = true;
        }
        if (!((Boolean) cruiseControl.getValue()).booleanValue()) {
            this.Field1004 = ElytraFly.mc.player.posY;
        }
        for (ItemStack itemStack : ElytraFly.mc.player.getArmorInventoryList()) {
            if (itemStack.getItem() instanceof ItemElytra) {
                Field1000 = true;
                break;
            }
            Field1000 = false;
        }
        if (this.Field1009.Method737(1500.0) && !this.Field1009.Method737(2000.0)) {
            NewGui.INSTANCE.Field1134.Method749(this);
        }
        if (!ElytraFly.mc.player.isElytraFlying() && mode.getValue() != Class403.PACKET) {
            if (this.Field1011 && timer.getValue() != timer.getValue() && !ElytraFly.mc.player.onGround) {
                NewGui.INSTANCE.Field1134.Method746(this, 25, 0.3f);
            }
            if (!ElytraFly.mc.player.onGround && ((Boolean) instantFly.getValue()).booleanValue() && ElytraFly.mc.player.motionY < 0.0) {
                if (!this.Field1006.Method737(1000.0f * ((Float) timeout.getValue()).floatValue())) {
                    return;
                }
                this.Field1006.Method739();
                ElytraFly.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) ElytraFly.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                this.Field1011 = false;
                this.Field1009.Method739();
            }
            return;
        }
    }

    @Subscriber
    public void Method131(PacketEvent packetEvent) {
        block6: {
            SPacketEntityMetadata sPacketEntityMetadata;
            block5: {
                if (ElytraFly.mc.player == null || ElytraFly.mc.world == null) {
                    return;
                }
                if (!(packetEvent.getPacket() instanceof SPacketPlayerPosLook)) break block5;
                if (mode.getValue() != Class403.PACKET && mode.getValue() != Class403.FIREWORK) break block6;
                if (ElytraFly.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
                    this.Field1001 = true;
                }
                if (ElytraFly.mc.player.isElytraFlying()) {
                    this.Field1008.Method739();
                    if (ElytraFly.mc.player != null) {
                        this.Field1004 = ElytraFly.mc.player.posY;
                    }
                }
                break block6;
            }
            if (!(packetEvent.getPacket() instanceof SPacketEntityMetadata) || (sPacketEntityMetadata = (SPacketEntityMetadata) packetEvent.getPacket()).getEntityId() != ElytraFly.mc.player.getEntityId()) break block6;
            NewGui.INSTANCE.Field1134.Method749(this);
            if (mode.getValue() == Class403.PACKET) {
                packetEvent.Cancel();
            }
        }
    }

    public static boolean Method972() {
        return mode.getValue() == Class403.FIREWORK;
    }

    public static boolean Method973() {
        return mode.getValue() == Class403.BOOST && (Boolean) cruiseControl.getValue() != false;
    }

    public ElytraFly() {
        super("ElytraFly", Category.MOVEMENT, "ElytraPlus");
    }

    @Subscriber
    public void Method974(TickEvent tickEvent) {
        block13: {
            Block block;
            boolean bl;
            double d;
            if (tickEvent.Method324() != net.minecraftforge.fml.common.gameevent.TickEvent.Phase.START) {
                return;
            }
            if (ElytraFly.mc.player == null) {
                return;
            }
            if (!ElytraFly.mc.player.isElytraFlying()) {
                return;
            }
            if (mode.getValue() != Class403.FIREWORK) {
                return;
            }
            if (ElytraFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                this.Field1004 += (double)((Float) upFactor.getValue()).floatValue() * 0.5;
            } else if (ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown()) {
                this.Field1004 -= (double)((Float) downFactor.getValue()).floatValue() * 0.5;
            }
            if (((Boolean) forceHeight.getValue()).booleanValue()) {
                this.Field1004 = ((Integer) height.getValue()).intValue();
            }
            Vec3d vec3d = new Vec3d(ElytraFly.mc.player.motionX, ElytraFly.mc.player.motionY, ElytraFly.mc.player.motionZ);
            double d2 = vec3d.length() * 20.0;
            double d3 = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
            double d4 = MathHelper.clamp((double)(d3 / 1.7), (double)0.0, (double)1.0);
            double d5 = 1.0 - Math.sqrt(d4);
            double d6 = 0.6;
            if (d4 >= 0.5 || ElytraFly.mc.player.posY > this.Field1004 + 1.0) {
                d = -((45.0 - d6) * d5 + d6);
                double d7 = (this.Field1004 + 1.0 - ElytraFly.mc.player.posY) * 2.0;
                double d8 = MathHelper.clamp((double)Math.abs(d7), (double)0.0, (double)1.0);
                double d9 = -Math.toDegrees(Math.atan2(Math.abs(d7), d3 * 30.0)) * Math.signum(d7);
                double d10 = (d9 - d) * d8;
                ElytraFly.mc.player.rotationPitch = (float)d;
                ElytraFly.mc.player.rotationPitch += (float)d10;
                ElytraFly.mc.player.prevRotationPitch = ElytraFly.mc.player.rotationPitch;
            }
            if (!this.Field1008.Method737(1000.0f * ((Float) factor.getValue()).floatValue())) break block13;
            d = this.Field1004 - ElytraFly.mc.player.posY;
            boolean bl2 = bl = d > 0.25 && d < 1.0 || d2 < (double)((Integer) minSpeed.getValue()).intValue();
            if (((Boolean) groundSafety.getValue()).booleanValue() && (block = ElytraFly.mc.world.getBlockState(new BlockPos((Entity) ElytraFly.mc.player).down()).getBlock()) != Blocks.AIR && !(block instanceof BlockLiquid) && ElytraFly.mc.player.getEntityBoundingBox().minY - Math.floor(ElytraFly.mc.player.getEntityBoundingBox().minY) > (double)((Float) triggerHeight.getValue()).floatValue()) {
                bl = true;
            }
            if (((Boolean) autoSwitch.getValue()).booleanValue() && bl && ElytraFly.mc.player.getHeldItemMainhand().getItem() != Items.FIREWORKS) {
                for (int i = 0; i < 9; ++i) {
                    if (ElytraFly.mc.player.inventory.getStackInSlot(i).getItem() != Items.FIREWORKS) continue;
                    ElytraFly.mc.player.inventory.currentItem = i;
                    ElytraFly.mc.playerController.updateController();
                    break;
                }
            }
            if (ElytraFly.mc.player.getHeldItemMainhand().getItem() == Items.FIREWORKS && bl) {
                ElytraFly.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
                this.Field1008.Method739();
            }
        }
    }

    public static boolean Method393() {
        return mode.getValue() != Class403.PACKET && mode.getValue() != Class403.FIREWORK;
    }

    @Override
    public void onEnable() {
        this.Field1001 = false;
        this.Field1002 = 0.0;
        if (ElytraFly.mc.player != null) {
            this.Field1004 = ElytraFly.mc.player.posY;
            if (!ElytraFly.mc.player.isCreative()) {
                ElytraFly.mc.player.capabilities.allowFlying = false;
            }
            ElytraFly.mc.player.capabilities.isFlying = false;
        }
        this.Field1010 = false;
        Field1000 = false;
    }

    public static boolean Method535() {
        return mode.getValue() == Class403.BOOST;
    }

    public static boolean Method975() {
        return mode.getValue() == Class403.PACKET;
    }

    public static boolean Method976() {
        return Field1000;
    }

    public static boolean Method977() {
        return mode.getValue() == Class403.CONTROL;
    }

    public boolean Method978(int n) {
        for (int i = MathHelper.floor((double) ElytraFly.mc.player.getEntityBoundingBox().minX); i < MathHelper.ceil((double) ElytraFly.mc.player.getEntityBoundingBox().maxX); ++i) {
            for (int j = MathHelper.floor((double) ElytraFly.mc.player.getEntityBoundingBox().minZ); j < MathHelper.ceil((double) ElytraFly.mc.player.getEntityBoundingBox().maxZ); ++j) {
                IBlockState iBlockState = ElytraFly.mc.world.getBlockState(new BlockPos(i, n, j));
                if (iBlockState.getBlock() == Blocks.AIR) continue;
                return false;
            }
        }
        return true;
    }

    public static boolean Method979() {
        return mode.getValue() == Class403.BOOST;
    }

    public static boolean Method519() {
        return mode.getValue() == Class403.PACKET;
    }

    public static boolean Method980() {
        return mode.getValue() == Class403.BOOST;
    }

    public static boolean Method981() {
        return mode.getValue() == Class403.BOOST;
    }

    @Subscriber
    public void Method536(Class24 class24) {
        block2: {
            if (!(class24.getPacket() instanceof CPacketPlayer) || strict.getValue() != Class426.NCP || mode.getValue() != Class403.PACKET || this.Field1001 || !(Math.abs(ElytraFly.mc.player.motionX) >= 0.05) && !(Math.abs(ElytraFly.mc.player.motionZ) >= 0.05)) break block2;
            double d = 1.0E-8 + 1.0E-8 * (1.0 + (double)this.Field1005.nextInt(1 + (this.Field1005.nextBoolean() ? this.Field1005.nextInt(34) : this.Field1005.nextInt(43))));
            if (ElytraFly.mc.player.onGround || ElytraFly.mc.player.ticksExisted % 2 == 0) {
                ((ICPacketPlayer)class24.getPacket()).Method1698(((CPacketPlayer)class24.getPacket()).getY(ElytraFly.mc.player.posY) + d);
            } else {
                ((ICPacketPlayer)class24.getPacket()).Method1698(((CPacketPlayer)class24.getPacket()).getY(ElytraFly.mc.player.posY) - d);
            }
        }
    }

    public static boolean Method539() {
        return mode.getValue() == Class403.FIREWORK;
    }

    public static boolean Method394() {
        return mode.getValue() == Class403.PACKET;
    }

    public static boolean Method982() {
        return mode.getValue() == Class403.BOOST;
    }

    @Subscriber(priority=30)
    public void Method135(UpdateEvent updateEvent) {
        block0: {
            block1: {
                if (ElytraFly.mc.player.onGround || ElytraFly.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != Items.ELYTRA || mode.getValue() != Class403.PACKET) break block0;
                if (((Boolean) infDurability.getValue()).booleanValue()) break block1;
                if (ElytraFly.mc.player.isElytraFlying()) break block0;
            }
            ElytraFly.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) ElytraFly.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
        }
    }

    public static boolean Method388() {
        return mode.getValue() == Class403.BOOST;
    }

    public static boolean Method983() {
        return mode.getValue() == Class403.FIREWORK;
    }

    public static boolean Method984() {
        return (mode.getValue() == Class403.FIREWORK || mode.getValue() == Class403.BOOST && (Boolean) cruiseControl.getValue() != false) && (Boolean) forceHeight.getValue() != false;
    }

    @Subscriber
    public void Method985(Class32 class32) {
        block41: {
            if (ElytraFly.mc.world == null || ElytraFly.mc.player == null || !Field1000) {
                return;
            }
            if (!ElytraFly.mc.player.isElytraFlying()) {
                if (mode.getValue() == Class403.BOOST) {
                    if (!this.Field1006.Method737(2000.0f * ((Float) limit.getValue()).floatValue()) && ((Boolean) better.getValue()).booleanValue() && !ElytraFly.mc.player.onGround) {
                        NewGui.INSTANCE.Field1134.Method746(this, 20, 0.1f);
                        return;
                    }
                    NewGui.INSTANCE.Field1134.Method749(this);
                }
                return;
            }
            NewGui.INSTANCE.Field1134.Method749(this);
            if (mode.getValue() == Class403.PACKET || mode.getValue() == Class403.FIREWORK) {
                return;
            }
            if (!(class32.Method282() == ElytraFly.mc.player && ElytraFly.mc.player.isServerWorld() || ElytraFly.mc.player.canPassengerSteer() && !ElytraFly.mc.player.isInWater() || ElytraFly.mc.player != null && ElytraFly.mc.player.capabilities.isFlying && !ElytraFly.mc.player.isInLava()) && (!ElytraFly.mc.player.capabilities.isFlying || !ElytraFly.mc.player.isElytraFlying())) break block41;
            class32.setCanceled(true);
            if (mode.getValue() != Class403.BOOST) {
                double d;
                Vec3d vec3d = ElytraFly.mc.player.getLookVec();
                float f = ElytraFly.mc.player.rotationPitch * ((float)Math.PI / 180);
                double d2 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                double d3 = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
                double d4 = vec3d.length();
                float f2 = MathHelper.cos((float)f);
                f2 = (float)((double)f2 * (double)f2 * Math.min(1.0, d4 / 0.4));
                if (mode.getValue() != Class403.CONTROL) {
                    ElytraFly.mc.player.motionY += -0.08 + (double)f2 * (0.06 / (double)((Float) downFactor.getValue()).floatValue());
                }
                if (mode.getValue() == Class403.CONTROL) {
                    if (ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        ElytraFly.mc.player.motionY = -((Float) downSpeed.getValue()).floatValue();
                    } else if (!ElytraFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                        ElytraFly.mc.player.motionY = -3.0E-14 * (double)((Float) downFactor.getValue()).floatValue();
                    }
                } else if (mode.getValue() != Class403.CONTROL && ElytraFly.mc.player.motionY < 0.0 && d2 > 0.0) {
                    d = ElytraFly.mc.player.motionY * -0.1 * (double)f2;
                    ElytraFly.mc.player.motionY += d;
                    ElytraFly.mc.player.motionX += vec3d.x * d / d2 * (double)((Float) factor.getValue()).floatValue();
                    ElytraFly.mc.player.motionZ += vec3d.z * d / d2 * (double)((Float) factor.getValue()).floatValue();
                }
                if (f < 0.0f && mode.getValue() != Class403.CONTROL) {
                    d = d3 * (double)(-MathHelper.sin((float)f)) * 0.04;
                    ElytraFly.mc.player.motionY += d * 3.2 * (double)((Float) upFactor.getValue()).floatValue();
                    ElytraFly.mc.player.motionX -= vec3d.x * d / d2;
                    ElytraFly.mc.player.motionZ -= vec3d.z * d / d2;
                } else if (mode.getValue() == Class403.CONTROL && ElytraFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                    if (d3 > (double)(((Float) upFactor.getValue()).floatValue() / ((Float) upFactor.Method1182()).floatValue())) {
                        d = d3 * 0.01325;
                        ElytraFly.mc.player.motionY += d * 3.2;
                        ElytraFly.mc.player.motionX -= vec3d.x * d / d2;
                        ElytraFly.mc.player.motionZ -= vec3d.z * d / d2;
                    } else {
                        double[] dArray = MathUtil.Method1086(((Float) speed.getValue()).floatValue());
                        ElytraFly.mc.player.motionX = dArray[0];
                        ElytraFly.mc.player.motionZ = dArray[1];
                    }
                }
                if (d2 > 0.0) {
                    ElytraFly.mc.player.motionX += (vec3d.x / d2 * d3 - ElytraFly.mc.player.motionX) * 0.1;
                    ElytraFly.mc.player.motionZ += (vec3d.z / d2 * d3 - ElytraFly.mc.player.motionZ) * 0.1;
                }
                if (mode.getValue() == Class403.CONTROL && !ElytraFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                    double[] dArray = MathUtil.Method1086(((Float) speed.getValue()).floatValue());
                    ElytraFly.mc.player.motionX = dArray[0];
                    ElytraFly.mc.player.motionZ = dArray[1];
                }
                if (!((Boolean) noDrag.getValue()).booleanValue()) {
                    ElytraFly.mc.player.motionX *= (double)0.99f;
                    ElytraFly.mc.player.motionY *= (double)0.98f;
                    ElytraFly.mc.player.motionZ *= (double)0.99f;
                }
                double d5 = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
                if (((Boolean) speedLimit.getValue()).booleanValue() && d5 > (double)((Float) maxSpeed.getValue()).floatValue()) {
                    ElytraFly.mc.player.motionX *= (double)((Float) maxSpeed.getValue()).floatValue() / d5;
                    ElytraFly.mc.player.motionZ *= (double)((Float) maxSpeed.getValue()).floatValue() / d5;
                }
                ElytraFly.mc.player.move(MoverType.SELF, ElytraFly.mc.player.motionX, ElytraFly.mc.player.motionY, ElytraFly.mc.player.motionZ);
            } else {
                double d;
                double d6;
                double d7;
                double d8;
                double d9;
                boolean bl = false;
                float f = ElytraFly.mc.player.movementInput.moveForward;
                if (((Boolean) cruiseControl.getValue()).booleanValue()) {
                    if (ElytraFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                        this.Field1004 += (double)((Float) upFactor.getValue()).floatValue() * 0.5;
                    } else if (ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown()) {
                        this.Field1004 -= (double)((Float) downFactor.getValue()).floatValue() * 0.5;
                    }
                    if (((Boolean) forceHeight.getValue()).booleanValue()) {
                        this.Field1004 = ((Integer) height.getValue()).intValue();
                    }
                    double d10 = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
                    d9 = MathHelper.clamp((double)(d10 / 1.7), (double)0.0, (double)1.0);
                    d8 = 1.0 - Math.sqrt(d9);
                    d7 = 0.6;
                    if (d10 >= (Double) minUpSpeed.getValue() && this.Field1006.Method737(2000.0f * ((Float) limit.getValue()).floatValue())) {
                        double d11 = -((45.0 - d7) * d8 + d7);
                        d6 = (this.Field1004 + 1.0 - ElytraFly.mc.player.posY) * 2.0;
                        double d12 = MathHelper.clamp((double)Math.abs(d6), (double)0.0, (double)1.0);
                        double d13 = -Math.toDegrees(Math.atan2(Math.abs(d6), d10 * 30.0)) * Math.signum(d6);
                        double d14 = (d13 - d11) * d12;
                        ElytraFly.mc.player.rotationPitch = (float)d11;
                        ElytraFly.mc.player.rotationPitch += (float)d14;
                        ElytraFly.mc.player.prevRotationPitch = ElytraFly.mc.player.rotationPitch;
                    } else {
                        ElytraFly.mc.player.rotationPitch = 0.25f;
                        ElytraFly.mc.player.prevRotationPitch = 0.25f;
                        f = 1.0f;
                    }
                }
                Vec3d vec3d = ElytraFly.mc.player.getLookVec();
                float f3 = ElytraFly.mc.player.rotationPitch * ((float)Math.PI / 180);
                d9 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                d8 = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
                d7 = vec3d.length();
                float f4 = MathHelper.cos((float)f3);
                f4 = (float)((double)f4 * (double)f4 * Math.min(1.0, d7 / 0.4));
                ElytraFly.mc.player.motionY += -0.08 + (double)f4 * 0.06;
                if (ElytraFly.mc.player.motionY < 0.0 && d9 > 0.0) {
                    d = ElytraFly.mc.player.motionY * -0.1 * (double)f4;
                    ElytraFly.mc.player.motionY += d;
                    ElytraFly.mc.player.motionX += vec3d.x * d / d9;
                    ElytraFly.mc.player.motionZ += vec3d.z * d / d9;
                }
                if (f3 < 0.0f) {
                    d = d8 * (double)(-MathHelper.sin((float)f3)) * 0.04;
                    ElytraFly.mc.player.motionY += d * 3.2;
                    ElytraFly.mc.player.motionX -= vec3d.x * d / d9;
                    ElytraFly.mc.player.motionZ -= vec3d.z * d / d9;
                }
                if (d9 > 0.0) {
                    ElytraFly.mc.player.motionX += (vec3d.x / d9 * d8 - ElytraFly.mc.player.motionX) * 0.1;
                    ElytraFly.mc.player.motionZ += (vec3d.z / d9 * d8 - ElytraFly.mc.player.motionZ) * 0.1;
                }
                if (!((Boolean) noDrag.getValue()).booleanValue()) {
                    ElytraFly.mc.player.motionX *= (double)0.99f;
                    ElytraFly.mc.player.motionY *= (double)0.98f;
                    ElytraFly.mc.player.motionZ *= (double)0.99f;
                }
                float f5 = ElytraFly.mc.player.rotationYaw * ((float)Math.PI / 180);
                if (f3 > 0.0f && (ElytraFly.mc.player.motionY < 0.0 || bl)) {
                    if (f != 0.0f && this.Field1006.Method737(2000.0f * ((Float) limit.getValue()).floatValue()) && this.Field1007.Method737(1000.0f * ((Float) delay.getValue()).floatValue())) {
                        if (((Boolean) stopMotion.getValue()).booleanValue()) {
                            ElytraFly.mc.player.motionX = 0.0;
                            ElytraFly.mc.player.motionZ = 0.0;
                        }
                        this.Field1006.Method739();
                        ElytraFly.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity) ElytraFly.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                    } else if (!this.Field1006.Method737(2000.0f * ((Float) limit.getValue()).floatValue())) {
                        ElytraFly.mc.player.motionX -= (double)f * Math.sin(f5) * (double)((Float) factor.getValue()).floatValue() / 20.0;
                        ElytraFly.mc.player.motionZ += (double)f * Math.cos(f5) * (double)((Float) factor.getValue()).floatValue() / 20.0;
                        this.Field1007.Method739();
                    }
                }
                d6 = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
                if (((Boolean) speedLimit.getValue()).booleanValue() && d6 > (double)((Float) maxSpeed.getValue()).floatValue()) {
                    ElytraFly.mc.player.motionX *= (double)((Float) maxSpeed.getValue()).floatValue() / d6;
                    ElytraFly.mc.player.motionZ *= (double)((Float) maxSpeed.getValue()).floatValue() / d6;
                }
                if (((Boolean) freeze.getValue()).booleanValue() && Keyboard.isKeyDown((int)56)) {
                    ElytraFly.mc.player.setVelocity(0.0, 0.0, 0.0);
                }
                ElytraFly.mc.player.move(MoverType.SELF, ElytraFly.mc.player.motionX, ElytraFly.mc.player.motionY, ElytraFly.mc.player.motionZ);
            }
        }
    }

    public static boolean Method986() {
        return mode.getValue() == Class403.FIREWORK || mode.getValue() == Class403.BOOST && (Boolean) cruiseControl.getValue() != false;
    }

    public static boolean Method987() {
        return mode.getValue() == Class403.BOOST;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Subscriber
    public void Method503(MoveEvent moveEvent) {
        if (mode.getValue() == Class403.PACKET) {
            if (ElytraFly.mc.player.onGround) {
                return;
            }
            if (!Field1000) {
                return;
            }
            if (((Boolean) accelerate.getValue()).booleanValue()) {
                if (this.Field1001) {
                    this.Field1002 = 1.0;
                    this.Field1001 = false;
                }
                if (this.Field1002 < (double)((Float) factor.getValue()).floatValue()) {
                    this.Field1002 += 0.1 * (double)((Float) acceleration.getValue()).floatValue();
                }
                if (this.Field1002 - 0.1 > (double)((Float) factor.getValue()).floatValue()) {
                    this.Field1002 -= 0.1 * (double)((Float) acceleration.getValue()).floatValue();
                }
            } else {
                this.Field1002 = ((Float) factor.getValue()).floatValue();
            }
            if (ElytraFly.mc.gameSettings.keyBindJump.isKeyDown()) {
                ElytraFly.mc.player.motionY = ((Float) upFactor.getValue()).floatValue();
                moveEvent.setY(ElytraFly.mc.player.motionY);
            } else if (ElytraFly.mc.gameSettings.keyBindSneak.isKeyDown()) {
                ElytraFly.mc.player.motionY = -((Float) downFactor.getValue()).floatValue();
                moveEvent.setY(ElytraFly.mc.player.motionY);
            } else if (strict.getValue() == Class426.NORMAL) {
                if (ElytraFly.mc.player.ticksExisted % 32 == 0 && !this.Field1001 && (Math.abs(moveEvent.getX()) >= 0.05 || Math.abs(moveEvent.getZ()) >= 0.05)) {
                    ElytraFly.mc.player.motionY = -2.0E-4;
                    moveEvent.setY(0.006200000000000001);
                } else {
                    ElytraFly.mc.player.motionY = -2.0E-4;
                    moveEvent.setY(-2.0E-4);
                }
            } else if (strict.getValue() == Class426.GLIDE) {
                ElytraFly.mc.player.motionY = -1.0E-5f;
                moveEvent.setY(-1.0E-5f);
            } else {
                ElytraFly.mc.player.motionY = 0.0;
                moveEvent.setY(0.0);
            }
            moveEvent.setX(moveEvent.getX() * (this.Field1001 ? 0.0 : this.Field1002));
            moveEvent.setZ(moveEvent.getZ() * (this.Field1001 ? 0.0 : this.Field1002));
            if (((Boolean) antiKick.getValue()).booleanValue() && moveEvent.getX() == 0.0 && moveEvent.getZ() == 0.0 && !this.Field1001) {
                moveEvent.setX(Math.sin(Math.toRadians(ElytraFly.mc.player.ticksExisted % 360)) * 0.03);
                moveEvent.setZ(Math.cos(Math.toRadians(ElytraFly.mc.player.ticksExisted % 360)) * 0.03);
            }
            this.Field1001 = false;
        }
    }

    public static boolean Method538() {
        return mode.getValue() != Class403.PACKET;
    }

    public static boolean Method988() {
        return mode.getValue() == Class403.CONTROL;
    }

    @Override
    public void onDisable() {
        if (ElytraFly.mc.player != null) {
            if (!ElytraFly.mc.player.isCreative()) {
                ElytraFly.mc.player.capabilities.allowFlying = false;
            }
            ElytraFly.mc.player.capabilities.isFlying = false;
        }
        NewGui.INSTANCE.Field1134.Method749(this);
        Field1000 = false;
    }

    public static boolean Method989() {
        return mode.getValue() != Class403.PACKET && mode.getValue() != Class403.FIREWORK;
    }

    public static boolean Method990() {
        return mode.getValue() == Class403.BOOST;
    }

    public static boolean Method991() {
        return mode.getValue() == Class403.FIREWORK && (Boolean) groundSafety.getValue() != false;
    }

    public static boolean Method992() {
        return mode.getValue() == Class403.PACKET;
    }

    public static boolean Method993() {
        return (Boolean) speedLimit.getValue() != false && mode.getValue() != Class403.PACKET && mode.getValue() != Class403.FIREWORK;
    }

    public static boolean Method994() {
        return mode.getValue() == Class403.PACKET;
    }
}
