package com.allergenie.server.controller;

import com.allergenie.server.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/data")
@RequiredArgsConstructor
public class DataController {
    private final DataService dataService;

    @GetMapping("/list")
    public String getDataList(@RequestParam(name = "pageNo") String pageNo) throws IOException {
        return dataService.getDataList(pageNo);
    }
}
