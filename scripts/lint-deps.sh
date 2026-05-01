#!/bin/bash
# 检查 Maven 模块之间的依赖关系是否符合分层架构

set -e

PROJECT_ROOT=$(cd "$(dirname "$0")/.." && pwd)
ERRORS=0

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}检查模块依赖方向...${NC}"

# 检查 pom.xml 中的依赖
check_pom_dependency() {
    local module=$1
    local pom_file="$PROJECT_ROOT/$module/pom.xml"
    local should_not_depend=$2
    local reason=$3

    if [ ! -f "$pom_file" ]; then
        return
    fi

    if grep -q "<artifactId>$should_not_depend</artifactId>" "$pom_file"; then
        echo -e "${RED}❌ $module 模块依赖了 $should_not_depend${NC}"
        echo "   原因: $reason"
        ERRORS=$((ERRORS+1))
    fi
}

# 规则1: api-module 不能依赖 dao-module
check_pom_dependency "api-module" "dao-module" "Controller层必须通过Service层访问数据，不能直接依赖DAO"

# 规则2: service-module 不能依赖 api-module
check_pom_dependency "service-module" "api-module" "Service层不能依赖Controller层"

# 规则3: dao-module 不能依赖 service-module
check_pom_dependency "dao-module" "service-module" "DAO层不能依赖Service层"

# 规则4: data-module 不能依赖任何内部模块
check_internal_deps() {
    local pom_file="$PROJECT_ROOT/data-module/pom.xml"
    if [ -f "$pom_file" ]; then
        if grep -q "<artifactId>.*-module</artifactId>" "$pom_file" | grep -v "data-module"; then
            echo -e "${RED}❌ data-module 不能依赖任何其他内部模块${NC}"
            echo "   data-module 应该保持纯净，只依赖 JDK 和第三方库"
            ERRORS=$((ERRORS+1))
        fi
    fi
}
check_internal_deps

# 规则5: 检查Java代码中的非法import
check_java_import() {
    local module=$1
    local src_dir="$PROJECT_ROOT/$module/src/main/java"
    local forbidden_pattern=$2
    local reason=$3

    if [ ! -d "$src_dir" ]; then
        return
    fi

    find "$src_dir" -name "*.java" | while read file; do
        if grep -q "$forbidden_pattern" "$file"; then
            echo -e "${RED}❌ $file${NC}"
            echo "   发现非法import: $forbidden_pattern"
            echo "   原因: $reason"
            ERRORS=$((ERRORS+1))
        fi
    done
}

# api-module 不能直接 import dao-module 的类
check_java_import "api-module" "import com.example.dao" "Controller层不能直接使用DAO类"

# service-module 不能直接 import dao-module 的实现类（只能导入接口）
check_java_import "service-module" "import com.example.dao.impl" "Service层只能依赖DAO接口，不能依赖实现类"

if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✓ 模块依赖检查通过${NC}"
    exit 0
else
    echo -e "${RED}发现 $ERRORS 个依赖错误${NC}"
    exit 1
fi