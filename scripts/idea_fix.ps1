# 实验二 环境自检与依赖预热脚本
# 用途：IDEA 报 "无法解析符号 springframework/junit" 时，先在命令行确认 Maven 工程本身是健康的，
#       再回到 IDEA 执行 "Reload All Maven Projects"。
#
# 运行：powershell -NoProfile -ExecutionPolicy Bypass -File scripts\idea_fix.ps1

$ErrorActionPreference = "Stop"
$projectDir = Split-Path -Parent $PSScriptRoot
Set-Location $projectDir

# 1) 固定使用 JDK 17（系统 PATH 默认是 JDK 8，会导致 "release version 17 not supported"）
$env:JAVA_HOME = "C:\Program Files\Java\LibericaJDK-17"
Write-Host "JAVA_HOME = $env:JAVA_HOME"

# 2) 检查 Maven settings 与本地仓库
$settings = "C:\Users\$env:USERNAME\.m2\settings.xml"
$repo = "C:\Users\$env:USERNAME\.m2\repository"
Write-Host ("settings.xml 存在 : {0}" -f (Test-Path $settings))
Write-Host ("本地仓库存在    : {0}  ({1})" -f (Test-Path $repo), $repo)

# 3) 预热 IDEA 导入所需插件（首次导入会用到这些，未缓存时会因离线/镜像超时而导入失败）
Write-Host "`n[1/3] 预热 Maven 插件（dependency / help）..."
& .\mvnw.cmd -q dependency:build-classpath "-Dmdep.outputFile=target\cp.txt" 2>&1 | Out-Null
Write-Host "      OK -> target\cp.txt"

# 4) 验证测试依赖（JUnit 5 + Spring Boot Test）是否解析成功
Write-Host "`n[2/3] 校验测试类能否编译（Test1ApplicationTests）..."
& .\mvnw.cmd -o clean test-compile 2>&1 | Select-String -Pattern "testCompile|Compiling 1 source|BUILD"
if ($LASTEXITCODE -ne 0) { throw "test-compile 失败，请查看上方 Maven 输出" }

# 5) 实际执行一次单元测试
Write-Host "`n[3/3] 执行单元测试（需要 MySQL 已启动）..."
& .\mvnw.cmd -o test 2>&1 | Select-String -Pattern "Tests run|BUILD"
if ($LASTEXITCODE -ne 0) { throw "单元测试失败，请查看上方 Maven 输出" }

Write-Host "`n命令行工程健康。若 IDEA 仍标红，请在 IDEA 中依次执行："
Write-Host "  1. Maven 工具窗口 -> 刷新图标 (Reload All Maven Projects)"
Write-Host "  2. File -> Invalidate Caches -> Invalidate and Restart"
Write-Host "  3. Settings -> Build,Execution,Deployment -> Maven，确认 Maven home=D:\apache-maven-3.10.0，JDK for importer=17"
