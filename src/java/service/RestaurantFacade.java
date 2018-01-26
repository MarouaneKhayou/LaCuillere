/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Category;
import bean.City;
import bean.City_;
import bean.Restaurant;
import bean.Restaurant_;
import bean.User;
import java.util.Date;
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
    
     public boolean ifMailExists(String email) {
        return getRestaurantByMail(email) != null;
    }
    
    /**
     * Cette méthode permet de lister les restaurant en spécifiant l'adresse mail du restaurant
     * @param email
     * @return liste de restaurant
     */
    public Restaurant getRestaurantByMail(String email) {
        List<Restaurant> res = em.createQuery("SELECT u FROM Restaurant u WHERE u.mail='" + email + "'").getResultList();
        if (res.isEmpty()) {
            return null;
        } else {
            return res.get(0);
        }
    }
    /**
     * Cette méthode permet de donner à l'utilisateur de chercher les restaurants selon plusieurs critères:
     * @param city: ville
     * @param category: catégorie
     * @param name: nome du restaurant
     * @param date: date de resérvation souhaiter
     * @return liste de restaurant
     */
    public List<Restaurant> getRestaurantByCriteres(City city, Category category, String name, String date) {
        String req = "SELECT r FROM Restaurant r WHERE 1=1 ";

        if (city != null) {
            req += "And r.city.id = " + city.getId();
        }
        if (category != null) {
            req += " And r.category.id= " + category.getId();
        }
        if (!name.equals("")) {
            req += "And r.name like  '%" + name + "%' ";
        }
        if (date != null) {
            req += "And exists (SELECT a FROM Annonce a WHERE a.user.restaurant.id=r.id AND a.dateAnnonce='" + date + "')";
        }

        return em.createQuery(req).getResultList();
    }
    /**
     * Cette méthode permet de mettre à jour les informations d'un restaurant elle prend comme arguments:
     * @param res restaurant
     * @param name le nom
     * @param address l'adresse 
     * @param mail l'adresse mail
     * @param description la description
     * @param city la ville
     * @param category la catégorie
     * @return 
     */
    public int updateRestaurantInformation(Restaurant res, String name, String address, String mail, String description, City city, Category category) {
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
        if (category != null) {
            res.setCategory(category);
        }
        if (city != null) {
            res.setCity(city);
        }
        edit(res);
        return 1;
}
    
}
