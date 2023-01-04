package top.meethigher.filestore.impl;

import org.junit.Test;

import java.io.FileInputStream;

import static org.junit.Assert.*;

/**
 * ftp单元测试
 *
 * @author chenchuancheng
 * @since 2023/1/4 17:28
 */
public class FTPStoreServiceTest {

    private final String fileName = "白凤111.jpg";

    private FTPStoreService ftpStoreService = FTPStoreService.builder()
            .host("10.0.0.10")
            .port(21)
            .userName("ftpadmin")
            .password("meethigher")
            .path("/test")
            .tempPath("C:/Users/meethigher/Desktop/nihao")
            .build();


    @Test
    public void write() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("D:\\wallPapers\\壁纸5.jpg");
        ftpStoreService.write(fileInputStream, fileName);
    }

    @Test
    public void read() throws Exception {
        System.out.println(ftpStoreService.read(fileName));
    }

    @Test
    public void exist() throws Exception {
        System.out.println(ftpStoreService.exist(fileName));
    }

    @Test
    public void delete() throws Exception {
        System.out.println(ftpStoreService.delete(fileName));
    }
}
