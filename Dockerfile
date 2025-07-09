# Build stage: ソースコードからjarを作る
FROM eclipse-temurin:17 AS build

WORKDIR /app

# ソースコードとビルドツールをコピー
COPY . .

# Maven Wrapperを使ってビルド（テストはスキップ）
RUN ./mvnw package -DskipTests

# Run stage: 実行だけする軽いJREイメージ
FROM eclipse-temurin:17-jre

WORKDIR /app

# ビルド成果物のjarだけをbuildステージからコピー
COPY --from=build /app/target/matcha-0.0.1-SNAPSHOT.jar app.jar

# コンテナが受け付けるポート（application.propertiesのserver.portに合わせて）
EXPOSE 8083

# アプリを起動
ENTRYPOINT ["java", "-jar", "app.jar"]
