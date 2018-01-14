/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.User;
import controler.util.PasswordUtil;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.SessionUtil;

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

    public int connexion(String mail, String password) {
        User user = new User();
        user.setMail(mail);
        user.setPassword(password);
        User loadedUser = getUserByMail(mail);
        if (loadedUser != null) {
            if (PasswordUtil.getHashedPassword(password).equals(loadedUser.getPassword())) {
                SessionUtil.registerUser(loadedUser);
                return 1;
            }
        }
        return -1;
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
        return getUserByMail(email) != null;
    }

    public User getUserByMail(String email) {
        List<User> res = em.createQuery("SELECT u FROM User u WHERE u.mail='" + email + "'").getResultList();
        if (res.isEmpty()) {
            return null;
        } else {
            return res.get(0);
        }
    }
}
