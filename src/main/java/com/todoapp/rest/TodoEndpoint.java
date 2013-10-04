package com.todoapp.rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import com.todoapp.model.Todo;

/**
 * 
 */
@Stateless
@Path("/todos")
public class TodoEndpoint
{
   @PersistenceContext(unitName = "forge-default")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Todo entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(TodoEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Todo entity = em.find(Todo.class, id);
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      em.remove(entity);
      return Response.noContent().build();
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces("application/json")
   public Response findById(@PathParam("id") Long id)
   {
      TypedQuery<Todo> findByIdQuery = em.createQuery("SELECT DISTINCT t FROM Todo t WHERE t.id = :entityId ORDER BY t.id", Todo.class);
      findByIdQuery.setParameter("entityId", id);
      Todo entity = findByIdQuery.getSingleResult();
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/json")
   public List<Todo> listAll()
   {
      final List<Todo> results = em.createQuery("SELECT DISTINCT t FROM Todo t ORDER BY t.id", Todo.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Todo entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}