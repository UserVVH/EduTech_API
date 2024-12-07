package com.edutechit.edutechit_api.controller;

import com.edutechit.edutechit_api.dto.SearchResponseDto;
import com.edutechit.edutechit_api.service.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/search")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping()
    public ResponseEntity<SearchResponseDto> search(@RequestParam String searchText) {
        SearchResponseDto response = searchService.search(searchText);
        return ResponseEntity.ok(response);
    }
}