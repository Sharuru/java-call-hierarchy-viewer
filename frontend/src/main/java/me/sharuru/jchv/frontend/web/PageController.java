package me.sharuru.jchv.frontend.web;

import lombok.extern.slf4j.Slf4j;
import me.sharuru.jchv.frontend.config.TableNameStrategy;
import me.sharuru.jchv.frontend.model.BusinessApiResponse;
import me.sharuru.jchv.frontend.service.MetaContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Web controller
 */
@Controller
@RequestMapping("/")
@Slf4j
public class PageController {

    @Autowired
    MetaContentService metaContentService;

    @Autowired
    BuildProperties buildProperties;

    @Autowired
    TableNameStrategy tableNameStrategy;

    /**
     * Index
     *
     * @return index template
     */
    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("appVersion", buildProperties.getVersion());
        model.addAttribute("appBuildTime", buildProperties.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        model.addAttribute("appTableName", tableNameStrategy.getTableName());
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

    /**
     * Endpoint of name helper
     *
     * @param methodSimpleClass  query keyword
     * @param methodSimpleMethod query keyword
     * @return query result
     */
    @RequestMapping("/getQualifiedNames")
    @ResponseBody
    public BusinessApiResponse getQualifiedNames(@RequestParam(required = false) String methodSimpleClass, @RequestParam(required = false) String methodSimpleMethod) {
        return metaContentService.getQualifiedNames(methodSimpleClass, methodSimpleMethod);
    }
}
