package top.meethigher.filestore.impl;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * 基于apache VFS的抽象文件存储服务
 *
 * @author chenchuancheng
 * @since 2023/1/4 20:19
 */
public abstract class AbstractVfsFileStore extends AbstractFileStore {

    private final Logger log = LoggerFactory.getLogger(AbstractVfsFileStore.class);


    /**
     * VFS默认的文件系统
     * 支持协议列表https://commons.apache.org/proper/commons-vfs/filesystems.html
     */
    private final DefaultFileSystemManager manager;

    /**
     * 自定义本地路径
     *
     * @param uri      归档存储uri
     * @param tempPath 本地存储路径
     */
    public AbstractVfsFileStore(URI uri, @Nullable String tempPath) {
        super(uri, tempPath);
        try {
            this.manager = (DefaultFileSystemManager) VFS.getManager();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public DefaultFileSystemManager getManager() {
        return manager;
    }

    /**
     * 转换文件名编码
     *
     * @param fileName 文件名
     * @return 转换编码后的文件名
     */
    protected String convertName(String fileName) {
        return fileName;
    }


    @Override
    public boolean write(InputStream is, String fileName) {
        notNull(is, "参数inputStream不可为空");
        notNull(fileName, "参数fileName不可为空");
        byte[] bytes = new byte[1024];
        int len;
        try (OutputStream os = getManager().resolveFile(getFilePath(getUri().toString(), convertName(fileName))).getContent().getOutputStream()) {
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            log.error("VFS write error:{}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String read(String fileName) {
        notNull(fileName, "参数fileName不可为空");
        String localFilePath = getFilePath(getTempPath(), fileName);
        byte[] bytes = new byte[1024];
        int len;
        try (InputStream is = getManager().resolveFile(getFilePath(getUri().toString(), convertName(fileName))).getContent().getInputStream();
             OutputStream os = new FileOutputStream(localFilePath)) {
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
            return localFilePath;
        } catch (Exception e) {
            log.error("VFS read error:{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public boolean delete(String fileName) {
        notNull(fileName, "参数fileName不可为空");
        boolean delete;
        try {
            FileObject fileObject = getManager().resolveFile(getFilePath(getUri().toString(), convertName(fileName)));
            delete = fileObject.delete();
        } catch (Exception e) {
            log.error("VFS delete error:{}", e.getMessage());
            delete = false;
        }
        return delete;
    }

    @Override
    public boolean exist(String fileName) {
        notNull(fileName, "参数fileName不可为空");
        try {
            FileObject fileObject = getManager().resolveFile(getFilePath(getUri().toString(), convertName(fileName)));
            return fileObject.exists();
        } catch (Exception e) {
            log.error("VFS exist error:{}", e.getMessage());
        }
        return false;
    }
}
