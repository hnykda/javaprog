import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;


abstract class Scrapper {

    public String baseURL;
    public WebDriver driver;
    private String jsonName;
    private HashMap<String, String> data;

    public Scrapper(String _maternalURL, String _baseURL,
                    String _xpathOfInputField, WebDriver driver, String jsonName) {
        this.baseURL = _baseURL;
        this.jsonName = jsonName;

        this.driver = driver;
        this.driver.get(_baseURL);
        WebElement query = driver.findElement(By.xpath(_xpathOfInputField));
        query.clear();
        query.sendKeys(_maternalURL);
        query.submit();

        try {
            if (!this.check_availability()) {
                throw new RuntimeException("No data available for this webpage");
            }
        } catch (NoSuchElementException ex) {
            //System.out.println("Data are present...");
        }

    }

    private Map<String, String> loadJson() throws IOException {

        InputStream in = getClass().getResourceAsStream("/scraps.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String jsonStringToBeRead = org.apache.commons.io.IOUtils.toString(reader);
        //String jsonStringToBeRead = new String(encoded, StandardCharsets.UTF_8);

        Type mapOfStringObjectType = new TypeToken<Map<String, Map<String, String>>>() {
        }.getType();
        Gson gson = new Gson();
        Map<String, Map<String, String>> jsonOb = gson.fromJson(jsonStringToBeRead, mapOfStringObjectType);

        return (Map<String, String>) jsonOb.get(this.jsonName);
    }

    /**
     * This collect values from all values in given map
     *
     * @param singles map in format NameOfTheFeature : xpathOfTheFeature
     * @return map in format NameOfTheFeature : collectedValueOfFeature
     */
    private HashMap<String, String> _collect_singles(Map<String, String> singles) {

        HashMap<String, String> res = new HashMap<>();

        for (Map.Entry<String, String> entry : singles.entrySet()) {
            try {
                String val = this.driver.findElement(By.xpath(entry.getValue())).getText();
                res.put(entry.getKey(), val);
            } catch (NoSuchElementException e) {
                res.put(entry.getKey(), null);
            }
        }

        return res;
    }

    /**
     * This function works for collection all informations
     */
    public void collect() throws IOException {
        Map<String, String> loaded = (Map<String, String>) this.loadJson();
        this.data = this._collect_singles(loaded);
    }

    public HashMap<String, String> getData() {
        return this.data;
    }

    /**
     * Checks if scrapper has information about given webpage.
     * Can legally throw NoSuchElementException if looking
     * for an element present only on page without information.
     *
     * @return True if there is info about page, instead false
     */
    abstract boolean check_availability();

    /**
     * Close the running driver
     */
    public void quit() {
        this.driver.quit();
    }

}
