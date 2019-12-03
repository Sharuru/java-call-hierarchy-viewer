package me.sharuru.jchv.frontend.web;

import me.sharuru.jchv.frontend.model.BusinessApiResponse;
import me.sharuru.jchv.frontend.service.MetaContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Web controller
 */
@Controller
@RequestMapping("/")
public class PageController {

    @Autowired
    MetaContentService metaContentService;

    /**
     * Index
     *
     * @return index template
     */
    @RequestMapping("/")
    public String index() {
        return "page";
    }

    /**
     * Endpoint of getting callee information
     *
     * @param methodQualifiedName query keyword
     * @return query result
     */
    @RequestMapping("/getCalleeMethodInfo")
    @ResponseBody
    public BusinessApiResponse getCalleeMethodInfo(@RequestParam String methodQualifiedName) {
        return metaContentService.getCalleeMethodInfo(methodQualifiedName);
    }

    /**
     * Endpoint of getting caller information
     *
     * @param methodQualifiedName query keyword
     * @return query result
     */
    @RequestMapping("/getCallerMethodInfo")
    @ResponseBody
    public BusinessApiResponse getCallerMethodInfo(@RequestParam String methodQualifiedName) {
        return metaContentService.getCallerMethodInfo(methodQualifiedName);
    }
}
