@echo off
echo ============================================
echo  Flood Zoning PoC — Oracle DB Setup
echo ============================================
echo.

set SQLPLUS=D:\WINDOWS.X64_193000_db_home\bin\sqlplus.exe
set CONN=flood_app/flood_app@//localhost:1521/flooddb.tail81da12.ts.net.FLOODDB

echo [1/4] Skipped — tables already created.

echo [2/4] Registering spatial metadata...
echo EXIT | %SQLPLUS% -S %CONN% @"%~dp0\02-spatial-metadata.sql"

echo [3/4] Creating spatial indexes...
echo EXIT | %SQLPLUS% -S %CONN% @"%~dp0\03-spatial-indexes.sql"

echo [4/4] Seeding data...
echo EXIT | %SQLPLUS% -S %CONN% @"%~dp0\04-seed-data.sql"

echo.
echo ============================================
echo  Done!
echo ============================================
pause
