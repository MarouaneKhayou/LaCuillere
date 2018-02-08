/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.AnnonceItem;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Marouane
 */
@Stateless
public class AnnonceItemFacade extends AbstractFacade<AnnonceItem> {

    @PersistenceContext(unitName = "LaCuillerePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AnnonceItemFacade() {
        super(AnnonceItem.class);
    }

    public int getAnnonceItemReservationsSize(AnnonceItem annonceItem) {
        return em.createQuery("SELECT R FROM Reservation R WHERE R.annonceItem.id=" + annonceItem.getId()).getResultList().size();
    }

}
