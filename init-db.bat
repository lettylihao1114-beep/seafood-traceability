@echo off
chcp 65001 >nul
cd /d %~dp0
rem ==== 一次性初始化数据库（建库 + 建表 + 种子数据）====
rem 可先 set DB_PASSWORD=你的密码 覆盖下面的默认值
if "%DB_PASSWORD%"=="" set DB_PASSWORD=123456
set MYSQL=D:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe

echo [初始化] 数据库 seafood_trace ...
"%MYSQL%" -uroot -p%DB_PASSWORD% -e "CREATE DATABASE IF NOT EXISTS seafood_trace DEFAULT CHARSET utf8mb4;" 2>nul

echo [初始化] 建表（utf8mb4）...
"%MYSQL%" -uroot -p%DB_PASSWORD% --default-character-set=utf8mb4 seafood_trace < sql\seafood_schema.sql

echo [初始化] 导入种子数据...
"%MYSQL%" -uroot -p%DB_PASSWORD% --default-character-set=utf8mb4 seafood_trace < sql\seafood_data.sql

echo [完成] 数据库初始化结束。账号密码见 README（默认 123456）。
pause
