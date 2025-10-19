package com.tablehub.thbackend.ui.layout;

import com.tablehub.thbackend.ui.view.RestaurantAdminView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("TableHub CMS");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);
        addToNavbar(new DrawerToggle(), logo);
    }

    private void createDrawer() {
        Tabs tabs = new Tabs(
                createTab(VaadinIcon.BUILDING, "Restaurants", RestaurantAdminView.class)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        addToDrawer(tabs);
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class<? extends Component> navTarget) {
        Icon icon = viewIcon.create();
        icon.getStyle().set("margin-inline-end", "var(--lumo-space-m)");

        RouterLink link = new RouterLink(viewName, navTarget);
        return new Tab(icon, link);
    }
}
