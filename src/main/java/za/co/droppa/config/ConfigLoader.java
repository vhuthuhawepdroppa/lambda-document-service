package za.co.droppa.config;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigLoader {

    private static Properties properties;

    private static ConfigLoader instance;

    private final String AUTH_TOKEN = "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";
    private final String REPO = "droppa2016/droppa-config";
    private final String BRANCH = "main";
    private final String FILE_NAME = "droppa-conf.properties";


    public ConfigLoader() {
        try {
            GitHub github = GitHub.connectUsingOAuth(AUTH_TOKEN);

            GHRepository repo = github.getRepository(REPO);

            GHBranch branch = repo.getBranch(BRANCH);

            GHContent fileContent = repo.getFileContent(FILE_NAME, branch.getName());

            loadProperties(fileContent.getContent());


        } catch (Exception e) {
            System.out.println("Unable to connect to repository: " + e.getMessage());
        }
    }

    public static synchronized ConfigLoader config() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }

    private void loadProperties(String content) throws IOException {
        properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new java.io.ByteArrayInputStream(content.getBytes())))) {
            properties.load(reader);
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getMongoConnectionString() {
        return getProperty("mongo.url");
    }
}
