package dtu.compute.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import dtu.compute.server.print.PrinterService;
import dtu.compute.util.Configuration;
import dtu.compute.util.Crypto;
import dtu.compute.util.DB;

public class Client {
	private static final DB db = new DB();
	public static void main(String[] args)
			throws MalformedURLException, NotBoundException, RemoteException {

		String pwHash = Crypto.hash(Configuration.testUserPassword);
		// Add test user to database
		db.addUser(Configuration.testUsername, Crypto.salt(pwHash));

		// The following codes describe a typical procedure to use the printing service
		PrinterService printer = (PrinterService) Naming.lookup(Configuration.url + "/printer");
		String cookie = printer.authenticate(Configuration.testUsername, pwHash, Configuration.validSessionTime);
		System.out.println(printer.start(cookie));
		System.out.println(printer.print("test1.txt", "printer1", cookie));
		System.out.println(printer.print("test2.txt", "printer2", cookie));
		System.out.println(printer.print("test3.txt", "printer2", cookie));
		System.out.println(printer.print("test4.txt", "printer1", cookie));
		System.out.println(printer.print("test5.txt", "printer1", cookie));
		System.out.println(printer.queue("printer2", cookie));
		System.out.println(printer.queue("printer1", cookie));
		System.out.println(printer.setConfig("printers", "2", cookie));
		System.out.println(printer.readConfig("printers", cookie));
		System.out.println(printer.topQueue("printer1", 3, cookie));
		System.out.println(printer.queue("printer1", cookie));
		System.out.println(printer.status("printer1", cookie));
		System.out.println(printer.status("printer2", cookie));
		System.out.println(printer.restart(cookie));
		cookie = printer.authenticate(Configuration.testUsername, pwHash, Configuration.validSessionTime);
		System.out.println(printer.queue("printer1", cookie));
		System.out.println(printer.queue("printer2", cookie));
		System.out.println(printer.stop(cookie));

		// Clear the database
		db.clear();
	}

}
