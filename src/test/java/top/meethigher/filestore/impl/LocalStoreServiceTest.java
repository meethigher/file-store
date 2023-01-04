package top.meethigher.filestore.impl;

import org.junit.Test;
import top.meethigher.filestore.FileStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * 单测
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/1/4 22:00
 */
public class LocalStoreServiceTest {

    private static final String fileName = "白凤.png";

    private final FileStore service = new LocalStoreService("C:/Users/meethigher/Desktop/nihao");


    @Test
    public void test01Write() throws Exception {
        InputStream is = new FileInputStream("D:\\wallPapers\\壁纸5.jpg");
        boolean result = service.write(is, fileName);
        assertTrue(result);
    }

    @Test
    public void test02Read() throws Exception {
        String filePath = service.read(fileName);
        System.out.println(filePath);
        assertNotNull(filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
    }

    @Test
    public void test03Exist() {
        System.out.println(service.exist(fileName));
    }

    @Test
    public void test04Delete() {
        System.out.println(service.delete(fileName));
    }
}