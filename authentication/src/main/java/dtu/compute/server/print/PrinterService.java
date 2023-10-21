package dtu.compute.server.print;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrinterService extends Remote {
    String print(String filename, String printer, String cookie) throws RemoteException;
    String queue(String printer, String cookie) throws RemoteException;
    String topQueue(String printer, int job, String cookie) throws RemoteException;
    String start(String cookie) throws RemoteException;
    String stop(String cookie) throws RemoteException;
    String restart(String cookie) throws RemoteException;
    String status(String printer, String cookie) throws RemoteException;
    String readConfig(String parameter, String cookie) throws RemoteException;
    String setConfig(String parameter, String value, String cookie) throws RemoteException;
    String authenticate(String username, String password, int validTime) throws RemoteException;
}
