package com.makeawishbatchserver.job;

import com.makeawishbatchserver.domain.TmpBirthday;
import com.makeawishbatchserver.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
    private final String DAY_BEFORE_STEP_NAME = "BirthDayBeforeRemindStep";
    private final int CHUNK_SIZE = 100;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMdd");

    @Bean
    public Job birthdayRemindJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(birthdayRemindStep())
                .build();
    }

    @Bean
    public Step birthdayRemindStep() {
        return stepBuilderFactory.get(STEP_NAME)
                .<TmpBirthday, TmpBirthday>chunk(CHUNK_SIZE)
                .reader(selectBirthdayNextWeek(0))
                .writer(sendBirthdayAlarmTalk(null))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<TmpBirthday> selectBirthdayNextWeek(@Value("#{jobParameters['dateDiff']}") int dateDiff) {
        LocalDate date = LocalDate.now().plusDays(dateDiff);
        final String baseDate = date.format(DATE_FORMATTER);
        JpaPagingItemReader<TmpBirthday> itemReader = new JpaPagingItemReader<>();
        itemReader.setEntityManagerFactory(entityManagerFactory);
        itemReader.setQueryString("select b FROM TmpBirthday b where b.birthDate= :baseDate order by b.id");
        itemReader.setPageSize(CHUNK_SIZE);
        itemReader.setParameterValues(Map.of("baseDate", baseDate));
        return itemReader;
    }

    @Bean
    @StepScope
    public ItemWriter<TmpBirthday> sendBirthdayAlarmTalk(@Value("#{jobParameters['templateName']}") String templateName) {
        return list -> {
            for (TmpBirthday info : list) {
                messageService.sendATS(templateName, info.getPhoneNumber());
            }
        };
    }
}
