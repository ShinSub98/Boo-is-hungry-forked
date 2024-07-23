package com.chaeshin.boo.service.member.auth;

import com.chaeshin.boo.domain.LangCode;
import com.chaeshin.boo.domain.Member;
import com.chaeshin.boo.repository.member.MemberRepository;
import com.chaeshin.boo.service.member.auth.dto.GoogleMemberInfoDto;
import com.chaeshin.boo.service.member.auth.dto.LoginDto;
import com.chaeshin.boo.service.member.auth.dto.TokenRequestDto;
import com.chaeshin.boo.utils.RandomStringGenerator;
import com.chaeshin.boo.utils.ResponseDto;
import com.chaeshin.boo.utils.jwt.JwtProvider;
import com.chaeshin.boo.utils.jwt.TokenReissueDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberRepository memberRepository;

    private final JwtProvider jwtProvider;

    @Value("${google.appKey}")
    private String clientId;

    @Value("${google.secretKey}")
    private String clientSecret;

    static private final String scope = URLEncoder.encode("https://www.googleapis.com/auth/userinfo.email" +
            " https://www.googleapis.com/auth/userinfo.profile", StandardCharsets.UTF_8);

    private final String googleAuthUri = "https://accounts.google.com/o/oauth2/v2/auth";

    private final WebClient tokenClient = WebClient.create("https://oauth2.googleapis.com/token");

    private final WebClient memberInfoClient = WebClient.create("https://www.googleapis.com/oauth2/v2/userinfo");

    private final Random random = new Random();


    @Override
    public String getRedirectUri(Redirection redirection) {
        return googleAuthUri + "?client_id=" + clientId +
                "&response_type=code&redirect_uri=" + redirection.getRedirectUri() + "&scope=" + scope;
    }


    @Override
    @Transactional
    public ResponseDto<LoginDto> login(String code, Redirection redirection) {
        String googleToken = requestGoogleToken(code, redirection);
        GoogleMemberInfoDto googleMemberInfoDto = getMemberInfoFromGoogle(googleToken);
        String googleId = googleMemberInfoDto.getId();

        List<Member> searchedResult = memberRepository.findByGoogleId(googleId);

        if (searchedResult.isEmpty()) {
            Member member = createMember(googleMemberInfoDto);
            String accessToken = jwtProvider.createAccessToken(member.getId());
            String refreshToken = jwtProvider.createRefreshToken(member.getId());
            LoginDto loginDto = new LoginDto(accessToken, refreshToken, false);
            return new ResponseDto<>("새로운 사용자 로그인 성공", loginDto);
        }

        String accessToken = jwtProvider.createAccessToken(searchedResult.get(0).getId());
        String refreshToken = jwtProvider.createRefreshToken(searchedResult.get(0).getId());
        LoginDto loginDto = new LoginDto(accessToken, refreshToken, true);
        return new ResponseDto<>("기존 사용자 로그인 성공", loginDto);
    }


    @Transactional
    public TokenReissueDto reIssueAccessToken(String refreshToken) {
        Map<String, String> tokenMap = jwtProvider.refreshAccessToken(refreshToken);
        return new TokenReissueDto(tokenMap.get("access"), tokenMap.get("refresh"));
    }


    /*구글 인증 서버에 구글 액세스 토큰 요청*/
    private String requestGoogleToken(String code, Redirection redirection) {
        TokenRequestDto tokenRequestDto = new TokenRequestDto(clientId, clientSecret);
        if (redirection.equals(Redirection.BACK)) {
            tokenRequestDto.setCodeAndRedirectUri(code, redirection.getRedirectUri());
        } else if (redirection.equals(Redirection.FRONT)) {
            tokenRequestDto.setCodeAndRedirectUri(code, redirection.getRedirectUri());
        } else {
            for (int i = 0; i < 10; i++) {
                log.debug(redirection.getRedirectUri());
            }
            tokenRequestDto.setCodeAndRedirectUri(code, redirection.getRedirectUri());
        }
        Map<String, String> tokenMap = tokenClient.post()
                .body(BodyInserters.fromValue(tokenRequestDto))
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String,String>>() {
                }).block();
        for (int i = 0; i < 1000; i++) {
            tokenMap.toString();
        }
        return tokenMap.get("access_token");
    }


    /*구글 인증 서버에 유저 정보 요청*/
    private GoogleMemberInfoDto getMemberInfoFromGoogle(String token) {
        return memberInfoClient.get()
                .header("Authorization", "Bearer " + token)
                .retrieve().bodyToMono(GoogleMemberInfoDto.class)
                .block();
    }


    /*새로운 유저 데이터 생성*/
    private Member createMember(GoogleMemberInfoDto googleMemberInfoDto) {
        LangCode[] langCodes = LangCode.values();
        int randomIdx = random.nextInt(langCodes.length);

        String nickname = RandomStringGenerator.generateRandomString(8);

        Member member =  Member.builder()
                .googleId(googleMemberInfoDto.getId())
                .langCode(langCodes[randomIdx])
                .nickname(nickname)
                .build();

        memberRepository.save(member);
        return member;
    }


}
