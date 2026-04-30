#Requires -Version 5.1
# 须以 UTF-8（含 BOM）保存：Windows PowerShell 5.1 对无 BOM 的 .ps1 会按系统代码页解析，易引发乱码与「意外的标记」错误。
<#
.SYNOPSIS
  从本脚手架「复制」出新项目：在新目录内改名、可选初始化全新 Git 仓库并推送远程。
.DESCRIPTION
  仅使用 Windows 自带的 PowerShell；复制使用 robocopy，不依赖 Git Bash / rsync / WSL。
  若系统 PATH 中已有 git，将自动执行 git init / add / commit / push；否则只完成改名与复制，并提示后续命令。
.NOTES
  用法：在模板仓库根目录于 PowerShell 中执行：
    powershell -NoProfile -ExecutionPolicy Bypass -File .\docs\sh\windows\rename-project.ps1
  或双击同目录下的 rename-project.cmd（会调用上述命令）。
  说明：不会修改当前模板目录，仅生成新目录。
#>

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Die([string]$Message) {
  [Console]::Error.WriteLine("错误: $Message")
  exit 1
}

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$TemplateRoot = Resolve-Path (Join-Path $ScriptDir '..\..\..') | Select-Object -ExpandProperty Path

function Test-GitAvailable {
  return [bool](Get-Command git -ErrorAction SilentlyContinue)
}

if (-not (Test-Path -LiteralPath (Join-Path $TemplateRoot 'pom.xml'))) {
  Die "未找到根 pom.xml，请在模板仓库根目录执行（例如 .\docs\sh\windows\rename-project.ps1）。"
}

$rootPom = Join-Path $TemplateRoot 'pom.xml'
$pomText = [System.IO.File]::ReadAllText($rootPom, [System.Text.Encoding]::UTF8)
if ($pomText -notmatch '<artifactId>project-scaffold</artifactId>') {
  Die "根 pom 的 artifactId 不是 project-scaffold；本脚本用于从官方模板复制出新项目，当前仓库可能已改名。"
}

if (-not (Test-Path -LiteralPath (Join-Path $TemplateRoot 'project-common') -PathType Container)) {
  Die "未找到目录 project-common，仓库结构不符合本脚手架预期。"
}

Write-Host '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
Write-Host '  从模板复制新项目（改名 + 可选 Git 仓库与推送）'
Write-Host '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━'
Write-Host ''

