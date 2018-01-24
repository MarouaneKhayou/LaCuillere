/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Category;
import bean.City;
import bean.Restaurant;
import bean.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Marouane
 */
@Stateless
public class RestaurantFacade extends AbstractFacade<Restaurant> {

    @PersistenceContext(unitName = "LaCuillerePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RestaurantFacade() {
        super(Restaurant.class);
    }

    public boolean ifRestaurantMailExists(String email) {
        return getRestaurantByMail(email) != null;
    }

    public Restaurant getRestaurantByMail(String email) {
        List<Restaurant> res = em.createQuery("SELECT u FROM Restaurant u WHERE u.mail='" + email + "'").getResultList();
        if (res.isEmpty()) {
            return null;
        } else {
            return res.get(0);
        }
    }

    public int updateRestaurantInformation(Restaurant res, String name, String address, String mail, String description, City city, Category categorie) {
        if (!name.equals("")) {
            res.setName(name);
        }
        if (!address.equals("")) {
            res.setAddress(address);
        }
        if (!mail.equals("")) {
            res.setMail(mail);
        }
        if (!description.equals("")) {
            res.setDescription(description);
        }
        if (categorie != null) {
            res.setCategory(categorie);
        }
        if (city != null) {
            res.setCity(city);
        }
        if (!mail.equals("")) {
            if (ifRestaurantMailExists(mail)) {
                return -1;
            }
            res.setMail(mail);
        }
        edit(res);
        return 1;
    }

} 
