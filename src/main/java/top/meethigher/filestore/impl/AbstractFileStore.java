package top.meethigher.filestore.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.meethigher.filestore.FileStore;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URI;

/**
 * 抽象文件存储服务
 *
 * @author chenchuancheng
 * @since 2023/1/4 20:47
 */
public abstract class AbstractFileStore implements FileStore {

    private static final Logger log = LoggerFactory.getLogger(AbstractFileStore.class);

    /**
     * 默认的存储文件夹
     */
    private static final String LOCAL_DIR = "/upload";

    /**
     * 实际存储文件的URI
     */
    private URI uri;

    /**
     * 本地临时存储路径
     */
    private String tempPath;

    /**
     * 自定义本地路径
     *
     * @param uri      归档存储uri
     * @param tempPath 本地存储路径
     */
    public AbstractFileStore(URI uri, @Nullable String tempPath) {
        notNull(uri, "参数 uri 不可为空");
        notNull(uri.getPath(), "参数 uri.getPath() 不可为空");
        tempPath = tempPath == null ? getDirByOS() : tempPath;
        createDir(tempPath);
        this.uri = uri;
        this.tempPath = tempPath;
    }

    /**
     * 获取文件地址
     *
     * @param fileName 文件名
     * @param baseDir  文件路径
     * @return 文件完整路径
     */
    protected String getFilePath(String baseDir, String fileName) {
        notNull(baseDir, "参数 baseDir 不可为空");
        notNull(fileName, "参数 fileName 不可为空");
        if (baseDir.endsWith("/")) {
            return String.format("%s%s", baseDir, fileName);
        } else {
            return String.format("%s/%s", baseDir, fileName);
        }
    }


    /**
     * 获取默认本地存储路径
     * 根据操作系统获取路径
     *
     * @return 对应系统的路径
     */
    protected String getDirByOS() {
        //默认路径，防止没有写权限，放到当前应用工作路径
        String currentWorkingPath = System.getProperty("user.dir");
        if (currentWorkingPath == null) {
            return LOCAL_DIR;
        }
        //Linux获取的路径是/，而Windows获取的路径是\，此处做统一处理
        currentWorkingPath = currentWorkingPath.replace("\\", "/");
        String localBaseDir = String.format("%s%s", currentWorkingPath, LOCAL_DIR);
        createDir(localBaseDir);
        return localBaseDir;
    }

    /**
     * 创建路径
     *
     * @param tempPath 路径文件夹
     */
    private void createDir(String tempPath) {
        File dir = new File(tempPath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) {
                log.error("创建本地存储路径失败:{}", tempPath);
            } else {
                log.info("存储路径=>{}不存在，已创建", tempPath);
            }
        }
    }


    /**
     * 校验
     *
     * @param o       对象
     * @param message 错误提示信息
     */
    protected void notNull(Object o, String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }


    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }
}
