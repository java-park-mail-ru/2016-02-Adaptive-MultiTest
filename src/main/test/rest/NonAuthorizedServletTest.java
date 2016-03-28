package rest;

/*import main.RestApplication;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import javax.servlet.http.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;*/

/**
 * Created by a.serebrennikova
 */
public class NonAuthorizedServletTest /*extends JerseyTest*/ {

    @Override
    protected Application configure() {
        final ResourceConfig config = ResourceConfig.forApplication(new RestApplication());
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpSession session = mock(HttpSession.class);
        //noinspection AnonymousInnerClassMayBeStatic
        config.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(request).to(HttpServletRequest.class);
                bind(session).to(HttpSession.class);
                when(request.getSession()).thenReturn(session);
                when(session.getId()).thenReturn("session");
            }
        });

        return config;
    }


    @Test
    public void testGetAllUsers() {
        final String actualJson = target("user").request().get(String.class);
        final String expectedJson = "[{\"email\":\"admin@admin\",\"login\":\"admin\",\"password\":\"admin\"}," +
                "{\"email\":\"guest@guest\",\"login\":\"guest\",\"password\":\"12345\"}]";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetAdminUser() {
        final String actualJson = target("user").path("0").request().get(String.class);
        final String expectedJson = "{\"email\":\"admin@admin\",\"login\":\"admin\",\"password\":\"admin\"}";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testGetNonexistentUserFail() {
        final Response actualResponse = target("user").path("100").request().get();
        assertEquals(403, actualResponse.getStatus());
    }

    @Test
    public void testSignUp() {
        final UserProfile newUser = new UserProfile("test", "test", "test@test");
        final long newUserId = UserProfile.getCurrentID();
        final String actualJson = target("user").request().post(Entity.json(newUser), String.class);
        final String expectedJson = "{ \"id\": \"" + newUserId + "\" }";
        assertEquals(expectedJson, actualJson);
    }

    @Test
    public void testSignUpUserExistsFail() {
        final UserProfile newUser = new UserProfile("admin", "123", "adm@adm");
        final Response actualResponse = target("user").request().post(Entity.json(newUser));
        assertEquals(403, actualResponse.getStatus());
    }

    @Test
    public void testSignIn() {
        final UserProfile user = new UserProfile("guest", "12345");
        final Response actualJson = target("session").request().post(Entity.json(new UserProfile("admin", "admin")));
        final String expectedJson = "{ \"id\": \"0\" }";
        assertEquals(500, actualJson);
    }

    @Test
    public void testIsAuthorized() {
        //HttpServletRequest request = mock(HttpServletRequest.class);
        //HttpSession session = mock(HttpSession.class);

       // when(request.getSession()).thenReturn(session);
        //when(session.getId()).thenReturn("session");

        final Response actualResponse = target("session").request().get();
        assertEquals(401, actualResponse.getStatus());
    }*/
}
