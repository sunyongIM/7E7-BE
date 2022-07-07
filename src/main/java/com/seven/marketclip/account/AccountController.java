package com.seven.marketclip.account;

import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.service.AccountService;
import com.seven.marketclip.account.validation.AccountReqDtoValidation;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.goods.service.S3Uploader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.seven.marketclip.exception.ResponseCode.NICKNAME_VALIDATION_SUCCESS;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
public class AccountController {

    private final AccountReqDtoValidation accountReqDtoValidation;
    private final AccountService accountService;
    private final S3Uploader s3Uploader;
    private final AccountRepository accountRepository;

    @InitBinder("accountReqDTO")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountReqDtoValidation);
    }

    @ApiOperation(value = "회원가입", notes = "회원가입 하는 API")
    @PostMapping("/api/sign-up")
    public ResponseEntity<HttpResponse> signUp(@Validated @RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.addUser(accountReqDTO));
    }

    //회원가입 - 닉네임 중복체크
    @ApiOperation(value = "닉네임 중복체크", notes = "닉네임 중복체크 하는 API")
    @PostMapping("/api/nickname-validation")
    public ResponseEntity<HttpResponse> validatedNickname(@Validated @RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(NICKNAME_VALIDATION_SUCCESS);
    }

    //프로필 보기 -> 토큰에 img 넣기
    //프로필 사진 수정
    @ApiOperation(value = "프로필 이미지 수정", notes = "회원 프로필 사진 수정하기")
    @GetMapping("/api/profile-img")
    public ResponseEntity<HttpResponse> updateProfileImg(@RequestParam("userId") Long id, @RequestPart("userfile") MultipartFile multipartFile) {
        System.out.println("프로필 이미지 수정1 = " + id);
        System.out.println("프로필 이미지 수정2  = " + multipartFile);
        return HttpResponse.toResponseEntity(accountService.updateProfileImg(id,multipartFile));
    }
    
    //프로필 닉네임 수정
    @ApiOperation(value = "프로필 닉네임 수정", notes = "회원 프로필 닉네임 수정하기")
    @GetMapping("/api/profile-nickname")
    public ResponseEntity<HttpResponse> updateNickname(@RequestParam("userId") Long id, @Validated @RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.updateNickname(id,accountReqDTO));
    }
}