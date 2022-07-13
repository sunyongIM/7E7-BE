package com.seven.marketclip.account;

import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.service.AccountService;
import com.seven.marketclip.exception.HttpResponse;
import com.seven.marketclip.goods.domain.Goods;
import com.seven.marketclip.goods.repository.GoodsRepository;
import com.seven.marketclip.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "유저 컨트롤러")
@RequiredArgsConstructor
@RestController
public class AccountController {

//    private final AccountReqDtoValidation accountReqDtoValidation;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final GoodsRepository goodsRepository;

    @ApiOperation(value = "회원가입", notes = "회원가입 하는 API")
    @PostMapping("/api/sign-up")
    public ResponseEntity<HttpResponse> signUp(@RequestBody AccountReqDTO accountReqDTO) {
        return HttpResponse.toResponseEntity(accountService.addUser(accountReqDTO));
    }

    @ApiOperation(value = "닉네임 중복확인", notes = "닉네임 중복 확인하는 API")
    @PostMapping("/api/nickname-check")
    public ResponseEntity<HttpResponse> nicknameCheck(@RequestBody Map<String, String> map) {
        String nickname = map.get("nickname");
        return HttpResponse.toResponseEntity(accountService.checkNickname(nickname));
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
//        SecurityContextHolder.getContext().setAuthentication((Authentication) userDetails);
        return HttpResponse.toResponseEntity(accountService.updateProfileImg(userDetails.getId(),userDetails.getProfileImgUrl(),multipartFile));
    }
    //프로필 닉네임 수정
    @ApiOperation(value = "프로필 닉네임 수정", notes = "회원 프로필 닉네임 수정하기")
    @PostMapping("/api/profile-nickname")
    public ResponseEntity<HttpResponse> updateNickname(@AuthenticationPrincipal UserDetailsImpl userDetails,@Validated @RequestBody AccountReqDTO accountReqDTO) {
//        SecurityContextHolder.getContext().setAuthentication((Authentication) userDetails);
        return HttpResponse.toResponseEntity(accountService.updateNickname(userDetails.getId(),accountReqDTO));
    }
    //비밀번호 변경
    @ApiOperation(value = "프로필 비밀번호 수정", notes = "회원 프로필 비밀번호 수정하기")
    @PostMapping("/api/profile-password")
    public ResponseEntity<HttpResponse> updatePassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody AccountReqDTO accountReqDTO){
        return HttpResponse.toResponseEntity(accountService.updatePassword(userDetails.getId(),accountReqDTO));
    }


    //TODO 상품상태 new 뺴고 게시글 작성 3일 안에 해당하는걸로
    //MyPage
    //판매내역(거래중,거래완료)(reserved,soldout)
    //구매목록
    //저장목록
    @GetMapping("/api/mypage/selllist/reserved")
    @ApiOperation(value = "마이페이지 판매내역(거래중)", notes = "마이페이지의 판매내역(거래중)을 조회하는 API")
    public ResponseEntity<HttpResponse> reservedSellList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        //유저 id, 상품 상태 reserved ->
        Account account = new Account(userDetails);
        List<Goods> goodsList = goodsRepository.findAllByAccount(account);
        for (Goods sd :goodsList) {
            System.out.println(sd.getTitle());
        }




        return null;
    }

}
