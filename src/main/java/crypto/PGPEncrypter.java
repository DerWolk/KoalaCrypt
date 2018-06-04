package crypto;

import java.io.File;
import java.net.URI;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;


/**
 * Allows PGP encryption with Apache Camel
 *
 * @author stockmann
 */
public class PGPEncrypter extends RouteBuilder
{

  private final CamelContext camelContext = new DefaultCamelContext();

  private static String publicKeyFileName;

  private static String keyUserId;

  private static String inputFile;

  private static String outputFile;

  /**
   * Inner instance holder, will be initialized when needed. Thread-safety is implicit.
   */
  private static final class InstanceHolder
  {

    static final PGPEncrypter INSTANCE = new PGPEncrypter();
  }

  /**
   * Singleton instance getter
   *
   * @return
   */
  public static PGPEncrypter getInstance()
  {
    return InstanceHolder.INSTANCE;
  }

  /**
   * Initialize the configuration
   *
   * @param publicKeyFileName
   * @param keyUserId
   * @param inputFile file to encrypt
   * @param outputFile output file for encrypted result
   */
  public static void init(URI publicKeyFileName, String keyUserId, URI inputFile, URI outputFile)
  {
    if (!new File(publicKeyFileName).exists())
    {
      throw new RuntimeException("Public key not existent.");
    }
    PGPEncrypter.publicKeyFileName = publicKeyFileName.toString();
    PGPEncrypter.keyUserId = keyUserId;
    PGPEncrypter.inputFile = inputFile.toString();
    PGPEncrypter.outputFile = outputFile.toString();
  }

  /**
   * Prevent outer instance creation.
   *
   * @throws Exception
   */
  private PGPEncrypter()
  {
    try
    {
      String errorMsg = "Not correctly initialized. Call init first.";
      if (publicKeyFileName == null || keyUserId == null)
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
    System.out.println("Public PGP Key:\t\t" + publicKeyFileName);
    System.out.println("Key ID:\t\t\t" + keyUserId);
    System.out.println("Files to encrypt:\t" + inputFile);
    System.out.println("Decrypted files :\t" + outputFile);

    from(inputFile).marshal().pgp(publicKeyFileName, keyUserId).to(outputFile);
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
