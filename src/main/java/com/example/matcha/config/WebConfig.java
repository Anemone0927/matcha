package com.example.matcha.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 💡 2. application.propertiesで設定したパスを格納するフィールドを追加
    @Value("${upload.dir}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // 🚨 修正点 1: デフォルトの静的リソースパスを復活させる 
        //    (src/main/resources/static/ 以下の画像やCSSがこれでアクセス可能になる)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/");

        // uploadDirの値に「file:」と末尾の「/」を追加し、リソースロケーションを構成する。
        String resourceLocation = "file:" + uploadDir + "/";

        // 🚨 修正点 2: アップロードされた画像を /images/** で公開する場合 (既存のimagesと競合する可能性あり)
        //    今回は、静的リソースとアップロードリソースを分けて公開することが推奨されます。
        //    しかし、ユーザーが /images/** でアップロード画像にアクセスしたいという意図があるかもしれないため、
        //    ここでは、元の /images/** マッピングに静的アセットの場所も追加する形に修正します。

        // 📝 旧コード:
        // registry.addResourceHandler("/images/**")
        //            .addResourceLocations(resourceLocation);
        
        // 📝 新しいコード (uploadDirと静的アセットの両方を /images/** で解決できるようにする):
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                    resourceLocation,                  // 1. アップロードフォルダ (file:./uploads/ など)
                    "classpath:/static/images/",       // 2. 静的アセットの /images/ フォルダ
                    "classpath:/public/images/"
                );
        
        // 🚨 追記: application.propertiesで upload.uri=/uploads/ と設定した場合、
        //    アップロード画像へのアクセスを /uploads/** に分ける方が安全です。
        //    (このコードには upload.uri の @Value がないので、/images/** に集約していますが、
        //     もし upload.uri があるなら、前回の私の提案の通り /uploads/** で分けてください)
    }
}
