package com.viettelpost.fms.utm_integration.repository.predicate;

import com.viettelpost.fms.common.predicate.QueryCriteria;
import com.viettelpost.fms.utm_integration.domain.QExample;
import org.springframework.util.StringUtils;

import java.util.List;

public class ExampleQueryCriteria extends QueryCriteria {

    private static final QExample qExample = QExample.example;

    public ExampleQueryCriteria ids(List<String> ids) {
        if (ids != null && !ids.isEmpty()) {
            predicate.and(qExample.uuid.in(ids));
        }

        return this;
    }

    public ExampleQueryCriteria withCode(String code) {
        if (StringUtils.hasText(code)) {
            predicate.and(qExample.code.eq(code));
        }

        return this;
    }

    public ExampleQueryCriteria withName(String name) {
        if (StringUtils.hasText(name)) {
            predicate.and(qExample.name.eq(name));
        }

        return this;
    }

    public ExampleQueryCriteria text(String text) {
        if (StringUtils.hasText(text)) {
            String pattern = "%" + text.trim() + "%";
            predicate.andAnyOf(
                    qExample.name.likeIgnoreCase(pattern),
                    qExample.code.likeIgnoreCase(pattern)
            );
        }

        return this;
    }
}
