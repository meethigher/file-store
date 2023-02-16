package top.meethigher.filestore.impl;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * ftp文件存储服务
 *
 * @author chenchuancheng
 * @since 2023/1/4 20:25
 */
public class FTPStoreService extends AbstractVfsFileStore {
    /**
     * 自定义本地路径
     *
     * @param uri      归档存储uri
     * @param tempPath 本地存储路径
     */
    public FTPStoreService(URI uri, @Nullable String tempPath) {
        super(uri, tempPath);
    }


    public static class FTPStoreServiceBuilder {

        /**
         * 主机地址
         */
        private String host;

        /**
         * 端口
         */
        private int port;

        /**
         * 存储路径
         */
        private String path;

        /**
         * 用户名
         */
        private String userName;

        /**
         * 密码
         */
        private String password;


        /**
         * 临时文件存储路径
         */
        private String tempPath;


        public FTPStoreServiceBuilder host(String host) {
            this.host = host;
            return this;
        }

        public FTPStoreServiceBuilder port(int port) {
            this.port = port;
            return this;
        }

        public FTPStoreServiceBuilder path(String path) {
            this.path = path;
            return this;
        }

        public FTPStoreServiceBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public FTPStoreServiceBuilder password(String password) {
            this.password = password;
            return this;
        }

        public FTPStoreServiceBuilder tempPath(String tempPath) {
            this.tempPath = tempPath;
            return this;
        }

        public FTPStoreService build() {
            URI uri = null;
            try {
                //解决账号密码中带有特殊字符的问题
                uri = URI.create(String.format("ftp://%s:%s@%s:%s%s",
                        URLEncoder.encode(this.userName, "utf-8"),
                        URLEncoder.encode(this.password, "utf-8"),
                        this.host,
                        this.port,
                        this.path));
            } catch (Exception e) {
                e.printStackTrace();
                uri = URI.create(String.format("ftp://%s:%s@%s:%s%s",
                        this.userName,
                        this.password,
                        this.host,
                        this.port,
                        this.path));
            }
            return new FTPStoreService(uri, this.tempPath);
        }
    }

    public static FTPStoreServiceBuilder builder() {
        return new FTPStoreServiceBuilder();
    }


    /**
     * FTP协议里面，规定文件名编码为iso-8859-1，所以目录名或文件名需要转码
     * [FTPClient 中文目录、中文文件名乱码、上传文件失败 解决方法 - 江南小镇的一缕阳光 - 博客园](https://www.cnblogs.com/shaowangwu/p/16642193.html)
     *
     * @param fileName
     * @return
     */
    @Override
    protected String encodeName(String fileName) {
        try {
            return new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        } catch (Exception e) {
            return fileName;
        }
    }


    @Override
    protected String decodeName(String fileName) {
        try {
            return new String(fileName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return fileName;
        }
    }
}
