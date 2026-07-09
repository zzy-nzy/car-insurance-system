# ============================================================
# 自動車保険見積システム - フロントエンド Dockerイメージ
# マルチステージビルド: ビルド → Nginx配信
# ============================================================

# --- ステージ1: Node.jsでフロントエンドをビルド ---
# 阶段1：容器内编译打包前端
FROM node:18-alpine AS builder
WORKDIR /app
# 依存関係ファイルを先にコピーしてキャッシュ効率を向上
# 先复制依赖文件，加速缓存
COPY package.json package-lock.json ./
RUN npm install
# 全ソースコードをコンテナにコピー
# 复制全部前端源代码
COPY . .
# 本番用ビルドを実行
# 容器内自动执行打包，不需要你本地build
RUN npm run build

# --- ステージ2: Nginxで静的ファイルを配信 ---
# 阶段2：轻量Nginx运行网页
FROM nginx:alpine
# ビルド済みの成果物をNginxのhtmlディレクトリにコピー
# 从上面编译阶段复制打包好的产物（容器内部生成build）
COPY --from=builder /app/build /usr/share/nginx/html
# カスタムnginx.confがある場合はコメント解除
# 如果你有自定义nginx.conf配置文件，解开下面注释
# COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
