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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * WIP
     *
     * @return
     */
    @RequestMapping("/batch")
    @ResponseBody
    public String exportMethodData() {
        try {
            Files.readAllLines(new File("D:\\import.txt").toPath()).stream().distinct().forEachOrdered(line -> {
                BusinessApiResponse response = metaContentService.getCallerMethodInfo(line);
                response.getEdgeNodeQualifiedName().stream().distinct().forEachOrdered(l -> {
                    String nameForMatch = l.toLowerCase();
                    String subSysCdRegexp = "\\.(XX\\d\\d)\\.";
                    Pattern r = Pattern.compile(subSysCdRegexp);
                    Matcher m = r.matcher(nameForMatch);
                    String subSysCd = "NOT_FOUND";
                    String manageUnit = "NOT_FOUND";
                    String systemId = "NOT_FOUND";
                    String methodClass = "NOT_FOUND";
                    String methodName = "NOT_FOUND";
                    if(m.find()){
                        manageUnit = m.group(1).toUpperCase();
                        subSysCd = manageUnit.substring(0, 2);
                        String systemIdRegexp = "\\." + manageUnit.toLowerCase() + "(s.*)\\.";
                        Pattern r2 = Pattern.compile(systemIdRegexp);
                        Matcher m2 = r2.matcher(nameForMatch);
                        if(m2.find()){
                            systemId = m2.group(1).toUpperCase();
                            int idx =  systemId.indexOf('.');
                            systemId = systemId.substring(0, idx == -1 ? systemId.length() : idx);
                        }

                        String[] methodArr = l.split("\\.");
                        methodClass = methodArr[methodArr.length - 2].concat(".java");
                        methodName = methodArr[methodArr.length - 1];

                    }
                    String writeLine = subSysCd + "\t" + manageUnit  + "\t" + systemId + "\t" +  methodClass + "\t" +  methodName + "\t" + l + "\t" + line +  "\t" + response.getFuzzyQualifiedName()  + "\n";
                    try {
                        Files.write(new File("D:\\export.txt").toPath(), writeLine.getBytes(), StandardOpenOption.APPEND);
                        log.info("Write: {}, {}, {}, {}, {}, {}", subSysCd, manageUnit, systemId, methodClass, methodName, l);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                response.getNotFoundCallerQualifiedName().stream().distinct().forEachOrdered(l -> {
                    String writeLine = "NOT_FOUND" + "\t" + "NOT_FOUND"  + "\t" + "NOT_FOUND" + "\t" +  "NOT_FOUND" + "\t" +  "NOT_FOUND" + "\t" + l +  "\t" + line +  "\t" + response.getFuzzyQualifiedName()  + "\n";
                    try {
                        Files.write(new File("D:\\export.txt").toPath(), writeLine.getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    log.info("Not found write: {}" , l);
                });
            });
            return "OK";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "N/A";
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
