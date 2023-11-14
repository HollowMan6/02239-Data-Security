package dtu.compute.server.ac;

import dtu.compute.util.db.AccessControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class List implements Model {
    private static final Logger logger = LogManager.getLogger(List.class);
    public static AccessControl accessControl = new AccessControl();

    @Override
    public boolean isMethodGranted(String username, String method) {
        Map<String, Boolean> accessControlListByName = accessControl.getAccessControlListByName(username);
        boolean result = accessControlListByName.get(method);
        if (result) logger.info(String.format("%s is allowed to %s", username, method));
        else logger.info(String.format("%s is not allowed to %s", username, method));
        return result;
    }

}
