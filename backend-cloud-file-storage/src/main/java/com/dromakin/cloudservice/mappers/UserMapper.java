package com.dromakin.cloudservice.mappers;

import com.dromakin.cloudservice.dao.Status;
import com.dromakin.cloudservice.dao.security.Role;
import com.dromakin.cloudservice.dao.security.User;
import com.dromakin.cloudservice.dto.UserProfileDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleToString")
    @Mapping(target = "isActive", source = "status", qualifiedByName = "isActiveStatus")
    @Mapping(target = "isSuperUser", source = "roles", qualifiedByName = "isSuperUser")
    UserProfileDTO userToUserDto(User user);


    List<UserProfileDTO> usersToUserDtos(List<User> users);

    @Named("roleToString")
    default List<String> roleToString(List<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Named("isActiveStatus")
    default boolean isActive(Status status) {
        return status == Status.ACTIVE;
    }

    @Named("isSuperUser")
    default boolean isSuperUser(List<Role> roles) {
        return roles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

}