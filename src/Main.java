
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import  org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import sun.misc.BASE64Decoder;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.time.Duration;
import net.sourceforge.tess4j.*;

public class Main {

    public static void main(String[] args) {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        ITesseract iT = new Tesseract();
        String actualTextHistory = "" ;

        try {
            File file;
            String screenshotBase64 ;
            byte[]imageBytes ;
            BufferedImage image = null;
            int rgb =0;

            driver.get("https://luckyblock.com/es/casino/spribe/aviator/play");


            WebElement firstResult = new WebDriverWait(driver, Duration.ofSeconds(30))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[2]/div/main/div/div[1]/div/div[2]/button")));


            System.out.println(firstResult.getText());
            firstResult.click();
            //creo ciclo para esta cada 5 segundos leer el valor del multiplicador

            TimeUnit.SECONDS.sleep(3);
            while (true)
            {
                String actualimgText = "";
                String beforeimgText = "";

                 file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                 screenshotBase64 = ((TakesScreenshot)driver).getScreenshotAs(OutputType.BASE64);

                BASE64Decoder decoder = new BASE64Decoder();
                imageBytes =  decoder.decodeBuffer(screenshotBase64);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                image = ImageIO.read(bis);
                bis.close();

                //guardo scren para luego procesarla
                File outputfile = new File("image.png");
                ImageIO.write(image, "png", outputfile);

             //el codigo  genera pequeños fracmentos de imagenes de acuerdo a las coordenadas enviadas, esto sirve
                // y las guarda en la carpeta de ejecucion del programa
            //    BufferedImage actualResultImage   = cropImage(outputfile,370 , 150, 80,50, "Actual.png");
           //     BufferedImage BeforeResultImage   = cropImage(outputfile,468 , 150, 80,50, "Anterior.png");
               actualimgText = iT.doOCR(image,new Rectangle(370 , 150, 80,50)).trim();
               beforeimgText = iT.doOCR(image,new Rectangle(468 , 150, 76,50)).trim();



           if ((!actualimgText.equals( actualTextHistory) || actualimgText.equals( beforeimgText ))  &&  isNumber(actualimgText) )
           {

               actualTextHistory = actualimgText;
               System.out.println("el valor del multiplicador actual  es : "+  actualimgText  );
           }


                TimeUnit.SECONDS.sleep(3);
            }



        }

        catch (Exception ex)
        {

            System.out.println(ex.getMessage());
            driver.quit();
        }


    }


    private  static boolean isNumber(String m)
    {
        String multiplicador = m.toLowerCase().replace("x","").replace(".","") ;
        boolean esNumero = true;
        for (int i = 0; i < multiplicador.length(); i++) {
            if (!Character.isDigit(multiplicador.charAt(i))) {
                esNumero = false;
                break;
            }
        }

        if (esNumero && m.length() > 3) {
           return  true;
        } else {
            return  false;
        }

    }

    private static BufferedImage cropImage(File filePath, int x, int y, int w, int h, String Name){

        try {
            BufferedImage originalImgage = ImageIO.read(filePath);

            BufferedImage subImgage = originalImgage.getSubimage(x, y, w, h);
            File outputfile = new File(Name);
            ImageIO.write(subImgage, "png",outputfile);
            return subImgage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}