package controler;

import bean.Menu;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.MenuFacade;

import java.io.Serializable;
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
import util.SessionUtil;

@Named("menuController")
@SessionScoped
public class MenuController implements Serializable {

    @EJB
    private service.MenuFacade ejbFacade;
    private List<Menu> items = null;
    private Menu selected;
    private String price;

    public String getPrice() {
        if (this.price == null) {
            this.price = "";
        }
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public MenuController() {
    }

    public Menu getSelected() {
        return selected;
    }

    public void setSelected(Menu selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MenuFacade getFacade() {
        return ejbFacade;
    }

    public Menu prepareCreate() {
        selected = new Menu();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MenuCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MenuUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MenuDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Menu> getItems() {
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
                    try {
                        Double p = new Double(this.price);
                        selected.setRestaurant(SessionUtil.getConnectedUser().getRestaurant());
                        selected.setPrice(p);
                        getFacade().create(selected);
                        JsfUtil.addSuccessMessage(successMessage);
                    } catch (Exception e) {
                        JsfUtil.addErrorMessage("Valuer de prix erronée");
                    } finally {
                        this.price = "";
                    }
                } else if (persistAction != PersistAction.UPDATE) {
                    getFacade().edit(selected);
                } else if (persistAction != PersistAction.DELETE) {
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

    public Menu getMenu(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Menu> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Menu> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Menu.class)
    public static class MenuControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MenuController controller = (MenuController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "menuController");
            return controller.getMenu(getKey(value));
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
            if (object instanceof Menu) {
                Menu o = (Menu) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Menu.class.getName()});
                return null;
            }
        }

    }

}
