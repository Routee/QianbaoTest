package com.routee.qianbaotest.model.bean

/**
 * Created by hdb on 2017/8/7.
 * description: ${TODO}
 */

class UserInfo private constructor() {
    var userName: String? = null

    companion object {
        private var sUserInfo: UserInfo? = null

        val instance: UserInfo
            get() {
                synchronized(UserInfo::class.java) {
                    if (sUserInfo == null) {
                        sUserInfo = UserInfo()
                    }
                }
                return this!!.sUserInfo!!
            }
    }
}
