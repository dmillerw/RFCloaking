package dmillerw.shield.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

/**
 * @author dmillerw
 */
public class ShieldTransformer implements IClassTransformer {

    private static FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

    private static final String STATIC_HANDLER = "dmillerw/shield/asm/StaticMethodHandler";

    private static final String STATIC_RENDERBLOCK = "shouldRenderBlock";
    private static final String STATIC_RENDERBLOCK_DESC = "(Lnet/minecraft/block/Block;ILnet/minecraft/world/World;III)Z";

    private static final String STATIC_RENDERTILE = "shouldRenderTile";
    private static final String STATIC_RENDERTILE_DESC = "(Lnet/minecraft/tileentity/TileEntity;Lnet/minecraft/world/World;III)Z";

    private static final String STATIC_RAYTRACE = "raytraceViaVec";
    private static final String STATIC_RAYTRACE_DESC = "(Lnet/minecraft/world/World;Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZZ)Lnet/minecraft/util/MovingObjectPosition;";

    private static final String STATIC_LIGHTOPACITY = "getLightOpacity";
    private static final String STATIC_LIGHTOPACITY_DESC = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;III)I";

    private static final String STATIC_RENDERSIDE = "shouldSideBeRendered";
    private static final String STATIC_RENDERSIDE_DESC = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/IBlockAccess;IIII)Z";

    private static final String STATIC_COLLIDE = "addCollisionBoxesToList";
    private static final String STATIC_COLLIDE_DESC = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;IIILnet/minecraft/util/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V";

    private static final String STATIC_NORMAL = "isBlockNormalCube";
    private static final String STATIC_NORMAL_DESC = "(Lnet/minecraft/world/World;III)Z";

    public static final Side getSide() {
        Thread thr = Thread.currentThread();
        if ((thr.getName().equals("Server thread"))) {
            return Side.SERVER;
        }

        return Side.CLIENT;
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (getSide() == Side.CLIENT) {
            if (transformedName.equals("net.minecraft.client.renderer.WorldRenderer")) {
                return transformWorldRenderer(name, basicClass);
            } else if (transformedName.equals("net.minecraft.world.World")) {
                return transformWorld_client(name, basicClass);
            } else if (transformedName.equals("net.minecraft.entity.Entity")) {
                return transformEntity(name, basicClass);
            } else if (transformedName.equals("net.minecraft.world.chunk.Chunk")) {
                return transformChunk(name, basicClass);
            } else if (transformedName.equals("net.minecraft.client.renderer.RenderBlocks")) {
                return transformRenderBlocks(name, basicClass);
            } else if (transformedName.equals("net.minecraft.entity.EntityLivingBase")) {
                return transformEntityLivingBase(name, basicClass);
            } else {
                return basicClass;
            }
        } else {
            if (transformedName.equals("net.minecraft.world.World")) {
                return transformWorld_server(name, basicClass);
            } else {
                return basicClass;
            }
        }
    }

    private byte[] transformWorldRenderer(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode targetNode = null;
        MethodInsnNode targetInsnNode1 = null;
        MethodInsnNode targetInsnNode2 = null;

        FieldInsnNode getRenderDispatcher = null;

        for (MethodNode methodNode : classNode.methods) {
            if (equals(methodNode.name, "updateRenderer")) {
                targetNode = methodNode;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "canRenderInPass")) {
                            targetInsnNode1 = methodInsnNode;
                        } else if (equals(methodInsnNode.name, "hasSpecialRenderer")) {
                            targetInsnNode2 = methodInsnNode;
                        }
                    } else if (abstractInsnNode.getOpcode() == Opcodes.GETSTATIC) {
                        FieldInsnNode methodInsnNode = (FieldInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "instance")) {
                            getRenderDispatcher = methodInsnNode;
                        }
                    }
                }
            }
        }

        int x = -1;
        int y = -1;
        int z = -1;
        int block = -1;
        int pass = -1;

        if (targetNode != null) {
            for (LocalVariableNode localVariableNode : targetNode.localVariables) {
                if (equals(localVariableNode.name, "j3")) x = localVariableNode.index;
                if (equals(localVariableNode.name, "l2")) y = localVariableNode.index;
                if (equals(localVariableNode.name, "i3")) z = localVariableNode.index;
                if (equals(localVariableNode.name, "block")) block = localVariableNode.index;
                if (equals(localVariableNode.name, "k2")) pass = localVariableNode.index;
            }

            if (allSet(x, y, z, block, pass)) {
                if (targetInsnNode1 != null) {
                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new FieldInsnNode(Opcodes.GETFIELD, name.replace(".", "/"), "worldObj", "Lnet/minecraft/world/World;"));
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, x));
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, y));
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, z));

                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_RENDERBLOCK, STATIC_RENDERBLOCK_DESC, false));

                    targetNode.instructions.insertBefore(targetInsnNode1, insnList);
                    targetNode.instructions.remove(targetInsnNode1);
                }

                if (targetInsnNode2 != null && getRenderDispatcher != null) {
                    targetNode.instructions.remove(getRenderDispatcher);

                    InsnList insnList = new InsnList();
                    insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    insnList.add(new FieldInsnNode(Opcodes.GETFIELD, name.replace(".", "/"), "worldObj", "Lnet/minecraft/world/World;"));
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, x));
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, y));
                    insnList.add(new VarInsnNode(Opcodes.ILOAD, z));

                    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_RENDERTILE, STATIC_RENDERTILE_DESC, false));

                    targetNode.instructions.insertBefore(targetInsnNode2, insnList);
                    targetNode.instructions.remove(targetInsnNode2);
                }

                ClassWriter classWriter = new ClassWriter(0);
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            }
        }

        return clazz;
    }

    private byte[] transformWorld_server(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode methodGetBB = null;

        MethodInsnNode targetOldInvoke = null;

        for (MethodNode methodNode : classNode.methods) {
            if (equals(methodNode.name, "getCollidingBoundingBoxes")) {
                methodGetBB = methodNode;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "addCollisionBoxesToList")) {
                            targetOldInvoke = methodInsnNode;
                        }
                    }
                }
            }
        }

        if (methodGetBB != null && targetOldInvoke != null) {
            methodGetBB.instructions.insertBefore(targetOldInvoke, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_COLLIDE, STATIC_COLLIDE_DESC, false));
            methodGetBB.instructions.remove(targetOldInvoke);

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }

        return clazz;
    }

    private byte[] transformWorld_client(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode methodGetBB = null;
        MethodNode calcLight = null;
        MethodNode updateLight = null;

        MethodInsnNode targetOldInvoke = null;
        MethodInsnNode targetOldGetLightOpac1 = null;
        MethodInsnNode targetOldGetLightOpac2 = null;

        for (MethodNode methodNode : classNode.methods) {
            if (equals(methodNode.name, "getCollidingBoundingBoxes")) {
                methodGetBB = methodNode;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "addCollisionBoxesToList")) {
                            targetOldInvoke = methodInsnNode;
                        }
                    }
                }
            } else if (equals(methodNode.name, "computeLightValue")) {
                calcLight = methodNode;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "getLightOpacity")) {
                            targetOldGetLightOpac1 = methodInsnNode;
                        }
                    }
                }
            } else if (equals(methodNode.name, "updateLightByType")) {
                updateLight = methodNode;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "getLightOpacity")) {
                            targetOldGetLightOpac2 = methodInsnNode;
                        }
                    }
                }
            }
        }

        boolean b_getBB = false;
        boolean b_calcLight = false;
        boolean b_updateLight = false;

        if (methodGetBB != null && targetOldInvoke != null) {
            methodGetBB.instructions.insertBefore(targetOldInvoke, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_COLLIDE, STATIC_COLLIDE_DESC, false));
            methodGetBB.instructions.remove(targetOldInvoke);
            b_getBB = true;
        }

        if (calcLight != null && targetOldGetLightOpac1 != null) {
            calcLight.instructions.insertBefore(targetOldGetLightOpac1, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_LIGHTOPACITY, STATIC_LIGHTOPACITY_DESC, false));
            calcLight.instructions.remove(targetOldGetLightOpac1);
            b_calcLight = true;
        }

        if (updateLight != null && targetOldGetLightOpac2 != null) {
            updateLight.instructions.insertBefore(targetOldGetLightOpac2, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_LIGHTOPACITY, STATIC_LIGHTOPACITY_DESC, false));
            updateLight.instructions.remove(targetOldGetLightOpac2);
            b_updateLight = true;
        }

        if (b_getBB && b_calcLight && b_updateLight) {
            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }

        return clazz;
    }

    private byte[] transformChunk(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode node1 = null;
        MethodNode node2 = null;

        MethodInsnNode opacityCall1 = null;
        MethodInsnNode opacityCall2 = null;
        MethodInsnNode opacityCall3 = null;

        for (MethodNode methodNode : classNode.methods) {
            if (equals(methodNode.name, "func_150808_b")) {
                node1 = methodNode;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "addCollisionBoxesToList")) {
                            opacityCall1 = methodInsnNode;
                        }
                    }
                }
            } else if (equals(methodNode.name, "func_150807_a")) {
                node2 = methodNode;
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "getLightOpacity")) {
                            if (opacityCall2 == null)
                                opacityCall2 = methodInsnNode;
                            else
                                opacityCall3 = methodInsnNode;
                        }
                    }
                }
            }
        }

        boolean b_node1 = false;
        boolean b_node2 = false;
        boolean b_node3 = false;

        if (node1 != null && opacityCall1 != null) {
            node1.instructions.insertBefore(opacityCall1, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_LIGHTOPACITY, STATIC_LIGHTOPACITY_DESC, false));
            node1.instructions.remove(opacityCall1);
            b_node1 = true;
        }

        if (node2 != null) {
            if (opacityCall2 != null) {
                node2.instructions.insertBefore(opacityCall2, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_LIGHTOPACITY, STATIC_LIGHTOPACITY_DESC, false));
                node2.instructions.remove(opacityCall2);
                b_node2 = true;
            }

            if (opacityCall3 != null) {
                node2.instructions.insertBefore(opacityCall3, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_LIGHTOPACITY, STATIC_LIGHTOPACITY_DESC, false));
                node2.instructions.remove(opacityCall3);
                b_node3 = true;
            }
        }

        if (b_node1 && b_node2 && b_node3) {
            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }

        return clazz;
    }

    private byte[] transformEntity(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        MethodNode targetNode = null;
        MethodInsnNode targetInsnNode1 = null;
        MethodInsnNode targetInsnNode2 = null;

        for (MethodNode methodNode : classNode.methods) {
            targetNode = methodNode;
            if (equals(methodNode.name, "isEntityInsideOpaqueBlock")) {
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "getBlock")) {
                            targetInsnNode1 = methodInsnNode;
                        } else if (equals(methodInsnNode.name, "isNormalCube")) {
                            targetInsnNode2 = methodInsnNode;
                        }
                    }
                }
            }
        }

        if (targetNode != null && targetInsnNode1 != null && targetInsnNode2 != null) {
            targetNode.instructions.insertBefore(targetInsnNode1, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_NORMAL, STATIC_NORMAL_DESC, false));
            targetNode.instructions.remove(targetInsnNode1);
            targetNode.instructions.remove(targetInsnNode2);

            ClassWriter classWriter = new ClassWriter(0);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        } else {
            return clazz;
        }
    }

    private byte[] transformRenderBlocks(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode abstractInsnNode = iterator.next();
                if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;

                    if (equals(methodInsnNode.name, "shouldSideBeRendered")) {
                        methodNode.instructions.insertBefore(methodInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_RENDERSIDE, STATIC_RENDERSIDE_DESC, false));
                        iterator.remove();
                    }
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private byte[] transformEntityLivingBase(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("rayTrace")) {
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "func_147447_a")) {
                            methodNode.instructions.insertBefore(methodInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_RAYTRACE, STATIC_RAYTRACE_DESC, false));
                            iterator.remove();
                        }
                    }
                }
            }
        }

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private boolean equals(String name, String ... strings) {
        for (String string : strings) {
            if (name.equals(string)) {
                return true;
            }
        }
        return false;
    }

    private boolean allSet(int ... ints) {
        for (int i : ints) {
            if (i == -1) {
                return false;
            }
        }
        return true;
    }
}
