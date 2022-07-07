package com.seven.marketclip.account.validation;

import com.seven.marketclip.account.Account;
import com.seven.marketclip.account.AccountRepository;
import com.seven.marketclip.account.dto.AccountReqDTO;
import com.seven.marketclip.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

import static com.seven.marketclip.exception.ResponseCode.*;


@Component
@RequiredArgsConstructor
public class AccountReqDtoValidation implements Validator {
    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(AccountReqDTO.class);
    }

    @Override
    public void validate(Object object, Errors errors) throws CustomException {
        AccountReqDTO dto = (AccountReqDTO)object;
        Optional<Account> accountOptEmail = accountRepository.findByEmail(dto.getEmail());
        Optional<Account> accountOptNickname = accountRepository.findByNickname(dto.getNickname());
        if (accountOptEmail.isPresent()) {
            errors.rejectValue("email", "invalid.email", new Object[]{dto.getEmail()}, "이미 사용중인 이메일입니다.");
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        }

        if (accountOptNickname.isPresent()) {
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{dto.getEmail()}, "이미 사용중인 닉네임입니다.");
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        }
    }

}
