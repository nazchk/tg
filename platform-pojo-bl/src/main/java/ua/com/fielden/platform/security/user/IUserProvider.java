package ua.com.fielden.platform.security.user;

import ua.com.fielden.platform.security.provider.IUserEx;

/**
 * An abstraction for accessing a logged in application user.
 * 
 * @author TG Team
 * 
 */
public interface IUserProvider {
    User getUser();

    void setUsername(final String username, final IUserEx coUserEx);
}
