package org.apirocet.digipres.pcms;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apirocet.digipres.episode.EpisodeMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.awt.desktop.SystemEventListener;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.slf4j.LoggerFactory.getLogger;

public class PCMSDataMapper {

    private static final Logger LOGGER = getLogger(EpisodeMapper.class);
    private static PCMSClient pcms_client;

    private static final String BASE_URL = "https://pcms.poetryfoundation.org/";
    private static OAuth2AccessToken access_token;
    private static OAuth20Service service;

    public PCMSDataMapper() {
        PCMSClient pcms_client = PCMSClient.getInstance();
        service = pcms_client.getOAuth2Service();
        access_token = pcms_client.getOAuth2AccessToken();
    }

    public Date getMagazineDate(int pcms_id) {
        Date date = null;
        String url = BASE_URL + "api/v1/magazine/" + pcms_id;

        JSONObject obj = getPCMSData(url);

        try {
            String yr = String.valueOf(obj.getJSONArray("entries").getJSONObject(0).getInt("year"));
            String mo = obj.getJSONArray("entries").getJSONObject(0).getString("month");
            date = new SimpleDateFormat("yyyy-MM").parse(yr + "-" + mo);
        } catch (JSONException| ParseException ex) {
            System.err.println("Cannot parse date for magazine PCMS ID " + pcms_id + ": " + ex.getMessage());
            LOGGER.error("Cannot parse date for magazine PCMS ID " + pcms_id + ": " + ex.getMessage());
            date = null;
        }

        return date;
    }

    public String getAudioTitle(int pcms_id) {
        String title = null;
        String url = BASE_URL + "api/v1/audio/" + pcms_id;

        JSONObject obj = getPCMSData(url);

        try {
            title = obj.getJSONArray("entries").getJSONObject(0).getString("title");
        } catch (JSONException ex) {
            LOGGER.warn("Cannot retrieve title for audio PCMS ID " + pcms_id + ": " + ex.getMessage());
            title = "TITLE NOT FOUND - CHECK PCMS RECORD";
        }

        return title;
    }

    public Date getAudioReleaseDate(int pcms_id) {
        Date date = null;
        String url = BASE_URL + "api/v1/audio/" + pcms_id;

        JSONObject obj = getPCMSData(url);

        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(obj.getJSONArray("entries").getJSONObject(0).getString("releaseDate"));
        } catch (JSONException| ParseException ex) {
            System.err.println("Cannot parse release date for audio PCMS ID " + pcms_id + ": " + ex.getMessage());
            LOGGER.warn("Cannot parse release date for audio PCMS ID " + pcms_id + ": " + ex.getMessage());
            date = null;
        }

        return date;
    }

    public int getAudioTextPoemPcmsId(int pcms_id) {
        int audio_text_pcms_id = 0;
        String url = BASE_URL + "api/v1/content/" + pcms_id;

        JSONObject obj = getPCMSData(url);

        try {
            JSONArray arr = obj.getJSONArray("entries").getJSONObject(0).getJSONArray("relatedContent");
            if (arr != null && ! arr.isEmpty()) {
                for (int i = 0; i < arr.length(); i++) {
                    int id = arr.getJSONObject(i).getInt("id");
                    String type = arr.getJSONObject(i).getJSONObject("meta").getString("dataType");
                    if ("poem".equals(type)) {
                        audio_text_pcms_id = id;
                        break;
                    }
                }
            }
        } catch (JSONException ex) {
            System.err.println("Cannot parse release date for audio PCMS ID " + pcms_id + ": " + ex.getMessage());
            LOGGER.warn("Cannot parse release date for audio PCMS ID " + pcms_id + ": " + ex.getMessage());
        }
        return audio_text_pcms_id;
    }

    public String getAuthorName(int pcms_id) {
        String author = null;
        String fn = null;
        String mn = null;
        String ln = null;
        String url = BASE_URL + "api/v1/author/" + pcms_id;

        JSONObject obj = getPCMSData(url);

        try {
            JSONObject jo = obj.getJSONArray("entries").getJSONObject(0);
            if (jo != null) {
                if (jo.has("firstName") && ! jo.isNull("firstName") && ! jo.isEmpty())
                    fn = obj.getJSONArray("entries").getJSONObject(0).getString("firstName");
                if (jo.has("middleName") && ! jo.isNull("middleName") && ! jo.isEmpty())
                    mn = obj.getJSONArray("entries").getJSONObject(0).getString("middleName");
                if (jo.has("lastName") && ! jo.isNull("lastName") && ! jo.isEmpty())
                    ln = obj.getJSONArray("entries").getJSONObject(0).getString("lastName");
            }

            StringBuilder sb = new StringBuilder();
            if (ln != null && ! ln.isEmpty()) {
                sb.append(ln);
            }

            if (fn != null && ! fn.isEmpty()) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(fn);
            }

            if (mn != null && ! mn.isEmpty() && sb.length() > 0) {
                if (fn != null && ! fn.isEmpty()) {
                    sb.append(" ");
                } else if (ln != null && ! ln.isEmpty()) {
                    sb.append(", ");
                }
                sb.append(mn);
            }

            if (sb.length() > 0) {
                author = sb.toString();
            } else {
                author = "UNKNOWN?";
            }
        } catch (JSONException ex) {
            LOGGER.warn("Cannot retrieve names for author PCMS ID " + pcms_id + ": " + ex.getMessage());
            author = "AUTHOR NAME NOT FOUND - CHECK PCMS RECORD";
        }

        return author;
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
