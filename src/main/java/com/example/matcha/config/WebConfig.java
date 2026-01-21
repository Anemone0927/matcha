package com.example.matcha.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC の設定をカスタマイズするクラス。
 * 主に静的リソース（CSS, JS）とアップロードされたファイルへのアクセスパスを設定する。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // application.properties で設定されたアップロードディレクトリのパスを取得
    @Value("${upload.dir}")
    private String uploadDir;
    
    /**
     * リソースハンドラーを追加し、特定のURLパスをファイルシステムの場所やクラスパスにマッピングする。
     * * @param registry ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // 1. デフォルトの静的リソース (src/main/resources/static, /public など) へのマッピング
        // 例: /style.css は classpath:/static/style.css を参照
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/");

        // 2. アップロードされたファイル（画像など）へのマッピング設定
        // file: プレフィックスを付与し、末尾に / を追加してリソースロケーションを構成
        // 例: file:./uploads/
        String resourceLocation = "file:" + uploadDir + "/";

        // /images/** のURLパターンでアクセスがあった場合、以下のリソースロケーションを探索する。
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        resourceLocation,             // 1. アップロードフォルダ（アップロードされた画像）
                        "classpath:/static/images/",  // 2. 静的アセットの /images/ フォルダ
                        "classpath:/public/images/"   // 3. 静的アセットの /public/images/ フォルダ
                );
    }
}