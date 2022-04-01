package ncu.cc.activedirectory.services;

import ncu.cc.activedirectory.models.ApiResult;
import ncu.cc.activedirectory.models.ChangePasswordModel;

public interface ActiveDirectoryService {
    String retrieve();

    ApiResult getAllPersonNames();

    ApiResult findById(String acct);

    ApiResult changePassword(ChangePasswordModel model);

    ApiResult suspendUser(String account);

    ApiResult resumeUser(String account);
}
