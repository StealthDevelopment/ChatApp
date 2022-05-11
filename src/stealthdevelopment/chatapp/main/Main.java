package stealthdevelopment.chatapp.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import eu.derzauberer.javautils.events.ClientConnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent;
import eu.derzauberer.javautils.events.ClientDisconnectEvent.DisconnectCause;
import eu.derzauberer.javautils.events.ClientMessageReceiveEvent;
import eu.derzauberer.javautils.events.ConsoleInputEvent;
import eu.derzauberer.javautils.handler.CommandHandler;
import eu.derzauberer.javautils.sockets.Client;
import eu.derzauberer.javautils.sockets.Server;
import eu.derzauberer.javautils.util.Console;
import eu.derzauberer.javautils.util.DataUtil;
import eu.derzauberer.javautils.util.Sender.MessageType;
import stealthdevelopment.chatapp.commands.ConnectCommand;

public class Main {
	
	private static Server server;
	private static Client client;
	private static CommandHandler commands;
	private static Console console;
	
	private static HashMap<Client, String> names = new HashMap<>();
	
	public static void main(String[] args) {
		commands = new CommandHandler();
		commands.registerCommand("/connect", new ConnectCommand());
		console = new Console();
		console.setOnInput(Main::onClientConsoleInput);
		if (args.length == 1) {
			if (DataUtil.isIntegerString(args[0])) {
				try {
					server = new Server(Integer.parseInt(args[0]));
					console.setOnInput(Main::onServerConsoleInput);
					server.setOnMessageReceive(Main::onServerMessageReceive);
					server.setOnClientConnect(Main::onServerClientConnect);
					server.setOnClientDisconnect(Main::onServerClientDisconnect);
					console.sendMessage(MessageType.INFO, "Running server on " + server.getAdress() + ":" + server.getLocalPort() + "!");
				} catch (NumberFormatException | IOException exception) {
					console.sendMessage(MessageType.ERROR, exception.getMessage());
				}
			} else {
				console.sendMessage(MessageType.ERROR, args[0] + "is not a valid port number!");
			}
		}
	}
	
	public static void onClientConsoleInput(ConsoleInputEvent event) {
		if (event.getInput().startsWith("/exit")) System.exit(0);
		else if (event.getInput().startsWith("/connect")) commands.executeCommand(console, event.getInput());
		else if (client != null) client.sendMessage(event.getInput()); 
	}
	
	public static void onServerConsoleInput(ConsoleInputEvent event) {
		if (event.getInput().startsWith("/exit")) System.exit(0);
		else if (event.getInput().startsWith("/")) commands.executeCommand(console, event.getInput());
		else server.sendMessage("Server : " + event.getInput()); 
	}
	
	public static void onServerMessageReceive(ClientMessageReceiveEvent event) {
		if (event.getMessage().startsWith("/")) {
			commands.executeCommand(event.getClient(), event.getMessage());
			console.sendMessage(MessageType.INFO, names.get(event.getClient()) + ": " + "run command " + event.getMessage());
		} else {
			server.sendMessage(names.get(event.getClient()) + ": " + event.getMessage());
			console.sendMessage(names.get(event.getClient()) + ": " + event.getMessage());
		}
	}
	
	public static void onServerClientConnect(ClientConnectEvent event) {
		String name = Integer.toString(new Random().nextInt(9999));
		while (name.length() > 4) name = "0" + name;
		name = "User" + name;
		names.put(event.getClient(), name);
		server.sendMessage(name + " connected!");
		console.sendMessage(name + " connected!");
	}
	
	public static void onServerClientDisconnect(ClientDisconnectEvent event) {
		String name = names.get(event.getClient());
		names.remove(event.getClient());
		if (event.getCause() == DisconnectCause.DISCONNECTED) {
			server.sendMessage(name + " disconnected!");
			console.sendMessage(name + " disconnected!");
		} else {
			server.sendMessage(name + " timed out!");
			console.sendMessage(name + " timed out!");
		}
	}
	
	public static Server getServer() {
		return server;
	}
	
	public static boolean setClient(String adress, int port) {
		try {
			client = new Client(adress, port);
			client.setOnMessageReceive(event -> console.sendMessage(MessageType.DEFAULT, event.getMessage()));
			client.setOnClientDisconnect(event -> console.sendMessage(MessageType.INFO, "Disconnected!"));
			return true;
		} catch (IOException exception) {
			return false;
		}
	}
	
	public static Client getClient() {
		return client;
	}
	
	public static CommandHandler getCommands() {
		return commands;
	}
	
	public static Console getConsole() {
		return console;
	}
	
}
