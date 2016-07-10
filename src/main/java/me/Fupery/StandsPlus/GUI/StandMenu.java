package me.Fupery.StandsPlus.GUI;


import me.Fupery.InventoryMenu.Utils.SoundCompat;
import me.Fupery.StandsPlus.GUI.API.InventoryMenu;
import me.Fupery.StandsPlus.GUI.API.MenuButton;
import me.Fupery.StandsPlus.StandPart;
import me.Fupery.StandsPlus.StandsPlus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

/**
 * Created by aidenhatcher on 10/07/2016.
 */
public class StandMenu extends InventoryMenu {
    private final ArmorStand stand;
    private final StandsPlus plugin;
    private boolean switchingMenus = false;

    public StandMenu(StandsPlus plugin, ArmorStand stand) {
        super(null, ChatColor.BOLD + "Editing Armor Stand", InventoryType.DISPENSER);
        this.stand = stand;
        MenuButton[] buttons = new MenuButton[]{
                new PropertyButton(StandProperty.BASEPLATE),
                new PartButton(Material.CHAINMAIL_HELMET, StandPart.HEAD),
                new PropertyButton(StandProperty.ARMS),
                new PartButton(Material.LEVER, StandPart.LEFT_ARM),
                new PartButton(Material.CHAINMAIL_CHESTPLATE, StandPart.BODY),
                new PartButton(Material.LEVER, StandPart.RIGHT_ARM),
                new PropertyButton(StandProperty.GRAVITY),
                new LegButton(),
                new PropertyButton(StandProperty.SMALL)
        };
        this.plugin = plugin;
        addButtons(buttons);
    }

    @Override
    protected Map<UUID, InventoryMenu> getOpenMenus() {
        return plugin.getOpenMenus();
    }

    @Override
    public void open(JavaPlugin plugin, Player player) {
        super.open(plugin, player);
        stand.setGlowing(true);
        switchingMenus = false;
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (!switchingMenus) stand.setGlowing(false);
    }

    protected ArmorStand getStand() {
        return stand;
    }

    private StandMenu getMenu() {
        return this;
    }

    public StandsPlus getPlugin() {
        return plugin;
    }

    private class PartButton extends MenuButton {

        private StandPart part;

        PartButton(Material type, StandPart part) {
            super(type, part.fancyName(true), ChatColor.GREEN + "Click to rotate");
            this.part = part;
            ItemMeta meta = getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            setItemMeta(meta);
        }

        @Override
        public void onClick(JavaPlugin plugin, Player player, ClickType click) {
            switchingMenus = true;
            new PartMenu(getMenu(), stand, part).open(plugin, player);
        }
    }

    private class LegButton extends MenuButton {

        LegButton() {
            super(Material.CHAINMAIL_LEGGINGS, ChatColor.GOLD + "§e•§6 Legs §e•",
                    "§aLeft-Click §7to rotate §aLeft Leg",
                    "§aRight-Click §7to rotate §aRight Leg");
            ItemMeta meta = getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            setItemMeta(meta);
        }

        @Override
        public void onClick(JavaPlugin plugin, Player player, ClickType click) {
            switchingMenus = true;
            if (click.isLeftClick()) {
                new PartMenu(getMenu(), stand, StandPart.LEFT_LEG).open(plugin, player);
            } else if (click.isRightClick()) {
                new PartMenu(getMenu(), stand, StandPart.RIGHT_LEG).open(plugin, player);
            }
        }
    }

    private class PropertyButton extends MenuButton {

        boolean value;
        StandProperty property;

        PropertyButton(StandProperty property) {
            super(property.getIcon(), property.getButtonTitle(property.getValue(stand)),
                    property.getDescription(), ChatColor.YELLOW + "Click to toggle");
            this.value = property.getValue(stand);
            this.property = property;
            if (value) {
                addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
            }
            ItemMeta meta = getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            setItemMeta(meta);
        }

        @Override
        public void onClick(JavaPlugin plugin, Player player, ClickType click) {
            value = !value;
            if (value) {
                addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
            } else {
                removeEnchantment(Enchantment.LOOT_BONUS_MOBS);
            }
            property.apply(stand, value);
            updateButton();
            updateInventory(plugin, player);
            SoundCompat.UI_BUTTON_CLICK.play(player);
        }

        void updateButton() {
            ItemMeta meta = getItemMeta();
            meta.setDisplayName(property.getButtonTitle(value));
            setItemMeta(meta);
        }
    }
}