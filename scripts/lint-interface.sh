#!/bin/bash
# 检查是否遵循"面向接口编程"原则

set -e

PROJECT_ROOT=$(cd "$(dirname "$0")/.." && pwd)
ERRORS=0

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}检查接口规范...${NC}"

# 检查 Service 实现类是否正确实现了接口
check_service_impl() {
    local service_dir="$PROJECT_ROOT/service-module/src/main/java/com/example/service/impl"

    if [ ! -d "$service_dir" ]; then
        return
    fi

    find "$service_dir" -name "*ServiceImpl.java" | while read file; do
        local class_name=$(basename "$file" .java)
        local interface_name="${class_name%Impl}"

        # 检查是否实现了对应的接口
        if ! grep -q "implements $interface_name" "$file"; then
            echo -e "${RED}❌ $file${NC}"
            echo "   $class_name 应该实现 $interface_name 接口"
            ERRORS=$((ERRORS+1))
        fi

        # 检查是否有 @Service 注解
        if ! grep -q "@Service" "$file"; then
            echo -e "${YELLOW}⚠️  $file 缺少 @Service 注解${NC}"
        fi
    done
}

# 检查 DAO 实现类是否正确实现了接口
check_dao_impl() {
    local dao_dir="$PROJECT_ROOT/dao-module/src/main/java/com/example/dao/impl"

    if [ ! -d "$dao_dir" ]; then
        return
    fi

    find "$dao_dir" -name "*DAOImpl.java" | while read file; do
        local class_name=$(basename "$file" .java)
        local interface_name="${class_name%Impl}"

        if ! grep -q "implements $interface_name" "$file"; then
            echo -e "${RED}❌ $file${NC}"
            echo "   $class_name 应该实现 $interface_name 接口"
            ERRORS=$((ERRORS+1))
        fi

        if ! grep -q "@Repository" "$file"; then
            echo -e "${YELLOW}⚠️  $file 缺少 @Repository 注解${NC}"
        fi
    done
}

# 检查字段注入是否使用接口类型
check_field_injection() {
    local modules=("api-module" "service-module" "dao-module")

    for module in "${modules[@]}"; do
        local src_dir="$PROJECT_ROOT/$module/src/main/java"

        if [ ! -d "$src_dir" ]; then
            continue
        fi

        find "$src_dir" -name "*.java" | while read file; do
            # 检查 @Autowired 字段的类型是否是接口
            if grep -q "@Autowired" "$file"; then
                # 提取 @Autowired 后面的字段声明
                local field_lines=$(grep -A 1 "@Autowired" "$file" | grep -E "(private|protected|public)")

                # 检查是否依赖了实现类（Impl结尾）
                if echo "$field_lines" | grep -q "Impl "; then
                    echo -e "${RED}❌ $file${NC}"
                    echo "   字段注入应该使用接口类型，而不是实现类"
                    ERRORS=$((ERRORS+1))
                fi
            fi
        done
    done
}

check_service_impl
check_dao_impl
check_field_injection

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✓ 接口规范检查通过${NC}"
    exit 0
else
    echo -e "${RED}发现 $ERRORS 个接口规范错误${NC}"
    exit 1
fi