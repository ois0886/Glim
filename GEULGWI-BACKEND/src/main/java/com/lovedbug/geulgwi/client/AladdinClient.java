package com.lovedbug.geulgwi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.lovedbug.geulgwi.config.AladdinFeignConfig;
import com.lovedbug.geulgwi.dto.request.AladdinBookSearchConditionDto;
import com.lovedbug.geulgwi.dto.request.AladdinBookListConditionDto;
import com.lovedbug.geulgwi.dto.request.AladdinBookSearchDto;

@FeignClient(
    name = "AladinApiClient",
    url = "http://www.aladin.co.kr",
    configuration = AladdinFeignConfig.class)
public interface AladdinClient {

    @GetMapping("/ttb/api/ItemSearch.aspx")
    AladdinBookSearchDto searchBooksByCondition(@SpringQueryMap AladdinBookSearchConditionDto searchCondition);

    @GetMapping("/ttb/api/ItemList.aspx")
    AladdinBookSearchDto getBooks(@SpringQueryMap AladdinBookListConditionDto condition);
}
