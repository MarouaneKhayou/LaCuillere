/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.User;
import controler.util.PasswordUtil;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Marouane
 */
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "LaCuillerePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    public int createNormalUser(User user) {
        if (ifMailExists(user.getMail())) {
            return -1;
        }
        user.setPassword(PasswordUtil.getHashedPassword(user.getPassword()));
        user.setProfil("N");
        create(user);
        return 1;
    }

    public boolean ifMailExists(String email) {
        if (em.createQuery("SELECT u FROM User u WHERE u.mail='" + email + "'").getResultList().isEmpty()) {
            return false;
        }
        return true;
    }
}
