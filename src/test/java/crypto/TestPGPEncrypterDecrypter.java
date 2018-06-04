package crypto;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.Test;


public class TestPGPEncrypterDecrypter
{

  private static final Path RESOURCE_FOLDER = Paths.get("src/test/resources");

  private static final String ENCRYPTED_FILE_NAME = "encrypted.txt";

  private static final String DECRYPTED_FILE_NAME = "decrypted.txt";

  /**
   * Test whether the PGPEncrypter works correctly and creates an encrypted file at the desired destination.
   *
   * @throws Exception
   */
  @Test
  public void testEncryption() throws Exception
  {
    FileUtils.copyFile(RESOURCE_FOLDER.resolve("encryptMe.txt").toFile(),
                       RESOURCE_FOLDER.resolve("in/" + ENCRYPTED_FILE_NAME).toFile());
    String keyUserId = "koala@koala.de";
    PGPEncrypter.init(RESOURCE_FOLDER.resolve("public.pgp").toUri(),
                      keyUserId,
                      RESOURCE_FOLDER.resolve("in/").toUri(),
                      RESOURCE_FOLDER.resolve("out/").toUri());
    PGPEncrypter classUnderTest = PGPEncrypter.getInstance();
    classUnderTest.start();
    Thread.sleep(3000);
    classUnderTest.stop();

    File encryptedFile = RESOURCE_FOLDER.resolve("out/" + ENCRYPTED_FILE_NAME).toFile();

    assertTrue("Encrypted file", encryptedFile.exists());

    FileUtils.forceDelete(encryptedFile);

  }

  @Test
  public void testDecryption() throws Exception
  {
    FileUtils.copyFile(RESOURCE_FOLDER.resolve("decryptMe.txt").toFile(),
                       RESOURCE_FOLDER.resolve("out/" + DECRYPTED_FILE_NAME).toFile());
    String keyUserId = "koala@koala.de";
    String keyPass = "pimmel";

    PGPDecrypter.init(RESOURCE_FOLDER.resolve("private.pgp").toUri(),
                      keyUserId,
                      keyPass,
                      RESOURCE_FOLDER.resolve("out/").toUri(),
                      RESOURCE_FOLDER.resolve("in/").toUri());

    PGPDecrypter classUnderTest = PGPDecrypter.getInstance();
    classUnderTest.start();
    Thread.sleep(3000);
    classUnderTest.stop();

    File decryptedFile = RESOURCE_FOLDER.resolve("in/" + DECRYPTED_FILE_NAME).toFile();

    assertTrue("Decrypted file", decryptedFile.exists());

    assertTrue("Decrypted correctly",
               FileUtils.contentEquals(decryptedFile, RESOURCE_FOLDER.resolve("encryptMe.txt").toFile()));

    FileUtils.forceDelete(decryptedFile);
  }
}
