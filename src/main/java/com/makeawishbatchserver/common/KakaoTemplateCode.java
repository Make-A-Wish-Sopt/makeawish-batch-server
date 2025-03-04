package com.makeawishbatchserver.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum KakaoTemplateCode {
    WISH_REMIND_WEEK_BEFORE("D-7 소원 알림톡 발송");

    private String code;
}
