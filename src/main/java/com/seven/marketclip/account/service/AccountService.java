package com.seven.marketclip.account.service;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.AccountRoleEnum;
import com.seven.marketclip.account.AccountTypeEnum;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.email.EmailService;
import com.seven.marketclip.exception.CustomException;
import com.seven.marketclip.exception.DataResponseCode;
import com.seven.marketclip.exception.ResponseCode;
import com.seven.marketclip.goods.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.seven.marketclip.exception.ResponseCode.*;

@RequiredArgsConstructor
@Service
public class AccountService {

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
    public DataResponseCode updateProfileImg(Long id, MultipartFile multipartFile) throws CustomException{
        Optional<Account> account = accountRepository.findById(id);

        if(!account.isPresent()){ //없으면 예외처리
            throw new CustomException(USER_NOT_FOUND); //커스텀 익셉션
        }
        //이미지 넣기
        String fileUrl = s3Uploader.uploadFile(multipartFile);
        account.get().changeProfileImg(fileUrl);
        return new DataResponseCode(PROFILEIMG_UPDATE_SUCCESS,fileUrl);
    }

    //프로필 닉네임 수정
    @Transactional
    public ResponseCode updateNickname(Long id, AccountReqDTO accountReqDTO){
        Optional<Account> account = accountRepository.findById(id);
        account.get().changeNickname(accountReqDTO.getNickname());
        return NICKNAME_UPDATE_SUCCESS;
    }

}
