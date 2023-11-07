package by.komikow.service;

import by.komikow.entity.Role;
import by.komikow.entity.User;
import by.komikow.repository.RoleRepository;
import by.komikow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public boolean saveUser(User user) {
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));
        if (optionalUser.isPresent()) return false;
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L, "ROLE_USER"));
        user.setRoles(roles);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Transactional
    @Modifying
    @Query(value = "update users u set u.email= :newEmail where u.id = :userId", nativeQuery = true)
    public boolean updateUserById(@Param("userId") Long userId, @Param("newEmail") String newEmail) {
        User user = userRepository.findById(userId).orElse(new User());
        user.setEmail(newEmail);
        userRepository.save(user);
        return true;
    }

    @Transactional
    @Modifying
    @Query(value = "update users u set u.email= :newEmail where u.username = :username", nativeQuery = true)
    public boolean updateUserByUsername(@Param("username") String username, @Param("newEmail") String newEmail) {
        User user = userRepository.findByUsername(username);
        user.setEmail(newEmail);
        userRepository.save(user);
        return true;
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id > :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }

    public User readUser(String username) {
        return userRepository.findByUsername(username);
    }
}