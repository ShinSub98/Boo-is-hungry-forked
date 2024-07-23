package com.chaeshin.boo.controller;

import com.chaeshin.boo.service.member.MemberService;
import com.chaeshin.boo.service.member.auth.MemberAuthService;
import com.chaeshin.boo.service.member.auth.Redirection;
import com.chaeshin.boo.service.member.auth.dto.LoginDto;
import com.chaeshin.boo.utils.ResponseDto;
import com.chaeshin.boo.utils.jwt.JwtProvider;
import com.chaeshin.boo.utils.jwt.TokenReissueDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class MemberController {

    private final MemberAuthService memberAuthService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    @PostMapping("/nickname/")
    public ResponseEntity<ResponseDto> updateNickname(
            HttpServletRequest request, @RequestBody Map<String, String> body) {
        jwtProvider.validateToken(request);

        Long memberId = jwtProvider.getMemberId(request);
        return new ResponseEntity<>(
                memberService.updateNickname(memberId, body.get("nickname")),
                HttpStatus.OK
        );
    }


    @GetMapping("/info/{memberId}/")
    public ResponseEntity<ResponseDto> getInfo(HttpServletRequest request, @PathVariable Long memberId) {
        jwtProvider.validateToken(request);

        return new ResponseEntity<>(
                memberService.getMemberInfo(memberId),
                HttpStatus.OK
        );
    }


    @GetMapping("/google/")
    public ResponseEntity<?> redirectBackDev() {
        HttpHeaders headers = new HttpHeaders();
        String redirectUri = memberAuthService.getRedirectUri(Redirection.BACK);
        headers.setLocation(URI.create(redirectUri));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }


    @GetMapping("/login/")
    public ResponseEntity<ResponseDto<LoginDto>> loginBackDev(@RequestParam String code) {
        ResponseDto<LoginDto> result = memberAuthService.login(code, Redirection.BACK);
        if (!result.getData().isExistUser()) {
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }


    @GetMapping("/server/google/")
    public ResponseEntity<?> redirectServer() {
        HttpHeaders headers = new HttpHeaders();
        String redirectUri = memberAuthService.getRedirectUri(Redirection.DOMAIN);
        headers.setLocation(URI.create(redirectUri));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }


    @GetMapping("/server/login/")
    public ResponseEntity<ResponseDto> loginServer(@RequestParam String code) {
        return new ResponseEntity(
            memberAuthService.login(code, Redirection.DOMAIN),
            HttpStatus.OK);
    }


    @GetMapping("/signin/")
    public ResponseEntity<RedirectUriDto> redirectFrontDev() {
        String redirectUri = memberAuthService.getRedirectUri(Redirection.FRONT);
        return new ResponseEntity<>(
                new RedirectUriDto(redirectUri),
                HttpStatus.OK
        );
    }


    @GetMapping("/userinfo/")
    public ResponseEntity<ResponseDto> loginFrontDev(@RequestBody Map<String, String> body) {
        return new ResponseEntity(
                memberAuthService.login(body.get("address"),
                        Redirection.FRONT),
                HttpStatus.OK);
    }


    @PostMapping("/token/refresh/")
    public ResponseEntity<TokenReissueDto> refreshToken(@RequestBody Map<String, String> body) {
        return new ResponseEntity<>(
                memberAuthService.reIssueAccessToken(body.get("refresh")),
                HttpStatus.OK
        );
    }


    @Data
    @AllArgsConstructor
    static class RedirectUriDto {
        private String address;
    }
}
