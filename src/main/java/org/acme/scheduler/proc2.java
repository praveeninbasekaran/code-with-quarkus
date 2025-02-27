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
    RiskAssessmentRepository riskAssessmentRepository;

    @GET
    @Path("/copy-assessment")
    @Produces(MediaType.APPLICATION_JSON)
    public List<RiskAssessmentResult> copyRiskAssessment(@QueryParam("newRaId") String newRaId, 
                                                        @QueryParam("oldRaId") String oldRaId, 
                                                        @QueryParam("assessmentUnits") String assessmentUnits, 
                                                        @QueryParam("stage") String stage) {
        try {
            // Call the stored procedure
            entityManager.createNativeQuery("CALL strap_sit.copy_risk_assessment(:newRaId, :oldRaId, :assessmentUnits, :stage)")
                .setParameter("newRaId", newRaId)
                .setParameter("oldRaId", oldRaId)
                .setParameter("assessmentUnits", assessmentUnits)
                .setParameter("stage", stage)
                .executeUpdate();

            // Query the temporary table using Panache
            List<RiskAssessmentResult> results = riskAssessmentRepository.listAll();
            return results;
        } catch (Exception e) {
            // Log the error or throw a custom exception
            throw new RuntimeException("Error calling stored procedure or querying results: " + e.getMessage(), e);
        }
    }
}