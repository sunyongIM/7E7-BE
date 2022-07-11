package com.seven.marketclip.account.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.account.AccountTypeEnum;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.account.validation.AccountVerfication;
import com.seven.marketclip.email.EmailService;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;

import static com.seven.marketclip.exception.ResponseCode.*;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountVerfication accountVerfication;
    private final EmailService emailService;
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Uploader s3Uploader;

    // marketClip 회원가입
    @Transactional
    public ResponseCode addUser(AccountReqDTO accountReqDTO) throws CustomException {
        String encodedPassword = bCryptPasswordEncoder.encode(accountReqDTO.getPassword());

        Account account = Account.builder()
                .email(accountReqDTO.getEmail())
                .nickname(accountReqDTO.getNickname())
                .password(encodedPassword)
                .role(AccountRoleEnum.USER)
                .type(AccountTypeEnum.MARKETCLIP)
                .build();

        //TODO 이거 지워도 될듯?
        if(!emailService.checkVerified(accountReqDTO.getEmail())){
            throw new CustomException(UNVERIFIED_EMAIL);
        }

        accountRepository.save(account);

        return SIGNUP_SUCCESS;
    }

    //프로필 이미지 수정
    @Transactional
    public ResponseCode updateProfileImg(Long id, String imgUrl, MultipartFile multipartFile) throws CustomException{

        Account account = accountVerfication.checkVerificationId(id);
        //USER_NOT_FOUND로 해야하나?

        //이미지 넣기
        String fileUrl = s3Uploader.uploadFile(multipartFile);
        account.changeProfileImg(fileUrl);

        System.out.println(imgUrl);
        //기존 이미지 s3에서 삭제
        if(imgUrl!=null || !imgUrl.isEmpty() || imgUrl.length() != 0 || imgUrl.equals("")){
            s3Uploader.deleteFile(imgUrl);
        }
//        throw new CustomException(LOGIN_FILTER_NULL);
        return PROFILEIMG_UPDATE_SUCCESS;
    }

    //프로필 닉네임 수정
    @Transactional
    public ResponseCode updateNickname(Long id, AccountReqDTO accountReqDTO){
        Account account = accountVerfication.checkVerificationId(id);
        account.changeNickname(accountReqDTO.getNickname());

        //여기에 JWT 재발급? -> 다른 수정들도...

        return NICKNAME_UPDATE_SUCCESS;
    }
    //프로필 비밀번호 수정
    @Transactional
    public ResponseCode updatePassword(Long id, AccountReqDTO accountReqDTO) {
        Account account = accountVerfication.checkVerificationId(id);
        account.changePassword(accountReqDTO.getPassword());
        account.encodePassword(bCryptPasswordEncoder);
        return PASSWORD_VALIDATION_SUCCESS;
    }

}
