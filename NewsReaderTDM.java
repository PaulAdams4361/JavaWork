import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import org.jsoup.nodes.Document;

/*
This program was tested with 'http://lite.cnn.io/' for its simplicity, but will be enhanced for more
it works best to receive data (articles) from the same source(s) (web sites/servers) to reduce risk of
variation in output. 
*/

/*
Data output is appended to a CSV file named DataWarehouse.csv. The directory to this must be reset every time it is
cleared. Data does not delete on its own. The cycle ID starts back over at 1 with each new program session. The runtime
sequence number does not reset ever, but the last one to two bytes is/are the cycle ID number(s)
*/
/*
This is a basic proof of concept that I will develope further with separate classes, where possible, to
improve performance speed, different array sets to target different categories and attributes, particularly stock
performance information and data to assist with making purchase and sell decisions.
*/

public class NewsReaderTDM 
{     
  int g;         // integers are used for counting
  int p = 0;
  double b = 0;
  double i = 0;  // doubles are used for mathematical operation
  double j = 0;
  double k;
  double q = 0;
  String line;
  String csvPath = ("C:/Users/Guest/Documents/NewsReaderTDM/DataWarehouse.csv");
  //change array for some economic strings: {"gains", "gain", "gaining", "positive", "negative", "loss"};
  // develop multiple arrays to compare multiple categories at once, e.g. String[] goodStock, String[] badStock
  String[] arrPolitics = {"president", "prime minister", "ruler", "america", "american", "russia", "russian", "china"
            + "chinese", "congress", "war", "blame", "1950", "1953", "cold", "gun", "guns", "abuse", "abused"
            + "activists", "voting", "votes", "voters", "voter", "voted", "elect", "elected", "power", "recuse","tariff"
            + "recused", "cuba", "riot", "riots", "rioting", "protest", "protesters", "protesting","wartime","tariffs"
            + "violence", "violent", "defense", "defensive", "missiles", "destruction","korea","jong","invasion"};
    
  // does not require a lot of key words, just enough to be significant and have a balance between profit and loss
  String[] arrProfit = {"gain", "gains", "positive", "margin", "top", "success", "outperforming", "outgaining"
                 + "succeeding", "succeeded", "successfully", "successful", "profitable", "beat", "buy"};
    
  String[] arrLoss = {"loss", "losses", "lost", "lose", "fail", "failed", "under", "underperform", "underperformance"
               + "underperforming", "unsuccessful", "unsuccessfully", "negative", "failing", "sell"};
    
