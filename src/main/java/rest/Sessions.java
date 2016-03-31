package rest;

import base.AccountService;
import base.dataSets.UserDataSet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by a.serebrennikova
 */
@Path("/session")
public class Sessions {
    @Inject
    private main.Context context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthenticatedUser(@Context HttpServletRequest request) {
        final AccountService accountService = context.get(AccountService.class);
        final String sessionId = request.getSession().getId();
        if (accountService.isAuthenticated(sessionId)) {
            final String jsonString = "{ \"id\": \"" + accountService.getUserBySession(sessionId).getId() + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(UserDataSet user, @Context HttpServletRequest request, @Context HttpHeaders headers) {
        final AccountService accountService = context.get(AccountService.class);
        final UserDataSet actualUser = accountService.getUserByLogin(user.getLogin());
        if (actualUser != null && accountService.isValidUser(user)) {
            final String sessionId = request.getSession().getId();
            accountService.addSession(sessionId, actualUser);
            final String jsonString = "{ \"id\": \"" + actualUser.getId() + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response logOut(@Context HttpServletRequest request) {
        final AccountService accountService = context.get(AccountService.class);
        final String sessionId = request.getSession().getId();
        accountService.deleteSession(sessionId);
        final String jsonString = "{}";
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }

}
