package rest;

import main.AccountService;
import org.eclipse.jetty.server.Authentication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 * Created by a.serebrennikova
 */
@Singleton
@Path("/session")
public class Sessions {
    private AccountService accountService;

    public Sessions(AccountService accountService) { this.accountService = accountService; }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticatedUser(@Context HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        if (accountService.isAuthenticated(sessionId)) {
            String jsonString = "{ \"id\": \"" + accountService.getUserBySession(sessionId).getId() + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(UserProfile user, @Context HttpServletRequest request, @Context HttpHeaders headers) {
        UserProfile actualUser = accountService.getUserByLogin(user.getLogin());
        if (actualUser != null && accountService.isValidUser(user)) {
            String sessionId = request.getSession().getId();
            accountService.addSession(sessionId, actualUser);
            String jsonString = "{ \"id\": \"" + actualUser.getId() + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response logOut(@Context HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        accountService.deleteSession(sessionId);
        return Response.status(Response.Status.OK).build();
    }

}
