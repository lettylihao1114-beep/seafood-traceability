@echo off
chcp 65001 >nul
cd /d %~dp0
rem ==== 一键启动：后端(8080) + 三个前端(3001/3002/3003) ====
rem 数据库可先运行 init-db.bat 初始化；DB_PASSWORD 可在调用前 set 覆盖默认的 123456
if "%DB_PASSWORD%"=="" set DB_PASSWORD=123456

echo [1/4] 检查后端 jar（缺失则用 Maven 打包）...
if not exist "seafood-server\target\seafood-server-1.0.0.jar" (
  cd seafood-server
  E:\Maven\apache-maven-3.9.9\bin\mvn -Dmaven.repo.local=E:\Maven\repository -DskipTests package
  cd ..
)

echo [2/4] 启动后端 8080 ...
start "冷冻海产品-后端8080" cmd /k "cd /d %~dp0seafood-server && set DB_USER=root&& set DB_PASSWORD=%DB_PASSWORD%&& java -jar target\seafood-server-1.0.0.jar"

echo [3/4] 启动三个前端 ...
start "冷冻海产品-管理端3001" cmd /k "cd /d %~dp0seafood-admin-web && npm run dev"
start "冷冻海产品-流通端3002" cmd /k "cd /d %~dp0seafood-node-web && npm run dev"
start "冷冻海产品-消费端3003" cmd /k "cd /d %~dp0seafood-consumer-web && npm run dev"

echo [4/4] 已启动 4 个窗口：
echo    后端   http://localhost:8080/api
echo    管理端 http://localhost:3001  (admin / 123456)
echo    流通端 http://localhost:3002  (breeding001.. / 123456)
echo    消费端 http://localhost:3003  (免登录, 溯源码 NFTS-20240901001)
echo 如需数据库初始化, 先运行 init-db.bat。
pause
