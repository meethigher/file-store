package top.meethigher.filestore;

/**
 * 文件信息
 * 用于通用文件的信息存储，后续可直接扩展该对象，而不需要修改代码
 *
 * @author chenchuancheng
 * @since 2023/2/16 9:36
 */
public class FileInfo {

    /**
     * 文件的上次修改时间
     */
    private Long lastModified;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 是否是文件
     */
    private Boolean isFile;


    /**
     * 文件长度
     * 字节为单位
     */
    private Long length;


    public Long getLastModified() {
        return lastModified;
    }

    public String getName() {
        return name;
    }

    public Boolean getIsFile() {
        return isFile;
    }

    public Long getLength() {
        return length;
    }


    public static final class FileInfoBuilder {
        private Long lastModified;
        private String name;
        private Boolean isFile;
        private Long length;

        private FileInfoBuilder() {
        }

        public static FileInfoBuilder builder() {
            return new FileInfoBuilder();
        }

        public FileInfoBuilder lastModified(Long lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public FileInfoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FileInfoBuilder isFile(Boolean isFile) {
            this.isFile = isFile;
            return this;
        }

        public FileInfoBuilder length(Long length) {
            this.length = length;
            return this;
        }

        public FileInfo build() {
            FileInfo fileInfo = new FileInfo();
            fileInfo.lastModified = this.lastModified;
            fileInfo.isFile = this.isFile;
            fileInfo.name = this.name;
            fileInfo.length = this.length;
            return fileInfo;
        }
    }
}
