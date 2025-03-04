package com.makeawishbatchserver.job;

import com.makeawishbatchserver.domain.TmpBirthday;
import com.makeawishbatchserver.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

import static com.makeawishbatchserver.common.KakaoTemplateCode.WISH_REMIND_WEEK_BEFORE;

/**
 * 생일날 알림을 설정한 사람들에게 알림톡을 보낸다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BirthdayRemindJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final MessageService messageService;
    private final String JOB_NAME = "BirthdayRemindJob";
    private final String STEP_NAME = "BirthDayRemindStep";
    private final int CHUNK_SIZE = 100;

    @Bean
    public Job birthdayRemindJob() {
        return jobBuilderFactory.get(JOB_NAME).incrementer(new RunIdIncrementer()).start(birthdayRemindStep()).build();
    }

    @Bean
    public Step birthdayRemindStep() {
        return stepBuilderFactory.get(STEP_NAME).<TmpBirthday, TmpBirthday>chunk(CHUNK_SIZE).reader(selectTodayBirthday()).writer(sendBirthdayAlarmTalk()).build();
    }

    @Bean
    public JpaPagingItemReader<TmpBirthday> selectTodayBirthday() {
        JpaPagingItemReader<TmpBirthday> itemReader = new JpaPagingItemReader<>();
        itemReader.setEntityManagerFactory(entityManagerFactory);
        itemReader.setQueryString("select b FROM TmpBirthday b order by b.id");
        itemReader.setPageSize(CHUNK_SIZE);
        return itemReader;
    }

    @Bean
    public ItemWriter<TmpBirthday> sendBirthdayAlarmTalk() {
        return list -> {
            for (TmpBirthday info : list) {
                messageService.sendATS(WISH_REMIND_WEEK_BEFORE.name(), info.getPhoneNumber());
            }
        };
    }

}
