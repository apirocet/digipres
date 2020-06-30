package org.apirocet.digipres.pcms;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class PCMSDataMapper {

    private static PCMSClient pcms_client;

    private static final String BASE_URL = "https://pcms.poetryfoundation.org/";
    private static OAuth2AccessToken access_token;
    private static OAuth20Service service;

    public PCMSDataMapper() {
        PCMSClient pcms_client = PCMSClient.getInstance();
        service = pcms_client.getOAuth2Service();
        access_token = pcms_client.getOAuth2AccessToken();
    }

    public String getEpisodeTitle(int pcms_id) {
        String title = null;
        String url = BASE_URL + "api/v1/audio/" + pcms_id;

        JSONObject obj = getPCMSData(url);

        try {
            title = obj.getJSONArray("entries").getJSONObject(0).getString("title");
        } catch (JSONException ex) {
            title = "TITLE NOT FOUND - CHECK PCMS RECORD";
        }

        return title;
    }

    public Date getEpisodeReleaseDate(int pcms_id) {
        Date date = null;
        String url = BASE_URL + "api/v1/audio/" + pcms_id;

        JSONObject obj = getPCMSData(url);

        //try {
        //    date = obj.getJSONArray("entries").getJSONObject(0).getString("title");
        //} catch (JSONException ex) {
        //    date = null;
        //}

        return date;
    }
    private JSONObject getPCMSData(String url) {
        String json_response = null;
        final OAuthRequest request = new OAuthRequest(Verb.GET, url);
        service.signRequest(access_token, request);
        try (Response response = service.execute(request)) {
            int status = response.getCode();
            if (status != 200) {
                System.err.println("Cannot retrieve data from PCMS API: Service returned HTTP status " + status);
                System.exit(1);
            }
            json_response = response.getBody();
        } catch (InterruptedException| ExecutionException | IOException ex) {
            System.err.println("Cannot retrieve data from PCMS API: " + ex.getMessage());
            System.exit(1);
        }
        return new JSONObject(json_response);
    }
}
