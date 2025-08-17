/**
 * eslint.config.mjs
 *
 * 이 파일은 ESLint의 새로운 Flat Config 형식으로 프로젝트의 코드 스타일 및 품질 규칙을 정의합니다.
 * ESLint는 코드에서 잠재적인 오류, 버그, 스타일 문제를 찾아내어 개발자가 일관된 코드를 작성하도록 돕는 도구입니다.
 *
 * 주요 설정:
 * 1. `FlatCompat`:
 *    - 기존의 `.eslintrc` 형식의 설정을 새로운 Flat Config 형식으로 변환하여 호환성을 제공합니다.
 *    - 이를 통해 기존에 잘 정의된 ESLint 플러그인 및 설정(`next/core-web-vitals`, `next/typescript`)을
 *      새로운 설정 시스템에서 사용할 수 있습니다.
 *
 * 2. `extends`:
 *    - `next/core-web-vitals`: Next.js 프로젝트의 웹 바이탈(Web Vitals) 최적화와 관련된 규칙을 적용합니다.
 *      성능, 접근성 등 사용자 경험에 중요한 요소들을 검사합니다.
 *    - `next/typescript`: TypeScript 코드에 특화된 ESLint 규칙을 적용합니다.
 *      TypeScript 문법 오류 및 타입 관련 문제를 검사합니다.
 *
 * 이 설정은 프로젝트의 코드 베이스가 일관된 품질과 스타일을 유지하도록 강제하며,
 * 잠재적인 문제를 개발 초기에 발견하여 수정 비용을 줄이는 데 기여합니다.
 */
import { dirname } from "path";
import { fileURLToPath } from "url";
import { FlatCompat } from "@eslint/eslintrc";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const compat = new FlatCompat({
  baseDirectory: __dirname,
});

const eslintConfig = [
  ...compat.extends("next/core-web-vitals", "next/typescript"),
];

export default eslintConfig;