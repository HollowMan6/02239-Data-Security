package dtu.compute.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Objects;

import dtu.compute.server.print.PrinterService;
import dtu.compute.util.Configuration;
import dtu.compute.util.Crypto;
import dtu.compute.util.db.AccessControl;
import dtu.compute.util.db.User;

public class Client {
    private static final User user = new User();
    private static final AccessControl accessControl = new AccessControl();

    public static void main(String[] args) throws MalformedURLException, NotBoundException, RemoteException {
        String testUserPWHash = Crypto.hash(Configuration.testUserPassword);

        String[] acTestUsers = Configuration.testACUser;
        int[] userACLists = Configuration.testACList;
        for (int i = 0; i < acTestUsers.length; i++) {
            accessControl.addAccessControlList(acTestUsers[i], userACLists[i]);
            user.addUser(acTestUsers[i], Crypto.salt(testUserPWHash), "none");
        }

        PrinterService printer = (PrinterService) Naming.lookup(Configuration.url + "/printer");
        String testUsername = "Bob";
        String access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        assert access_token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        // start allowed
        String response = printer.start(access_token);
        assert response.contains("started");
        // print rejected
        response = printer.print("test1_Bob", "printer1", access_token);
        assert response.contains("not allowed to");
        // queue rejected
        response = printer.queue("printer1", access_token);
        assert response.contains("not allowed to");
        // topQueue rejected
        printer.print("test2_Bob", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramBob", "Bob", access_token);
        assert response.contains(": ");
        // readConfig allowed
        printer.readConfig("paramBob", access_token);
        assert response.contains("paramBob");
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        printer.start(access_token);

        testUsername = "Cecilia";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        assert access_token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_Cecilia", "printer1", access_token);
        System.out.println(response);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_Cecilia", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig rejected
        response = printer.setConfig("paramCecilia", "Cecilia", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramCecilia", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        testUsername = "David";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        assert access_token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_Cecilia", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue rejected
        printer.print("test2_Cecilia", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart rejected
        response = printer.restart(access_token);
        assert response.contains("not allowed to");
        // setConfig rejected
        response = printer.setConfig("paramCecilia", "Cecilia", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramCecilia", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        testUsername = "Alice";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // start allowed
        response = printer.start(access_token);
        assert response.contains("started");
        // print allowed
        response = printer.print("test1_Alice", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_Alice", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramAlice", "Alice", access_token);
        assert response.contains(": ");
        // readConfig allowed
        response = printer.readConfig("paramAlice", access_token);
        assert response.contains("paramAlice");

        printer.stop(access_token);

        testUsername = "Bob";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        assert access_token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        // start allowed
        response = printer.start(access_token);
        assert response.contains("started");
        // print allowed
        response = printer.print("test1_Bob", "printer1", access_token);
        assert response.contains("not allowed to");
        // queue rejected
        response = printer.queue("printer1", access_token);
        assert response.contains("not allowed to");
        // topQueue rejected
        printer.print("test2_Bob", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramBob", "Bob", access_token);
        assert response.contains(": ");
        // readConfig allowed
        printer.readConfig("paramBob", access_token);
        assert response.contains("paramBob");
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        printer.start(access_token);

        testUsername = "Cecilia";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        assert access_token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_Cecilia", "printer1", access_token);
        System.out.println(response);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_Cecilia", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig rejected
        response = printer.setConfig("paramCecilia", "Cecilia", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramCecilia", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // User David, Erica, Fred and George have same permission, therefore, only David is tested here.
        testUsername = "David";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        assert access_token.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        // start rejected
        response = printer.start(access_token);
        assert response.contains("not allowed to");
        // print allowed
        response = printer.print("test1_Cecilia", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue rejected
        printer.print("test2_Cecilia", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert response.contains("not allowed to");
        // status rejected
        response = printer.status("printer1", access_token);
        assert response.contains("not allowed to");
        // restart rejected
        response = printer.restart(access_token);
        assert response.contains("not allowed to");
        // setConfig rejected
        response = printer.setConfig("paramCecilia", "Cecilia", access_token);
        assert response.contains("not allowed to");
        // readConfig rejected
        printer.readConfig("paramCecilia", access_token);
        assert response.contains("not allowed to");
        // stop rejected
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        testUsername = "Alice";

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        // stop allowed
        response = printer.stop(access_token);
        assert Objects.equals(response, "Service stop");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // start allowed
        response = printer.start(access_token);
        assert response.contains("started");
        // print allowed
        response = printer.print("test1_Alice", "printer1", access_token);
        assert response.contains("Print task added to ");
        // queue allowed
        response = printer.queue("printer1", access_token);
        assert Objects.equals(response, "");
        // topQueue allowed
        printer.print("test2_Alice", "printer1", access_token);
        response = printer.topQueue("printer1", 2, access_token);
        assert Objects.equals(response, "Specified job moved");
        // status allowed
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");
        // restart allowed
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        access_token = printer.authenticate(testUsername, Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);

        // setConfig allowed
        response = printer.setConfig("paramAlice", "Alice", access_token);
        assert response.contains(": ");
        // readConfig allowed
        response = printer.readConfig("paramAlice", access_token);
        assert response.contains("paramAlice");

        printer.stop(access_token);

        // Delete user Bob 
        user.deleteUserByName("Bob");
        accessControl.deleteAccessControlListByName("Bob");
        // Change the permissions of George 
        accessControl.updateAccessControlList("George", 0b110000111);
        // Add Henry 
        accessControl.addAccessControlList("Henry", 0b110000000);
        user.addUser("Henry", Crypto.salt(testUserPWHash), "none");
        // Add Ida 
        accessControl.addAccessControlList("Ida", 0b111001000);
        user.addUser("Ida", Crypto.salt(testUserPWHash), "none");

        access_token = printer.authenticate("Alice", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.start(access_token);
        assert response.contains("started");

        // Test the permissions of George 
        access_token = printer.authenticate("George", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_George", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");

        // Test the permissions of Henry 
        access_token = printer.authenticate("Henry", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_Henry", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // Test the permissions of Ida 
        access_token = printer.authenticate("Ida", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_Ida", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        // Delete user Bob 
        user.deleteUserByName("Bob");
        // Delete user Henry
        user.deleteUserByName("Henry");
        // Delete user Ida
        user.deleteUserByName("Ida");
        // Change the role of George 
        String originalRole = user.getUserRoleByName("George");
        user.updateUserRoleByName(originalRole + "&" + "tech", "George");
        // Add new employee Henry 
        user.addUser("Henry", Crypto.salt(testUserPWHash), "ordinary_user");
        // Add new employee Ida 
        user.addUser("Ida", Crypto.salt(testUserPWHash), "power_user");

        access_token = printer.authenticate("Alice", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.start(access_token);
        assert response.contains("started");

        // Test the permissions of George 
        access_token = printer.authenticate("George", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_George", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.status("printer1", access_token);
        assert response.contains("tasks");

        // Test the permissions of Henry 
        access_token = printer.authenticate("Henry", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_Henry", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.stop(access_token);
        assert response.contains("not allowed to");

        // Test the permissions of Ida 
        access_token = printer.authenticate("Ida", Crypto.hash(Configuration.testUserPassword), Configuration.validSessionTime);
        response = printer.print("file_Ida", "printer1", access_token);
        assert response.contains("Print task added to ");
        response = printer.restart(access_token);
        assert Objects.equals(response, "Printing service restarted");

        user.clear();
        accessControl.clear();
    }
}
