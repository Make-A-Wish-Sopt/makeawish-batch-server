package com.makeawishbatchserver.service;

import com.makeawishbatchserver.util.DateUtil;
import com.makeawishbatchserver.domain.AlarmTemplate;
import com.makeawishbatchserver.repository.AlarmTemplateRepository;
import com.popbill.api.KakaoService;
import com.popbill.api.PopbillException;
import com.popbill.api.kakao.KakaoButton;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {
    private final KakaoService kakaoService;
    private final AlarmTemplateRepository alarmTemplateRepository;
    @Value("${popbill.corpNum}")
    private String corpNum;
    @Value("${popbill.senderNum}")
    private String senderNum;
    @Value("${popbill.linkId}")
    private String userID;

    public void sendATS(String templateName, String phoneNumber) {
        AlarmTemplate template = alarmTemplateRepository.findAlarmTemplateByTemplateName(templateName)
                .orElseThrow(EntityNotFoundException::new);
        try {
            String sendDate = DateUtil.getCurrentDate();
            String[] variables = StringUtils.isEmpty(template.getTemplateVariables()) ? new String[0] :template.getTemplateVariables().split(",");
            String content = getReplacedContent(template.getTemplateContent(), variables);
            String receiptNum = kakaoService.sendATS(corpNum, template.getTemplateCode(), senderNum, content,
                    "", "", phoneNumber, userID, sendDate, userID, sendDate+phoneNumber, getKakaoButtons(template.getTemplateButton(), template.getTemplateButtonUrl()));
        } catch (PopbillException e) {
            log.error("e.message : " + e.getMessage());
        }
    }

    private String getReplacedContent(String content, String[] variables, String ... words) {
        for(int index = 0; index < variables.length; index++) {
            content = content.replace(variables[index], words[index]);
        }
        return content;
    }

    private KakaoButton[] getKakaoButtons(String buttonName, String buttonUrl){
        KakaoButton[] buttons = new KakaoButton[1];
        KakaoButton button = new KakaoButton();
        button.setN(buttonName);
        button.setT("WL");
        button.setU1(buttonUrl);
        button.setU2(buttonUrl);
        buttons[0] = button;
        return buttons;
    }
}

