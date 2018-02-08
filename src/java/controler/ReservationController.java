package controler;

import bean.Annonce;
import bean.AnnonceItem;
import bean.Menu;
import bean.Reservation;
import bean.Restaurant;
import bean.User;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import java.io.IOException;
import service.ReservationFacade;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import service.AnnonceFacade;
import service.MenuFacade;
import util.SessionUtil;

@Named("reservationController")
@SessionScoped
public class ReservationController implements Serializable {

    @EJB
    private service.ReservationFacade ejbFacade;
    @EJB
    private service.UserFacade userFacade;
    @EJB
    private MenuFacade menuFacade;
    @EJB
    private AnnonceFacade annonceFacade;

    private List<Reservation> items = null;
    private Reservation selected;

    private Date dateReservation;
    private String hourTemplate;
    private String nbrPersonTemplate;
    private Restaurant selectedRestaurant;
    private List<Reservation> restaurantReservations;
    private Annonce selectedAnnonce;
    private AnnonceItem selectedAnnonceItem;
    private boolean useBonus;

    List<String> dates = new ArrayList<>();

    public boolean canUseBonus() {
        if (SessionUtil.getConnectedUser() != null) {
            int points = userFacade.getUserPonits(SessionUtil.getConnectedUser());
            return !((points / 100) >= 1);
        }
        return true;
    }

    public boolean disableValidationAndAnnulationButton(Reservation reservation) {
        return reservation.getStateReservation().equals("2")
                | reservation.getStateReservation().equals("-2") | reservation.getStateReservation().equals("-1");
    }

    public void validReservationRequest(Reservation reservation) {
        int res = ejbFacade.validateReservation(reservation);
        if (res == 1) {
            JsfUtil.addSuccessMessage("Réservation validée");
        } else {
            JsfUtil.addErrorMessage("Erreur d'acceptation de la réservation");
        }
    }

    public void annulerReservationRequest(Reservation reservation) {
        int res = ejbFacade.annulateReservation(reservation);
        if (res == 1) {
            JsfUtil.addSuccessMessage("Réservation annulée");
        } else {
            JsfUtil.addErrorMessage("Erreur d'annulation de la réservation");
        }
    }

    public void annonceDetail(Annonce annonce) throws IOException {
        selectedAnnonce = annonce;
        SessionUtil.redirect("/reservation/annonceReservations.xhtml");
    }

    public void annonceItemDetail(AnnonceItem annonceItem) throws IOException {
        selectedAnnonceItem = annonceItem;
        SessionUtil.redirect("/reservation/annonceItemReservations.xhtml");
    }

    public boolean isUseBonus() {
        return useBonus;
    }

    public void setUseBonus(boolean useBonus) {
        this.useBonus = useBonus;
    }

