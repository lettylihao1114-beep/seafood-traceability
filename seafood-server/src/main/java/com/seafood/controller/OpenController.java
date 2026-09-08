package com.seafood.controller;

import com.seafood.common.Result;
import com.seafood.dto.TraceNodeVO;
import com.seafood.service.TraceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/open")
public class OpenController {

    private final TraceService traceService;

    public OpenController(TraceService traceService) {
        this.traceService = traceService;
    }

    @GetMapping("/trace")
    public Result<List<TraceNodeVO>> trace(@RequestParam String traceCode) {
        return Result.ok(traceService.trace(traceCode));
    }
}
