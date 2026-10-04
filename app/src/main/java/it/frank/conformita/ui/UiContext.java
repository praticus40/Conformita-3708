package it.frank.conformita.ui;

import it.frank.conformita.core.ApplicationFacade;

public record UiContext(ApplicationFacade facade, NavigationController navigation, MainCallbacks callbacks) {}
