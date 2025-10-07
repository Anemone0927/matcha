# ----------------------------------
# 1. BUILD STAGE: アプリケーションのビルド (Jarファイルの作成)
# ----------------------------------
# Maven環境（Java + Maven）のイメージを使う
FROM maven:3.9.5-eclipse-temurin-17 AS build

# 作業ディレクトリを設定
WORKDIR /app

# Maven Wrapperとpom.xmlをコピー
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 【ここが修正点！】mvnwに実行権限を与える
RUN chmod +x mvnw

# 依存関係（ライブラリ）のダウンロード
RUN mvn dependency:go-offline -B

# すべてのソースコードをコピー
COPY src src

# アプリケーションのビルド（テストはスキップ）
RUN ./mvnw package -DskipTests

# ----------------------------------
# 2. RUNTIME STAGE: 実行用イメージの作成
# ----------------------------------
# 実行環境は、より軽量なJava Runtime Environment (JRE) イメージを使う
FROM eclipse-temurin:17-jre-jammy

# 作業ディレクトリを設定
WORKDIR /app

# 環境変数で設定を上書き可能 (Railwayの変数が優先されます)
# 注: この設定は環境変数に依存するため、RailwayのVariables設定が優先されます。
#     このDockerfile内の値は、ここでは参照として機能しています。
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}
ENV SPRING_DATASOURCE_USERNAME=${PGUSER}
ENV SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}
ENV PORT=8080

# ビルドステージで作成したJarファイルをコピー
COPY --from=build /app/target/matcha-0.0.1-SNAPSHOT.jar app.jar

# 画像に読み取り権限を付ける
RUN chmod -R 755 /app/src/main/resources/static/images || true

# アプリケーションの起動コマンド
ENTRYPOINT ["java", "-jar", "app.jar"]