  public NewsReaderTDM() throws IOException
  {
    System.out.println("Enter the URL of the site you want to know about (HTTP(S) only):");
    List<String> politics = Arrays.asList(arrPolitics); //converting the array into a politics of strings so strings 
                                                        //can match against strings
    List<String> buy = Arrays.asList(arrProfit);
    List<String> sell = Arrays.asList(arrLoss);
    Scanner read = new Scanner(System.in);
    String t = read.next();
    String s = null;

    { // this section is for validating user-input URL and correcting when necessary
      if (!t.toLowerCase().contains("http") && !t.toLowerCase().contains("www"))
      {
        System.out.println("This URL is invalid. Please wait while we resolve it...");
        s = ("https://www." + t);
      }

      if (!t.toLowerCase().contains("http") && t.toLowerCase().contains("www"))
      {
        System.out.println("This URL is valid. Please wait while we read and process the site data...");
        s = ("https://" + t);
      }

      if (t.toLowerCase().contains("http"))
      {
        System.out.println("This URL is valid. Please wait while we read and process the site data...");
        s = (t);
      }
    }// end URL validation
 
    final URL url = new URL(s);
    java.net.URLConnection connection = url.openConnection(); // connect to the data source
        
    try (InputStream inputStream = connection.getInputStream()) // receive/stream the data
    {
        final String connType = connection.getContentType(); // "final" because it does not get updated by the program

        if (connType != null)
        {
          System.out.println("\nContent type: " + connType);

        if (connType.toLowerCase().contains("html"))
        {
          Document doc = null;
          System.out.println("\nThis site has HTML, so we have to parse it down.\n");
             
        try
        {
          doc = Jsoup.connect(s).get();
          String title = doc.title();
          //Element body = doc.body(); //more elements, by node, can be added below
          Elements siteData = doc.select("p"); // "p" for "paragraph" tags <p></p>
          String articleTextt = siteData.toString();
          String articleText = articleTextt.toLowerCase();
          //System.out.println("Article" + body);
          System.out.println("Title: " + title + "\n");
          System.out.println(articleText + "\n");

          // because HTML can vary widely between larger and smaller sites, a validation step gauges
          // reliability. This is the calculated word count for parallel validation
          for (String a:articleText.split("[ </>',]")) 
          {   // words longer than one character or equal to "a"; all other one-byte words are not words:
            if ((a.length() > 1) || (a.length() == 1 && a == "a"))
            {
              b = p++; // b is now the rough calculated article word count
            }
          } // end for
                  
          // words are separated by blank spaces, trimmed to one blank space
          String wordCnt[] = articleText.trim().split("[ ]"); // words separated by blanks trimmed to one
          int wordCount = wordCnt.length;
                  
          // BufferedWriter filePath1 = new BufferedWriter(new FileWriter(csvPath, false));
          //PrintWriter writeData1 = new PrintWriter(filePath1);
          //writeData1.close();
          // ***data will stay in CSV until it is deleted. directory must be re-targeted after***
          // unique runtime sequence number composite with timestamp so can never duplicate
          //writeData1.println("This is the header,");
          for (String a:articleText.trim().split("[ ]")) // for each word in the article:
          {
             // this can be enhanced to contain different categorical arrays of words to match 
             // instead of only 'politics', e.g. "buy," "sell"             
              if (politics.contains(a)) // if any of each word matches/for each word that matches:
              {
                q = i++; // q is the current total match count. Both q and g get updated by i++
                k = q/wordCount;
                // integer g is the cycle sequence number, sequence resets with next URL
                g = (int) Math.rint(i);
                // create a new cycle sequence number with each match iteration:
                System.out.println("Match Iteration Sequence " + /* + timestamp + */ g + ": " + a);
                // "true" to make the CSV appendable instead of only settable
                // directory path must be hard-coded to be initialized in the FileWriter
              
                BufferedWriter filePath = new BufferedWriter(new FileWriter(csvPath, true));
                PrintWriter writeData = new PrintWriter(filePath);
                // ***data will stay in CSV until it is deleted. directory must be re-targeted after***
                // unique runtime sequence number composite with timestamp so can never duplicate
                writeData.print(System.currentTimeMillis() + g + ",");
                writeData.print(g + ","); // cycle record number
                writeData.print(a.toUpperCase() + ","); // matched word
                writeData.print((k*100.00) + ","); // match words as percent of overall words in cycle
                writeData.print(new java.util.Date() + ","); // date (with time) of data cycle
                writeData.print(url); // URL of data
                writeData.println();
                writeData.close();
                filePath.close();
              } // end if string "a" matches to politics
            } // end for string "a" in article
                  
            double sigmaTypeI = wordCount/b;
            double sigPercent = sigmaTypeI * 100;
            double kPercent = k * 100;
            int matchCount = (int) Math.rint(g);
            int calculatedCount = (int) Math.rint(b);
                  
            System.out.println("\nTotal word count (official): " + wordCount);
            // type I error is accepting as truth what is not; this is "90% confident" no type I error
            // develop statistics to measure confidence intervals here
            // if the first word count is greater than or less than 10% of the other word count, reject:
            if ((sigmaTypeI > 1.10) || (sigmaTypeI < 0.90))
            {
              System.err.println("This website's data structure is too complex for reliable analysis."
                        + "\nPlease provide a site with a simpler format:\n");
            }
            //if both word counts are within 10% of each other, provide some basic analysis (for now):
            if (sigmaTypeI <= 1.10 && sigmaTypeI >= 0.90)
            {
              System.out.println("Total word count estimate (calculated reliability estimate): "
                                   + calculatedCount);
              System.out.println("Word count reliability percentage: " + (sigPercent) + "%. This is"
                            + " reliable.");
              System.out.println("\nThe total count of occurrences matching political criteria: " 
                                  + matchCount);
              System.out.println("\nTotal percent of overall article words matching to political "
                                  + "criteria: " + (kPercent) + "%");
              // the below logic can be modified to account for predictions, e.g. "make a purchase"
              if( kPercent > 0.01)
              {
                  System.out.println("\nThis article is very political.");
              } 

              if (0.0005 < kPercent && kPercent <= 0.01)
              {
                  System.out.println("\nThis article is moderately political.");
              }

              if (kPercent <= 0.0005)
              {
                  System.out.println("\nThis article is not directly political.");
              }
              
              System.out.println("\nIf you would like to scan another website, enter the URL below. Otherwise, exit the"
                + " the program.\nYour CSV data file 'DataWarehouse.csv' has been updated and can be opened"
                + " at '" + csvPath + "'.\n");
          } // close if sigmaTypeI
        } // close try JSoup

        catch(IOException io)
        {
           System.err.println("An error ocurred while processing your data. Please restart the program.");
        }
      } // close if connType contains 'html'
         
         if (!connType.toLowerCase().contains("html"))
         {
          System.err.println("\nThis site does not have any HTML. Please provide a URL with HTML:");
         }
         } // close if connType is not null
     } //close try inputStream

        catch (MalformedURLException badURL)
        {
           System.err.println("The website at " + s + " is not valid. Please confirm you have a valid URL and re-enter"
                      + " when ready.");
        }
        
        catch (IOException io)
        {
            System.err.println("Your input is not a valid entry. Please re-try.");
        }
    } // end public NewsReaderTDM()

    public static void main(String[] args)
    {
        for (;;)
        {
            try
            {
                new NewsReaderTDM();
            }

            catch(IOException io)
            {
             System.err.println("There was a runtime compilation error. Please restart the program.");
            }
        }
    } // end main
} // end StockNews