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

    public List<Annonce> getRestaurantAnnonces(Long id) {
        List<Annonce> res = em.createQuery("SELECT a FROM Annonce a WHERE a.user.restaurant.id=" + id).getResultList();
        return res;
    }

    public int addAnnonceByRestaurateur(User user, Date dateAnnonce, String phone, String mail, String stateAnnonce, int reduction) {

        Annonce annonce = new Annonce();
        annonce.setDateAnnonce(dateAnnonce);
        annonce.setMail(mail);
        annonce.setPhone(phone);
        annonce.setStateAnnonce("0");
        annonce.setReduction(reduction);
        annonce.setAnnonceItems(new ArrayList<AnnonceItem>());
        annonce.setUser(user);
        annonce.setStateAnnonce(stateAnnonce);
        
        for (int i = user.getRestaurant().getOpeningHour(); i <= user.getRestaurant().getClosingHour(); i++) {
            AnnonceItem annonceItem = new AnnonceItem();
            annonceItem.setAnnonce(annonce);
            annonceItem.setAnnonceItemHour(i);
            annonce.getAnnonceItems().add(annonceItem);
        }
        create(annonce);
        return 1;
    }
}
