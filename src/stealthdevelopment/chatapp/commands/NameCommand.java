package stealthdevelopment.chatapp.commands;

import eu.derzauberer.javautils.sockets.Client;
import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.Sender;
import eu.derzauberer.javautils.util.Sender.MessageType;
import stealthdevelopment.chatapp.main.Main;

public class NameCommand implements Command {

	@Override
	public boolean onCommand(Sender sender, String label, String[] args) throws Exception {
		if (args.length < 1) {
			sender.sendMessage(MessageType.ERROR, "Not enough arguments!");
		} else if (Main.getNames().values().contains(args[0])) {
			sender.sendMessage(MessageType.ERROR, "This name does already exist!");
		} else {
			String oldName = Main.getNames().get((Client) sender);
			Main.getNames().put((Client) sender, args[0]);
			Main.getServer().sendMessage(MessageType.INFO, "{} is {} now!", oldName, args[0]);
			return true;
		}
		return false;
	}

}
