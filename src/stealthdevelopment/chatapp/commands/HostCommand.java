package stealthdevelopment.chatapp.commands;

import java.io.IOException;

import eu.derzauberer.javautils.util.Command;
import eu.derzauberer.javautils.util.DataUtil;
import eu.derzauberer.javautils.util.Sender;
import eu.derzauberer.javautils.util.Sender.MessageType;
import stealthdevelopment.chatapp.main.Main;

public class HostCommand implements Command {

	@Override
	public boolean onCommand(Sender sender, String label, String[] args) throws Exception {
		if (args.length < 1) {
			sender.sendMessage(MessageType.ERROR, "Not enough arguments!");
			return false;
		} else if (!DataUtil.isIntegerString(args[0])) {
			sender.sendMessage(MessageType.ERROR, args[0] + " is not an integer!");
			return false;
		} else {
			try {
				Main.setServer(Integer.parseInt(args[0]));
				sender.sendMessage(MessageType.INFO, "Running server on " + Main.getServer().getAdress() + ":" + Main.getServer().getLocalPort() + "!");
				return true;
			} catch (IOException exception) {
				sender.sendMessage(MessageType.ERROR, exception.getMessage());
				return false;
			}	
		}
	}

}
