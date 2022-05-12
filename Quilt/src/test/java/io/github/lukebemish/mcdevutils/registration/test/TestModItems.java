package io.github.lukebemish.mcdevutils.registration.test;

import io.github.lukebemish.mcdevutils.registration.api.Registrar;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Registrar(type = Item.class, mod_id = "test_modid")
public class TestModItems {
    @Registrar.Target
    public static TestModItems MOD_ITEMS;

    @Registrar.Exclude
    public String testExcluded = "stuff2";

    @Registrar.Named("item1")
    public Item item1 = new Item(new Item.Properties());

    public List<TestItemPair> itemList = new ArrayList<>();

    public HashMap<String, Item> itemMap = new HashMap<>();

    public TestModItems() {
        for (int i = 0; i < 4; i++) {
            itemMap.put("map_item"+i, new Item(new Item.Properties()));
        }
        for (int i = 0; i < 4; i++) {
            itemList.add(new TestItemPair("list_item"+i, new Item(new Item.Properties())));
        }
    }

    public record TestItemPair(String id, Item item) {}
}
