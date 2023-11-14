package dtu.compute.server;

public class Session {
    private final int validTime;
    private final long startTime;

    public Session(int validTime) {
        startTime = System.currentTimeMillis();
        this.validTime = validTime;
    }

    public boolean isAuthenticated() {
        // Valid time in second
        return System.currentTimeMillis() - startTime <= (validTime * 1000L);
    }
}
