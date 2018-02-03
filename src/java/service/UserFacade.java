/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Restaurant;
import bean.User;
import util.PasswordUtil;
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
//ee

    @PersistenceContext(unitName = "LaCuillerePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    public void addBonusIfAnnulate(User user, int points) {
        int p = user.getPoints();
        p += points;
        user.setPoints(p);
        edit(user);
    }
    
    public Restaurant getRestaurantByUser(User user) {
        return (Restaurant) em.createQuery("SELECT U.restaurant FROM User AS U WHERE U.id=" + user.getId()).getSingleResult();
    }
    public User getUserByRestaurant(Restaurant restaurant) {
        return (User) em.createQuery("SELECT U FROM User AS U WHERE U.restaurant.id=" + restaurant.getId()).getSingleResult();
    }

    public int changeUserPassword(User user, String recentPassword, String newPassword) {
        if (PasswordUtil.getHashedPassword(recentPassword).equals(user.getPassword())) {
            user.setPassword(PasswordUtil.getHashedPassword(newPassword));
            edit(user);
            return 1;
        } else {
            return -1;
        }
    }

    public int updateUserInformation(User user, String firstName, String lastName, String mail, String phone) {
        String req = "U";
        if (!mail.equals("")) {
            if (ifMailExists(mail)) {
                return -1;
            }
            user.setMail(mail);
        }
        if (!firstName.equals("")) {
            user.setFirstName(firstName);
        }
        if (!lastName.equals("")) {
            user.setLastName(lastName);
        }
        if (!phone.equals("")) {
            user.setPhone(phone);
        }
        edit(user);
        return 1;
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
