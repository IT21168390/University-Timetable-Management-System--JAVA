package edu.AF.UTMS.services;

import edu.AF.UTMS.models.consts.UserRoles;

public interface AdminService {
    boolean changeUserRole(UserRoles userRole, String userId);
}
