import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/risk")
public class RiskResource {

    @Inject
    EntityManager entityManager;

    @Inject
    RiskBehaviorRepository riskBehaviorRepository;

    @GET
    @Path("/behaviors")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RiskBehaviorResult> getRiskBehaviors(@QueryParam("raId") String raId, 
                                                    @QueryParam("unitName") String unitName, 
                                                    @QueryParam("stage") String stage) {
        try {
            // Call the stored procedure
            entityManager.createNativeQuery("CALL get_risk_behaviors(:raId, :unitName, :stage)")
                .setParameter("raId", raId)
                .setParameter("unitName", unitName)
                .setParameter("stage", stage)
                .executeUpdate();

            // Query the temporary table using Panache
            List<RiskBehaviorResult> results = riskBehaviorRepository.listAll();
            return results;
        } catch (Exception e) {
            // Log the error or throw a custom exception
            throw new RuntimeException("Error calling stored procedure or querying results: " + e.getMessage(), e);
        }
    }
}