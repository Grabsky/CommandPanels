package me.rockyhawk.commandpanels.builder.inventory.items.itemcomponents;

import me.rockyhawk.commandpanels.Context;
import me.rockyhawk.commandpanels.builder.inventory.items.ItemComponent;
import me.rockyhawk.commandpanels.session.inventory.PanelItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// this tag replaces all components on an item (except for material and amount), therefore, is generally not advised to be used with non-vanilla material components (e.g., nexo, itemsadder)
public class ComponentsComponent implements ItemComponent {

    @Override
    public ItemStack apply(Context ctx, ItemStack itemStack, Player player, PanelItem item) {
        if (item.components() == null || item.components().isEmpty()) return itemStack;
        // building components string
        final StringBuilder componentsBuilder = new StringBuilder(itemStack.getType().key().asString() + "[");
        item.components().forEach((component, value) -> {
            componentsBuilder.append(component).append("=").append(value).append(",");
        });
        // removing trailing comma
        componentsBuilder.deleteCharAt(componentsBuilder.length() - 1);
        componentsBuilder.append("]");
        // https://github.com/PaperMC/Paper/blob/main/paper-server/src/main/java/org/bukkit/craftbukkit/util/CraftMagicNumbers.java
        // in case of malformed input, this method does not throw but fails with a non-catchable console error
        // in case of failure, the original item stack is returned
        return Bukkit.getUnsafe().modifyItemStack(itemStack, componentsBuilder.toString());
    }
}
