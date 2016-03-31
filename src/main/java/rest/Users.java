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
import java.util.List;

/**
 * Created by a.serebrennikova
 */
@Path("/user")
public class Users {
    @Inject
    private main.Context context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        final AccountService accountService = context.get(AccountService.class);
        final List<UserDataSet> allUsers = accountService.getAllUsers();
        return Response.status(Response.Status.OK).entity(allUsers.toArray(new UserDataSet[allUsers.size()])).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(@PathParam("id") long id) {
        final AccountService accountService = context.get(AccountService.class);
        final UserDataSet user = accountService.getUser(id);
        if(user == null){
            return Response.status(Response.Status.FORBIDDEN).build();
        }else {
            String jsonString = "{ \"id\": \"" + user.getId() + "\",\"login\": \"" + user.getLogin()
                    + "\",\"email\": \"" + user.getEmail() + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserDataSet user, @Context HttpHeaders headers){
        final AccountService accountService = context.get(AccountService.class);
        long id = accountService.addUser(user);
        if(id != -1){
            String jsonString = "{ \"id\": \"" + id + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("id") long id, UserDataSet updatedUser,
                               @Context HttpHeaders headers, @Context HttpServletRequest request) {
        final AccountService accountService = context.get(AccountService.class);
        final UserDataSet user = accountService.getUser(id);
        String sessionId = request.getSession().getId();
        UserDataSet activeUser = accountService.getUserBySession(sessionId);

        if(user == null){
            return Response.status(Response.Status.FORBIDDEN).build();
        } else if (activeUser == null || id != activeUser.getId()){
            String jsonString = "{ \"status\": \"403\", \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(jsonString).build();
        }else {
            accountService.updateUser(updatedUser, id);
            String jsonString = "{ \"id\": \"" + id + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        }
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("id") long id, @Context HttpServletRequest request) {
        final AccountService accountService = context.get(AccountService.class);
        final UserDataSet user = accountService.getUser(id);
        String sessionId = request.getSession().getId();
        UserDataSet activeUser = accountService.getUserBySession(sessionId);

        if(user == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        } else if (activeUser == null || id != activeUser.getId()){
            String jsonString = "{ \"status\": \"403\", \"message\": \"Чужой юзер\" }";
            return Response.status(Response.Status.FORBIDDEN).entity(jsonString).build();
        } else {
            accountService.deleteUser(id);
            String jsonString = "{ \"id\": \"" + id + "\" }";
            return Response.status(Response.Status.OK).entity(jsonString).build();
        }
    }
}
