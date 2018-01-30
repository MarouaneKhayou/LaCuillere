/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Annonce;
import bean.AnnonceItem;
import bean.Restaurant;
import bean.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Marouane
 */
@Stateless
public class AnnonceFacade extends AbstractFacade<Annonce> {

    @PersistenceContext(unitName = "LaCuillerePU")
    private EntityManager em;
    @EJB
    private AnnonceItemFacade annonceItemFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnnonceFacade() {
        super(Annonce.class);
    }

    /**
     * Cette méthode fait appelle à une autre méthode qui permet de modifier
     * l'état d'une annonce
     *
     * @param annonce
     * @return 1
     */
    public int modifyAnnonceToCancelled(Annonce annonce) {
        return ModifyStateAnnonceTemplate(annonce, "-1");
    }

    private int ModifyStateAnnonceTemplate(Annonce annonce, String state) {
        annonce.setStateAnnonce(state);
        edit(annonce);
        return 1;
    }

    /**
     * Cette méthode permet de lister les restaurant selon leur Id
     *
     * @param id
     * @return liste de restaurant
     */
    public List<Annonce> getRestaurantAnnonces(Long id) {
        List<Annonce> res = em.createQuery("SELECT a FROM Annonce a WHERE a.user.restaurant.id=" + id).getResultList();
        return res;
    }

    /**
     * Cette méthode donne au restaurateur la possibilité d'ajouter une annonce
     * et de créer un créneau d'horaire appelé annonceIem contenant la plage
     * horaire d'ouverture du restaurant; elle prend comme paramètres :
     *
     * @param user l'utilisateur
     * @param dateAnnonce la date de l'annonce
     * @param phone le téléphone
     * @param mail le mail
     * @param stateAnnonce l'état de l'annonce
     * @param reduction reduction
     * @return 1
     */
    public int addAnnonceByRestaurateur(User user, Date dateAnnonce, String phone, String mail, int reduction) {

        Annonce annonce = new Annonce();
        annonce.setDateAnnonce(dateAnnonce);
        annonce.setMail(mail);
        annonce.setPhone(phone);
        annonce.setStateAnnonce("0");
        annonce.setReduction(reduction);
        annonce.setAnnonceItems(new ArrayList<AnnonceItem>());
        annonce.setUser(user);
        System.out.println("aaaaaaaaaa " + user.getRestaurant().getOpeningHour());
        for (int i = user.getRestaurant().getOpeningHour(); i <= user.getRestaurant().getClosingHour(); i++) {
            AnnonceItem annonceItem = new AnnonceItem();
            annonceItem.setAnnonce(annonce);
            annonceItem.setAnnonceItemHour(i);
            annonce.getAnnonceItems().add(annonceItem);
        }
        create(annonce);
        return 1;
    }

    public boolean IfRestaurantHasAnnonce(Restaurant restaurant, String date) {

        List<Annonce> a = em.createQuery("SELECT a FROM Annonce a WHERE a.user.restaurant.id=" + restaurant.id + " AND a.dateAnnonce='" + date + "' And a.stateAnnonce=0").getResultList();
        if (a.isEmpty()) {
            return false;
        }
        return true;
    }

}
