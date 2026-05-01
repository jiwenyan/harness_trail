#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Agent 开发前的统一验证管道"""

import subprocess
import sys
import argparse
from pathlib import Path
from typing import List, Tuple

class Validator:
    def __init__(self, project_root: Path):
        self.project_root = project_root
        self.errors = 0

    def run_maven_compile(self) -> bool:
        """运行 Maven 编译检查"""
        print("\n[MAVEN] 运行 Maven 编译...")
        import os
        env = os.environ.copy()
        env['MAVEN_OPTS'] = '-Dfile.encoding=UTF-8'
        env['JAVA_TOOL_OPTIONS'] = '-Dfile.encoding=UTF-8'
        result = subprocess.run(
            ["mvn", "clean", "compile", "-DskipTests"],
            cwd=self.project_root,
            capture_output=True,
            text=True,
            shell=True,
            env=env,
            encoding='utf-8',
            errors='replace'
        )

        if result.returncode != 0:
            print("[ERROR] Maven 编译失败")
            print(result.stdout[-500:])  # 只显示最后500行
            print(result.stderr[-500:])
            return False

        print("[OK] Maven 编译成功")
        return True

    def run_script(self, script_name: str) -> bool:
        """运行 lint 脚本"""
        script_path = self.project_root / "scripts" / script_name

        if not script_path.exists():
            print(f"⚠️  {script_name} 不存在，跳过")
            return True

        print(f"\n[SCRIPT] 运行 {script_name}...")
        result = subprocess.run(
            ["bash", str(script_path)],
            cwd=self.project_root,
            capture_output=True,
            text=True,
            encoding='utf-8',
            errors='replace'
        )

        if result.stdout:
            sys.stdout.reconfigure(encoding='utf-8', errors='replace')
            print(result.stdout)

        if result.returncode != 0:
            self.errors += 1
            return False

        return True

    def verify_action(self, action: str, file_path: str) -> bool:
        """验证单个操作是否合法"""
        print(f"\n[VALIDATE] 验证操作: {action} {file_path}")

        # 规则1: 不能在错误的包路径创建文件
        if "dto" in file_path:
            if "request" in file_path and not file_path.endswith("Request.java"):
                print(f"[ERROR] Request DTO 必须以 'Request.java' 结尾")
                return False
            if "response" in file_path and not file_path.endswith("Response.java"):
                print(f"[ERROR] Response DTO 必须以 'Response.java' 结尾")
                return False

        # 规则2: Service 实现必须在 impl 包下
        if "ServiceImpl" in file_path and "impl" not in file_path:
            print(f"[ERROR] Service 实现类必须在 impl 包下")
            return False

        # 规则3: DAO 实现必须在 impl 包下
        if "DAOImpl" in file_path and "impl" not in file_path:
            print(f"[ERROR] DAO 实现类必须在 impl 包下")
            return False

        # 规则4: 不能直接修改规范文件
        protected_files = ["AGENTS.md", "scripts/lint-deps.sh", "scripts/lint-interface.sh"]
        if file_path in protected_files and action != "read":
            print(f"[ERROR] {file_path} 是受保护文件，需人工审核")
            return False

        print(f"[OK] 操作验证通过")
        return True

    def validate_all(self) -> bool:
        """运行所有检查"""
        print("=" * 50)
        print("开始完整验证流程")
        print("=" * 50)

        # 1. Maven 编译
        if not self.run_maven_compile():
            return False

        # 2. Lint 脚本
        scripts = ["lint-deps.sh", "lint-interface.sh", "lint-dto.sh"]
        for script in scripts:
            self.run_script(script)

        if self.errors > 0:
            print(f"\n[ERROR] 发现 {self.errors} 个错误")
            return False

        print("\n[OK] 所有验证通过，可以继续开发")
        return True

def main():
    parser = argparse.ArgumentParser(description="Java项目验证工具")
    parser.add_argument("--action", choices=["create", "modify", "delete", "read"])
    parser.add_argument("--file", help="要操作的文件路径")
    parser.add_argument("--full", action="store_true", help="运行完整验证")

    args = parser.parse_args()

    project_root = Path(__file__).parent.parent
    validator = Validator(project_root)

    if args.full:
        success = validator.validate_all()
        sys.exit(0 if success else 1)

    if args.action and args.file:
        success = validator.verify_action(args.action, args.file)
        sys.exit(0 if success else 1)

    # 默认运行完整验证
    success = validator.validate_all()
    sys.exit(0 if success else 1)

if __name__ == "__main__":
    main()