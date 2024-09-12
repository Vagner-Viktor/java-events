package events.user;

import org.springframework.data.domain.Pageable;
import events.user.dto.NewUserDto;
import events.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAllByIds(List<Long> ids, Pageable pageable);

    UserDto create(NewUserDto newUserDto);

    void delete(Long userId);
}
