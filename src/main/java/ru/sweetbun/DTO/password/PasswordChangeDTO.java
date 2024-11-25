package ru.sweetbun.DTO.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDTO {

    private String username;
    private String password;
    private String code;
}
