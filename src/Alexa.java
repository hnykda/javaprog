/**
 * Created by dan on 5/26/15.
 */
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.net.URL;

public class Alexa extends Scrapper{

    public String url;

    public Alexa(String _maternalURL, String _baseURL,
                 String _xpathOfInputField, String browser)
    {
        super(_maternalURL, _baseURL,
                _xpathOfInputField,  browser);
    }

    public String collect()
    {
        WebElement el = this.driver.findElement(By.xpath("//*[@id=\"traffic-rank-content\"]/div/span[2]/div[1]/span/span/div/strong"));
        return el.getText();
    }
}
