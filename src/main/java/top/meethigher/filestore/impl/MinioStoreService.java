package top.meethigher.filestore.impl;

import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * minio文件存储服务
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2023/1/4 21:20
 */
public class MinioStoreService extends AbstractFileStore {



    private final Logger log = LoggerFactory.getLogger(MinioStoreService.class);

    /**
     * MinioClient
     */
    private final MinioClient minioClient;

    /**
     * 桶
     */
    private final String bucket;

    public MinioStoreService(String endpoint,
                             String accessKey,
                             String secretKey,
                             String bucket,
                             String path,
                             @Nullable String tempPath) {
        super(URI.create(path), tempPath);
        this.bucket = bucket;
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Override
    public boolean write(InputStream inputStream, String fileName) {
        notNull(inputStream, "参数inputStream不可为空");
        notNull(fileName, "参数fileName不可为空");
        String localFilePath = getFilePath(getTempPath(), fileName);
        byte[] bytes = new byte[1024];
        int len;
        //uploadObject需要一个文件对象计算一系列属性、类型等。该操作也可以自己通过putObject直接传入流实现，只不过需要自己计算。
        try (OutputStream os = new FileOutputStream(localFilePath)) {
            while ((len = inputStream.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
            UploadObjectArgs args = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(getFilePath(getUri().getPath(), fileName))
                    .filename(localFilePath).build();
            minioClient.uploadObject(args);
            return true;
        } catch (Exception e) {
            log.error("Minio write error:{}", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String read(String fileName) {
        notNull(fileName, "参数fileName不可为空");
        if (!exist(fileName)) {
            log.info("{} 文件不存在", fileName);
            return null;
        }
        GetObjectArgs args = GetObjectArgs.builder().bucket(bucket).object(getFilePath(getUri().getPath(), fileName)).build();
        String localFilePath = getFilePath(getTempPath(), fileName);
        int len;
        byte[] bytes = new byte[1024];
        try (InputStream is = minioClient.getObject(args); OutputStream os = new FileOutputStream(localFilePath)) {
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            os.flush();
            return localFilePath;
        } catch (Exception e) {
            log.error("Minio read error:{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean exist(String fileName) {
        notNull(fileName, "参数fileName不可为空");
        boolean exist = true;
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(getFilePath(getUri().getPath(), fileName)).build());
        } catch (Exception e) {
            log.error("Minio exist error：{}", e.getMessage());
            exist = false;
        }
        return exist;
    }

    @Override
    public boolean delete(String fileName) {
        notNull(fileName, "参数fileName不可为空");
        if (!exist(fileName)) {
            log.info("{} 文件不存在", fileName);
            return false;
        }
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(getFilePath(getUri().getPath(), fileName))
                .build();
        try {
            minioClient.removeObject(args);
        } catch (Exception e) {
            log.error("Minio delete error: {}", e.getMessage());
            return false;
        }
        return true;
    }

    public static class MinioStoreServiceBuilder {
        /**
         * 连接url
         */
        private String endpoint;

        /**
         * 公钥
         */
        private String accessKey;

        /**
         * 私钥
         */
        private String secretKey;

        /**
         * 桶
         */
        private String bucket;

        /**
         * 文件存储路径
         */
        private String path;


        /**
         * 本地临时存储路径
         */
        private String tempPath;


        public MinioStoreServiceBuilder() {
        }


        public MinioStoreServiceBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public MinioStoreServiceBuilder accessKey(String accessKey) {
            this.accessKey = accessKey;
            return this;
        }

        public MinioStoreServiceBuilder secretKey(String secretKey) {
            this.secretKey = secretKey;
            return this;
        }

        public MinioStoreServiceBuilder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        /**
         * @param path minio实际存储的路径
         *             如果是多级路径 eg: /阿巴阿巴/AVA
         *             如果是根目录 eg: /
         * @return
         */
        public MinioStoreServiceBuilder path(String path) {
            this.path = path;
            return this;
        }

        public MinioStoreServiceBuilder tempPath(String tempPath) {
            this.tempPath = tempPath;
            return this;
        }

        public MinioStoreService build() {
            return new MinioStoreService(
                    this.endpoint, this.accessKey, this.secretKey,
                    this.bucket,
                    this.path, this.tempPath
            );
        }
    }


    public static MinioStoreServiceBuilder builder() {
        return new MinioStoreServiceBuilder();
    }
}
