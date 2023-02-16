package top.meethigher.filestore.impl;

import org.junit.Test;
import top.meethigher.filestore.FileInfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Minio单侧
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/1/4 21:24
 */
public class MinioStoreServiceTest {

    private final MinioStoreService service = MinioStoreService.builder()
            .endpoint("http://10.0.0.10:9000")
            .accessKey("3HdOH8c6BwRxrRgF")
            .secretKey("N18WcNgi1ikVLC22Wpl6Ak4UwO9wwPhq")
            .bucket("test")
            //根目录
            .path("/ceshi")
            //如果指定多级目录，如下
            //.path("/阿巴阿巴/AVA")
            .build();


    @Test
    public void write() throws Exception {
        File file = new File("D:\\wallPapers\\壁纸5.jpg");
//        boolean write = minioStoreService.write(new FileInputStream(file), "白凤.jpg");
        boolean write = service.write(new FileInputStream(file), "白凤1.jpg");
        System.out.println(write);
    }

    @Test
    public void read() {
        String read = service.read("白凤1.jpg");
        System.out.println(read);
    }

    @Test
    public void exist() {
        System.out.println(service.exist("excel-import.xlsx"));
        System.out.println(service.exist("excel-import.xlsx1"));
    }

    @Test
    public void delete() {
        boolean delete = service.delete("白凤1.jpg");
        System.out.println(delete);
        delete = service.delete("白凤1.jpg");
        System.out.println(delete);
    }

    @Test
    public void listFiles() {
        //查所有文件
        List<String> strings = service.listFiles(fileInfo -> true);
        System.out.println(strings.size());
        System.out.println(strings);
        //根据文件类型查
        strings = service.listFiles(FileInfo::getIsFile);
        System.out.println(strings.size());
        System.out.println(strings);
        //根据大小查
        strings = service.listFiles(fileInfo -> fileInfo.getLength() > 175000);
        System.out.println(strings.size());
        System.out.println(strings);
        //根据修改时间查，查修改时间是2023年2月1号以后的
        strings = service.listFiles(fileInfo -> fileInfo.getLastModified() > 1675184460000L);
        System.out.println(strings.size());
        System.out.println(strings);


        System.out.println(service.exist(strings.get(0)));
    }
}
