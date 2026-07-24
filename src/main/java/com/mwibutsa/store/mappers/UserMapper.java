package com.mwibutsa.store.mappers;

import com.mwibutsa.store.dto.ChangePasswordRequest;
import com.mwibutsa.store.dto.RegisterUserRequest;
import com.mwibutsa.store.dto.UpdateUserRequest;
import com.mwibutsa.store.dto.UserDto;
import com.mwibutsa.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest userRequest);

    void updateUser(UpdateUserRequest userRequest, @MappingTarget User user);

    @Mapping(source = "newPassword", target = "password")
    void changePassword(ChangePasswordRequest changePasswordRequest, @MappingTarget User user);
}
