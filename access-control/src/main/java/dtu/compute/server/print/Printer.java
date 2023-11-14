package dtu.compute.server.print;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Printer {
    private final String name;
    private final List<String> queue;
    private static final Logger logger = LogManager.getLogger(Printer.class);

    public Printer(String name) {
        this.name = name;
        this.queue = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public void addFile(String filename) {
        queue.add(filename);
        logger.info(String.format("%s-%s queued in print service.", this.name, filename));
    }

    public void listQueue() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s-current tasks: \n", this.name));
        if (queue.size() == 0) {
            sb.append("None");
        } else {
            for (int i = 0; i < queue.size(); i++) {
                sb.append(String.format("<%d> <%s>\n", i + 1, queue.get(i)));
            }
        }

        logger.info(sb.toString());
    }

    public void topQueue(int job) {
        String target = queue.remove(job - 1);
        queue.add(0, target);
        logger.info(String.format("%s-%dth job moved to top", this.name, job));
    }

    public void clearQueue() {
        queue.clear();
        logger.info(String.format("%s-queue cleared", this.name));
    }

    public int getStatus() {
        int size = queue.size();
        logger.info(String.format("%s-%d tasks in queue", this.name, size));
        return size;
    }
}
