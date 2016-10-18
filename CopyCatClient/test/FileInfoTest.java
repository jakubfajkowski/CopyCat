import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class FileInfoTest {
    @Test
    public void getExtension() throws Exception {
        FileInfo fileInfo = new FileInfo(new File(getClass().getResource("getExtensionTestFile.txt").toURI()));

        assertEquals("txt", fileInfo.getExtension());
    }

}