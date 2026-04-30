#!/usr/bin/env bash
# 从本脚手架「复制」出新项目：在新目录内改名、初始化全新 Git 仓库、首次提交并推送远程。
# 用法：在模板仓库根目录执行  bash docs/sh/unix/rename-project.sh
# Windows（无需 Bash/rsync，使用自带 PowerShell + robocopy）：docs/sh/windows/rename-project.ps1 或双击 docs/sh/windows/rename-project.cmd
# 说明：不会修改当前模板目录，仅生成新目录。

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEMPLATE_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

die() { echo "错误: $*" >&2; exit 1; }

command -v git >/dev/null 2>&1 || die "未找到 git，请先安装。"
command -v rsync >/dev/null 2>&1 || die "未找到 rsync（macOS/Linux 一般自带），请先安装。"

[[ -f "$TEMPLATE_ROOT/pom.xml" ]] || die "未找到根 pom.xml，请在模板仓库根目录执行（例如 bash docs/sh/unix/rename-project.sh）。"

grep -q '<artifactId>project-scaffold</artifactId>' "$TEMPLATE_ROOT/pom.xml" 2>/dev/null || \
  die "根 pom 的 artifactId 不是 project-scaffold；本脚本用于从官方模板复制出新项目，当前仓库可能已改名。"

[[ -d "$TEMPLATE_ROOT/project-common" ]] || die "未找到目录 project-common，仓库结构不符合本脚手架预期。"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  从模板复制新项目（改名 + 新 Git 仓库 + 首次推送）"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

read -r -p "① 新项目根目录名（原 project-scaffold，将作为根 artifactId、JWT issuer 等）: " NEW_ROOT_NAME
NEW_ROOT_NAME="${NEW_ROOT_NAME//[[:space:]]/}"
[[ -n "$NEW_ROOT_NAME" ]] || die "名称不能为空。"
[[ "$NEW_ROOT_NAME" != *"/"* ]] || die "名称不能包含 /。"
[[ "$NEW_ROOT_NAME" != "." && "$NEW_ROOT_NAME" != ".." ]] || die "无效名称。"

read -r -p "② 模块前缀（原 project-*，例如 zhongsheng → zhongsheng-api；包名 com.qmy.project → com.qmy.<前缀>）: " MODULE_PREFIX
MODULE_PREFIX="${MODULE_PREFIX//[[:space:]]/}"
[[ -n "$MODULE_PREFIX" ]] || die "前缀不能为空。"
[[ "$MODULE_PREFIX" != *"/"* ]] || die "前缀不能包含 /。"

JAVA_PKG="$MODULE_PREFIX"
if [[ "$JAVA_PKG" =~ - ]]; then
  JAVA_PKG="${JAVA_PKG//-/_}"
  echo "（提示）包名段已自动将「$MODULE_PREFIX」转为「$JAVA_PKG」。"
fi
[[ "$JAVA_PKG" =~ ^[a-zA-Z_][a-zA-Z0-9_]*$ ]] || die "无法得到合法 Java 包名段。"

PARENT="$(dirname "$TEMPLATE_ROOT")"
DEFAULT_DEST="$PARENT/$NEW_ROOT_NAME"
read -r -p "③ 新项目完整路径（回车使用默认: ${DEFAULT_DEST}）: " DEST_INPUT
if [[ -z "${DEST_INPUT//[[:space:]]/}" ]]; then
  DEST="$DEFAULT_DEST"
else
  DEST="$DEST_INPUT"
fi
[[ -n "$DEST" ]] || die "路径不能为空。"
if [[ -e "$DEST" ]]; then
  die "目标已存在，请换路径或删除后重试: $DEST"
fi

echo ""
echo "即将："
echo "  • 复制模板 → ${DEST}"
echo "  • 根 artifact: project-scaffold → ${NEW_ROOT_NAME}"
echo "  • 模块: project-* → ${MODULE_PREFIX}-*"
echo "  • 包名: com.qmy.project → com.qmy.${JAVA_PKG}"
echo "  • 在新目录 git init，git add（遵守 .gitignore），commit「chore: 初始化${NEW_ROOT_NAME} 项目」"
echo "  • 再提示输入远程地址，可选 push"
echo ""
read -r -p "确认开始？[y/N] " CONFIRM
[[ "${CONFIRM:-}" =~ ^[yY]$ ]] || { echo "已取消。"; exit 0; }

echo ""
echo "正在复制模板（排除 .git、target）……"
mkdir -p "$(dirname "$DEST")"
rsync -a \
  --exclude='.git' \
  --exclude='target' \
  --exclude='.DS_Store' \
  "$TEMPLATE_ROOT/" "$DEST/"

REPO_ROOT="$DEST"

# macOS / Linux 兼容的 sed -i
if sed --version >/dev/null 2>&1; then
  sed_i() { sed -i "$@"; }
else
  sed_i() { sed -i '' "$@"; }
fi

