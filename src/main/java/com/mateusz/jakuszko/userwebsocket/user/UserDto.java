package com.mateusz.jakuszko.userwebsocket.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private final Long id;
    private final String name;
    private final String surname;
    private final String address;
}
