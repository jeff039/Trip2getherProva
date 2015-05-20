package group.lis.uab.trip2gether.model;

import com.parse.Parse;

public class Application extends android.app.Application {

    /**
     * Method onCreate. Es crida al iniciar la aplicació
     */
    public void onCreate() {
        //Inicialitzacióde la base de dates externa
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "lWa3WC7l4Y7DCZx5qChjsANfUwjclOOlIITaok2Q", "QmI8vQiaF19KvK1S2XrfozsqZ9yjE42ymeojuugx");
    }
}