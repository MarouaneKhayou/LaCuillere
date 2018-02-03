/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Annonce;
import bean.AnnonceItem;
import bean.Reservation;
import bean.Restaurant;
import bean.User;
import java.awt.MenuItem;
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
public class ReservationFacade extends AbstractFacade<Reservation> {

    @PersistenceContext(unitName = "LaCuillerePU")
    private EntityManager em;
    @EJB
    private service.UserFacade userFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public int validateReservation(Reservation reservation) {
        if (!reservation.getStateReservation().equals("-1")) {
            reservation.setStateReservation("2");
            edit(reservation);
            return 1;
        } else {
            return -1;
        }
    }

    private List<Reservation> getReservationsTemplate(Restaurant restaurant, Annonce annonce, AnnonceItem annonceItem) {
        String req = "SELECT R FROM Reservation R WHERE R.stateReservation<>0";
        if (restaurant != null) {
            req += " AND R.annonceItem.annonce.user.restaurant.id=" + restaurant.getId();
        }
        if (annonce != null) {
            req += " AND R.annonceItem.annonce.id=" + annonce.getId();
        }
        if (annonceItem != null) {
            req += " AND R.annonceItem.id=" + annonceItem.getId();
        }
        return em.createQuery(req).getResultList();
    }

    public List<Reservation> getRestaurantReservations(Restaurant restaurant) {
        return getReservationsTemplate(restaurant, null, null);
    }

    public List<Reservation> getAnnonceReservations(Annonce annonce) {
        return getReservationsTemplate(null, annonce, null);
    }

    public List<Reservation> getAnnonceItemReservations(AnnonceItem annonceItem) {
        return getReservationsTemplate(null, null, annonceItem);
    }

    public int annulateReservation(Reservation reservation) {
        if (!reservation.getStateReservation().equals("2")) {
            reservation.setStateReservation("-1");
            reservation.setComment("Nous sommes dans le regré dans ne pas avoir donner suite favorable à votre demande, cependant votre bonus sera augmenter pour palier à se dérangement");
            edit(reservation);
            userFacade.addBonusIfAnnulate(reservation.getUser(), 10);

            return 1;
        } else {
            return -1;
        }
    }

    public ReservationFacade() {
        super(Reservation.class);
    }

    public void deleteReservation(Reservation reservation) {
        remove(reservation);
    }

    public int confirmReservationFromPanier(Reservation reservation) {
        if (reservation.isIsBonusUsed()) {
            reservation.getUser().setPoints(reservation.getUser().getPoints() - reservation.getNbrPointsUsed());
            userFacade.edit(reservation.getUser());
        }
        reservation.setStateReservation("1");
        edit(reservation);
        return 1;
    }

    private List<Reservation> getUserReservationsTemplate(User user, String state) {
        return em.createQuery("SELECT R FROM Reservation AS R WHERE R.user.id=" + user.getId() + " AND R.stateReservation='" + state + "'").getResultList();
    }

    public List<Reservation> getUserReservations(User user) {
        return em.createQuery("SELECT R FROM Reservation AS R WHERE R.user.id=" + user.getId() + " AND R.stateReservation<>'0'").getResultList();
    }

    public List<Reservation> getUserReservationsPanier(User user) {
        return getUserReservationsTemplate(user, "0");
    }

    public int addReservationTemplate(int typeOfReservation, User user, AnnonceItem annonceitem, Date dateReservation, int nbrPlace, boolean isBonusUsed) {
        Reservation reservation = new Reservation();

        int reduction = user.getPoints() / 100;

        if (isBonusUsed) {
            reservation.setIsBonusUsed(true);
            reservation.setNbrPointsUsed(reduction * 100);
        } else {
            reservation.setIsBonusUsed(false);
            reservation.setNbrPointsUsed(0);
        }
        if (typeOfReservation == 0) {//Dans le cas d'un ajout au panier
            reservation.setStateReservation("0");
        } else if (typeOfReservation == 1) {//Dans le cas d'une reservation 
            reservation.setStateReservation("1");
            if (isBonusUsed) {
                user.setPoints(user.getPoints() - reduction * 100);
                userFacade.edit(user);
            }
        }
        reservation.setUser(user);
        reservation.setDateReservation(dateReservation);
        reservation.setNbrPlace(nbrPlace);
        reservation.setComment("");
        reservation.setAnnonceItem(annonceitem);
        create(reservation);
        return 1;
    }

    public int addReservationPanier(User user, AnnonceItem annonceitem, Date dateReservation, int nbrPlace, boolean isBonusUsed) {
        return addReservationTemplate(0, user, annonceitem, dateReservation, nbrPlace, isBonusUsed);
    }

    public int addReservation(User user, AnnonceItem annonceitem, Date dateReservation, int nbrPlace, boolean isBonusUsed) {
        return addReservationTemplate(1, user, annonceitem, dateReservation, nbrPlace, isBonusUsed);
    }

}
