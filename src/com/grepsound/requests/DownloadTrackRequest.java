package com.grepsound.requests;

import android.content.Context;
import android.util.Log;
import com.octo.android.robospice.request.SpiceRequest;
import com.soundcloud.api.Token;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lisional on 2014-04-21.
 */
public class DownloadTrackRequest extends SpiceRequest {
    private static final String TAG = DownloadTrackRequest.class.getSimpleName();

    private static String CLIENT_ID = "b45b1aa10f1ac2941910a7f0d10f8e28";
    private final Context mContext;
    private String songToRetrieve;

    public DownloadTrackRequest(Context c, String url) {
        super(null);
        songToRetrieve = url;
        mContext = c;
    }

    @Override
    public Object loadDataFromNetwork() throws Exception {
        StringBuffer page = getHtml(songToRetrieve);
        Document doc = Jsoup.parse(page.toString());
        String songID = getSongID(doc);
        String songTitle = getTitle(doc);

        String songURL = "https://api.sndcdn.com/i1/tracks/" + songID + "/streams?client_id=" + CLIENT_ID;

        StringBuffer songPage = getHtml(songURL);

        goGetSomeCookies(songPage.toString(), songTitle);

        return null;
    }

    private void goGetSomeCookies(String songPage, String title) throws IOException {

        String[] coucou = songPage.split("\"");

        for (String i : coucou) {
            Log.d(TAG, "splitted:" + i);
        }
        String goodURL = coucou[3].replace("\\u0026", "&");

        URL obj = new URL(goodURL);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setReadTimeout(5000);
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");

        System.out.println("Request URL ... " + goodURL);

        boolean redirect = false;

        // normally, 3xx is redirect
        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)
                redirect = true;
        }

        System.out.println("Response Code ... " + status);

        if (redirect) {

            // get redirect url from "location" header field
            String newUrl = conn.getHeaderField("Location");

            // get the cookie if need, for login
            String cookies = conn.getHeaderField("Set-Cookie");

            // open the new connnection again
            conn = (HttpURLConnection) new URL(newUrl).openConnection();
            conn.setRequestProperty("Cookie", cookies);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");

            System.out.println("Redirect to URL : " + newUrl);

        }

        File mTrackDownloadFile = new File(mContext.getExternalCacheDir(), title+".mp3");
        mTrackDownloadFile.createNewFile();
        final FileOutputStream fileOutputStream = new FileOutputStream(mTrackDownloadFile);
        final byte buffer[] = new byte[16 * 1024];

        final InputStream inputStream = conn.getInputStream();

        int len1 = 0;
        while ((len1 = inputStream.read(buffer)) > 0) {
            fileOutputStream.write(buffer, 0, len1);
        }
        fileOutputStream.flush();
        fileOutputStream.close();

    }

    private String getTitle(Document doc) {
        Elements titles = doc.select("em[itemprop=name]");
        return titles.get(0).text();
    }

    private String getSongID(Document doc) {

        Elements ids = doc.select("div[data-sc-track]");

        for (Element el : ids) {

            Log.i(TAG, el.attr("class"));
            if (el.attr("class").contains("small"))
                Log.i(TAG, "This one contains small");
            else {
                Log.i(TAG, "OK for this one");
                Log.i(TAG, "Song id is : " + el.attr("data-sc-track"));
                return el.attr("data-sc-track");
            }
        }
        return null;
    }

    private StringBuffer getHtml(String url) throws IOException {


        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setReadTimeout(5000);
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");

        System.out.println("Request URL ... " + url);

        boolean redirect = false;

        // normally, 3xx is redirect
        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)
                redirect = true;
        }

        System.out.println("Response Code ... " + status);

        if (redirect) {

            // get redirect url from "location" header field
            String newUrl = conn.getHeaderField("Location");

            // get the cookie if need, for login
            String cookies = conn.getHeaderField("Set-Cookie");

            // open the new connnection again
            conn = (HttpURLConnection) new URL(newUrl).openConnection();
            conn.setRequestProperty("Cookie", cookies);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");

            System.out.println("Redirect to URL : " + newUrl);

        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer html = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            Log.w("Test", inputLine);
            html.append(inputLine);
        }
        System.out.println("Done");
        in.close();
        return html;
    }
}