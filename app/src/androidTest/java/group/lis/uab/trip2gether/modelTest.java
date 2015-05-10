package group.lis.uab.trip2gether;

import android.content.Context;
import android.test.InstrumentationTestCase;
import com.parse.ParseFile;
import java.util.ArrayList;
import java.util.Date;
import group.lis.uab.trip2gether.model.FriendsListAdapter;
import group.lis.uab.trip2gether.model.Notification;
import group.lis.uab.trip2gether.model.NotificationListAdapter;
import group.lis.uab.trip2gether.model.Site;
import group.lis.uab.trip2gether.model.SiteListAdapter;
import group.lis.uab.trip2gether.model.Trip;
import group.lis.uab.trip2gether.model.TripListAdapter;
import group.lis.uab.trip2gether.model.User;

public class modelTest extends InstrumentationTestCase {

    Context context;
    User user;
    Trip trip;
    Site site;
    Notification notification;

    public void setUp() throws Exception {
        super.setUp();
        context = getInstrumentation().getContext();
        assertNotNull(context);
    }

    public void test01UserObject() {
        final String mail = "pepe@gmail.com";
        final String password = "abc123";
        final String name = "Pepe";
        final String surname= "Fernandez";
        final String country = "Spain";
        final String city = "Barcelona";
        final Date birth = new Date();
        birth.setDate(12);
        birth.setMonth(07);
        birth.setYear(2011);
        final String objectId = "jashdi23";
        User pepe = new User(mail, password, name, surname, country, city, birth, objectId);
        user = pepe;

        assertEquals("pepe@gmail.com", pepe.getMail());
        assertEquals("abc123", pepe.getPassword());
        assertEquals("Pepe", pepe.getName());
        assertEquals("Fernandez", pepe.getSurname());
        assertEquals("Spain", pepe.getCountry());
        assertEquals("Barcelona", pepe.getCity());
        assertEquals(12, pepe.getDateOfBirth().getDate());
        assertEquals(07, pepe.getDateOfBirth().getMonth());
        assertEquals(2011, pepe.getDateOfBirth().getYear());
        assertEquals("jashdi23", pepe.getObjectId());
    }

    public void test02TripObject() {
        final String nombre = "Verano2015";
        final String pais = "Alemania";
        final String ciudad = "Berlin";
        final Date fechaInicio = new Date();
        fechaInicio.setDate(01);
        fechaInicio.setMonth(05);
        fechaInicio.setYear(2014);
        final Date fechaFinal = new Date();
        fechaFinal.setDate(20);
        fechaFinal.setMonth(10);
        fechaFinal.setYear(2016);
        final ParseFile imagen = null;
        Trip viaje = new Trip(nombre, pais, ciudad, fechaInicio, fechaFinal, imagen);
        trip = viaje;

        assertEquals("Verano2015", viaje.getNombre());
        assertEquals("Alemania", viaje.getPais());
        assertEquals("Berlin", viaje.getCiudad());
        assertEquals(01, viaje.getFechaInicio().getDate());
        assertEquals(05, viaje.getFechaInicio().getMonth());
        assertEquals(2014, viaje.getFechaInicio().getYear());
        assertEquals(20, viaje.getFechaFinal().getDate());
        assertEquals(10, viaje.getFechaFinal().getMonth());
        assertEquals(2016, viaje.getFechaFinal().getYear());
        assertEquals(null, viaje.getImagen());
    }

    public void test03SiteObject() {
        final String objectId = "jakshd2o73hd23";
        final String name = "Sagrada familia";
        final String descripcion = "Esto es la Sagrada familia";
        final String idViatje = "masuidh23d";
        final ParseFile imagen = null;
        final double duracion = 59;
        final double precio = 25;
        final double latitud = 26545.2151;
        final double longitud = 21875.32131;
        Site sitio = new Site(name, descripcion, imagen, idViatje, objectId, duracion, precio, latitud, longitud);
        site = sitio;

        assertEquals("jakshd2o73hd23", sitio.getId());
        assertEquals("Sagrada familia", sitio.getNombre());
        assertEquals("Esto es la Sagrada familia", sitio.getDescripcion());
        assertEquals("masuidh23d", sitio.getIdViaje());
        assertEquals(null, sitio.getImagen());
        assertEquals(59, sitio.getDuracion(),0);
        assertEquals(25, sitio.getPrecio(),0);
        assertEquals(26545.2151, sitio.getLatitud(),0);
        assertEquals(21875.32131, sitio.getLongitud(),0);
    }

    public void test04Notification() {
        final String objectId = "asdawd23d";
        final Boolean estado = false;
        final String idEmisor = "fm23p8v32";
        final String idReceptor = "23rnfd2739";
        final String idTipo = "ju23of23";
        final String tipo = "2";
        Notification notification = new Notification(objectId, estado, idEmisor, idReceptor, idTipo, tipo);
        this.notification = notification;

        assertEquals("asdawd23d", notification.getObjectId());
        assertEquals(false, notification.getEstado().booleanValue());
        assertEquals("fm23p8v32", notification.getIdEmisor());
        assertEquals("23rnfd2739", notification.getIdReceptor());
        assertEquals("ju23of23", notification.getIdTipo());
        assertEquals("2", notification.getTipo());
    }

    public void testTripListAdapter(){
        int layoutResource = R.layout.trip_list_item_row;
        ArrayList<Trip> trips = new ArrayList<Trip>();
        trips.add(trip);
        TripListAdapter adapter = new TripListAdapter(context, layoutResource, trips, user);
        assertNotNull(adapter);
        assertEquals(context, adapter.getContext());
    }

    public void testSiteListAdapter(){
        int layoutResource = R.layout.site_list_item_row;
        ArrayList<Site> sites = new ArrayList<Site>();
        sites.add(site);
        SiteListAdapter adapter = new SiteListAdapter(context, layoutResource, sites);
        assertNotNull(adapter);
        assertEquals(context, adapter.getContext());
    }

    public void testNotificationListAdapter(){
        int layoutResource = R.layout.notification_list_item_row;
        ArrayList<Notification> notifications = new ArrayList<Notification>();
        notifications.add(notification);
        NotificationListAdapter adapter = new NotificationListAdapter(context, layoutResource, notifications, user);
        assertNotNull(adapter);
        assertEquals(context, adapter.getContext());
    }

    public void testFriendsListAdapter(){
        int layoutResource = R.layout.friends_item_row;
        ArrayList<User> friends = new ArrayList<User>();
        friends.add(user);
        FriendsListAdapter adapter = new FriendsListAdapter(context, layoutResource, friends, user);
        assertNotNull(adapter);
        assertEquals(context, adapter.getContext());
    }
}