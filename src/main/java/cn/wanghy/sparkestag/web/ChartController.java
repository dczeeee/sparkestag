package cn.wanghy.sparkestag.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class ChartController {

    @RequestMapping("/tags")
    public String tags() {
        return "tags";
    }

    @RequestMapping("/")
    public String index() {
        return "index";
    }

}