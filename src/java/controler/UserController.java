package controler;

import bean.Reservation;
import bean.Restaurant;
import bean.User;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import java.io.IOException;
import service.UserFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import util.SessionUtil;

@Named("userController")
@SessionScoped
public class UserController implements Serializable {

    @EJB
    private service.UserFacade ejbFacade;
    @EJB
    private service.RestaurantFacade restaurantFacade;
    @EJB
    service.ReservationFacade reservationFacade;
    private List<User> items = null;
    private User selected;
    private String pass2;
    private boolean isRestarantFormShown;
    private Restaurant restaurant;
    private String login;
    private String password;

    private String recentPassword;
    private String newPassword;
    private String repeatPassword;

    private String openingHourTemplate;
    private String closingHourTemplate;

    private User newUser;

    public String getOpeningHourTemplate() {
        return openingHourTemplate;
    }

    public void setOpeningHourTemplate(String openingHourTemplate) {
        this.openingHourTemplate = openingHourTemplate;
    }

    public String getClosingHourTemplate() {
        return closingHourTemplate;
    }

    public void setClosingHourTemplate(String closingHourTemplate) {
        this.closingHourTemplate = closingHourTemplate;
    }

    public boolean isUserRestaurant() {
        if (getConnectedUser() != null) {
            return getConnectedUser().getProfil().equals("R");
        }
        return false;
    }

