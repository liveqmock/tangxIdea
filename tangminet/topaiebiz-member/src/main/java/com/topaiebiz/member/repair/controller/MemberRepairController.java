package com.topaiebiz.member.repair.controller;

import com.nebulapaas.web.response.ResponseInfo;
import com.topaiebiz.member.repair.MemberBindRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hecaifeng on 2018/6/20.
 */
@RestController
@RequestMapping(value = "/member/repair/", method = RequestMethod.POST)
public class MemberRepairController {

    @Autowired
    private MemberBindRepairService memberBindRepairService;

    @RequestMapping(path = "/updateJson")
    public ResponseInfo fixData() {
        memberBindRepairService.start();
        return new ResponseInfo();
    }
}
