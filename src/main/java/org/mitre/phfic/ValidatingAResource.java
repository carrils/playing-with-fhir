package org.mitre.phfic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.IValidationContext;
import ca.uhn.fhir.validation.IValidatorModule;
import ca.uhn.fhir.validation.ValidationResult;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.OperationOutcome;
// import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;


public class ValidatingAResource {

    // this was copied and pasted with new comments from the fhirstarters repo, example 20
    public static void icecream() {
        // Create an incomplete encounter (status is required)
        Encounter enc = new Encounter();
        enc.addIdentifier().setSystem("http://acme.org/encNums").setValue("12345");

        // Create a validator
        FhirContext contextR4 = FhirContext.forR4();
        FhirValidator validator = contextR4.newValidator();

        // Supplies structure definitions (Resource Instance Validator)
        DefaultProfileValidationSupport support = new DefaultProfileValidationSupport(contextR4);

        // Validate the resource to produce a positive or negative result
        ValidationResult result = validator.validateWithResult(enc);
        System.out.println("Success: " + result.isSuccessful());

        // Cast AND mutate result to operation outcome (why) then create an xml parser to print the outcome
        OperationOutcome outcome = (OperationOutcome) result.toOperationOutcome();
        IParser parser = contextR4.newXmlParser().setPrettyPrint(true);
        System.out.println(parser.encodeResourceToString(outcome));
    }
}
