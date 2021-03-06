package io.swagger.controller;

import io.swagger.pojo.dao.Status;
import io.swagger.pojo.dao.Tag;
import io.swagger.pojo.dto.BasicResponse;
import io.swagger.service.WebTagService;
import io.swagger.service.WebTagServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RequestMapping("/api/tag")
@RestController
public class WebTagController extends WebBasicController{

    @Autowired
    private WebTagService tagService;

    /**
     * 判断标签是否已被使用
     * @param id
     * @return
     */
    @GetMapping("/detectTagIfUsed")
    public BasicResponse detectTagIfUsed(@RequestParam Long id){
        BasicResponse basicResponse = new BasicResponse();
        tagService.findIfUsed(id);
        if(tagService.findIfUsed(id)==1){
            basicResponse.setData("标签已被使用");
        }
        else{
            basicResponse.setData("标签未被使用");
        }
        return basicResponse;
    }

    /**
     * 增加标签
     */
    @PostMapping("/add")
    public BasicResponse add(@RequestBody Tag tag) {
        BasicResponse basicResponse = new BasicResponse();

        Long createBy = super.getUserId();
        try {
            tagService.add(tag, createBy);
            basicResponse.setData("标签添加成功");

        } catch (Exception e) {
            basicResponse.setCode(BasicResponse.ERRORCODE);
            basicResponse.setData("标签添加失败: " + e.getMessage());
        }

        return basicResponse;
    }

    /**
     * 删除标签
     */
    @PostMapping("/delete")
    public BasicResponse deleteAll(@RequestBody List<Long> idList) {
        BasicResponse basicResponse = new BasicResponse();

        try {
            tagService.deleteAll(idList);
            basicResponse.setData("标签删除成功");
        } catch (Exception e) {
            basicResponse.setData("标签删除失败");
            basicResponse.setCode(BasicResponse.ERRORCODE);
        }

        return basicResponse;
    }

    /**
     * 查找所有标签
     */
    @GetMapping("/list")
    public BasicResponse list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        BasicResponse basicResponse = new BasicResponse();

        pageNumber = (pageNumber < 0 ? 0 : pageNumber);
        pageSize = (pageSize < 1 || pageSize > 100 ? 100 : pageSize);

        try {
            Map<String, Object> resultMap = tagService.list(pageNumber, pageSize);
            basicResponse.setData(resultMap);
        } catch (Exception e) {
            basicResponse.setCode(BasicResponse.ERRORCODE);
            basicResponse.setData("query error : " + e.getMessage());
        }

        return basicResponse;
    }

    /**
     * 更改标签值
     */
    @PutMapping("/update")
    public BasicResponse update(@RequestBody Tag tag) {
        BasicResponse basicResponse = new BasicResponse();

        try {
            Long updateBy = super.getUserId();
            tagService.update(tag, updateBy);
            basicResponse.setData("更改标签成功");
        } catch (Exception e) {
            basicResponse.setCode(BasicResponse.ERRORCODE);
            basicResponse.setData("更改标签失败: " + e.getMessage());
        }

        return basicResponse;
    }
}
