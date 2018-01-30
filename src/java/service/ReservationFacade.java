/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Annonce;
import bean.AnnonceItem;
import bean.Reservation;
import bean.ReservationMenuItem;
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
    
    public ReservationFacade() {
        super(Reservation.class);
    }
    
    public int addReservationTemplate(int typeOfReservation, User user, AnnonceItem annonceitem, List<ReservationMenuItem> reservationMenuItems, Date dateReservation, int nbrPlace, boolean isBonusUsed) {
        Reservation reservation = new Reservation();
        
        int reduction = user.getPoints() / 100;
        
        double totalbrut = 0;
        double totalPriceReductionBonus = 0;
        
        for (ReservationMenuItem reservationMenuItem : reservationMenuItems) {
            totalbrut += reservationMenuItem.getPrice();
        }
        if (annonceitem.getAnnonce().getReduction() > 0) {
            totalbrut = totalbrut - totalbrut * ((double) annonceitem.getAnnonce().getReduction() / 100);
        }
        totalPriceReductionBonus = totalbrut;
        if (isBonusUsed) {
            totalPriceReductionBonus = totalPriceReductionBonus - reduction;
            reservation.setIsBonusUsed(true);
            reservation.setNbrPointsUsed(reduction * 100);
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
        reservation.setTotalPrice(totalbrut);
        reservation.setTotalPriceReductionBonus(totalPriceReductionBonus);
        reservation.setDateReservation(dateReservation);
        reservation.setNbrPlace(nbrPlace);
        reservation.setComment("");
        reservation.setAnnonceItem(annonceitem);
        create(reservation);
        return 1;
    }
    
    public int addReservationPanier(User user, AnnonceItem annonceitem, List<ReservationMenuItem> reservationMenuItems, Date dateReservation, int nbrPlace, boolean isBonusUsed) {
        return addReservationTemplate(0, user, annonceitem, reservationMenuItems, dateReservation, nbrPlace, isBonusUsed);
    }
    
    public int addReservation(User user, AnnonceItem annonceitem, List<ReservationMenuItem> reservationMenuItems, Date dateReservation, int nbrPlace, boolean isBonusUsed) {
        return addReservationTemplate(1, user, annonceitem, reservationMenuItems, dateReservation, nbrPlace, isBonusUsed);
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
}
