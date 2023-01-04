package top.meethigher.filestore.impl;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.*;

/**
 * Minio单侧
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/1/4 21:24
 */
public class MinioStoreServiceTest {

    private final MinioStoreService minioStoreService = MinioStoreService.builder()
            .endpoint("http://10.0.0.10:9000")
            .accessKey("minioadmin")
            .secretKey("minioadmin")
            .bucket("iloveyou")
            //根目录
            .path("/")
            //如果指定多级目录，如下
            //.path("/阿巴阿巴/AVA")
            .build();


    @Test
    public void write() throws Exception {
        File file = new File("D:\\wallPapers\\壁纸5.jpg");
//        boolean write = minioStoreService.write(new FileInputStream(file), "白凤.jpg");
        boolean write = minioStoreService.write(new FileInputStream(file), "白凤1.jpg");
        System.out.println(write);
    }

    @Test
    public void read() {
        String read = minioStoreService.read("白凤1.jpg");
        System.out.println(read);
    }

    @Test
    public void exist() {
        System.out.println(minioStoreService.exist("excel-import.xlsx"));
        System.out.println(minioStoreService.exist("excel-import.xlsx1"));
    }

    @Test
    public void delete() {
        boolean delete = minioStoreService.delete("白凤1.jpg");
        System.out.println(delete);
        delete = minioStoreService.delete("白凤1.jpg");
        System.out.println(delete);
    }
}