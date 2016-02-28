package rest;

import main.AccountService;
import org.eclipse.jetty.server.Authentication;

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
@Path("/user")
public class Users {
    private AccountService accountService;

    public Users(AccountService accountService) {
        this.accountService = accountService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        final Collection<UserProfile> allUsers = accountService.getAllUsers();
        return Response.status(Response.Status.OK).entity(allUsers.toArray(new UserProfile[allUsers.size()])).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") long id) {
        final UserProfile user = accountService.getUser(id);
        if(user == null){
            return Response.status(Response.Status.FORBIDDEN).build();
        }else {
            return Response.status(Response.Status.OK).entity(user).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserProfile user, @Context HttpHeaders headers){
        if(accountService.addUser(user)){
            String jsonString = "{ \"id\": \"" + user.getId() + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") long id, UserProfile updatedUser,
                               @Context HttpHeaders headers, @Context HttpServletRequest request) {
        final UserProfile userToUpdate = accountService.getUser(id);
        String sessionId = request.getSession().getId();
        UserProfile activeUser = accountService.getUserBySession(sessionId);

        if(userToUpdate == null){
            return Response.status(Response.Status.FORBIDDEN).build();
        } else if (!userToUpdate.getLogin().equals(activeUser.getLogin())){
            String jsonString = "{ \"status\": \"403\", \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(jsonString).build();
        }else {
            accountService.updateUser(userToUpdate, updatedUser);
            String jsonString = "{ \"id\": \"" + id + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") long id, @Context HttpServletRequest request) {
        final UserProfile userToDelete = accountService.getUser(id);
        String sessionId = request.getSession().getId();
        UserProfile activeUser = accountService.getUserBySession(sessionId);

        if(userToDelete == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } else if (!userToDelete.getLogin().equals(activeUser.getLogin())){
            String jsonString = "{ \"status\": \"403\", \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(jsonString).build();
        } else {
            accountService.deleteUser(userToDelete);
            String jsonString = "{ \"id\": \"" + userToDelete.getId() + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        }
    }
}