    public String getSelectedAnnonceDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(getSelectedAnnonce().getDateAnnonce());
    }

    public String getSelectedAnnonceItemDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(getSelectedAnnonceItem().getAnnonce().getDateAnnonce());
    }

    public AnnonceItem getSelectedAnnonceItem() {
        if (selectedAnnonceItem == null) {
            selectedAnnonceItem = new AnnonceItem();
        }
        return selectedAnnonceItem;
    }

    public void setSelectedAnnonceItem(AnnonceItem selectedAnnonceItem) {
        this.selectedAnnonceItem = selectedAnnonceItem;
    }

    public Annonce getSelectedAnnonce() {
        if (selectedAnnonce == null) {
            selectedAnnonce = new Annonce();
        }
        return selectedAnnonce;
    }

    public void setSelectedAnnonce(Annonce selectedAnnonce) {
        this.selectedAnnonce = selectedAnnonce;
    }

    public List<Reservation> getAnnonceReservationsItems() {
        return ejbFacade.getAnnonceReservations(selectedAnnonce);
    }

    public List<Reservation> getAnnonceReservations(Annonce annonce) {
        return ejbFacade.getAnnonceReservations(annonce);
    }

    public List<Reservation> getAnnonceItemReservations() {
        return ejbFacade.getAnnonceItemReservations(selectedAnnonceItem);
    }

    public List<Reservation> getRestaurantReservations() {
        return ejbFacade.getRestaurantReservations(userFacade.getRestaurantByUser(SessionUtil.getConnectedUser()));
    }

    public void setRestaurantReservations(List<Reservation> restaurantReservations) {
        this.restaurantReservations = restaurantReservations;
    }

    public void validerReservationPanier(Reservation reservation) {
        ejbFacade.confirmReservationFromPanier(reservation);
        JsfUtil.addSuccessMessage("Réservation validée avec success");
    }

    public void deleteReservationPanier(Reservation reservation) {
        ejbFacade.deleteReservation(reservation);
        JsfUtil.addSuccessMessage("Réservation supprimée avec success");
    }

    public boolean isValider(Reservation reservation) {
        return reservation.getStateReservation().equals("2");
    }

    public boolean isEnCours(Reservation reservation) {
        return reservation.getStateReservation().equals("1");
    }

    public boolean isAnnuler(Reservation reservation) {
        return reservation.getStateReservation().equals("-1");
    }

    public List<Menu> getRestaurantMenu() {
        return menuFacade.getRestaurantMenus(selectedRestaurant);
    }

    public void redirectToRestaurantDetail(Restaurant restaurant) throws IOException {
        selectedRestaurant = restaurant;
        SessionUtil.redirect("/restaurant/restaurantDetail.xhtml");
    }

    public Restaurant getSelectedRestaurant() {
        return selectedRestaurant;
    }

    public void setSelectedRestaurant(Restaurant selectedRestaurant) {
        this.selectedRestaurant = selectedRestaurant;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public void showDate(Date d) {
        System.out.println("Show date : " + d);
    }

    public String[] getAnnonceDays() {

        List<Annonce> annonces = annonceFacade.getRestaurantAnnonces(selectedRestaurant.getId());
        String[] result = new String[annonces.size()];
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < annonces.size(); i++) {
            result[i] = String.format("'%s'", sdf.format(annonces.get(i).getDateAnnonce()));
        }
        return result;
    }

    public List<Reservation> getPanierReservations() {
        return ejbFacade.getUserReservationsPanier(SessionUtil.getConnectedUser());
    }

    public List<Reservation> getUserReservations() {
        return ejbFacade.getUserReservations(SessionUtil.getConnectedUser());
    }

    public void addToPanier() {
        reserverTemplate("Réservation ajoutée au panier", "0");
    }

    public void reserver() {
        reserverTemplate("Réservation effectué avec success", "1");
    }

    private void reserverTemplate(String message, String stateReservation) {
        Reservation reservation = new Reservation();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Annonce annonce = annonceFacade.getRestaurantAnnonceByDate(selectedRestaurant, sdf.format(dateReservation));
        AnnonceItem annonceItem = null;
        try {
            Integer reservationHour = new Integer(hourTemplate);
            Integer reservationNbrPerson = new Integer(nbrPersonTemplate);

            for (AnnonceItem ai : annonce.getAnnonceItems()) {
                if (ai.getAnnonceItemHour() == reservationHour) {
                    annonceItem = ai;
                }
            }
            if (annonceItem != null) {

                reservation.setAnnonceItem(annonceItem);
                reservation.setDateReservation(dateReservation);
                reservation.setNbrPlace(reservationNbrPerson);
                reservation.setStateReservation(stateReservation);

                if (SessionUtil.getConnectedUser() == null) {
                    SessionUtil.registerReservation(reservation);
                    SessionUtil.goLogin();
                } else {
                    reservation.setUser(SessionUtil.getConnectedUser());
                    int res = 0;
                    if (stateReservation.equals("0")) {
                        res = ejbFacade.addReservationPanier(SessionUtil.getConnectedUser(), annonceItem, dateReservation, reservationNbrPerson, useBonus);
                    } else {
                        res = ejbFacade.addReservation(SessionUtil.getConnectedUser(), annonceItem, dateReservation, reservationNbrPerson, useBonus);
                    }
                    if (res == 1) {
                        JsfUtil.addSuccessMessage(message);
                    }
                }
            } else {
                JsfUtil.addErrorMessage("Erreur! veuillez réesayez une autre fois");
            }
            //System.out.println(annonceFacade.getRestaurantAnnonceByDate(selectedRestaurant, sdf.format(dateReservation)).getAnnonceItems().size());
        } catch (Exception e) {
            JsfUtil.addErrorMessage("Erreur de valeur heure ou bien nombre de personnes");
        }

//if(SessionUtil.getConnectedUser()==null) 
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getHourTemplate() {
        return hourTemplate;
    }

    public void setHourTemplate(String hourTemplate) {
        this.hourTemplate = hourTemplate;
    }

    public String getNbrPersonTemplate() {
        return nbrPersonTemplate;
    }

    public void setNbrPersonTemplate(String nbrPersonTemplate) {
        this.nbrPersonTemplate = nbrPersonTemplate;
    }

    public ReservationController() {
    }

    public Reservation getSelected() {
        return selected;
    }

    public void setSelected(Reservation selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ReservationFacade getFacade() {
        return ejbFacade;
    }

    public void prepareReservation() throws IOException {
        dateReservation = null;
        hourTemplate = "";
        nbrPersonTemplate = "";
        if (SessionUtil.getConnectedUser() == null) {
            SessionUtil.goLogin();
        }
    }

    public Reservation prepareCreate() {
        selected = new Reservation();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ReservationCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ReservationUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ReservationDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Reservation> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Reservation getReservation(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Reservation> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Reservation> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Reservation.class)
    public static class ReservationControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ReservationController controller = (ReservationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "reservationController");
            return controller.getReservation(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Reservation) {
                Reservation o = (Reservation) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Reservation.class.getName()});
                return null;
            }
        }

    }

}
