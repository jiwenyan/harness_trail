#!/bin/bash
# 检查 DTO 使用规范

set -e

PROJECT_ROOT=$(cd "$(dirname "$0")/.." && pwd)
ERRORS=0

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}检查 DTO 规范...${NC}"

# 检查 Controller 是否返回 Entity
check_controller_return() {
    local api_dir="$PROJECT_ROOT/api-module/src/main/java/com/example/api/resources"

    if [ ! -d "$api_dir" ]; then
        return
    fi

    find "$api_dir" -name "*.java" | while read file; do
        # 检查方法返回类型是否是 Entity
        if grep -E "public.*Entity.*\(" "$file" | grep -v "Response"; then
            echo -e "${RED}❌ $file${NC}"
            echo "   Controller 方法不应该直接返回 Entity，应该返回 Response DTO"
            ERRORS=$((ERRORS+1))
        fi
    done
}

# 检查 DTO 是否在正确的包路径
check_dto_package() {
    local dto_dirs=("request" "response")

    for dto_type in "${dto_dirs[@]}"; do
        local dto_dir="$PROJECT_ROOT/api-module/src/main/java/com/example/api/dto/$dto_type"

        if [ ! -d "$dto_dir" ]; then
            continue
        fi

        find "$dto_dir" -name "*.java" | while read file; do
            # 检查类名规范
            local class_name=$(basename "$file" .java)

            if [ "$dto_type" == "request" ] && [[ ! "$class_name" =~ Request$ ]]; then
                echo -e "${YELLOW}⚠️  $file${NC}"
                echo "   Request DTO 类名应以 'Request' 结尾"
            fi

            if [ "$dto_type" == "response" ] && [[ ! "$class_name" =~ Response$ ]]; then
                echo -e "${YELLOW}⚠️  $file${NC}"
                echo "   Response DTO 类名应以 'Response' 结尾"
            fi
        done
    done
}

# 检查 DTO 是否有验证注解
check_dto_validation() {
    local request_dir="$PROJECT_ROOT/api-module/src/main/java/com/example/api/dto/request"

    if [ ! -d "$request_dir" ]; then
        return
    fi

    find "$request_dir" -name "*Request.java" | while read file; do
        # 检查是否有验证注解
        if ! grep -qE "@NotNull|@NotBlank|@NotEmpty|@Min|@Max|@Size" "$file"; then
            echo -e "${YELLOW}⚠️  $file${NC}"
            echo "   Request DTO 建议添加验证注解（@NotNull, @Min 等）"
        fi
    done
}

check_controller_return
check_dto_package
check_dto_validation

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✓ DTO 规范检查通过${NC}"
    exit 0
else
    echo -e "${RED}发现 $ERRORS 个 DTO 规范错误${NC}"
    exit 1
fi