package idc.inbound.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class FileUploadConfig {
    @Value("${file.upload.max-size}")
    private long maxFileSize;

    @Value("${file.upload.max-count}")
    private int maxFileCount;

}