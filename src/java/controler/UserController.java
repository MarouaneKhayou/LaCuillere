package controler;

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
    private List<User> items = null;
    private User selected;
    private String pass2;
    private boolean isRestarantFormShown;
    private Restaurant restaurant;
    private String login;
    private String password;

    public boolean isUserConnected() {
        System.out.println("aaaaaaaszzzz");
        return getConnectedUser() != null;
    }

    public void goLogin() throws IOException {
        SessionUtil.goLogin();
    }

    public void deconnexion() throws IOException {
        SessionUtil.deconnexion();
        SessionUtil.goHome();
    }

    public void connexion() throws IOException {
        int res = getFacade().connexion(login, password);
        if (res == 1) {
            JsfUtil.addSuccessMessage("Connexion reussi!");
            SessionUtil.redirect("home");
        } else {
            JsfUtil.addErrorMessage("Email ou mot de passe incorrect");
        }
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
        System.out.println("nnnnnnnnnnnnnnnnnnn");
        persist(PersistAction.CREATE, "Compte crée avec success");
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
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
                    if (pass2.equals(selected.getPassword())) {
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
                    } else {
                        JsfUtil.addErrorMessage("Les deux mots de passe doivent se correspondre");
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
