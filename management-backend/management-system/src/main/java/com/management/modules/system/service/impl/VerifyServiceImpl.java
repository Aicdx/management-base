/*
 *  Copyright 2019-2025 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.management.modules.system.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.management.domain.dto.EmailDto;
import com.management.exception.BadRequestException;
import com.management.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import com.management.modules.system.service.VerifyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;

/**
 * @author Zheng Jie
 * @date 2018-12-26
 */
@Service
@RequiredArgsConstructor
public class VerifyServiceImpl implements VerifyService {

    @Value("${code.expiration}")
    private Long expiration;
    private final RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmailDto sendEmail(String email, String key) {
        EmailDto emailDto;
        String content;
        String redisKey = key + email;
        // 如果不存在有效的验证码，就创建一个新的
        TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));
        Template template = engine.getTemplate("email.ftl");
        String oldCode =  redisUtils.get(redisKey, String.class);
        if(oldCode == null){
            String code = RandomUtil.randomNumbers (6);
            // 存入缓存
            if(!redisUtils.set(redisKey, code, expiration)){
                throw new BadRequestException("服务异常，请联系网站负责人");
            }
            content = template.render(Dict.create().set("code",code));
            // 存在就再次发送原来的验证码
        } else {
            content = template.render(Dict.create().set("code",oldCode));
        }
        emailDto = new EmailDto(Collections.singletonList(email),"ELADMIN后台管理系统",content);
        return emailDto;
    }

    @Override
    public void validated(String key, String code) {
        String value = redisUtils.get(key, String.class);
        if(value == null || !value.equals(code)){
            throw new BadRequestException("无效验证码");
        } else {
            redisUtils.del(key);
        }
    }
}
