package org.where2pair.venue.home

class HomeController {

    public static final String WELCOME_MESSAGE = 'Welcome to Where2Pair!!! Your installation is working. For a list of the endpoints available, please see the documentation.'

    def homepage() {
        WELCOME_MESSAGE
    }
}
