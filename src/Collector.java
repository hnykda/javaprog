import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.sun.syndication.io.FeedException;
import org.bson.BasicBSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Collects all available information about given feed URL
 * and saves that to database
 */
public class Collector {

    public URL feedUrl = null;
    private HashMap<String, HashMap<String, String>> data;
    private String id = null;
    private String maternalUrl;
    private String browser;
    private String outFile;
    private Boolean datab;

    public Collector(String feedUrl, String browser, boolean datab, String outFile)
            throws IOException, FeedException {

        this.feedUrl = new URL(feedUrl);
        this.browser = browser;
        this.outFile = outFile;
        this.datab = datab;

    }

    private HashMap<String, HashMap<String,String>> collect_feed_info() throws IOException, FeedException {
        Feeder feed = new Feeder(this.feedUrl);
        feed.collect();
        this.maternalUrl = feed.getMaternalUrl();
        HashMap<String, HashMap<String, String>> res = new HashMap<>();

        res.put("feed", feed.getData());
        return res;
    }

    private HashMap<String, HashMap<String, String>> collect_maternal_info() throws IOException {

        Alexa alexa = new Alexa(this.maternalUrl, "http://www.alexa.com",
                        "//*[@id=\"alx-content\"]/div/div/span/form/input", this.browser, "alexa");
        alexa.collect();

        Urlm urlm = new Urlm(this.maternalUrl, "http://www.urlm.co",
                "//*[@id=\"url\"]", this.browser, "urlm");
        urlm.collect();

        Websout websout = new Websout(this.maternalUrl, "http://www.websiteoutlook.com/",
                "//*[@id=\"header\"]/form/input[1]", this.browser, "websout");
        websout.collect();

        HashMap<String, HashMap<String, String>> maternalInfo = new HashMap<>();
        maternalInfo.put("alexa", alexa.getData());
        maternalInfo.put("urlm", urlm.getData());
        maternalInfo.put("websout", websout.getData());

        alexa.quit();
        urlm.quit();
        websout.quit();

        return maternalInfo;
    }

    /**
     * Collects all together
     * @throws IOException
     */
    public void collect_all() throws IOException, FeedException {

        HashMap<String, HashMap<String, String>> feedInfo = this.collect_feed_info();

        HashMap<String, HashMap<String, String>> maternalInfo = this.collect_maternal_info();

        feedInfo.putAll(maternalInfo);

        this.data = feedInfo;

        if (this.datab) {
            String id = this.push_to_db();
            System.out.println("\nSaved to database under id: " + id);
        }
        //System.out.println("\n\nShould i write? Huh: " + this.outFile);
        if (this.outFile != null)
        {
            BasicBSONObject jsonedData = new BasicBSONObject(this.data);
            FileWriter file = new FileWriter(this.outFile);
            try {
                file.write(jsonedData.toString());
                System.out.println("\nSuccessfully Copied JSON Object to File: " + this.outFile);
                System.out.println("\nJSON Object: " + jsonedData);

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                file.flush();
                file.close();
            }

        }

    }

    public HashMap<String, HashMap<String, String>> getData()
    {
        return this.data;
    }

    /**
     * Saves data to database
     * @return id of document in database
     */
    public String push_to_db() {

        //JSONObject jsonedData = new JSONObject(this.data);

        MongoClient mongoClient = new MongoClient("localhost", 27017);

        DB db = mongoClient.getDB("mydb");
        DBCollection coll = db.getCollection("testCollection");

        BasicDBObject ins = new BasicDBObject(this.data);
        coll.insert(ins);

        this.id = ins.get( "_id" ).toString();

        return this.id;
    }

    public String getId() {
        return this.id;
    }
}
