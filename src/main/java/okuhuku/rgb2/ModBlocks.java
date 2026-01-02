package okuhuku.rgb2;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.Field;   // ← これが抜けてた！！！
import java.util.HashMap;
import java.util.Map;
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Rgb2.MOD_ID);
    // ここに全部入る！（1677万個でもメモリ食わないよ）
    public static final Map<String, RegistryObject<Block>> RGB_BLOCKS = new HashMap<>();
//    public static final RegistryObject<Block> BLACK_BLOCK = BLOCKS.register("000000",
//            () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_BLACK)
//                    .strength(0.1F, 2000.0F)
//                    .requiresCorrectToolForDrops()
//            ));
//
//    public static final RegistryObject<Block> WHITE_BLOCK = BLOCKS.register("ffffff",
//            () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WOOL)
//                    .strength(0.1F, 2000.0F)
//                    .requiresCorrectToolForDrops()
//            ));
// ModBlocks.java の static { … } の中のループをこれに変更
static {
    String[] levels = {"00","11", "22", "33", "44", "55", "66", "77", "88", "99", "aa", "bb", "cc", "dd" ,"ee", "ff"};
    for (String rs : levels) {
        for (String gs : levels) {
            for (String bs : levels) {
                String hex = rs + gs + bs;
                int r = Integer.parseInt(rs, 16);
                int g = Integer.parseInt(gs,16);
                int b = Integer.parseInt(bs,16);

                MaterialColor color = findClosestMaterialColor(r, g, b);

                RegistryObject<Block> block = BLOCKS.register(hex, () -> new Block(
                        BlockBehaviour.Properties.of(Material.METAL, color)
                                .strength(0.1F, 2000.0F)
                                .requiresCorrectToolForDrops()
                ));

                // ここで同時にItemも登録！
                ModItems.registerBlockItem(hex, block);

                RGB_BLOCKS.put(hex, block);
            }
        }
    }
}

    private static MaterialColor findClosestMaterialColor(int r, int g, int b) {
        try {
            Field field = MaterialColor.class.getDeclaredField("MATERIAL_COLORS");
            field.setAccessible(true);
            MaterialColor[] colors = (MaterialColor[]) field.get(null);

            MaterialColor closest = MaterialColor.NONE;
            int minDistance = Integer.MAX_VALUE;

            for (MaterialColor color : colors) {
                if (color == null || color == MaterialColor.NONE || color.col == 0) continue;

                int cr = (color.col >> 16) & 0xFF;
                int cg = (color.col >> 8)  & 0xFF;
                int cb =  color.col        & 0xFF;

                int distance = (cr - r) * (cr - r) + (cg - g) * (cg - g) + (cb - b) * (cb - b);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = color;
                }
            }
            return closest != MaterialColor.NONE ? closest : MaterialColor.COLOR_BLACK;

        } catch (Exception e) {
            // 万が一失敗したら黒固定（安全）
            return MaterialColor.COLOR_BLACK;
        }
    }
}
