package group.lis.uab.trip2gether;

import android.content.Context;
import android.test.InstrumentationTestCase;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import group.lis.uab.trip2gether.Resources.Encrypt;

public class resourcesTest extends InstrumentationTestCase {

    Context context;
    Encrypt encrypt;

    public void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getContext();
        assertNotNull(context);
        encrypt = new Encrypt(context);
        assertNotNull(encrypt);
    }

    public void testEncryptPasswordEmpty(){
        final String encryptedPassword = encrypt.encryptPassword("");
        final String stringEncryptedCorrect = "da39a3ee5e6b4b0d3255bfef95601890afd80709";
        final String stringEncryptedFalse = "nlsdf4ivofqngpiklasmlñkmpnmf47noini322ff";
        assertEquals(stringEncryptedCorrect, encryptedPassword);
        assertNotSame(stringEncryptedFalse, encryptedPassword);
    }

    public void testEncryptPasswordString(){
        final String encryptedPassword = encrypt.encryptPassword("hola");
        final String stringEncryptedCorrect = "99800b85d3383e3a2fb45eb7d0066a4879a9dad0";
        final String stringEncryptedFalse = "nkjsdfn032fnnbnj849vj2002n2ncn23njhjsdhs";
        assertEquals(stringEncryptedCorrect, encryptedPassword);
        assertNotSame(stringEncryptedFalse, encryptedPassword);
    }

    public void testEncryptPasswordNumber(){
        final String encryptedPassword = encrypt.encryptPassword("123456");
        final String stringEncryptedCorrect = "7c4a8d09ca3762af61e59520943dc26494f8941b";
        final String stringEncryptedFalse = "asuidfauf0o2j3f028fhn97bn823j23923hd238h";
        assertEquals(stringEncryptedCorrect, encryptedPassword);
        assertNotSame(stringEncryptedFalse, encryptedPassword);
    }

    public void testEncryptPasswordStringAndNumber(){
        final String encryptedPassword = encrypt.encryptPassword("123hola456");
        final String stringEncryptedCorrect = "5514207ab964b3c009b371bca731d00d79ae9be2";
        final String stringEncryptedFalse = "iie9bv93n34vn384n023nvuhndsjkpojasdfw2eg";
        assertEquals(stringEncryptedCorrect, encryptedPassword);
        assertNotSame(stringEncryptedFalse, encryptedPassword);
    }
}