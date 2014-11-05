package dmillerw.cloak.asm;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

/**
 * @author dmillerw
 */
public class CloakTransformer implements IClassTransformer {

    private static FMLDeobfuscatingRemapper remapper = FMLDeobfuscatingRemapper.INSTANCE;

    private static final String STATIC_HANDLER = "dmillerw/cloak/asm/StaticMethodHandler";

    private static final String STATIC_GETBLOCK = "getBlock";
    private static final String STATIC_GETBLOCK_DESC = "(Lnet/minecraft/world/chunk/Chunk;III)Lnet/minecraft/block/Block;";

    private static final String STATIC_COLLIDE = "addCollisionBoxesToList";
    private static final String STATIC_COLLIDE_DESC = "(Lnet/minecraft/block/Block;Lnet/minecraft/world/World;IIILnet/minecraft/util/AxisAlignedBB;Ljava/util/List;Lnet/minecraft/entity/Entity;)V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.world.World")) {
            return transformWorld(name, basicClass);
        } else if (transformedName.equals("net.minecraft.world.ChunkCache")) {
            return transformChunkCache(name, basicClass);
        } else {
            return basicClass;
        }
    }

    private byte[] transformWorld(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("getBlock")) {
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "getBlock")) {
                            methodNode.instructions.insertBefore(methodInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_GETBLOCK, STATIC_GETBLOCK_DESC, false));
                            iterator.remove();
                        }
                    }
                }
            }

            if (equals(methodNode.name, "getCollidingBoundingBoxes")) {
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "addCollisionBoxesToList")) {
                            methodNode.instructions.insertBefore(methodInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_COLLIDE, STATIC_COLLIDE_DESC, false));
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

    private byte[] transformChunkCache(String name, byte[] clazz) {
        ClassReader classReader = new ClassReader(clazz);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals("getBlock")) {
                Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode abstractInsnNode = iterator.next();
                    if (abstractInsnNode.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) abstractInsnNode;
                        if (equals(methodInsnNode.name, "getBlock")) {
                            methodNode.instructions.insertBefore(methodInsnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, STATIC_HANDLER, STATIC_GETBLOCK, STATIC_GETBLOCK_DESC, false));
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
