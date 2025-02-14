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
package com.management.modules.security.rest;

import cn.hutool.core.util.IdUtil;
import com.management.annotation.rest.AnonymousGetMapping;
import com.management.annotation.rest.AnonymousPostMapping;
import com.management.base.BaseResult;
import com.management.config.properties.RsaProperties;
import com.management.exception.BadRequestException;
import com.management.modules.security.config.CaptchaConfig;
import com.management.modules.security.config.LoginProperties;
import com.management.modules.security.config.SecurityProperties;
import com.management.modules.security.config.enums.LoginCodeEnum;
import com.management.modules.security.security.TokenProvider;
import com.management.modules.security.service.OnlineUserService;
import com.management.modules.security.service.UserDetailsServiceImpl;
import com.management.modules.security.service.dto.AuthUserDto;
import com.management.modules.security.service.dto.JwtUserDto;
import com.management.utils.RedisUtils;
import com.management.utils.RsaUtils;
import com.management.utils.SecurityUtils;
import com.management.utils.StringUtils;
import com.wf.captcha.base.Captcha;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.management.annotation.Log;
import com.management.annotation.rest.AnonymousDeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(tags = "系统：系统授权接口")
public class AuthController {

    private final SecurityProperties properties;
    private final RedisUtils redisUtils;
    private final OnlineUserService onlineUserService;
    private final TokenProvider tokenProvider;
    private final CaptchaConfig captchaConfig;
    private final LoginProperties loginProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImpl userDetailsService;

    @Log("用户登录")
    @ApiOperation("登录授权")
    @AnonymousPostMapping(value = "/login")
    public ResponseEntity<Object> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        // 密码解密
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
        // // 查询验证码
        // String code = redisUtils.get(authUser.getUuid(), String.class);
        // // 清除验证码
        // redisUtils.del(authUser.getUuid());
        // if (StringUtils.isBlank(code)) {
        //     throw new BadRequestException("验证码不存在或已过期");
        // }
        // if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
        //     throw new BadRequestException("验证码错误");
        // }
        // 获取用户信息
        JwtUserDto jwtUser = userDetailsService.loadUserByUsername(authUser.getUsername());
        // 验证用户密码
        if (!passwordEncoder.matches(password, jwtUser.getPassword())) {
            throw new BadRequestException("登录密码错误");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(jwtUser, null, jwtUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌
        String token = tokenProvider.createToken(jwtUser);
        // 将密码设置为空
        jwtUser.setPassword(null);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<String, Object>(2) {{
            put("accessToken", properties.getTokenStartWith() + token);
            put("user", jwtUser);
        }};
        if (loginProperties.isSingleLogin()) {
            // 踢掉之前已经登录的token
            onlineUserService.kickOutForUsername(authUser.getUsername());
        }
        // 保存在线信息
        onlineUserService.save(jwtUser, token, request);
        // 返回登录信息
        return ResponseEntity.ok(BaseResult.success(authInfo));
    }

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/user/info")
    public ResponseEntity<Object> getUserInfo() {
        JwtUserDto jwtUser = (JwtUserDto) SecurityUtils.getCurrentUser();
        // 将密码设置为空
        jwtUser.setPassword(null);
        return ResponseEntity.ok(BaseResult.success(jwtUser));
    }

    @ApiOperation("获取用户权限码")
    @GetMapping(value = "/user/permissionsCode")
    public ResponseEntity<Object> getPermissionsCode() {
        //TODO 先返回空数组，后续再修改
        return ResponseEntity.ok(BaseResult.success(new String[]{}));
    }


    @ApiOperation("获取验证码")
    @AnonymousGetMapping(value = "/code")
    public ResponseEntity<Object> getCode() {
        // 获取运算的结果
        Captcha captcha = captchaConfig.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.ARITHMETIC.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisUtils.set(uuid, captchaValue, captchaConfig.getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return ResponseEntity.ok(BaseResult.success(imgResult));
    }

    @ApiOperation("退出登录")
    @AnonymousDeleteMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        String token = tokenProvider.getToken(request);
        onlineUserService.logout(token);
        return ResponseEntity.ok(BaseResult.success(null));
    }
}
