package dtu.compute.server.print;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dtu.compute.server.Session;
import dtu.compute.util.Configuration;
import dtu.compute.util.DB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dtu.compute.util.Crypto;

public class PrinterServant extends UnicastRemoteObject implements PrinterService {
	private static final Logger logger = LogManager.getLogger(PrinterServant.class);
	private final List<Printer> printers;
	private final HashMap<String, String> configs;
	private boolean started = false;
	private final Map<String, Session> sessions;
	private final Map<String, String> sessionUsers;
	private final DB db;

	private final String UNAUTHENTICATED = "Please authenticate first";
	private final String NOT_STARTED = "Printing service not started";
	private final String NOT_FOUND = "Specified printer not found";

	public PrinterServant() throws RemoteException {
		printers = new ArrayList<>();
		printers.add(new Printer("printer1"));
		printers.add(new Printer("printer2"));
		configs = new HashMap<>();
		sessions = new HashMap<>();
		sessionUsers = new HashMap<>();
		db = new DB();
	}

	public String print(String filename, String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "print"))
			return UNAUTHENTICATED;
		if (!started)
			return NOT_STARTED;

		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.addFile(filename);
				return "Print task added to " + printer;
			}
		}
		return NOT_FOUND;
	}

	public String queue(String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "queue"))
			return UNAUTHENTICATED;
		if (!started)
			return NOT_STARTED;

		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.listQueue();
				return "";
			}
		}
		return NOT_FOUND;
	}

	public String topQueue(String printer, int job, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "topQueue"))
			return UNAUTHENTICATED;
		if (!started)
			return NOT_STARTED;

		for (var p : printers) {
			if (p.getName().equals(printer)) {
				p.topQueue(job);
				return "Specified job moved";
			}
		}
		return NOT_FOUND;
	}

	public String start(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "start"))
			return UNAUTHENTICATED;
		if (started) {
			return "Printing service already started";
		}
		started = true;
		return "Printing service started";
	}

	public String stop(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "stop"))
			return UNAUTHENTICATED;
		if (!started) {
			return NOT_STARTED;
		}
		started = false;
		sessions.remove(cookie);
		sessionUsers.remove(cookie);
		return "Service stop";
	}

	public String restart(String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "restart"))
			return UNAUTHENTICATED;
		if (!started) {
			return NOT_STARTED;
		}

		for (var p : printers) {
			p.clearQueue();
		}
		sessions.remove(cookie);
		sessionUsers.remove(cookie);
		return "Printing service restarted";
	}

	public String status(String printer, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "status"))
			return UNAUTHENTICATED;
		if (!started) {
			return NOT_STARTED;
		}
		for (var p : printers) {
			if (p.getName().equals(printer)) {
				int status = p.getStatus();
				return printer + " has " + status + " tasks.";
			}
		}
		return NOT_FOUND;
	}

	public String readConfig(String parameter, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "readConfig"))
			return UNAUTHENTICATED;
		if (!started) {
			return NOT_STARTED;
		}
		if (configs.containsKey(parameter)) {
			logger.info(String.format("%s: %s.", parameter, configs.get(parameter)));
			return "";
		}

		String error = String.format("No '%s' on server", parameter);
		logger.error(error);
		return error;
	}

	public String setConfig(String parameter, String value, String cookie) throws RemoteException {
		if (!isAuthenticated(cookie, "setConfig"))
			return UNAUTHENTICATED;
		if (!started) {
			return NOT_STARTED;
		}
		configs.put(parameter, value);

		String success = String.format("%s: %s", parameter, value);
		logger.info(success);
		return success;
	}

	public String authenticate(String username, String password, int validSessionTime) throws RemoteException {
		String passwordHash = db.getUserPasswordHashByName(username);

		if (passwordHash == null) // Make sure it's time constant
			passwordHash = Configuration.randomHash;

		if (!Crypto.compare(password, passwordHash)) {
			logger.info(String.format("Authentication failed for %s", username));
			return "";
		}
		// If authentication success, return an uuid as cookie and add the associated
		// session
		String uuid = (UUID.randomUUID()).toString();
		Session session = new Session(validSessionTime);
		sessions.put(uuid, session);
		sessionUsers.put(uuid, username);

		logger.info(String.format("%s authenticates OK", username));
		return uuid;
	}

	private boolean isAuthenticated(String cookie, String serviceName) {
		if (!sessions.containsKey(cookie))
			return false;
		Session session = sessions.get(cookie);
		if (!session.isAuthenticated()) {
			sessions.remove(cookie);
			sessionUsers.remove(cookie);
			return false;
		}
		logger.info(String.format("%s requesting: %s", sessionUsers.get(cookie),
				serviceName));
		return true;
	}
}
