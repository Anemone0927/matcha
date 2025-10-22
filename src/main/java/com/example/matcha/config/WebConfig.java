package com.example.matcha.config;

import org.springframework.beans.factory.annotation.Value; // 💡 1. @Valueをインポート
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 💡 2. application.propertiesで設定したパスを格納するフィールドを追加
    @Value("${upload.dir}")
    private String uploadDir;
    
    // ⚠️ 元々あった C: ドライブのパス指定は不要なので削除します。
    // private static final String EXTERNAL_IMAGE_PATH = "file:///C:/Users/24i1004/matcha/images/"; 

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // uploadDirの値に「file:」と末尾の「/」を追加し、リソースロケーションを構成する。
        // 例: uploadDirが「./uploaded_images」の場合、resourceLocationは「file:./uploaded_images/」となる。
        String resourceLocation = "file:" + uploadDir + "/";

        registry.addResourceHandler("/images/**")
                // 💡 3. 動的に構成したパスを設定
                .addResourceLocations(resourceLocation);
    }
}
