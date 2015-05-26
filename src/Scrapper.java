import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.seleniumhq.jetty7.client.webdav.WebdavListener;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by dan on 5/26/15.
 */
public class Scrapper {

    public String baseURL;
    public String maternalURL;
    public WebDriver driver;

    public Scrapper(String _maternalURL, String _baseURL,
                    String _xpathOfInputField, String browser)
    {
        this.baseURL = _baseURL;

        WebDriver wdriver = null;
        if (browser == "phantomjs")
            wdriver = new PhantomJSDriver();
        else if (browser == "phantomjsTOR") {
            ArrayList<String> cliArgsCap = new ArrayList<String>();
            cliArgsCap.add("--proxy=localhost:9050");
            cliArgsCap.add("--proxy-type=socks5");
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
            capabilities.setCapability(
                    PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
            wdriver = new PhantomJSDriver(capabilities);
        }
        else if (browser == "firefox")
            wdriver = new FirefoxDriver();
        else if (browser == "chromium")
            wdriver = new ChromeDriver();
        else if (browser == "firefoxTOR") {
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("network.proxy.type", 1);
            profile.setPreference("network.proxy.socks", "localhost");
            profile.setPreference("network.proxy.socks_port", 9050);
            wdriver = new FirefoxDriver(profile);
        }

        if (wdriver == null)
            throw new IllegalArgumentException("Driver cannot be null");
        else
            this.driver = wdriver;

        this.driver.get(_baseURL);
        WebElement query = driver.findElement(By.xpath(_xpathOfInputField));
        query.clear();
        query.sendKeys(_maternalURL);
        query.submit();

        // TODO: What if no informations are availible!
    }

/*    private String selxs(String xpath)
    {
        try
        {
            List<WebElement> elems = this.driver.findElements(By.xpath(xpath));
            List<String> res;
            for (WebElement elem : elems) {
                res.add(elem.getText());
            }
        }
        catch (NoSuchElementException ex)
        {
            String res = "NA";
        }

        return res;
    }*/

}
