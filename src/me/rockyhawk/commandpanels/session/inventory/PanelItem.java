package me.rockyhawk.commandpanels.session.inventory;

import me.rockyhawk.commandpanels.session.CommandActions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record PanelItem(
        String id,
        String material,
        String stack,
        String displayName,
        List<String> lore,
        String attributes,
        String tooltip,
        String animate,
        String conditions,
        CommandActions actions,
        CommandActions leftClick,
        CommandActions rightClick,
        CommandActions shiftLeftClick,
        CommandActions shiftRightClick,
        String damage,
        String itemModel,
        String customModelData,
        String leatherColor,
        String armorTrim,
        String potionColor,
        String potion,
        String tooltipStyle,
        Map<String, String> components,
        List<String> banner,
        List<String> enchantments
) {
    public PanelItem(
            String id,
            String material,
            String stack,
            String displayName,
            List<String> lore,
            String attributes,
            String tooltip,
            String animate,
            String conditions,
            CommandActions actions,
            CommandActions leftClick,
            CommandActions rightClick,
            CommandActions shiftLeftClick,
            CommandActions shiftRightClick,
            String damage,
            String itemModel,
            String customModelData,
            String leatherColor,
            String armorTrim,
            String potionColor,
            String potion,
            String tooltipStyle,
            Map<String, String> components,
            List<String> banner,
            List<String> enchantments
    ) {
        this.id = id;
        this.material = material;
        this.stack = stack;
        this.displayName = displayName;
        this.lore = List.copyOf(lore);
        this.attributes = attributes;
        this.tooltip = tooltip;
        this.animate = animate;
        this.conditions = conditions;
        this.actions = actions;
        this.leftClick = leftClick;
        this.rightClick = rightClick;
        this.shiftLeftClick = shiftLeftClick;
        this.shiftRightClick = shiftRightClick;
        this.damage = damage;
        this.itemModel = itemModel;
        this.customModelData = customModelData;
        this.leatherColor = leatherColor;
        this.armorTrim = armorTrim;
        this.potionColor = potionColor;
        this.potion = potion;
        this.tooltipStyle = tooltipStyle;
        this.components = components;
        this.banner = List.copyOf(banner);
        this.enchantments = List.copyOf(enchantments);
    }

    public static PanelItem fromSection(String id, ConfigurationSection section) {
        String material = section.getString("material", "STONE");
        String stack = section.getString("stack", "1");
        String name = section.getString("name", "");
        List<String> lore = section.getStringList("lore");
        String attributes = section.getString("attributes", "false");
        String tooltip = section.getString("tooltip", "true");
        String animate = section.getString("animate", "");
        String conditions = section.getString("conditions", "");

        CommandActions actions = CommandActions.fromSection(section.getConfigurationSection("actions"));
        CommandActions leftClick = CommandActions.fromSection(section.getConfigurationSection("left-click"));
        CommandActions rightClick = CommandActions.fromSection(section.getConfigurationSection("right-click"));
        CommandActions shiftLeftClick = CommandActions.fromSection(section.getConfigurationSection("shift-left-click"));
        CommandActions shiftRightClick = CommandActions.fromSection(section.getConfigurationSection("shift-right-click"));

        String damage = section.getString("damage", "0");
        String itemModel = section.getString("item-model", null);
        String customModelData = section.getString("custom-model-data", null);
        String leatherColor = section.getString("leather-color", null);
        String armorTrim = section.getString("armor-trim", null);
        String potionColor = section.getString("potion-color", null);
        String potion = section.getString("potion", null);
        String tooltipStyle = section.getString("tooltip-style", null);
        Map<String, String> components = (section.getConfigurationSection("components") != null)
                ? section.getConfigurationSection("components").getValues(false).entrySet().stream()
                        // filtering out anything but strings, booleans and numbers; unfortunately, there's no easy way to convert e.g., map or configuration section to their raw representation
                        .map(it -> {
                            // values represented by a single string value (e.g., item_model) must be quoted
                            // values represented by an array or an object (i.e., lore, custom_name) must not be quoted
                            // this check is not comprehensive but should work in most cases
                            if (it.getValue() instanceof String value)
                                return Map.entry(it.getKey(), (!value.startsWith("{") && !value.startsWith("[")) ? ("\"" + value + "\"") : value);
                            else if (it.getValue() instanceof Boolean || it.getValue() instanceof Number)
                                return Map.entry(it.getKey(), it.getValue().toString());
                            // anything else is considered invalid and should be skipped
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                : null;
        List<String> banner = section.getStringList("banner");
        List<String> enchanted = section.getStringList("enchantments");

        return new PanelItem(
                id,
                material,
                stack,
                name,
                lore,
                attributes,
                tooltip,
                animate,
                conditions,
                actions,
                leftClick,
                rightClick,
                shiftLeftClick,
                shiftRightClick,
                damage,
                itemModel,
                customModelData,
                leatherColor,
                armorTrim,
                potionColor,
                potion,
                tooltipStyle,
                components,
                banner,
                enchanted
        );
    }

    public CommandActions getClickActions(ClickType clickType) {
        if(!actions.requirements().isEmpty() || !actions.commands().isEmpty()) return actions;
        // LEFT case defaults
        return switch (clickType) {
            case RIGHT -> rightClick;
            case SHIFT_LEFT -> shiftLeftClick;
            case SHIFT_RIGHT -> shiftRightClick;
            default -> leftClick;
        };
    }
}