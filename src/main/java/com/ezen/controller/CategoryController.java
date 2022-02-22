package com.ezen.controller;

import com.ezen.service.BoardService;
import com.ezen.service.CategoryService;
import com.ezen.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MemberService memberService;

    // [카테고리 생성 메소드]
    @GetMapping("/newCategoryController")
    @ResponseBody
    public String newTabController(@RequestParam("category") String category) {
        // 1. 인수로 이름, 카테고리를 전달받는다.
        // 2. service 로 이를 넘겨준다.
        boolean result = categoryService.createNewCategory(category);
        if (result) {
            return "1";
        } else {
            return "2";
        }
    }

}