$NEW_ROOT_NAME = Read-Host '[1] 新项目根目录名（原 project-scaffold，将作为根 artifactId、JWT issuer 等）'
$NEW_ROOT_NAME = ($NEW_ROOT_NAME -replace '\s', '')
if ([string]::IsNullOrEmpty($NEW_ROOT_NAME)) { Die '名称不能为空。' }
if ($NEW_ROOT_NAME.Contains('/') -or $NEW_ROOT_NAME.Contains('\')) { Die '名称不能包含路径分隔符 / 或 \。' }
if (@('.', '..') -contains $NEW_ROOT_NAME) { Die '无效名称。' }

$MODULE_PREFIX = Read-Host '[2] 模块前缀（原 project-*，例如 zhongsheng → zhongsheng-api；包名 com.qmy.project → com.qmy.<前缀>）'
$MODULE_PREFIX = ($MODULE_PREFIX -replace '\s', '')
if ([string]::IsNullOrEmpty($MODULE_PREFIX)) { Die '前缀不能为空。' }
if ($MODULE_PREFIX.Contains('/') -or $MODULE_PREFIX.Contains('\')) { Die '前缀不能包含路径分隔符 / 或 \。' }

$JAVA_PKG = $MODULE_PREFIX
if ($JAVA_PKG -match '-') {
  $JAVA_PKG = $JAVA_PKG -replace '-', '_'
  Write-Host ('（提示）包名段已自动将 ' + $MODULE_PREFIX + ' 转为 ' + $JAVA_PKG + '。')
}
if ($JAVA_PKG -notmatch '^[a-zA-Z_][a-zA-Z0-9_]*$') {
  Die '无法得到合法 Java 包名段。'
}

$Parent = Split-Path -Parent $TemplateRoot
$DefaultDest = Join-Path $Parent $NEW_ROOT_NAME
$DestInput = Read-Host "[3] 新项目完整路径（回车使用默认: $DefaultDest）"
$DestInput = ($DestInput -replace '\s', '')
if ([string]::IsNullOrEmpty($DestInput)) {
  $DEST = $DefaultDest
} else {
  $DEST = $DestInput
}

if ([string]::IsNullOrEmpty($DEST)) { Die '路径不能为空。' }
$DEST = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($DEST)
if (Test-Path -LiteralPath $DEST) {
  Die "目标已存在，请换路径或删除后重试: $DEST"
}

Write-Host ''
Write-Host '即将：'
Write-Host "  • 复制模板 → $DEST"
Write-Host "  • 根 artifact: project-scaffold → $NEW_ROOT_NAME"
Write-Host "  • 模块: project-* → ${MODULE_PREFIX}-*"
Write-Host "  • 包名: com.qmy.project → com.qmy.$JAVA_PKG"
if (Test-GitAvailable) {
  Write-Host ('  • 在新目录 git init，git add（遵守 .gitignore），首次提交信息: chore: 初始化 ' + $NEW_ROOT_NAME + ' 项目')
  Write-Host '  • 再提示输入远程地址，可选 push'
} else {
  Write-Host '  • 未检测到 git（PATH 中无 git.exe），将跳过 Git 初始化；可稍后在新目录手动 git init'
}
Write-Host ''
$CONFIRM = Read-Host '确认开始？[y/N]'
if ($CONFIRM -notmatch '^[yY]$') {
  Write-Host '已取消。'
  exit 0
}

Write-Host ''
Write-Host '正在复制模板（排除 .git、target、node_modules）……'
$DestParent = Split-Path -Parent $DEST
if (-not (Test-Path -LiteralPath $DestParent)) {
  New-Item -ItemType Directory -Path $DestParent -Force | Out-Null
}

# Windows 自带 robocopy，无需 rsync
$robocopyArgs = @(
  $TemplateRoot, $DEST,
  '/E',
  '/XD', '.git', 'target', 'node_modules',
  '/XF', '.DS_Store',
  '/NFL', '/NDL', '/NJH', '/NJS', '/NC', '/NS', '/NP'
)
& robocopy @robocopyArgs
if ($LASTEXITCODE -ge 8) {
  Die "robocopy 失败，退出码: $LASTEXITCODE"
}

$REPO_ROOT = $DEST

function Get-TextFileUtf8 {
  param([string]$Path)
  # 读取时接受带/不带 BOM 的 UTF-8
  return [System.IO.File]::ReadAllText($Path, [System.Text.Encoding]::UTF8)
}

function Set-TextFileUtf8NoBom {
  param([string]$Path, [string]$Content)
  $enc = New-Object System.Text.UTF8Encoding $false
  [System.IO.File]::WriteAllText($Path, $Content, $enc)
}

function Apply-Rewrites {
  param([string]$Content)
  $c = $Content
  $c = $c -replace 'com\.qmy\.project', "com.qmy.$JAVA_PKG"
  $c = $c -replace 'project-infrastructure', "$MODULE_PREFIX-infrastructure"
  $c = $c -replace 'project-application', "$MODULE_PREFIX-application"
  $c = $c -replace 'project-common', "$MODULE_PREFIX-common"
  $c = $c -replace 'project-api', "$MODULE_PREFIX-api"
  $c = $c -replace 'project-core', "$MODULE_PREFIX-core"
  $c = $c -replace 'project-test', "$MODULE_PREFIX-test"
  $c = $c -replace 'project-scaffold', $NEW_ROOT_NAME
  $c = $c -replace 'project-parent', "$MODULE_PREFIX-parent"
  $c = $c -replace 'project-logback', "$MODULE_PREFIX-logback"
  $c = $c -replace 'scaffold for project', "scaffold for $MODULE_PREFIX"
  return $c
}

$textExtensions = @(
  '.java', '.xml', '.yml', '.yaml', '.properties',
  '.md', '.sql', '.json', '.txt'
)

Write-Host '正在替换 pom、YAML、Java、README 等……'
Get-ChildItem -LiteralPath $REPO_ROOT -Recurse -File -ErrorAction SilentlyContinue | ForEach-Object {
  $full = $_.FullName
  $rel = $full.Substring($REPO_ROOT.Length).TrimStart([char[]]@('\', '/'))
  if ($rel -match '[\\/]\.git[\\/]' -or $rel -match '[\\/]target[\\/]' -or $rel -match '[\\/]node_modules[\\/]') {
    return
  }
  $ext = $_.Extension.ToLowerInvariant()
  if ($textExtensions -notcontains $ext) {
    return
  }
  $raw = Get-TextFileUtf8 -Path $full
  $new = Apply-Rewrites -Content $raw
  if ($new -ne $raw) {
    Set-TextFileUtf8NoBom -Path $full -Content $new
  }
}

Write-Host "正在调整 Java 包目录 com/qmy/project → com/qmy/$JAVA_PKG ……"
$pkgDirs = Get-ChildItem -LiteralPath $REPO_ROOT -Recurse -Directory -ErrorAction SilentlyContinue |
  Where-Object {
    $_.Name -eq 'project' -and
    ($_.FullName -replace '\\', '/') -match '/src/[^/]+/java/com/qmy/project$'
  }
foreach ($dir in $pkgDirs) {
  $parent = Split-Path -Parent $dir.FullName
  $destPkg = Join-Path $parent $JAVA_PKG
  if (Test-Path -LiteralPath $destPkg) {
    Die "目标已存在: $destPkg"
  }
  Move-Item -LiteralPath $dir.FullName -Destination $destPkg
}

Write-Host '正在重命名 Maven 子模块目录……'
foreach ($name in @('common', 'application', 'api', 'core', 'infrastructure', 'test')) {
  $oldPath = Join-Path $REPO_ROOT "project-$name"
  if (Test-Path -LiteralPath $oldPath -PathType Container) {
    $newPath = Join-Path $REPO_ROOT "$MODULE_PREFIX-$name"
    Write-Host "  project-$name → ${MODULE_PREFIX}-$name"
    Move-Item -LiteralPath $oldPath -Destination $newPath
  }
}

$SkillDir = Join-Path $REPO_ROOT 'docs\skills\project-scaffold-coding'
if (Test-Path -LiteralPath $SkillDir -PathType Container) {
  $NewSkill = Join-Path $REPO_ROOT "docs\skills\${NEW_ROOT_NAME}-coding"
  if (Test-Path -LiteralPath $NewSkill) {
    Die "目标已存在: docs/skills/${NEW_ROOT_NAME}-coding"
  }
  Move-Item -LiteralPath $SkillDir -Destination $NewSkill
}

$README_FILE = Join-Path $REPO_ROOT 'README.md'
if (Test-Path -LiteralPath $README_FILE) {
  Write-Host '正在精简新项目 README（去掉脚手架开头说明与复制脚本章节）……'
  $readme = Get-TextFileUtf8 -Path $README_FILE
  $readme = $readme -replace '(?m)^.*命名空间构建的 Spring Boot 多模块脚手架.*\r?\n', ''
  if ($readme -match '(?ms)^## 从模板复制新项目（一键脚本）') {
    $readme = $readme -replace '(?ms)^## 从模板复制新项目（一键脚本）.*$', ''
  }
  Set-TextFileUtf8NoBom -Path $README_FILE -Content $readme
}

$COMMIT_MSG = "chore: 初始化${NEW_ROOT_NAME} 项目"

Write-Host ''
$REMOTE_URL = Read-Host '远程仓库地址（git push 用，例如 git@github.com:org/repo.git；仅本地提交请留空）'
$REMOTE_URL = ($REMOTE_URL -replace '\s', '')

if (-not (Test-GitAvailable)) {
  Write-Host ''
  Write-Host '未在 PATH 中找到 git，已跳过仓库初始化。若已安装 Git for Windows，可将 git.exe 所在目录加入 PATH 后重新运行本脚本，或在新项目目录手动执行：'
  Write-Host "  cd `"$REPO_ROOT`""
  Write-Host '  git init -b master'
  Write-Host '  git add .'
  Write-Host "  git -c i18n.commitEncoding=utf-8 commit -m `"$COMMIT_MSG`""
  if ($REMOTE_URL) {
    Write-Host "  git remote add origin `"$REMOTE_URL`""
    Write-Host '  git push -u origin main'
  }
  Write-Host ''
  Write-Host '完成。新项目路径：'
  Write-Host ('  ' + $REPO_ROOT)
  Write-Host ''
  Write-Host '建议在新项目目录执行: mvn -q -DskipTests compile'
  Write-Host ''
  exit 0
}

Write-Host ''
Write-Host '正在初始化 Git 仓库……'
Push-Location $REPO_ROOT
try {
  if (Test-Path -LiteralPath '.git' -PathType Container) {
    Remove-Item -LiteralPath '.git' -Recurse -Force
  }
  & git init -b master 2>$null
  if ($LASTEXITCODE -ne 0) {
    & git init
    if ($LASTEXITCODE -ne 0) { Die 'git init 失败。' }
    & git branch -M master
    if ($LASTEXITCODE -ne 0) { Die 'git branch -M master 失败。' }
  }

  Write-Host '正在添加文件（遵守 .gitignore，被忽略的不会进入暂存区）……'
  & git add .
  if ($LASTEXITCODE -ne 0) { Die 'git add 失败。' }

  & git diff --cached --quiet
  if ($LASTEXITCODE -eq 0) {
    Die '暂存区为空，请检查复制与 .gitignore。'
  }

  Write-Host "正在提交：$COMMIT_MSG"
  & git -c i18n.commitEncoding=utf-8 commit -m $COMMIT_MSG
  if ($LASTEXITCODE -ne 0) { Die 'git commit 失败。' }

  if ($REMOTE_URL) {
    Write-Host '正在添加远程并推送……'
    & git remote add origin $REMOTE_URL
    if ($LASTEXITCODE -ne 0) { Die 'git remote add 失败。' }
    & git push -u origin master
    if ($LASTEXITCODE -eq 0) {
      Write-Host '已推送到 origin/master。'
    } else {
      Write-Host '推送失败，请检查网络与权限。本地提交已完成，可稍后执行：'
      Write-Host "  cd `"$REPO_ROOT`" ; git push -u origin master"
    }
  } else {
    Write-Host '未配置远程。稍后可执行：'
    Write-Host "  cd `"$REPO_ROOT`""
    Write-Host '  git remote add origin <你的仓库地址>'
    Write-Host '  git push -u origin master'
  }
} finally {
  Pop-Location
}

Write-Host ''
Write-Host '完成。新项目路径：'
Write-Host ('  ' + $REPO_ROOT)
Write-Host ''
Write-Host '建议在新项目目录执行: mvn -q -DskipTests compile'
Write-Host ''
