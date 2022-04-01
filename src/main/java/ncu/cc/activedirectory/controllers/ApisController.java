package ncu.cc.activedirectory.controllers;

import ncu.cc.activedirectory.models.ApiResult;
import ncu.cc.activedirectory.models.ChangePasswordModel;
import ncu.cc.activedirectory.services.ActiveDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static ncu.cc.activedirectory.constants.Constants.API_PATH;

@RestController
@RequestMapping(API_PATH + "/admin")
public class ApisController {
    @Autowired
    private ActiveDirectoryService activeDirectoryService;

    @GetMapping("/home")
    public String index() {
        return activeDirectoryService.retrieve();
    }

    @GetMapping("/all")
    public ApiResult all() {
        return activeDirectoryService.getAllPersonNames();
    }

    @GetMapping("/user/{acct}")
    public ApiResult findUser(@PathVariable("acct") String acct) {
        return activeDirectoryService.findById(acct);
    }

    @PostMapping("/password")
    public ApiResult changePassword(@RequestBody ChangePasswordModel model) {
        return activeDirectoryService.changePassword(model);
    }

    @PutMapping("/suspend/{acct}")
    public ApiResult suspendUser(@PathVariable("acct") String acct) {
        return activeDirectoryService.suspendUser(acct);
    }

    @PutMapping("/resume/{acct}")
    public ApiResult resumeUser(@PathVariable("acct") String acct) {
        return activeDirectoryService.resumeUser(acct);
    }
}
