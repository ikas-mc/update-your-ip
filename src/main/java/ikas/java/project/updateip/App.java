package ikas.java.project.updateip;

import ikas.java.project.updateip.git.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class App implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private GitService git;
    private Path path;
    private ScheduledThreadPoolExecutor timer;

    public void start() {
        var configFile = this.getClass().getClassLoader().getResource("app.properties");
        Objects.requireNonNull(configFile, "app.properties not found");

        Path appRootPath;
        try {
            var configPath = Paths.get(configFile.toURI());
            appRootPath = configPath.getParent();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("", e);
        }

        var config = new Properties();
        try (var stream = this.getClass().getClassLoader().getResourceAsStream("app.properties")) {
            config.load(stream);
        } catch (IOException e) {
            throw new IllegalArgumentException("config read error", e);
        }

        var pathParam = getRequiredValue(config, "path");
        var urlParam = getRequiredValue(config, "url");
        var keyParam = getRequiredValue(config, "key");
        var timeParam = getRequiredValue(config, "time");

        this.path = Paths.get(pathParam);
        if (!path.isAbsolute()) {
            path = appRootPath.resolve(path);
        }

        var key = Paths.get(keyParam);
        if (!key.isAbsolute()) {
            key = appRootPath.resolve(key);
        }

        if (!Files.exists(key)) {
            throw new RuntimeException("key not found");
        }

        this.git = new GitService(urlParam, path, key);

        var period = Integer.parseInt(timeParam);
        timer = new ScheduledThreadPoolExecutor(1);
        timer.scheduleAtFixedRate(this, 0, period, TimeUnit.MINUTES);
    }

    private String getRequiredValue(Properties properties, String name) {
        var value = properties.getProperty(name);
        if (null == value || value.isEmpty()) {
            throw new NullPointerException(name);
        }
        return value.trim();
    }

    public void stop() {
        if (null != timer) {
            timer.shutdown();
        }
        timer = null;
    }

    public void run() {
        try {
            logger.info("run check.. ");

            if (!Files.exists(path)) {
                logger.info("clone .. ");
                git.cloneRepo();
                logger.info("clone ok .. ");
            }

            var ip = IpUtil.FindBestIp();
            logger.info("ip is {}", ip);

            if (null == ip) {
                return;
            }

            var ipFile = path.resolve("ip");
            String oldIp = null;
            if (Files.exists(ipFile)) {
                oldIp = Files.readString(ipFile, StandardCharsets.UTF_8);
                logger.info("old ip is {}", oldIp);
            }

            if (!ip.equals(oldIp)) {
                Files.writeString(ipFile, ip, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                logger.info("commit and push");
                git.commitAndPush();
                logger.info("commit and push ok");
            }
        } catch (Throwable e) {
            logger.error("error", e);
        }
    }
}
