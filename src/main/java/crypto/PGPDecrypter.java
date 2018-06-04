package crypto;

import java.io.File;
import java.net.URI;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;


/**
 * Allows PGP decryption with Apache Camel and Bouncy Castle
 *
 * @author stockmann
 */
public class PGPDecrypter extends RouteBuilder
{

  private final CamelContext camelContext = new DefaultCamelContext();

  private static String privateKeyFileName;

  private static String keyUserId;

  private static String keyPass;

  private static String inputFile;

  private static String outputFile;

  /**
   * Inner instance holder, will be initialized when needed. Thread-safety is implicit.
   */
  private static final class InstanceHolder
  {

    static final PGPDecrypter INSTANCE = new PGPDecrypter();
  }

  /**
   * Singleton instance getter
   *
   * @return
   */
  public static PGPDecrypter getInstance()
  {
    return InstanceHolder.INSTANCE;
  }

  /**
   * Initialize the configuration
   *
   * @param privateKeyFileName
   * @param keyUserId
   * @param inputFile file to encrypt
   * @param outputFile output file for encrypted result
   */
  public static void init(URI privateKeyFileName,
                          String keyUserId,
                          String keyPass,
                          URI inputFile,
                          URI outputFile)
  {
    if (!new File(privateKeyFileName).exists())
    {
      throw new RuntimeException("Private key not existent.");
    }
    PGPDecrypter.privateKeyFileName = privateKeyFileName.toString();
    PGPDecrypter.keyUserId = keyUserId;
    PGPDecrypter.keyPass = keyPass;
    PGPDecrypter.inputFile = inputFile.toString();
    PGPDecrypter.outputFile = outputFile.toString();
  }

  /**
   * Prevent outer instance creation.
   *
   * @throws Exception
   */
  private PGPDecrypter()
  {
    try
    {
      String errorMsg = "Not correctly initialized. Call init first.";
      if (privateKeyFileName == null || keyUserId == null || keyPass == null)
      {
        throw new Exception(errorMsg);
      }
      camelContext.addRoutes(this);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void configure() throws Exception
  {
    System.out.println("Public PGP Key:\t\t" + privateKeyFileName);
    System.out.println("Key ID:\t\t\t" + keyUserId);
    System.out.println("Files to encrypt:\t" + inputFile);
    System.out.println("Decrypted files :\t" + outputFile);

    from(inputFile).unmarshal().pgp(privateKeyFileName, keyUserId, keyPass).to(outputFile);
  }

  /**
   * Start the camel context and therefore the encryption process.
   *
   * @throws Exception
   */
  public void start() throws Exception
  {
    camelContext.start();
    System.out.println("Camel context started.");
  }

  /**
   * Stop the camel context after successful encryption
   *
   * @throws Exception
   */
  public void stop() throws Exception
  {
    camelContext.stop();
    System.out.println("Camel context stopped.");
  }
}
