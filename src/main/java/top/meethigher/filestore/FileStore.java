package top.meethigher.filestore;

import java.io.InputStream;

/**
 * 文件存储服务
 *
 * @author chenchuancheng
 * @since 2023/1/4 20:46
 */
public interface FileStore {


    /**
     * 将字节流写入到对应的文件中
     *
     * @param inputStream 需要写入的输入流
     * @param fileName    写入的文件名称
     * @return true表示写入成功
     */
    boolean write(InputStream inputStream, String fileName);

    /**
     * 将指定的文件读取到本地存储，返回存储在本地完整的路径地址
     *
     * @param fileName 文件名称
     * @return 本地完整的路径地址
     */
    String read(String fileName);

    /**
     * 文件是否存在
     *
     * @param fileName 文件名称
     * @return true表示存在
     */
    boolean exist(String fileName);


    /**
     * 文件删除
     *
     * @param fileName 文件名称
     * @return true表示删除成功
     */
    boolean delete(String fileName);
}