    public boolean showHome() {
        if (getConnectedUser() != null) {
            if (getConnectedUser().getProfil().equals("N")) {
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean isUserNormal() {
        if (getConnectedUser() != null) {
            return getConnectedUser().getProfil().equals("N");
        }
        return false;
    }

    public void changePassword() {
        if (newPassword.equals(repeatPassword)) {
            int res = getFacade().changeUserPassword(getConnectedUser(), recentPassword, newPassword);
            if (res == -1) {
                JsfUtil.addErrorMessage("Ancien mot de passe incorrect");
            } else if (res == 1) {
                JsfUtil.addErrorMessage("Mot de passe modifié avec success");
                newPassword = "";
                recentPassword = "";
                repeatPassword = "";
            }
        } else {
            JsfUtil.addErrorMessage("Les deux mots de passe doivent se correpondre");
        }
    }

    public void updateUserInformation() {
        System.out.println(newUser.getFirstName());
        if (newUser.getFirstName().equals("") & newUser.getLastName().equals("") & newUser.getPhone().equals("")
                & newUser.getMail().equals("")) {
            JsfUtil.addErrorMessage("Veuillez indroduire des informations");
        } else {
            int res = getFacade().updateUserInformation(getConnectedUser(), newUser.getFirstName(), newUser.getLastName(),
                    newUser.getMail(), newUser.getPhone());
            if (res == -1) {
                JsfUtil.addErrorMessage("Le mail saisie déjà existant");
            } else if (res == 1) {
                JsfUtil.addSuccessMessage("Information modifiées avec success");
                newUser = new User();
            }
        }
    }

    public boolean isUserConnected() {
        return getConnectedUser() != null;
    }

    public void goLogin() throws IOException {
        SessionUtil.goLogin();
    }

    public void goHome() throws IOException {
        SessionUtil.goHome();
    }

    public void deconnexion() throws IOException {
        JsfUtil.addSuccessMessage("Déconnexion reussi!");
        SessionUtil.deconnexion();
        SessionUtil.goHome();
    }

    public void connexion() throws IOException {
        int res = getFacade().connexion(login, password);
        if (res == 1) {
            Reservation reservation = SessionUtil.getReservation();
            if (reservation == null) {
                JsfUtil.addSuccessMessage("Connexion reussi!");
                if (SessionUtil.getConnectedUser().getProfil().equals("N")) {
                    SessionUtil.redirect("index");
                } else {
                    SessionUtil.goRestaurantHome();
                }
            } else {
                System.out.println("ussser " + SessionUtil.getConnectedUser().getFirstName());
                reservation.setUser(SessionUtil.getConnectedUser());
                reservationFacade.edit(reservation);
                if (reservation.getStateReservation().equals("0")) {
                    JsfUtil.addSuccessMessage("Réservation ajoutée au panier");
                    SessionUtil.removeReservation();
                } else if (reservation.getStateReservation().equals("1")) {
                    JsfUtil.addSuccessMessage("Réservation effectué avec success");
                    SessionUtil.removeReservation();
                }
                SessionUtil.redirect("index");
            }
        } else {
            JsfUtil.addErrorMessage("Email ou mot de passe incorrect");
        }
    }

    public String getRecentPassword() {
        return recentPassword;
    }

    public void setRecentPassword(String recentPassword) {
        this.recentPassword = recentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public User getNewUser() {
        if (newUser == null) {
            newUser = new User();
        }
        return newUser;
    }

    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    public User getConnectedUser() {
        return SessionUtil.getConnectedUser();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Restaurant getRestaurant() {
        if (restaurant == null) {
            restaurant = new Restaurant();
        }
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getPass2() {
        pass2 = "";
        return pass2;
    }

    @PostConstruct
    public void intiIsShown() {
        this.isRestarantFormShown = false;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }

    public boolean isIsRestarantFormShown() {
        return isRestarantFormShown;
    }

    public void setIsRestarantFormShown(boolean isRestarantFormShown) {
        this.isRestarantFormShown = isRestarantFormShown;
    }

    public void setShowRestarantForm(boolean showRestarantForm) {
        if (showRestarantForm == false) {
            restaurant = new Restaurant();
        }
        this.isRestarantFormShown = showRestarantForm;
    }

    public void showRestarantForm() {
        System.out.println(isRestarantFormShown);
    }

    public UserController() {
    }

    public User getSelected() {
        if (selected == null) {
            selected = new User();
        }
        return selected;
    }

    public void setSelected(User selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserFacade getFacade() {
        return ejbFacade;
    }

    public User prepareCreate() {
        selected = new User();
        initializeEmbeddableKey();
        return selected;
    }

    public String hello() {
        System.out.println("marouane eeeeeeeeeeee");
        return "aa";

    }

    public void create() {
        if (selected != null) {
            if (pass2.equals(selected.getPassword())) {
                selected.setPoints(0);
                if (isRestarantFormShown) {
                    try {
                        Integer openingHour = new Integer(this.openingHourTemplate);
                        Integer closingHour = new Integer(this.closingHourTemplate);
                        if (closingHour > openingHour) {
                            restaurant.setOpeningHour(openingHour);
                            restaurant.setClosingHour(closingHour);
                            persist(PersistAction.CREATE, "Compte crée avec success");
                            if (!JsfUtil.isValidationFailed()) {
                                items = null;
                            }
                        } else {
                            JsfUtil.addErrorMessage("L'heure de fermutaire doit etre supérieure à l'heure d'ouvertaire");
                        }
                    } catch (Exception e) {
                        JsfUtil.addErrorMessage("Valeur erronée pour heure d'ouverture ou bien de fermutaire");
                    }
                } else {
                    persist(PersistAction.CREATE, "Compte crée avec success");
                    if (!JsfUtil.isValidationFailed()) {
                        items = null;
                    }
                }
            } else {
                JsfUtil.addErrorMessage("Les deux mots de passe doivent se correspondre");
            }
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<User> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.CREATE) {
                    int res = getFacade().createNormalUser(selected);
                    if (res == -1) {
                        JsfUtil.addErrorMessage("Email saisie déja existant!");
                    } else if (res == 1) {
                        if (isRestarantFormShown) {
                            restaurantFacade.create(restaurant);

                            selected.setProfil("R");
                            selected.setRestaurant(restaurant);
                            getFacade().edit(selected);
                        }
                        restaurant = new Restaurant();
                        selected = new User();
                        JsfUtil.addSuccessMessage(successMessage);
                        util.SessionUtil.redirect("login");
                    }

                } else if (persistAction == PersistAction.UPDATE) {
                    getFacade().edit(selected);
                } else if (persistAction == PersistAction.DELETE) {
                    getFacade().remove(selected);
                }
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

    public User getUser(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<User> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<User> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = User.class)
    public static class UserControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserController controller = (UserController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userController");
            return controller.getUser(getKey(value));
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
            if (object instanceof User) {
                User o = (User) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), User.class.getName()});
                return null;
            }
        }

    }

}
