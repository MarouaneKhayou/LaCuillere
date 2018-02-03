package controler;

import bean.Annonce;
import controler.util.JsfUtil;
import controler.util.JsfUtil.PersistAction;
import service.AnnonceFacade;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.primefaces.event.SelectEvent;
import util.SessionUtil;

@Named("annonceController")
@SessionScoped
public class AnnonceController implements Serializable {

    @EJB
    private service.AnnonceFacade ejbFacade;
    private service.UserFacade userFacade;
    private List<Annonce> items = null;
    private Annonce selected;
    private boolean isDateValid = true;
    private String reductionTemplate;

    public void annulateAnnonce(Annonce annonce) {
        int res = ejbFacade.annulateAnnonce(annonce);
        if (res == 1) {
            JsfUtil.addSuccessMessage("Annonce annulée avec success ");
        } else if (res == -1) {
            JsfUtil.addSuccessMessage("Erreur veillez réessayer plus tard");
        }
    }

    public List<Annonce> getRestaurantAnnonces() {
        if (SessionUtil.getConnectedUser().getProfil().equals("R")) {
            return getFacade().getRestaurantAnnonces(SessionUtil.getConnectedUser().getRestaurant().getId());
        }
        return new ArrayList<>();
    }

    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (getFacade().IfRestaurantHasAnnonce(SessionUtil.getConnectedUser().getRestaurant(), format.format(event.getObject()))) {
            isDateValid = false;
            JsfUtil.addErrorMessage("Annonce déjà existant pour la date : " + format.format(event.getObject()));
        } else {
            isDateValid = true;
        }
        System.out.println("ha valid : " + isDateValid);
        //facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }

    public String getReductionTemplate() {
        return reductionTemplate;
    }

    public void setReductionTemplate(String reductionTemplate) {
        this.reductionTemplate = reductionTemplate;
    }

    public boolean isIsDateValid() {
        return isDateValid;
    }

    public void setIsDateValid(boolean isDateValid) {
        this.isDateValid = isDateValid;
    }

    public AnnonceController() {
    }

    public Annonce getSelected() {
        return selected;
    }

    public void setSelected(Annonce selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AnnonceFacade getFacade() {
        return ejbFacade;
    }

    public Annonce prepareCreate() {
        selected = new Annonce();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AnnonceCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AnnonceUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AnnonceDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Annonce> getItems() {
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
                    if (!isDateValid) {
                        JsfUtil.addErrorMessage("Veuillez changer la date de l'annonce");
                        isDateValid = false;
                        reductionTemplate = "";
                    } else {
                        try {
                            Integer reduction = new Integer(this.reductionTemplate);
                            if (reduction < 0 | reduction > 100) {
                                JsfUtil.addErrorMessage("Reduction doit etre une valeur numeric comprise entre 0 et 100");
                                reductionTemplate = "";
                            }
                            getFacade().addAnnonceByRestaurateur(SessionUtil.getConnectedUser(), selected.getDateAnnonce(),
                                    selected.getPhone(), selected.getMail(), reduction.intValue());
                            JsfUtil.addSuccessMessage("Annonce créé avec success");
                            isDateValid = false;
                            reductionTemplate = "";
                        } catch (Exception e) {
                            JsfUtil.addErrorMessage("Reduction doit etre une valeur numeric comprise entre 0 et 100");
                            reductionTemplate = "";
                        }
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

    public Annonce getAnnonce(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Annonce> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Annonce> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Annonce.class)
    public static class AnnonceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AnnonceController controller = (AnnonceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "annonceController");
            return controller.getAnnonce(getKey(value));
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
            if (object instanceof Annonce) {
                Annonce o = (Annonce) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Annonce.class.getName()});
                return null;
            }
        }

    }

}
