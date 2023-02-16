package top.meethigher.filestore.impl;

import org.junit.Test;
import top.meethigher.filestore.FileInfo;

import java.io.FileInputStream;
import java.util.List;

/**
 * ftp单元测试
 *
 * @author chenchuancheng
 * @since 2023/1/4 17:28
 */
public class FTPStoreServiceTest {

    private final String fileName = "白凤111.jpg";

    private FTPStoreService service = FTPStoreService.builder()
            .host("10.0.0.10")
            .port(21)
            .userName("ftpadmin")
            .password("Test123!@#")
            .path("/test")
            .tempPath("C:/Users/meethigher/Desktop/nihao")
            .build();


    @Test
    public void write() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("D:\\wallPapers\\壁纸5.jpg");
        service.write(fileInputStream, fileName);
    }

    @Test
    public void read() throws Exception {
        System.out.println(service.read(fileName));
    }

    @Test
    public void exist() throws Exception {
        System.out.println(service.exist(fileName));
    }

    @Test
    public void delete() throws Exception {
        System.out.println(service.delete(fileName));
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
    }
}
