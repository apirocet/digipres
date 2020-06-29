package org.apirocet.digipres.pcms;

import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public final class PCMSClient {

    private static final String BASE_URL = "https://pcms.poetryfoundation.org/";

    private static final File propfile = new File("pcms.properties");
    private static volatile PCMSClient instance;

    private static String client_id;
    private static String client_secret;
    private static OAuth2AccessToken access_token;
    private static OAuth20Service service;

    private PCMSClient() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static PCMSClient getInstance() {
        if (instance == null) {
            synchronized (PCMSClient.class) {
                if (instance == null) {
                    instance = new PCMSClient();
                    loadProperties();
                    getOAuth2Token();
                }
            }
        }
        return instance;
    }

    public String getClientId() {
        return client_id;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public OAuth2AccessToken getOAuth2AccessToken() {
        return access_token;
    }

    private static void loadProperties() {

        try (InputStream inp = new FileInputStream(propfile)) {
            Properties prop = new Properties();
            prop.load(inp);

            client_id = prop.getProperty("pcms.id", "UNSET");
            client_secret = prop.getProperty("pcms.secret", "UNSET");

        } catch (IOException ex) {
            System.err.println("Cannot load properties file '" + propfile + "': " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void getOAuth2Token() {
        service = new ServiceBuilder(client_id)
                .apiSecret(client_secret)
                .build(PCMSApi.instance());

        try {
            access_token = service.getAccessTokenClientCredentialsGrant();
        } catch (IOException|InterruptedException|ExecutionException ex) {
            System.err.println("Cannot authenticate to PCMS API: " + ex.getMessage());
            System.exit(1);
        }
    }

    public String getEpisodeTitle(int pcms_id) {
        String title = null;
        String url = BASE_URL + "api/v1/audio/" + pcms_id;
        System.out.println(url);
        final OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(access_token, request);
        try (Response response = service.execute(request)) {
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());
        } catch (InterruptedException|ExecutionException|IOException ex) {
            System.err.println("Cannot retrieve data from PCMS API: " + ex.getMessage());
            System.exit(1);
        }

        return title;
    }
}
