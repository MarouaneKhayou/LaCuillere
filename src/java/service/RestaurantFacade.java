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

}
