package controler;

import bean.Category;
import bean.City;
import bean.Restaurant;
import bean.User;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import java.io.IOException;
import service.RestaurantFacade;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import service.UserFacade;
import util.SessionUtil;

@Named("restaurantController")
@SessionScoped
public class RestaurantController implements Serializable {

    @EJB
    private service.RestaurantFacade ejbFacade;
    @EJB
    private service.UserFacade userFacade;
    @EJB
    private service.RestaurantFacade restaurantFacade;
    @EJB
    private service.MenuFacade menuFacade;

    private List<Restaurant> items = null;
    private Restaurant selected;
    private User user;
    private String pass2;

    private Restaurant newRestaurant;

    private String nameRestaurant;
    private Date dateAnnonce;
    private Category category;
    private City city;

    private String nn;

    public String getNn() {
        return nn;
    }

    public void setNn(String nn) {
        this.nn = nn;
    }

    public void updateRestaurantInformation() {
        if (newRestaurant.getName().equals("") & newRestaurant.getPhone().equals("") & newRestaurant.getAddress().equals("")
                & newRestaurant.getMail().equals("") & newRestaurant.getDescription().equals("")) {
            JsfUtil.addErrorMessage("Veuillez indroduire des informations");
        } else {
            int res = restaurantFacade.updateRestaurantInformation(getConnectedUserRestaurant(), newRestaurant.getName(),
                    newRestaurant.getAddress(), newRestaurant.getMail(), newRestaurant.getDescription(), newRestaurant.getPhone(), null, null);
            if (res == 1) {
                JsfUtil.addSuccessMessage("Information modifiées avec success");
                newRestaurant = new Restaurant();
            }
        }
    }

    public RestaurantFacade getRestaurantFacade() {
        return restaurantFacade;
    }

    public void setRestaurantFacade(RestaurantFacade restaurantFacade) {
        this.restaurantFacade = restaurantFacade;
    }

    public Restaurant getNewRestaurant() {
        if (newRestaurant == null) {
            newRestaurant = new Restaurant();
        }
        return newRestaurant;
    }

    public void setNewRestaurant(Restaurant newRestaurant) {
        this.newRestaurant = newRestaurant;
    }

    public List<bean.Menu> getConnectedUserRestaurantMenu() {
        return menuFacade.getRestaurantMenus(SessionUtil.getConnectedUser().getRestaurant());
    }

    public Restaurant getConnectedUserRestaurant() {
        return userFacade.getRestaurantByUser(SessionUtil.getConnectedUser());
    }

    public void searchAnnonce() {
        String dateAnnonceString = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (dateAnnonce != null) {
            dateAnnonceString = format.format(dateAnnonce);
        }
        items = restaurantFacade.getRestaurantByCriteres(city, category, nameRestaurant, dateAnnonceString);
    }

    public String getNameRestaurant() {
        return nameRestaurant;
    }

    public void setNameRestaurant(String nameRestaurant) {
        this.nameRestaurant = nameRestaurant;
    }

    public Date getDateAnnonce() {
        return dateAnnonce;
    }

    public void setDateAnnonce(Date dateAnnonce) {
        this.dateAnnonce = dateAnnonce;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public RestaurantController() {
    }

    public UserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public String getPass2() {
        return pass2;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }

    public User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Restaurant getSelected() {
        if (selected == null) {
            selected = new Restaurant();
        }
        return selected;
    }

    public void setSelected(Restaurant selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RestaurantFacade getFacade() {
        return ejbFacade;
    }

    public Restaurant prepareCreate() {
        selected = new Restaurant();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        System.out.println("ssssssssss");
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RestaurantCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RestaurantUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RestaurantDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Restaurant> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        setEmbeddableKeys();
//            try {
//                if (persistAction == PersistAction.CREATE) {
//                    if (pass2.equals(user.getPassword())) {
//                        user.setProfil("R");
//                        int res = getUserFacade().createNormalUser(user);
//                        if (res == -1) {
//                            JsfUtil.addErrorMessage("Email saisie déja existant!");
//                        } else if (res == 1) {
//                            selected.setUser(user);
//                            getFacade().create(selected);
//                            JsfUtil.addSuccessMessage(successMessage);
//                        }
//                    } else {
//                        JsfUtil.addErrorMessage("Les deux mots de passe doivent se correspondre");
//                    }
//                } else if (persistAction == PersistAction.UPDATE) {
//                    getFacade().edit(selected);
//                } else if (persistAction == PersistAction.DELETE) {
//                    getFacade().remove(selected);
//                }
//                JsfUtil.addSuccessMessage(successMessage);
//            } catch (EJBException ex) {
//                String msg = "";
//                Throwable cause = ex.getCause();
//                if (cause != null) {
//                    msg = cause.getLocalizedMessage();
//                }
//                if (msg.length() > 0) {
//                    JsfUtil.addErrorMessage(msg);
//                } else {
//                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
//                }
//            } catch (Exception ex) {
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
//                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
//            }
    }

    public Restaurant getRestaurant(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Restaurant> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Restaurant> getItemsAvailableSelectOne() {
        return getFacade().findAll();

    }

    @FacesConverter(forClass = Restaurant.class)
    public static class RestaurantControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RestaurantController controller = (RestaurantController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "restaurantController");
            return controller.getRestaurant(getKey(value));
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
            if (object instanceof Restaurant) {
                Restaurant o = (Restaurant) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Restaurant.class.getName()});
                return null;
            }
        }

    }

}
