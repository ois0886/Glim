/**
 * scripts/2-process-images.js
 *
 * 이 스크립트는 `imageList.json` 파일에 나열된 이미지 URL들을 기반으로
 * 원본 이미지를 다운로드하고, 웹 애플리케이션에서 사용할 플레이스홀더 이미지를 생성합니다.
 *
 * 주요 기능:
 * 1. 이미지 목록 로드:
 *    - `imageList.json` 파일에서 이미지 URL 목록을 읽어옵니다.
 *
 * 2. 이미지 다운로드:
 *    - 각 이미지 URL에 대해 `axios`를 사용하여 원본 이미지를 다운로드합니다.
 *    - 다운로드된 이미지는 `public/images/books` 디렉토리에 저장됩니다.
 *    - 이미 이미지가 존재하면 다시 다운로드하지 않습니다.
 *
 * 3. 플레이스홀더 이미지 생성:
 *    - `sharp` 라이브러리를 사용하여 다운로드된 원본 이미지로부터 저해상도(40px 너비)의
 *      플레이스홀더 이미지를 생성합니다.
 *    - 생성된 플레이스홀더 이미지는 `public/images/placeholders` 디렉토리에 저장됩니다.
 *    - 이 플레이스홀더 이미지는 웹에서 이미지가 로딩되기 전에 사용자에게 보여져
 *      UX를 개선하는 데 사용됩니다.
 *
 * 실행 방법:
 * `node scripts/2-process-images.js`
 */
// scripts/2-process-images.js
const fs = require('fs');
const path = require('path');
const axios = require('axios');
const sharp = require('sharp');

const IMAGE_LIST_PATH = path.join(__dirname, '..', 'imageList.json');
// 이미지를 저장할 public 폴더 내 경로
const ORIGINAL_DIR = path.join(__dirname, '..', 'public', 'images', 'books');
const PLACEHOLDER_DIR = path.join(__dirname, '..', 'public', 'images', 'placeholders');

// 폴더가 없으면 생성
if (!fs.existsSync(ORIGINAL_DIR)) fs.mkdirSync(ORIGINAL_DIR, { recursive: true });
if (!fs.existsSync(PLACEHOLDER_DIR)) fs.mkdirSync(PLACEHOLDER_DIR, { recursive: true });

async function processImages() {
    console.log('이미지 처리 시작...');
    const imageUrls = JSON.parse(fs.readFileSync(IMAGE_LIST_PATH, 'utf-8'));

    for (const url of imageUrls) {
        try {
            // URL에서 파일 이름 추출 (예: 'the-little-prince.jpg')
            const filename = new URL(url).pathname.split('/').pop();
            const originalPath = path.join(ORIGINAL_DIR, filename);
            const placeholderPath = path.join(PLACEHOLDER_DIR, filename);

            // 1. 원본 이미지가 없으면 다운로드
            if (!fs.existsSync(originalPath)) {
                const response = await axios({ url, responseType: 'arraybuffer' });
                const buffer = Buffer.from(response.data, 'binary');
                fs.writeFileSync(originalPath, buffer);
                console.log(`다운로드 완료: ${filename}`);
            } else {
                console.log(`원본 이미 존재함: ${filename}`);
            }

            // 2. Sharp로 항상 플레이스홀더 이미지 생성 및 저장
            await sharp(originalPath) // 원본 파일 경로를 사용
                .resize(40) // 가로 40px로 리사이즈 (화질 개선)
                .toFile(placeholderPath);
            
            console.log(`플레이스홀더 생성 완료: ${filename}`);

        } catch (error) {
            console.error(`처리 실패 ${url}:`, error.message);
        }
    }
    console.log('✅ 모든 이미지 처리가 완료되었습니다!');
}

processImages();
