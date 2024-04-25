package edu.AF.UTMS.services.impl;

import edu.AF.UTMS.models.User;
import edu.AF.UTMS.models.consts.UserRoles;
import edu.AF.UTMS.repositories.UserRepository;
import edu.AF.UTMS.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean changeUserRole(UserRoles userRole, String userId) {
        try {
            User user = userRepository.findById(userId).get();
            if (!user.getUserRole().equals(userRole)){
                user.setUserRole(userRole);
                userRepository.save(user);
            }
            return true;
        } catch (NoSuchElementException nse) {
            System.out.println("Invalid userId ! : "+nse.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
