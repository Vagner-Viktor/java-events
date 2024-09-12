package events.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import events.exception.DataIntegrityViolationException;
import events.user.dao.UserRepository;
import events.user.dto.NewUserDto;
import events.user.dto.UserDto;
import events.user.model.User;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllByIds(List<Long> ids, Pageable pageable) {
        if (ids == null) return userRepository.findAll(pageable).get()
                .map(UserMapper::toUserDto)
                .toList();
        return UserMapper.toUserDtoCollection(userRepository.findAllByIdIn(ids, pageable));
    }

    @Transactional
    @Override
    public UserDto create(NewUserDto newUserDto) {
        if (userRepository.existsByEmail(newUserDto.getEmail())) {
            throw new DataIntegrityViolationException("This e-mail already exists!");
        }
        return UserMapper.toUserDto(userRepository.save(
                new User(null, newUserDto.getName(), newUserDto.getEmail(), null)
        ));
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
