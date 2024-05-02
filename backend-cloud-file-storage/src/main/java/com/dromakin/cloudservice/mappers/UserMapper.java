package com.dromakin.cloudservice.mappers;

import com.dromakin.cloudservice.dao.Status;
import com.dromakin.cloudservice.dao.security.Role;
import com.dromakin.cloudservice.dao.security.User;
import com.dromakin.cloudservice.dto.NewUserDTO;
import com.dromakin.cloudservice.dto.RolesDTO;
import com.dromakin.cloudservice.dto.UpdateUserDTO;
import com.dromakin.cloudservice.dto.UserProfileDTO;
import com.dromakin.cloudservice.repositories.user.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleToString")
    @Mapping(target = "isActive", source = "status", qualifiedByName = "isActiveStatus")
    @Mapping(target = "isSuperUser", source = "roles", qualifiedByName = "isSuperUser")
    public abstract UserProfileDTO userToUserDto(User user);

    public abstract List<UserProfileDTO> usersToUserDtos(List<User> users);

    @Named("roleToString")
    protected List<String> roleToString(List<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Named("isActiveStatus")
    protected boolean isActive(Status status) {
        return status == Status.ACTIVE;
    }

    @Named("isSuperUser")
    protected boolean isSuperUser(List<Role> roles) {
        return roles.stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    public User newUserDtoToUser(NewUserDTO userDto) {
        User user = new User();
        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        if (userDto.getRoles() != null) {
            user.setRoles(userDto.getRoles().stream().map(x -> roleRepository.findByName(x)).collect(Collectors.toList()));
        } else {
            user.setRoles(new ArrayList<>());
        }

        return user;
    }

    public User updateUserDtoToUser(UpdateUserDTO userDto) {
        User user = new User();

        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }

        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        return user;
    }

    public RolesDTO listRolesToDto(List<Role> roles) {
        return RolesDTO.builder().roles(roles.stream().map(Role::getName).collect(Collectors.toList())).build();
    }

}