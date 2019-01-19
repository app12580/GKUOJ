package com.oj.gkuoj.rest.portal;

import com.github.pagehelper.PageInfo;
import com.oj.gkuoj.entity.Problem;
import com.oj.gkuoj.entity.Tag;
import com.oj.gkuoj.response.RestResponseVO;
import com.oj.gkuoj.service.TagService;
import com.oj.gkuoj.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author m969130721@163.com
 * @date 18-12-17 上午10:37
 */
@Controller
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private TagService tagService;

    private final Integer SUGGEST_PROBLEM_ROW = 5;
    /**
     * 问题列表
     * @param request
     * @param pageNum
     * @param pageSize
     * @param keyword 标题、标签、分类或题目编号
     * @param level
     * @param tagName
     * @return
     */
    @RequestMapping("/problemListPage")
    public String problemListPage(HttpServletRequest request, @RequestParam(defaultValue = "1") Integer pageNum,@RequestParam(defaultValue = "40") Integer pageSize,
                                  @RequestParam(defaultValue = "",required = false) String keyword, @RequestParam(defaultValue = "-1",required = false)Integer level,
                                  @RequestParam(defaultValue = "不限",required = false) String tagName) {
        //题目
        RestResponseVO<PageInfo> result = problemService.listProblemToPage(keyword, level, tagName, pageNum, pageSize);
        PageInfo pageInfo = result.getData();

        //题目标签
        List<Tag> tagList = tagService.listAll().getData();
        Tag t = new Tag();
        t.setName("不限");
        t.setId(-1);
        tagList.add(0, t);

        //set data

        request.setAttribute("total",pageInfo.getTotal());
        request.setAttribute("problemList",pageInfo.getList());
        request.setAttribute("pageNum",pageNum);
        request.setAttribute("keyword",keyword);
        request.setAttribute("level",level);
        request.setAttribute("tagName",tagName);
        request.setAttribute("tagList", tagList);
        request.setAttribute("active2", true);
        return "portal/problem/problem-list";
    }


    /**
     * 题目详情页面
     * @param request
     * @param problemId
     * @return
     */
    @RequestMapping("/problemDetailPage")
    public String problemDetailPage(HttpServletRequest request,Integer problemId){
        Problem problem = problemService.getById(problemId).getData();
        //set data
        request.setAttribute("problem",problem);
        request.setAttribute("active2", true);
        return "portal/problem/problem-detail";
    }

    /**
     * 随机返回5道推荐题目
     * @param problemId
     * @return
     */
    @RequestMapping("/suggestProblemList")
    @ResponseBody
    public RestResponseVO<List<Problem>> suggestProblemList(Integer problemId){
        return problemService.listSuggestProblem(problemId,SUGGEST_PROBLEM_ROW);
    }

    /**
     * 随机选择一道题目
     * @return
     */
    @RequestMapping("/randomProblem")
    public String randomProblem(HttpServletRequest request){

        RestResponseVO<Integer> serverResponse = problemService.randomProblemId();
        if(serverResponse.isSuccess()){
            return "redirect:/problem/problemDetailPage?problemId="+serverResponse.getData();
        }else {
            //fixme
            return "500";
        }
    }



}
