/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;

/**
 *
 * @author Marouane
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateReservation;
    private int nbrPlace;
    private String stateReservation;
    private boolean isBonusUsed;
    private int nbrPointsUsed;
    private String comment;

    @ManyToOne
    private AnnonceItem annonceItem;
    @ManyToOne
    private User user;
 
    public int getNbrPointsUsed() {
        return nbrPointsUsed;
    }

    public void setNbrPointsUsed(int nbrPointsUsed) {
        this.nbrPointsUsed = nbrPointsUsed;
    }

    public boolean isIsBonusUsed() {
        return isBonusUsed;
    }

    public void setIsBonusUsed(boolean isBonusUsed) {
        this.isBonusUsed = isBonusUsed;
    }

    public AnnonceItem getAnnonceItem() {
        return annonceItem;
    }

    public void setAnnonceItem(AnnonceItem annonceItem) {
        this.annonceItem = annonceItem;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getNbrPlace() {
        return nbrPlace;
    }

    public void setNbrPlace(int nbrPlace) {
        this.nbrPlace = nbrPlace;
    }

    public String getStateReservation() {
        return stateReservation;
    }

    public void setStateReservation(String stateReservation) {
        this.stateReservation = stateReservation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "bean.Reservation[ id=" + id + " ]";
    }

}
