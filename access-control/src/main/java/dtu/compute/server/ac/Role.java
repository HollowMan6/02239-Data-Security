package dtu.compute.server.ac;

import dtu.compute.util.db.AccessControl;
import dtu.compute.util.db.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Role implements Model {
    public static User user = new User();
    public static AccessControl accessControl = new AccessControl();
    private static final Logger logger = LogManager.getLogger(Role.class);

    @Override
    public boolean isMethodGranted(String username, String method) {
        Set<String> roleSet = new HashSet<>();
        String userRoleByName = user.getUserRoleByName(username);
        if (!userRoleByName.contains("&")) roleSet.add(userRoleByName);
        else roleSet.addAll(Arrays.stream(userRoleByName.split("&")).collect(Collectors.toList()));
        boolean result = isMethodGrantedForRole(method, roleSet);
        if (result) logger.info(String.format("%s with role %s is allowed to %s", username, userRoleByName, method));
        else logger.info(String.format("%s with role %s is not allowed to %s", username, userRoleByName, method));
        return result;
    }

    private boolean isMethodGrantedForRole(String method, Set<String> roleSet) {
        for (var i : roleSet) {
            Map<String, Boolean> accessControlListByRole = accessControl.getAccessControlListByRole(i);
            if (accessControlListByRole.get(method)) return true;
        }

        return false;
    }
}