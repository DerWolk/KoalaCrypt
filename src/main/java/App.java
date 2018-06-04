import java.net.URI;
import java.net.URISyntaxException;

import crypto.PGPDecrypter;
import crypto.PGPEncrypter;


/**
 * KoalaCrypt
 *
 * @author stockmann
 */
public class App
{

  public static void main(String... args)
  {
    new App().call(args);
  }

  public String getGreeting()
  {
    return "\n######## Welcome to KoalaCrypt ########\n"
           + "\n\tPut all relevant files you wish to encrypt/decrypt, in the desired input folder.\n"
           + "\tThe output will be written to the desired output folder\n"
           + "\nHelp:\tjava -jar KoalaCrypt.jar -e|-d pgpKey keyUserID inputFolder outputFolder password (when using decrypt)\t\n";
  }

  public void call(String[] args)
  {
    System.out.println(getGreeting());

    if (args.length < 5)
    {
      System.out.println("Not enough arguments. Exit.");
      return;
    }
    String mode = args[0];
    if (mode.equals("-d") || mode.equals("-e"))
    {
      String pgpKey = args[1];
      String keyUserId = args[2];
      String inputFolder = args[3];
      String outputFolder = args[4];
      if (mode.equals("-e"))
      {
        // Encryption
        System.out.println("\nStarted encryption.\n");
        encrypt(pgpKey, keyUserId, inputFolder, outputFolder);
      }
      else if (mode.equals("-d"))
      {
        if (args.length < 6)
        {
          System.out.println("No Password for decryption specified. Exit.");
          return;
        }
        else
        {
          // Decryption
          System.out.println("\nStarted decryption.\n");
          String keyPass = args[5];
          decrypt(pgpKey, keyUserId, keyPass, inputFolder, outputFolder);
        }
      }
    }
    else
    {
      System.out.println("Please specify -e or -d as first argument. Exit.");
      return;
    }
  }

  private void encrypt(String publicKeyPath, String keyUserId, String inputFolder, String outputFolder)
  {
    try
    {
      PGPEncrypter.init(new URI("file:" + publicKeyPath),
                        keyUserId,
                        new URI("file:" + inputFolder),
                        new URI("file:" + outputFolder));
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }

    PGPEncrypter pgp = PGPEncrypter.getInstance();
    try
    {
      pgp.start();
      Thread.sleep(5000);
      pgp.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  private void decrypt(String privateKeyPath,
                       String keyUserId,
                       String keyPass,
                       String inputFolder,
                       String outputFolder)
  {
    try
    {
      PGPDecrypter.init(new URI("file:" + privateKeyPath),
                        keyUserId,
                        keyPass,
                        new URI("file:" + inputFolder),
                        new URI("file:" + outputFolder));
    }
    catch (URISyntaxException e)
    {
      e.printStackTrace();
    }

    PGPDecrypter pgp = PGPDecrypter.getInstance();
    try
    {
      pgp.start();
      Thread.sleep(5000);
      pgp.stop();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
