package me.sharuru.jchv.frontend.web;

import me.sharuru.jchv.frontend.model.SearchResponse;
import me.sharuru.jchv.frontend.service.MetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    MetaDataService service;

    @RequestMapping("/")
    public String index(){
        return "page";
    }

    @RequestMapping("/search")
    @ResponseBody
    public SearchResponse search(@RequestParam(required = false) String path){
        return service.search(path);
    }
}
