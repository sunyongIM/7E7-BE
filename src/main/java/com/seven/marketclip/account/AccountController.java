package com.seven.marketclip.account;

import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.service.AccountService;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.goods.service.S3Uploader;
import com.seven.marketclip.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;
    private final S3Uploader s3Uploader;
    private final AccountRepository accountRepository;

    @ApiOperation(value = "회원가입", notes = "회원가입 하는 API")
    @PostMapping("/api/sign-up")
    public ResponseEntity<HttpResponse> signUp(@Validated @RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.addUser(accountReqDTO));
    }

    //TODO @RequestParam으로 받는게 아니라 @Authentication으로 받는게 좋을 듯?
    //TODO JWT Provider에서 디코더를 할 때 백에서는 id만? 디코더 해도 좋을 듯?
    //프로필 사진 수정 -> 이것도 가져오기 JWT에서
    @ApiOperation(value = "프로필 이미지 수정", notes = "회원 프로필 사진 수정하기")
    @GetMapping("/api/profile-img")
    public ResponseEntity<HttpResponse> updateProfileImg(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam("imgUrl") String imgUrl, @RequestPart("userfile") MultipartFile multipartFile) {
        System.out.println("프로필 이미지 수정1 = " + userDetails.getId());
        System.out.println("프로필 이미지 수정2  = " + multipartFile);
        System.out.println("프로필 이미지 수정3  = " + imgUrl);
        System.out.println("프로필 이미지 수정4  = " + userDetails.getProfileImgUrl());
        return HttpResponse.toResponseEntity(accountService.updateProfileImg(userDetails.getId(),userDetails.getProfileImgUrl(),multipartFile));
    }
    //프로필 닉네임 수정
    @ApiOperation(value = "프로필 닉네임 수정", notes = "회원 프로필 닉네임 수정하기")
    @PostMapping("/api/profile-nickname")
    public ResponseEntity<HttpResponse> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,@Validated @RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.updateNickname(userDetails.getId(),accountReqDTO));
    }
    //비밀번호 변경
    @ApiOperation(value = "프로필 비밀번호 수정", notes = "회원 프로필 비밀번호 수정하기")
    @PostMapping("/api/profile-password")
    public ResponseEntity<HttpResponse> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AccountReqDTO accountReqDTO){


        SecurityContextHolder.getContext().setAuthentication((Authentication)userDetails);

        return HttpResponse.toResponseEntity(accountService.updatePassword(userDetails.getId(),accountReqDTO));
    }
    //프론트 -> jwt 필터 -> 해독해서 페이로드에 있는정보로 -> 컨텍스트 홀더 ->


    //판매목록 보기(최대 6개? 페이징과 판매된 것만 확인할 수 있게)
    
    //미판매목록 보기

    //구매목록(Goods에 구매자 목록도 있어야 할 듯)
    
}