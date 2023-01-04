package top.meethigher.filestore.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.filestore.FileStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 本地文件存储服务
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/1/4 21:59
 */
public class LocalStoreService implements FileStore {

    private final Logger log = LoggerFactory.getLogger(LocalStoreService.class);


    /**
     * 根路径
     */
    private final String rootPath;

    public LocalStoreService(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * 获取文件全路径
     *
     * @param fileName 文件名
     * @return 全路径
     */
    private String fullPath(String fileName) {
        if (rootPath.endsWith("/")) {
            return String.format("%s%s", rootPath, fileName);
        } else {
            return String.format("%s/%s", rootPath, fileName);
        }
    }

    @Override
    public boolean write(InputStream inputStream, String fileName) {
        String fullPath = fullPath(fileName);
        byte[] bytes = new byte[1024];
        int len;
        try (OutputStream os = new FileOutputStream(fullPath)) {
            while ((len = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            log.error("LocalStoreService write error: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String read(String fileName) {
        return fullPath(fileName);
    }

    @Override
    public boolean exist(String fileName) {
        String fullPath = fullPath(fileName);
        return new File(fullPath).exists();
    }

    @Override
    public boolean delete(String fileName) {
        String fullPath = fullPath(fileName);
        File file = new File(fullPath);
        if (exist(fileName)) {
            return file.delete();
        } else {
            log.info("文件 {} 不存在，删除失败", fileName);
            return false;
        }
    }
}
