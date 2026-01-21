# ----------------------------------
# 1. BUILD STAGE: アプリケーションのビルド
# ----------------------------------
FROM maven:3.9.5-eclipse-temurin-17 AS build

# 作業ディレクトリを設定
WORKDIR /app

# 必要なファイルをコピー
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# mvnwに実行権限を与える
RUN chmod +x mvnw

# 依存関係を先にダウンロード（ビルド時間の短縮）
RUN ./mvnw dependency:go-offline -B

# ソースコードをコピーしてビルド
COPY src src
RUN ./mvnw package -DskipTests

# ----------------------------------
# 2. RUNTIME STAGE: 実行用イメージの作成
# ----------------------------------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# ビルドステージで作成したJarファイルをコピー
# (名前を app.jar に変更してコピーすることで、起動コマンドをシンプルにします)
COPY --from=build /app/target/matcha-0.0.1-SNAPSHOT.jar app.jar

# 静的リソース（画像など）への読み取り権限を付与
RUN chmod -R 755 /app || true

# Renderが割り当てるポート(デフォルト8080)を開放
EXPOSE 8080

# アプリケーションの起動
# 環境変数はRenderの管理画面（Environment）から自動で注入されます
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]
```
eof

### これを反映させる手順

1.  **VS Code** などで、プロジェクトのルートにある `Dockerfile` をこの内容に書き換えて保存します。
2.  **Git で Push** します。
    ```bash
    git add Dockerfile
    git commit -m "Fix Dockerfile for Render"
    git push origin main