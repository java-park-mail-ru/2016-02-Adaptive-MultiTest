package frontend.rest;

import base.AccountService;
import base.dataSets.UserDataSet;
import helpers.Context;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Sasha on 03.04.16.
 */
@Path("/scores")
public class Scores {
    @Inject
    private Context context;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTopPlayers() {
        final AccountService accountService = context.get(AccountService.class);
        final List<UserDataSet> topPlayers = accountService.getTopPlayers();
        return Response.status(Response.Status.OK).entity(topPlayers.toArray(new UserDataSet[topPlayers.size()])).build();
    }

}