package events.user;

import events.user.dto.UserDto;
import events.user.dto.UserShortDto;
import events.user.model.User;

import java.util.List;

public class UserMapper {

    public static List<UserDto> toUserDtoCollection(List<User> users) {
        return users.stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }
}
