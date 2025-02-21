import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity(name = "risk_details_table")
public class RiskDetails extends PanacheEntityBase {
    @Id
    @Column(name = "ra_id")
    public String raId;

    @Column(name = "risk_behaviour")
    public String riskBehaviour;

    @Column(name = "unit_name")
    public String unitName;

    @Column(name = "assessment_stage")
    public String assessmentStage;

    @Column(name = "meta_version_number")
    public Double metaVersionNumber;

    @Column(name = "value", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String value; // Store as String

    @Column(name = "risk_assessment_details_id")
    public Long riskAssessmentDetailsId;

    @Column(name = "stage")
    public String stage;

    @Column(name = "validated")
    public Boolean validated;

    // Getter and setter for value
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    // Method to parse JSON string into JsonNode
    public com.fasterxml.jackson.databind.JsonNode parseValue() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readTree(value);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON value: " + e.getMessage(), e);
        }
    }
}