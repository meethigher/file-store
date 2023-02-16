package top.meethigher.filestore.impl;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.filestore.FileInfo;

import javax.annotation.Nullable;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 基于apache VFS的抽象文件存储服务
 *
 * @author chenchuancheng
 * @since 2023/1/4 20:19
 */
public abstract class AbstractVfsFileStore extends AbstractFileStore {

    private static final Logger log = LoggerFactory.getLogger(AbstractVfsFileStore.class);


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
     * 文件名编码
     *
     * @param fileName 文件名
     * @return 转换编码后的文件名
     */
    protected String encodeName(String fileName) {
        return fileName;
    }

    /**
     * 文件名解码
     *
     * @param fileName 文件名
     * @return 转换解码后的文件名
     */
    protected String decodeName(String fileName) {
        return fileName;
    }


    @Override
    public boolean write(InputStream is, String fileName) {
        notNull(is, "参数inputStream不可为空");
        notNull(fileName, "参数fileName不可为空");
        byte[] bytes = new byte[1024];
        int len;
        try (OutputStream os = getManager().resolveFile(getFilePath(getUri().toString(), encodeName(fileName))).getContent().getOutputStream()) {
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
        try (InputStream is = getManager().resolveFile(getFilePath(getUri().toString(), encodeName(fileName))).getContent().getInputStream();
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
            FileObject fileObject = getManager().resolveFile(getFilePath(getUri().toString(), encodeName(fileName)));
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
            FileObject fileObject = getManager().resolveFile(getFilePath(getUri().toString(), encodeName(fileName)));
            return fileObject.exists();
        } catch (Exception e) {
            log.error("VFS exist error:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public List<String> listFiles(Predicate<FileInfo> predicate) {
        List<String> list = new LinkedList<>();
        try {
            //fileObject本身迭代，可以拿到所有文件，包含子目录。如果只用getChildren方法，则取当前目录下的
            FileObject fileObject = getManager().resolveFile(getFilePath(getUri().toString(), ""));
            FileObject[] children = fileObject.getChildren();
            for (FileObject fo : children) {
                FileInfo fileInfo = FileInfo.FileInfoBuilder.builder()
                        .length(fo.isFile() ? fo.getContent().getSize() : 0L)
                        .isFile(fo.isFile())
                        .name(decodeName(fo.getName().getBaseName()))
                        .lastModified(fo.isFile() ? fo.getContent().getLastModifiedTime() : 0L)
                        .build();
                if (predicate.test(fileInfo)) {
                    list.add(fileInfo.getName());
                }
            }
        } catch (Exception e) {
            log.error("VFS list one file error:{}", e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
}
