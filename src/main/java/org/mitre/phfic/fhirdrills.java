package org.mitre.phfic;

/*
    !!IMPORTANT!!
    This code is not my own and was gratefully borrowed from https://fhir-drills.github.io/fhir-api.html#api-find-patient
 */

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.*;

import java.io.IOException;


/*
    Demonstrates uploading a patient in hapi fhir r4
 */
public class fhirdrills {
    public static void main(String[] args) throws IOException {
        // context - create this once, as it's an expensive operation
        // see http://hapifhir.io/doc_intro.html
        FhirContext ctx = FhirContext.forR4();
        String serverBaseUrl = "http://hapi.fhir.org/baseR4";

        Patient pat = new Patient();

        pat.addName().setUse(HumanName.NameUse.OFFICIAL)
                .addPrefix("Mr").setFamily("Fhirman").addGiven("Sam");
        pat.addIdentifier()
                .setSystem("http://ns.electronichealth.net.au/id/hi/ihi/1.0")
                .setValue("8003608166690503");

        // increase timeouts since the server might be powered down
        // see http://hapifhir.io/doc_rest_client_http_config.html
        ctx.getRestfulClientFactory().setConnectTimeout(60 * 1000);
        ctx.getRestfulClientFactory().setSocketTimeout(60 * 1000);

        // create the RESTful client to work with our FHIR server
        // see http://hapifhir.io/doc_rest_client.html
        IGenericClient client = ctx.newRestfulGenericClient(serverBaseUrl);

        System.out.println("Press Enter to send the patient to server: " + serverBaseUrl);
        System.in.read();

        try {
            // send our resource up - result will be stored in 'outcome'
            // see http://hapifhir.io/doc_rest_client.html#Create_-_Type
            MethodOutcome outcome = client.create()
                    .resource(pat)
                    .prettyPrint()
                    .encodedJson()
                    .execute();

            IdType id = (IdType) outcome.getId();
            System.out.println("Resource is available at: " + id.getValue());

            IParser jsonParser = ctx.newJsonParser().setPrettyPrint(true);
            Patient receivedPatient = (Patient) outcome.getResource();
            System.out.println("This is what we sent up: \n"
                    + jsonParser.encodeResourceToString(pat)
                    + "\n\nThis is what we received: \n"
                    + jsonParser.encodeResourceToString(receivedPatient));
        } catch (DataFormatException e) {
            System.out.println("An error occurred trying to upload:");
            e.printStackTrace();
        }

        System.out.println("Press Enter to end.");
        System.in.read();
    }
}