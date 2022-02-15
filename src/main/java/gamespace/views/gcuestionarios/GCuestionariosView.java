package gamespace.views.gcuestionarios;

import com.vaadin.collaborationengine.CollaborationAvatarGroup;
import com.vaadin.collaborationengine.CollaborationBinder;
import com.vaadin.collaborationengine.UserInfo;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import gamespace.data.entity.Cuestionarios;
import gamespace.data.service.CuestionariosService;
import gamespace.views.MainLayout;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("GCuestionarios")
@Route(value = "GCuestionarios/:cuestionariosID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("admin")
public class GCuestionariosView extends Div implements BeforeEnterObserver {

    private final String CUESTIONARIOS_ID = "cuestionariosID";
    private final String CUESTIONARIOS_EDIT_ROUTE_TEMPLATE = "GCuestionarios/%s/edit";

    private Grid<Cuestionarios> grid = new Grid<>(Cuestionarios.class, false);

    CollaborationAvatarGroup avatarGroup;

    private TextField cuestionario;
    private TextField juego;
    private TextField descripcion;
    private DatePicker fecha;
    private TextField usuario;
    private TextField criterio1;
    private TextField promedio1;
    private TextField criterio2;
    private TextField promedio2;
    private TextField criterio3;
    private TextField proedio3;
    private TextField criterio4;
    private TextField promedio4;
    private TextField criterio5;
    private TextField promedio5;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private CollaborationBinder<Cuestionarios> binder;

    private Cuestionarios cuestionarios;

    private CuestionariosService cuestionariosService;

    public GCuestionariosView(@Autowired CuestionariosService cuestionariosService) {
        this.cuestionariosService = cuestionariosService;
        addClassNames("g-cuestionarios-view", "flex", "flex-col", "h-full");

        // UserInfo is used by Collaboration Engine and is used to share details
        // of users to each other to able collaboration. Replace this with
        // information about the actual user that is logged, providing a user
        // identifier, and the user's real name. You can also provide the users
        // avatar by passing an url to the image as a third parameter, or by
        // configuring an `ImageProvider` to `avatarGroup`.
        UserInfo userInfo = new UserInfo(UUID.randomUUID().toString(), "Steve Lange");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        avatarGroup = new CollaborationAvatarGroup(userInfo, null);
        avatarGroup.getStyle().set("visibility", "hidden");

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("cuestionario").setAutoWidth(true);
        grid.addColumn("juego").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn("fecha").setAutoWidth(true);
        grid.addColumn("usuario").setAutoWidth(true);
        grid.addColumn("criterio1").setAutoWidth(true);
        grid.addColumn("promedio1").setAutoWidth(true);
        grid.addColumn("criterio2").setAutoWidth(true);
        grid.addColumn("promedio2").setAutoWidth(true);
        grid.addColumn("criterio3").setAutoWidth(true);
        grid.addColumn("proedio3").setAutoWidth(true);
        grid.addColumn("criterio4").setAutoWidth(true);
        grid.addColumn("promedio4").setAutoWidth(true);
        grid.addColumn("criterio5").setAutoWidth(true);
        grid.addColumn("promedio5").setAutoWidth(true);
        grid.setItems(query -> cuestionariosService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(CUESTIONARIOS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(GCuestionariosView.class);
            }
        });

        // Configure Form
        binder = new CollaborationBinder<>(Cuestionarios.class, userInfo);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(cuestionario, String.class).withConverter(new StringToUuidConverter("Invalid UUID"))
                .bind("cuestionario");
        binder.forField(promedio1, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("promedio1");
        binder.forField(promedio2, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("promedio2");
        binder.forField(proedio3, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("proedio3");
        binder.forField(promedio4, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("promedio4");
        binder.forField(promedio5, String.class).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("promedio5");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.cuestionarios == null) {
                    this.cuestionarios = new Cuestionarios();
                }
                binder.writeBean(this.cuestionarios);

                cuestionariosService.update(this.cuestionarios);
                clearForm();
                refreshGrid();
                Notification.show("Cuestionarios details stored.");
                UI.getCurrent().navigate(GCuestionariosView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the cuestionarios details.");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> cuestionariosId = event.getRouteParameters().get(CUESTIONARIOS_ID).map(UUID::fromString);
        if (cuestionariosId.isPresent()) {
            Optional<Cuestionarios> cuestionariosFromBackend = cuestionariosService.get(cuestionariosId.get());
            if (cuestionariosFromBackend.isPresent()) {
                populateForm(cuestionariosFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested cuestionarios was not found, ID = %d", cuestionariosId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(GCuestionariosView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        cuestionario = new TextField("Cuestionario");
        juego = new TextField("Juego");
        descripcion = new TextField("Descripcion");
        fecha = new DatePicker("Fecha");
        usuario = new TextField("Usuario");
        criterio1 = new TextField("Criterio1");
        promedio1 = new TextField("Promedio1");
        criterio2 = new TextField("Criterio2");
        promedio2 = new TextField("Promedio2");
        criterio3 = new TextField("Criterio3");
        proedio3 = new TextField("Proedio3");
        criterio4 = new TextField("Criterio4");
        promedio4 = new TextField("Promedio4");
        criterio5 = new TextField("Criterio5");
        promedio5 = new TextField("Promedio5");
        Component[] fields = new Component[]{cuestionario, juego, descripcion, fecha, usuario, criterio1, promedio1,
                criterio2, promedio2, criterio3, proedio3, criterio4, promedio4, criterio5, promedio5};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(avatarGroup, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Cuestionarios value) {
        this.cuestionarios = value;
        String topic = null;
        if (this.cuestionarios != null && this.cuestionarios.getId() != null) {
            topic = "cuestionarios/" + this.cuestionarios.getId();
            avatarGroup.getStyle().set("visibility", "visible");
        } else {
            avatarGroup.getStyle().set("visibility", "hidden");
        }
        binder.setTopic(topic, () -> this.cuestionarios);
        avatarGroup.setTopic(topic);

    }
}