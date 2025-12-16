#!/bin/bash

# 打包源码脚本 - 排除不必要的文件
# 用于提交给老师

PROJECT_NAME="BlitzBuy"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
OUTPUT_FILE="${PROJECT_NAME}_source_${TIMESTAMP}.zip"

echo "正在打包源码..."
echo "排除目录: node_modules, target, build, .git, .idea, .vscode"

# 使用 zip 命令，排除指定目录
zip -r "$OUTPUT_FILE" . \
    -x "*.git/*" \
    -x "*node_modules/*" \
    -x "*target/*" \
    -x "*build/*" \
    -x "*.idea/*" \
    -x "*.vscode/*" \
    -x "*.DS_Store" \
    -x "*.iml" \
    -x "*.log" \
    -x "*.zip" \
    -x "*.tar.gz" \
    > /dev/null 2>&1

if [ $? -eq 0 ]; then
    FILE_SIZE=$(du -h "$OUTPUT_FILE" | cut -f1)
    echo "✅ 打包完成!"
    echo "📦 文件: $OUTPUT_FILE"
    echo "📊 大小: $FILE_SIZE"
    echo ""
    echo "已排除的目录:"
    echo "  - node_modules/ (可通过 npm install 重新安装)"
    echo "  - target/ (可通过 mvn compile 重新生成)"
    echo "  - build/ (可通过 npm run build 重新生成)"
    echo "  - .git/ (版本控制历史)"
else
    echo "❌ 打包失败"
    exit 1
fi
