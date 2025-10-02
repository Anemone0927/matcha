# ベースイメージ
FROM eclipse-temurin:17-jdk-jammy

# 作業ディレクトリ作成
WORKDIR /app

# Mavenでビルド済みのJarをコピー
COPY target/matcha-0.0.1-SNAPSHOT.jar app.jar

# 環境変数で設定を上書き可能
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://<POSTGRES_HOST>:5432/<DB_NAME>
ENV SPRING_DATASOURCE_USERNAME=<DB_USER>
ENV SPRING_DATASOURCE_PASSWORD=<DB_PASSWORD>
ENV PORT=10000

# 画像に読み取り権限を付ける
RUN chmod -R 644 /app/src/main/resources/static/images

# アプリ起動
ENTRYPOINT ["java","-jar","app.jar"]
