package de.homerbond005.redstonecommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Command implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0)
            args = new String[] { "help" };
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(ChatColor.RED + "-----RSC Help-----");
                player.sendMessage(ChatColor.RED + "/rsc [name]  " + ChatColor.GREEN + "Toggles Redstone.");
                player.sendMessage(ChatColor.RED + "/rsc list  " + ChatColor.GREEN + "Shows all RSCs.");
                player.sendMessage(ChatColor.RED + "/rsc delete [name]   " + ChatColor.GREEN + "Deletes a RSC entry.");
                player.sendMessage(ChatColor.RED + "/rsc on [name]   " + ChatColor.GREEN + "Turn on a RSC.");
                player.sendMessage(ChatColor.RED + "/rsc off [name]   " + ChatColor.GREEN + "Turn off a RSC.");
                player.sendMessage(ChatColor.RED + "/rsc showmsg [name] [bool]   " + ChatColor.GREEN + "Toggle display of messages.");
                player.sendMessage(ChatColor.RED + "/rsc help   " + ChatColor.GREEN + "Shows this page.");
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (player.hasPermission("RSC.list") || player.hasPermission("RSC.*")) {
                    listRSC(sender);
                } else
                    player.sendMessage(ChatColor.RED + "You do not have the required permission!");
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length == 2)
                    if (player.hasPermission("RSC.delete." + args[1]) || player.hasPermission("RSC.delete.*") || player.hasPermission("RSC.*"))
                        player.sendMessage(deleteRSC(args[1]));
                    else
                        player.sendMessage(ChatColor.RED + "You do not have the required permission!");
                else
                    player.sendMessage(ChatColor.RED + "Wrong arguments! Usage: /rsc delete <name>");
            } else if (args[0].equalsIgnoreCase("on")) {
                if (args.length == 2)
                    if (player.hasPermission("RSC.use." + args[1]) || player.hasPermission("RSC.use.*") || player.hasPermission("RSC.*"))
                        sendMessageToSender(sender, args[1], turnRSCon(args[1]));
                    else
                        player.sendMessage(ChatColor.RED + "You do not have the required permission!");
                else
                    player.sendMessage(ChatColor.RED + "Wrong arguments! Usage: /rsc on <name>");
            } else if (args[0].equalsIgnoreCase("off")) {
                if (args.length == 2)
                    if (player.hasPermission("RSC.use." + args[1]) || player.hasPermission("RSC.use.*") || player.hasPermission("RSC.*"))
                        sendMessageToSender(sender, args[1], turnRSCoff(args[1]));
                    else
                        player.sendMessage(ChatColor.RED + "You do not have the required permission!");
                else
                    player.sendMessage(ChatColor.RED + "Wrong arguments! Usage: /rsc off <name>");
            } else if (args[0].equalsIgnoreCase("showmsg")) {
                if (args.length == 3)
                    if (player.hasPermission("RSC.setmsg." + args[1]) || player.hasPermission("RSC.setmsg.*") || player.hasPermission("RSC.*"))
                        player.sendMessage(setMsg(args[1], args[2]));
                    else
                        player.sendMessage(ChatColor.RED + "You do not have the required permission!");
                else
                    player.sendMessage(ChatColor.RED + "Wrong arguments! Usage: /rsc showmsg <name> <boolean>");
            } else {
                if (player.hasPermission("RSC.use." + args[0]) || player.hasPermission("RSC.use.*") || player.hasPermission("RSC.*"))
                    toggleRSC(args[0], sender);
                else
                    player.sendMessage(ChatColor.RED + "You do not have the required permission!");
            }
        } else {
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(ChatColor.RED + "-----RSC Help-----");
                sender.sendMessage(ChatColor.RED + "rsc [name]  " + ChatColor.GREEN + "Toggles Redstone.");
                sender.sendMessage(ChatColor.RED + "rsc list  " + ChatColor.GREEN + "Shows all RSCs.");
                sender.sendMessage(ChatColor.RED + "rsc delete [name]   " + ChatColor.GREEN + "Deletes a RSC entry.");
                sender.sendMessage(ChatColor.RED + "rsc on [name]   " + ChatColor.GREEN + "Turn on a RSC.");
                sender.sendMessage(ChatColor.RED + "rsc off [name]   " + ChatColor.GREEN + "Turn off a RSC.");
                sender.sendMessage(ChatColor.RED + "rsc showmsg [name] [bool]   " + ChatColor.GREEN + "Toggle display of messages.");
                sender.sendMessage(ChatColor.RED + "rsc help   " + ChatColor.GREEN + "Shows this page.");
                return true;
            } else if (args[0].equalsIgnoreCase("list")) {
                listRSC(sender);
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length == 2)
                    sender.sendMessage(deleteRSC(args[1]));
                else
                    sender.sendMessage(ChatColor.RED + "Wrong arguments! Usage: rsc delete <name>");
            } else if (args[0].equalsIgnoreCase("on")) {
                if (args.length == 2)
                    sendMessageToSender(sender, args[1], turnRSCon(args[1]));
                else
                    sender.sendMessage(ChatColor.RED + "Wrong arguments! Usage: rsc on <name>");
            } else if (args[0].equalsIgnoreCase("off")) {
                if (args.length == 2)
                    sendMessageToSender(sender, args[1], turnRSCoff(args[1]));
                else
                    sender.sendMessage(ChatColor.RED + "Wrong arguments! Usage: rsc off <name>");
            } else if (args[0].equalsIgnoreCase("showmsg")) {
                if (args.length == 3)
                    sender.sendMessage(setMsg(args[1], args[2]));
                else
                    sender.sendMessage(ChatColor.RED + "Wrong arguments! Usage: rsc showmsg <name> <value>");
            } else {
                toggleRSC(args[0], sender);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>(Arrays.asList("list", "delete", "on", "off", "showmsg", "help"));
            list.addAll(RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().keySet());
            return StringUtil.copyPartialMatches(args[0], list, new ArrayList<>());
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("showmsg"))) {
            return StringUtil.copyPartialMatches(args[1], new ArrayList<>(RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().keySet()), new ArrayList<>());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("showmsg")) {
            return StringUtil.copyPartialMatches(args[2], Arrays.asList("true", "false"), new ArrayList<>());
        }
        return Collections.emptyList();
    }

    /**
     * Set the message that is displayed if a RSC is used
     *
     * @param name The name of the RSC
     * @param valueAsString The message that should be set
     * @return An answer that could be sent to the CommandSender
     */
    private String setMsg(String name, String valueAsString) {
        name = name.toLowerCase();
        if (!RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().containsKey(name)) {
            return ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name;
        }
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + name + ".MSG", Boolean.parseBoolean(valueAsString));
        RedstoneCommand.getPlugin(RedstoneCommand.class).saveConfig();
        RedstoneCommand.getPlugin(RedstoneCommand.class).reloadRSCs();
        if (Boolean.parseBoolean(valueAsString)) {
            return ChatColor.GREEN + "Messages will be displayed when using the RSC " + ChatColor.GOLD + name;
        } else {
            return ChatColor.GREEN + "Messages will not be displayed when using the RSC " + ChatColor.GOLD + name;
        }
    }

    /**
     * Turn on a RSC
     *
     * @param name The name of the RSC
     * @return An answer that could be sent to the CommandSender
     */
    public String turnRSCon(String name) {
        name = name.toLowerCase();
        if (!RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().containsKey(name)) {
            return ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name;
        }
        RSCSign rsc = RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().get(name);
        if (rsc.isON())
            return ChatColor.GREEN + "The RSC " + ChatColor.GOLD + name + ChatColor.GREEN + " is already on.";
        rsc.turnON();
        return ChatColor.GREEN + "Successfully turned on RSC named " + ChatColor.GOLD + name;
    }

    /**
     * Turn off a RSC
     *
     * @param name The name of the RSC
     * @return An answer that could be sent to the CommandSender
     */
    public String turnRSCoff(String name) {
        name = name.toLowerCase();
        if (!RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().containsKey(name)) {
            return ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name;
        }
        RSCSign rsc = RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().get(name);
        if (!rsc.isON())
            return ChatColor.GREEN + "The RSC " + ChatColor.GOLD + name + ChatColor.GREEN + " is already off.";
        rsc.turnOFF();
        return ChatColor.GREEN + "Successfully turned off RSC named " + ChatColor.GOLD + name;
    }

    /**
     * Delete a RSC
     *
     * @param name The name of the RSC
     * @return An answer that could be sent to the CommandSender
     */
    public String deleteRSC(String name) {
        name = name.toLowerCase();
        if (!RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().containsKey(name)) {
            return ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name;
        }
        RSCSign rsc = RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().get(name);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".X", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".Y", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".Z", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".Xchange", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".Ychange", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".Zchange", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".WORLD", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".MSG", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + rsc.getName() + ".DELAY", null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().set("RedstoneCommands.Locations." + name, null);
        RedstoneCommand.getPlugin(RedstoneCommand.class).getConfig().options().copyDefaults(false);
        RedstoneCommand.getPlugin(RedstoneCommand.class).saveConfig();
        rsc.turnOFF();
        rsc.getSignLocation().getBlock().setType(Material.AIR);
        RedstoneCommand.getPlugin(RedstoneCommand.class).reloadRSCs();
        return ChatColor.GREEN + "Successfully deleted the RSC " + ChatColor.GOLD + name;
    }

    /**
     * List all defined RSCs
     *
     * @param sender The CommandSender that executed the list command
     */
    public void listRSC(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "The following RSCs are set:");
        if (RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().size() == 0) {
            sender.sendMessage(ChatColor.GRAY + "No RSCs are set.");
        }
        StringBuilder rscsstring = new StringBuilder();
        for (String rsc : RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().keySet())
            rscsstring.append(rsc).append(", ");
        if (rscsstring.length() != 0)
            rscsstring = new StringBuilder(rscsstring.substring(0, rscsstring.length() - 2));
        sender.sendMessage(ChatColor.GOLD + rscsstring.toString());
    }

    /**
     * Toggle a RSC
     *
     * @param name The name of the RSC
     * @param sender The CommandSender that executed the toggle command
     */
    public void toggleRSC(String name, final CommandSender sender) {
        name = name.toLowerCase();
        if (!RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().containsKey(name)) {
            sendMessageToSender(sender, name, ChatColor.RED + "The following RSC doesn't exist: " + ChatColor.GOLD + name);
            return;
        }
        final RSCSign rsc = RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().get(name);
        if (rsc.isON()) {
            rsc.turnOFF();
            sendMessageToSender(sender, rsc.getName(), ChatColor.GREEN + "Successfully turned off RSC named " + ChatColor.GOLD + name);
        } else {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RedstoneCommand.getPlugin(RedstoneCommand.class), () -> {
                if (rsc.getDelay() != 0) {
                    rsc.turnOFF();
                    sendMessageToSender(sender, rsc.getName(), ChatColor.GREEN + "Successfully delayed RSC named " + ChatColor.GOLD + rsc.getName());
                } else {
                    sendMessageToSender(sender, rsc.getName(), ChatColor.GREEN + "Successfully turned on RSC named " + ChatColor.GOLD + rsc.getName());
                }
            }, 20L * rsc.getDelay());
            rsc.turnON();
        }
    }

    /**
     * Transfer a message to a CommandSender
     *
     * @param sender The CommandSender that should receive the message
     * @param rsc The name of the RSC that
     * @param msg The message that should be transfered
     */
    private void sendMessageToSender(CommandSender sender, String rsc,
                                     String msg) {
        rsc = rsc.toLowerCase();
        if (!RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().containsKey(rsc)) {
            sender.sendMessage(msg);
        } else {
            if (RedstoneCommand.getPlugin(RedstoneCommand.class).getRscs().get(rsc).displayMessages())
                sender.sendMessage(msg);
        }
    }
}
