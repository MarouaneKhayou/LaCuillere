/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Menu;
import bean.Restaurant;
import bean.Type;
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
public class MenuFacade extends AbstractFacade<Menu> {

    @PersistenceContext(unitName = "LaCuillerePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MenuFacade() {
        super(Menu.class);
    }

    public List<Menu> getRestaurantMenus(Restaurant restaurant) {
        return em.createQuery("SELECT M FROM Menu AS M WHERE M.restaurant.id=" + restaurant.getId()).getResultList();
    }

    public int addMenuToRestaurant(Restaurant restaurant, String name, Double price, String produits, Type type) {

        Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(price);
        menu.setProduits(produits);
        menu.setType(type);
        menu.setRestaurant(restaurant);

        create(menu);
        return 1;
    }

}
