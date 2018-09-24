package net.bigmir.venzor.services;

import net.bigmir.venzor.dto.SimpleUserDTO;
import net.bigmir.venzor.entities.SimpleUser;
import net.bigmir.venzor.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public SimpleUser findByPhone(String phone) {
        return userRepository.findByPhoneIs(phone);
    }

    public List<SimpleUser> findAll(){
        return userRepository.findAll();
    }

    public SimpleUserDTO getUserDTO(SimpleUser user) {
        return new SimpleUserDTO(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhoneIs(phone);
    }

    @Transactional
    public void updateUser(SimpleUser simpleUser) {
        userRepository.save(simpleUser);
    }

    public SimpleUser getCurrentUser(HttpServletResponse response) {
        User user  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        String phone = user.getUsername();
        if(existsByPhone(phone)){
            return findByPhone(phone);
        }else{
            try {
                response.sendRedirect("/login");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }





}
