package com.topaiebiz.guider.controller;

import com.topaiebiz.guider.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by admin on 2018/5/30.
 */
@RestController
@RequestMapping("/guider/achievement")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Integer test() {
        return 1;
    }
}
