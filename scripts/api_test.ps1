# 实验二 接口回归测试脚本：多条件分页、增删改、格式校验、唯一/外键约束、防 SQL 注入
# 运行前：先执行 sql/medical_db.sql 与 sql/02_init_data.sql 重置数据库，并启动后端（8080）
$ErrorActionPreference = "Stop"
$base = "http://localhost:8080"
$script:pass = 0
$script:fail = 0

function Call-Api {
    param([string]$Method, [string]$Path, [string]$Body)
    $uri = "$base$Path"
    try {
        if ($Body) {
            $bytes = [System.Text.Encoding]::UTF8.GetBytes($Body)
            $r = Invoke-WebRequest -Uri $uri -Method $Method -Body $bytes `
                -ContentType "application/json; charset=utf-8" -UseBasicParsing -TimeoutSec 15
        } else {
            $r = Invoke-WebRequest -Uri $uri -Method $Method -UseBasicParsing -TimeoutSec 15
        }
        $text = [System.Text.Encoding]::UTF8.GetString($r.RawContentStream.ToArray())
        return [pscustomobject]@{ Status = [int]$r.StatusCode; Json = ($text | ConvertFrom-Json); Raw = $text }
    } catch {
        $resp = $_.Exception.Response
        if ($resp -and $resp.GetResponseStream) {
            $sr = New-Object System.IO.StreamReader($resp.GetResponseStream(), [System.Text.Encoding]::UTF8)
            $text = $sr.ReadToEnd()
            return [pscustomobject]@{ Status = [int]$resp.StatusCode; Json = ($text | ConvertFrom-Json); Raw = $text }
        }
        throw
    }
}

function New-IdCard([string]$prefix17) {
    $w = @(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2)
    $c = @('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')
    $s = 0
    for ($i = 0; $i -lt 17; $i++) { $s += ([int]([string]$prefix17[$i])) * $w[$i] }
    return "$prefix17$($c[$s % 11])"
}

function Check {
    param([string]$Id, [string]$Case, [string]$Expect, $Result, [scriptblock]$Assert)
    # 断言块通过 $R 引用响应对象（$R 在 Check 作用域内定义，脚本块可按动态作用域读取）
    $null = New-Variable -Name "R" -Value $Result -Scope Local -Force
    $ok = $false
    try { $ok = [bool](& $Assert) } catch { $ok = $false }
    if ($ok) { $script:pass++ } else { $script:fail++ }
    $mark = if ($ok) { "PASS" } else { "FAIL" }
    Write-Host ("[{0}] {1,-4} {2,-34} {3}" -f $mark, $Id, $Case, $Expect)
    if (-not $ok) { Write-Host "        -> HTTP $($Result.Status) $($Result.Raw)" }
}

Write-Host "================ 1. 分页与多条件查询 ================"
Check "T01" "科室分页 page=1&size=3" "total=6, 返回 3 条, pages=2" (Call-Api GET "/api/departments?page=1&size=3") {
    $R.Json.data.total -eq 6 -and $R.Json.data.records.Count -eq 3 -and $R.Json.data.pages -eq 2 }
Check "T02" "科室名称模糊查询" "total=1" (Call-Api GET "/api/departments?deptName=%E5%86%85") {
    $R.Json.data.total -eq 1 -and $R.Json.data.records[0].deptName -eq "内科" }
Check "T03" "科室位置模糊+电话精确" "total=1" (Call-Api GET "/api/departments?location=%E9%97%A8%E8%AF%8A%E6%A5%BC&tel=0731-8801002") {
    $R.Json.data.total -eq 1 -and $R.Json.data.records[0].deptCode -eq "D002" }
Check "T04" "医生 科室+性别+状态 组合" "返回记录均满足 deptId=1 且 gender=M 且 status=1" (Call-Api GET "/api/doctors?deptId=1&gender=M&status=1") {
    $R.Json.data.total -ge 1 -and @($R.Json.data.records | Where-Object { $_.deptId -ne 1 -or $_.gender -ne "M" -or $_.status -ne 1 }).Count -eq 0 }
Check "T05" "医生姓名模糊查询" "包含 刘雅琴" (Call-Api GET "/api/doctors?name=%E5%88%98") {
    $R.Json.data.records[0].name -eq "刘雅琴" }
Check "T06" "医生 职称+科室 组合" "total=1 (内科主任医师张建国)" (Call-Api GET "/api/doctors?deptId=1&titleId=1&status=1") {
    $R.Json.data.total -eq 1 -and $R.Json.data.records[0].name -eq "张建国" }
Check "T07" "患者手机号精确查询" "total=1" (Call-Api GET "/api/patients?phone=13800000003") {
    $R.Json.data.total -eq 1 -and $R.Json.data.records[0].name -eq "罗俊" }
Check "T08" "患者性别+姓名模糊" "全部为女性且 total>=1" (Call-Api GET "/api/patients?gender=F&name=%E6%9D%8E")  {
    $R.Status -eq 200 -and (($R.Json.data.records | Where-Object { $R.gender -ne "F" }).Count -eq 0) }
Check "T09" "分页越界 page=99" "records 为空, total 不变" (Call-Api GET "/api/departments?page=99&size=10") {
    $R.Json.data.records.Count -eq 0 -and $R.Json.data.total -eq 6 }

Write-Host "================ 2. 新增 / 修改 / 删除 ================"
$newDept = Call-Api POST "/api/departments" '{"deptCode":"D007","deptName":"皮肤科","location":"门诊楼 6F-A 区","tel":"0731-8801007","description":"皮肤病与性病门诊"}'
Check "T10" "新增科室 D007" "code=200 且返回自增 deptId" $newDept { $R.Json.code -eq 200 -and $R.Json.data.deptId -gt 0 }
$deptId = $newDept.Json.data.deptId
Check "T11" "修改科室电话" "tel 更新为 0731-8801707" (Call-Api PUT "/api/departments/$deptId" '{"deptCode":"D007","deptName":"皮肤科","location":"门诊楼 6F-A 区","tel":"0731-8801707","description":"皮肤病、医美与性病门诊"}') {
    $R.Json.data.tel -eq "0731-8801707" }
Check "T12" "新增医生（合法身份证/手机号）" "code=200" (Call-Api POST "/api/doctors" ('{"doctorNo":"DOC011","deptId":' + $deptId + ',"titleId":3,"name":"林可欣","gender":"F","phone":"13900000011","idCard":"' + (New-IdCard "11010119921203002") + '","status":1}')) {
    $R.Json.code -eq 200 -and $R.Json.data.doctorId -gt 0 }
$doctorId = (Call-Api GET "/api/doctors?phone=13900000011").Json.data.records[0].doctorId
Check "T13" "修改医生职称与状态" "titleId=2, status=0" (Call-Api PUT "/api/doctors/$doctorId" ('{"doctorNo":"DOC011","deptId":' + $deptId + ',"titleId":2,"name":"林可欣","gender":"F","phone":"13900000011","idCard":"' + (New-IdCard "11010119921203002") + '","status":0}')) {
    $R.Json.data.titleId -eq 2 -and $R.Json.data.status -eq 0 }
Check "T14" "新增患者（自动生成就诊卡号）" "patientNo 以 P 开头" (Call-Api POST "/api/patients" ('{"name":"周敏","gender":"F","phone":"13800000011","idCard":"' + (New-IdCard "44010119900501001") + '","address":"广东省深圳市南山区科苑路 9 号"}')) {
    $R.Json.code -eq 200 -and $R.Json.data.patientNo.StartsWith("P") }
$patientId = (Call-Api GET "/api/patients?phone=13800000011").Json.data.records[0].patientId
Check "T15" "修改患者地址" "address 已更新" (Call-Api PUT "/api/patients/$patientId" ('{"name":"周敏","gender":"F","phone":"13800000011","idCard":"' + (New-IdCard "44010119900501001") + '","address":"广东省深圳市福田区深南大道 1 号"}')) {
    $R.Json.data.address -eq "广东省深圳市福田区深南大道 1 号" }
Check "T16" "删除无挂号记录的患者" "code=200" (Call-Api DELETE "/api/patients/$patientId") { $R.Json.code -eq 200 }
Check "T17" "删除患者后再查询" "total=0" (Call-Api GET "/api/patients?phone=13800000011") { $R.Json.data.total -eq 0 }
Check "T18" "删除仍有挂号记录的医生" "HTTP 400 并给出友好提示" (Call-Api DELETE "/api/doctors/1") { $R.Status -eq 400 -and $R.Json.message -match "挂号记录" }
Check "T19" "删除仍有医生的科室" "HTTP 400 并给出友好提示" (Call-Api DELETE "/api/departments/1") { $R.Status -eq 400 -and $R.Json.message -match "医生" }

Write-Host "================ 3. 敏感字段格式校验 ================"
Check "T20" "手机号 123" "HTTP 400 手机号格式不正确" (Call-Api POST "/api/doctors" '{"doctorNo":"DOC012","deptId":1,"titleId":1,"name":"测试","gender":"M","phone":"123","idCard":"110101199212030024"}') { $R.Status -eq 400 -and $R.Json.message -match "手机号" }
Check "T21" "身份证校验位错误" "HTTP 400 校验位不正确" (Call-Api POST "/api/doctors" '{"doctorNo":"DOC013","deptId":1,"titleId":1,"name":"测试","gender":"M","phone":"13900000013","idCard":"110101199212030025"}') { $R.Status -eq 400 -and $R.Json.message -match "校验位" }
Check "T22" "身份证出生日期 1992-02-30" "HTTP 400 出生日期不合法" (Call-Api POST "/api/doctors" '{"doctorNo":"DOC014","deptId":1,"titleId":1,"name":"测试","gender":"M","phone":"13900000014","idCard":"110101199202300024"}') { $R.Status -eq 400 -and $R.Json.message -match "出生日期" }
Check "T23" "身份证长度 17 位" "HTTP 400 格式不正确" (Call-Api POST "/api/doctors" '{"doctorNo":"DOC015","deptId":1,"titleId":1,"name":"测试","gender":"M","phone":"13900000015","idCard":"11010119921203002"}') { $R.Status -eq 400 }
Check "T24" "姓名为空" "HTTP 400 姓名不能为空" (Call-Api POST "/api/patients" '{"name":"  ","gender":"M","phone":"13800000012","idCard":"440101199005010019"}') { $R.Status -eq 400 -and $R.Json.message -match "姓名" }
Check "T25" "性别非法 X" "HTTP 400 性别格式不正确" (Call-Api POST "/api/patients" '{"name":"张三","gender":"X","phone":"13800000013","idCard":"440101199005010019"}') { $R.Status -eq 400 -and $R.Json.message -match "性别" }
Check "T26" "每页条数 size=1000" "HTTP 400 分页参数被拒绝" (Call-Api GET "/api/doctors?size=1000") { $R.Status -eq 400 }
Check "T27" "页码 page=0" "HTTP 400" (Call-Api GET "/api/doctors?page=0") { $R.Status -eq 400 }
Check "T28" "科室电话非法 abc" "HTTP 400" (Call-Api POST "/api/departments" '{"deptCode":"D009","deptName":"康复科","location":"门诊楼 7F","tel":"abc"}') { $R.Status -eq 400 }

Write-Host "================ 4. 唯一约束 / 外键约束 ================"
Check "T29" "重复手机号" "HTTP 400 手机号已存在" (Call-Api POST "/api/patients" '{"name":"重复","gender":"M","phone":"13800000001","idCard":"440101199005010027"}') { $R.Status -eq 400 -and $R.Json.message -match "手机号已存在" }
Check "T30" "重复身份证号" "HTTP 400 身份证号已存在" (Call-Api POST "/api/patients" '{"name":"重复2","gender":"M","phone":"13800000014","idCard":"370101198304190117"}') { $R.Status -eq 400 -and $R.Json.message -match "身份证号已存在" }
Check "T31" "重复科室名称" "HTTP 400 名称已存在" (Call-Api POST "/api/departments" '{"deptCode":"D010","deptName":"内科","location":"门诊楼 9F","tel":"0731-8801099"}') { $R.Status -eq 400 -and $R.Json.message -match "已存在" }
Check "T32" "医生所属科室不存在 deptId=99" "HTTP 400" (Call-Api POST "/api/doctors" ('{"doctorNo":"DOC017","deptId":99,"titleId":1,"name":"测试","gender":"M","phone":"13900000017","idCard":"' + (New-IdCard "11010119921203007") + '"}')) { $R.Status -eq 400 -and $R.Json.message -match "科室不存在" }
Check "T33" "医生职称不存在 titleId=99" "HTTP 400" (Call-Api POST "/api/doctors" ('{"doctorNo":"DOC018","deptId":1,"titleId":99,"name":"测试","gender":"M","phone":"13900000018","idCard":"' + (New-IdCard "11010119921203007") + '"}')) { $R.Status -eq 400 -and $R.Json.message -match "职称不存在" }

Write-Host "================ 5. PreparedStatement 防 SQL 注入 ================"
Check "T34" "注入串 ' OR '1'='1（科室名）" "total=0，未泄露数据" (Call-Api GET "/api/departments?deptName=%27%20OR%20%271%27%3D%271") { $R.Json.data.total -eq 0 -and $R.Json.data.records.Count -eq 0 }
Check "T35" "注入串 %' OR 1=1 -- (医生姓名)" "total=0" (Call-Api GET "/api/doctors?name=%25%27%20OR%201%3D1%20--%20") { $R.Json.data.total -eq 0 }
Check "T36" "注入串 1' UNION SELECT ... (患者手机号)" "total=0" (Call-Api GET "/api/patients?phone=1%27%20UNION%20SELECT%20*%20FROM%20mysql.user%20--%20") { $R.Json.data.total -eq 0 }
Check "T37" "删除注入 /api/departments/1 OR 1=1" "HTTP 400 参数类型错误，数据未被删除" (Call-Api DELETE "/api/departments/1%20OR%201=1") { $R.Status -eq 400 }
Check "T38" "注入后数据完好" "科室仍为 7 条" (Call-Api GET "/api/departments?size=100") { $R.Json.data.total -eq 7 }

Write-Host "================ 6. 汇总 ================"
Write-Host ("通过 {0} 项，失败 {1} 项" -f $script:pass, $script:fail)
if ($script:fail -gt 0) { exit 1 }
