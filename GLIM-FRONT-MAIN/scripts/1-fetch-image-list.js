/**
 * scripts/1-fetch-image-list.js
 *
 * 이 스크립트는 외부 API로부터 이미지 URL 목록을 가져와 `imageList.json` 파일로 저장합니다.
 * 이 목록은 이후 `2-process-images.js` 스크립트에서 이미지를 다운로드하고 처리하는 데 사용됩니다.
 *
 * 주요 기능:
 * 1. API 호출:
 *    - `axios`를 사용하여 지정된 API 엔드포인트(예: `https://glim-main.netlify.app/api/v1/admin/search-keywords/quotes`)에
 *      GET 요청을 보냅니다.
 *    - 페이지네이션을 지원하여 모든 이미지 데이터를 가져올 때까지 반복적으로 호출합니다.
 *
 * 2. 이미지 URL 추출 및 가공:
 *    - API 응답에서 `quoteImage` 필드를 찾아 이미지 파일 이름을 추출합니다.
 *    - 추출된 파일 이름에 `IMAGE_SERVER_BASE_URL`을 결합하여 완전한 이미지 URL을 생성합니다.
 *    - 중복된 URL을 제거하여 고유한 이미지 URL 목록을 만듭니다.
 *
 * 3. JSON 파일 저장:
 *    - 수집된 고유 이미지 URL 목록을 `imageList.json` 파일에 JSON 형식으로 저장합니다.
 *    - 이 파일은 `public/imageList.json`과는 별개로, 이미지 처리 스크립트의 입력으로 사용됩니다.
 *
 * 실행 방법:
 * `node scripts/1-fetch-image-list.js`
 */
// scripts/1-fetch-image-list.js
const fs = require('fs');
const path = require('path');
const axios = require('axios');

// 1. 실제 운영중인 Netlify 주소로 변경합니다. (https 프로토콜 포함)
const API_BASE_URL = 'https://glim-main.netlify.app'; 
const API_PATH = '/api/v1/admin/search-keywords/quotes';

// 🚨 이미지는 원본 서버에서 가져와야 할 수 있으므로, 이미지 서버의 기본 주소를 별도로 정의합니다.
const IMAGE_SERVER_BASE_URL = 'http://i13d202.p.ssafy.io:8080';

const API_ENDPOINT = `${API_BASE_URL}${API_PATH}`;
const OUTPUT_PATH = path.join(__dirname, '..', 'imageList.json');

async function fetchImageList() {
    try {
        console.log(`API에서 이미지 목록을 가져오는 중... (${API_ENDPOINT})`);
        
        let allImageUrls = [];
        let page = 0;
        let hasMore = true;

        while(hasMore) {
            console.log(`페이지 ${page} 데이터 요청 중...`);
            
            const response = await axios.get(API_ENDPOINT, {
                params: {
                    // 2. keyword: '' (빈 문자열)을 명시적으로 추가하여 '전체 검색'을 트리거합니다.
                    keyword: '',
                    page: page,
                    size: 100, // 한 번에 100개씩 요청
                    sort: 'views,desc'
                }
            });
            
            const data = response.data;

            if (data && Array.isArray(data) && data.length > 0) {
                // 3. API 문서에 따르면 Admin 검색 시 이미지 필드 이름은 'quoteImage' 입니다.
                const imageUrls = data
                    .filter(item => item.quoteImage)
                    .map(item => `${IMAGE_SERVER_BASE_URL}/images/${item.quoteImage}`);
                
                allImageUrls = allImageUrls.concat(imageUrls);
                console.log(`> 페이지 ${page}: ${data.length}개 항목 발견. 총 ${allImageUrls.length}개 URL 수집`);
                page++;

                if (data.length < 100) {
                    hasMore = false;
                }

            } else {
                hasMore = false;
                console.log('더 이상 데이터가 없어 루프를 종료합니다.');
            }
        }

        const uniqueImageUrls = [...new Set(allImageUrls)];

        fs.writeFileSync(OUTPUT_PATH, JSON.stringify(uniqueImageUrls, null, 2));
        console.log(`\n✅ 성공! 총 ${uniqueImageUrls.length}개의 고유 이미지 URL을 imageList.json 파일에 저장했습니다.`);

    } catch (error) {
        console.error('❌ 에러 발생:', error.response ? `${error.response.status} - ${error.response.data}` : error.message);
    }
}

fetchImageList();