rewrite_text_files() {
  local f
  while IFS= read -r -d '' f; do
    case "$f" in
      */.git/*|*/target/*|*/node_modules/*) continue ;;
    esac
    sed_i \
      -e "s|com\.qmy\.project|com.qmy.${JAVA_PKG}|g" \
      "$f"
    sed_i \
      -e "s|project-infrastructure|${MODULE_PREFIX}-infrastructure|g" \
      -e "s|project-application|${MODULE_PREFIX}-application|g" \
      -e "s|project-common|${MODULE_PREFIX}-common|g" \
      -e "s|project-api|${MODULE_PREFIX}-api|g" \
      -e "s|project-core|${MODULE_PREFIX}-core|g" \
      -e "s|project-test|${MODULE_PREFIX}-test|g" \
      "$f"
    sed_i \
      -e "s|project-scaffold|${NEW_ROOT_NAME}|g" \
      "$f"
    sed_i \
      -e "s|project-parent|${MODULE_PREFIX}-parent|g" \
      -e "s|project-logback|${MODULE_PREFIX}-logback|g" \
      "$f"
    sed_i -e "s|scaffold for project|scaffold for ${MODULE_PREFIX}|g" "$f"
  done < <(find "$REPO_ROOT" \( \
      -name "*.java" -o -name "*.xml" -o -name "*.yml" -o -name "*.yaml" -o -name "*.properties" -o \
      -name "*.md" -o -name "*.sql" -o -name "*.json" -o -name "*.txt" \
    \) ! -path '*/.git/*' ! -path '*/target/*' -print0)
}

echo "正在替换 pom、YAML、Java、README 等……"
rewrite_text_files

echo "正在调整 Java 包目录 com/qmy/project → com/qmy/${JAVA_PKG} ……"
while IFS= read -r -d '' dir; do
  parent="$(dirname "$dir")"
  if [[ -d "$dir" && "$(basename "$dir")" == "project" ]]; then
    if [[ -e "$parent/$JAVA_PKG" ]]; then
      die "目标已存在: $parent/$JAVA_PKG"
    fi
    mv "$dir" "$parent/$JAVA_PKG"
  fi
done < <(find "$REPO_ROOT" -type d -path '*/src/*/java/com/qmy/project' ! -path '*/.git/*' ! -path '*/target/*' -print0)

echo "正在重命名 Maven 子模块目录……"
for name in common application api core infrastructure test; do
  if [[ -d "$REPO_ROOT/project-${name}" ]]; then
    echo "  project-${name} → ${MODULE_PREFIX}-${name}"
    mv "$REPO_ROOT/project-${name}" "$REPO_ROOT/${MODULE_PREFIX}-${name}"
  fi
done

SKILL_DIR="$REPO_ROOT/docs/skills/project-scaffold-coding"
if [[ -d "$SKILL_DIR" ]]; then
  NEW_SKILL="${NEW_ROOT_NAME}-coding"
  if [[ -e "$REPO_ROOT/docs/skills/${NEW_SKILL}" ]]; then
    die "目标已存在: docs/skills/${NEW_SKILL}"
  fi
  mv "$SKILL_DIR" "$REPO_ROOT/docs/skills/${NEW_SKILL}"
fi

# 新项目已非模板：去掉 README 开头脚手架简介、末尾「从模板复制新项目」整章，再提交
README_FILE="$REPO_ROOT/README.md"
if [[ -f "$README_FILE" ]]; then
  echo "正在精简新项目 README（去掉脚手架开头说明与复制脚本章节）……"
  # 首段：「基于 `com.qmy.xxx` 命名空间构建的 Spring Boot 多模块脚手架，……」
  sed_i '/命名空间构建的 Spring Boot 多模块脚手架/d' "$README_FILE"
  if grep -q '^## 从模板复制新项目（一键脚本）' "$README_FILE" 2>/dev/null; then
    sed_i '/^## 从模板复制新项目（一键脚本）/,$d' "$README_FILE"
  fi
fi

COMMIT_MSG="chore: 初始化${NEW_ROOT_NAME} 项目"

echo ""
read -r -p "远程仓库地址（git push 用，例如 git@github.com:org/repo.git；仅本地提交请留空）: " REMOTE_URL
REMOTE_URL="${REMOTE_URL//[[:space:]]/}"

echo "正在初始化 Git 仓库……"
cd "$REPO_ROOT"
rm -rf .git 2>/dev/null || true
git init -b master 2>/dev/null || { git init && git branch -M master; }

echo "正在添加文件（遵守 .gitignore，被忽略的不会进入暂存区）……"
git add .

if git diff --cached --quiet; then
  die "暂存区为空，请检查复制与 .gitignore。"
fi

echo "正在提交：${COMMIT_MSG}"
git -c i18n.commitEncoding=utf-8 commit -m "$COMMIT_MSG"

if [[ -n "$REMOTE_URL" ]]; then
  echo "正在添加远程并推送……"
  git remote add origin "$REMOTE_URL"
  if git push -u origin master; then
    echo "已推送到 origin/master。"
  else
    echo "推送失败，请检查网络与权限。本地提交已完成，可稍后执行："
    echo "  cd \"$REPO_ROOT\" && git push -u origin master"
  fi
else
  echo "未配置远程。稍后可执行："
  echo "  cd \"$REPO_ROOT\""
  echo "  git remote add origin <你的仓库地址>"
  echo "  git push -u origin master"
fi

echo ""
echo "完成。新项目路径："
echo "  $REPO_ROOT"
echo ""
echo "建议：cd \"$REPO_ROOT\" && mvn -q -DskipTests compile"
echo ""
