package org.mitre.phfic;

// HAPI FHIR API
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.CapturingInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.util.BundleBuilder;

// Base HL7 FHIR
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;

import org.json.simple.JSONObject;
import java.io.*;
import java.util.*;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;

//logback output levels (asc precedence): TRACE, DEBUG, INFO, WARN, ERROR

/*
    ToDo:
    Create example resources in hapi fhir for
        - US Core Patient [-]
        - US Core Condition [ ]
        - US Core Encounter [ ]
        - US Core Location [ ]
        - US Core QuestionResponse [ ]
        - US Core CarePlan [ ]
        - Specimen Resource [ ]

    Figure out which profile refers to which and double check you built the above correctly [ ]

    Upload them to hapi fhir test server [ ]

    Retrieve all with a patient?id=<whatever>$everything server interaction [ ]
 */


public class Main {
    public static void main(String[] args) throws IOException {
        String serverBase = "http://hapi.fhir.org/baseR4"; // the server base url for your server
        FhirContext contextR4 = FhirContext.forR4();

        // create a client factory
        IRestfulClientFactory clientFactory = contextR4.getRestfulClientFactory();
        // increase timeouts since the server might be powered down
        clientFactory.setServerValidationMode(ServerValidationModeEnum.NEVER);
        clientFactory.setConnectTimeout(60 * 1000);
        clientFactory.setSocketTimeout(60 * 1000);
        // capturing interceptor
        CapturingInterceptor capturer = new CapturingInterceptor();
        // create a generic client
        IGenericClient client = contextR4.newRestfulGenericClient(serverBase);
        // register the capturing interceptor
        client.registerInterceptor(capturer);


        // [PATIENT RESOURCE]
        Patient pat = new Patient();
        HumanName name = pat.addName().setFamily("Aquato").addGiven("Razputin").addGiven("E");
        Identifier identifier = pat.addIdentifier().setSystem("MEDSS").setValue("45D783B1");
        ContactPoint telecom = pat.addTelecom().setSystem(ContactPointSystem.PHONE).setUse(ContactPointUse.HOME).setValue("488 715-9846");
        pat.setGender(Enumerations.AdministrativeGender.MALE).setBirthDate(new Date(92, 8, 12));
        Address address = pat.addAddress().setUse(Address.AddressUse.HOME).addLine("101 main str").setCity("Minneapolis").setState("MN").setPostalCode("55403");
        Communication communication = (Communication) new Communication().setLanguage("en");
        // StringType is just a FHIR String data type, proprietary to the HAPI FHIR API. Race extension
        pat.addExtension("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race", new StringType("Asian"));
        // Ethnicity extension
        pat.addExtension("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity", new StringType("Not Hispanic or Latino"));
        // Gender Identity extension
        pat.addExtension("http://hl7.org/fhir/us/core/STU5.0.1/StructureDefinition-us-core-genderIdentity.html", new StringType("M"));
        // Birthsex Extension
        pat.addExtension("http://hl7.org/fhir/us/core/STU5.0.1/StructureDefinition-us-core-birthsex.html", new StringType("M"));

        // [ENCODE]
        IParser jsonParser = contextR4.newJsonParser().setPrettyPrint(true);
        String patEncoded = jsonParser.encodeResourceToString(pat);
        System.out.println("[json patient]\n" + patEncoded + "\n");

        // [PRINT]
        try {
            Writer outf = new FileWriter("generated-patient.json");
            outf.write(patEncoded);
            outf.close();
        } catch (Exception e) {
            System.out.println("Well now look what you've done. How do we explain this to corporate.");
            e.getStackTrace();
        }


         // [UPLOAD (to the serverbase url)]
        System.out.println("Press Enter to send the patient to server: " + serverBase);
        System.in.read();

        try {
            // send resource up to the server - the result is stored in outcome hence the weird notation
            MethodOutcome outcome = client.create()
                    .resource(pat)
                    .prettyPrint()
                    .encodedJson()
                    .execute();
            // Retrieve id from outcome
            IdType id = (IdType) outcome.getId();
            System.out.println("Resource is available at: " + id.getValue());

            // System.out.println("[OUTCOME]\n" + outcome.toString());

            // Retrieve patient from outcome
            Patient receivedPatient = (Patient) outcome.getResource();
            System.out.println("This is what we sent up: \n" + jsonParser.encodeResourceToString(pat) + "\n");
            System.out.println("And this is what we received back: \n" + jsonParser.encodeResourceToString(receivedPatient));
        } catch (DataFormatException e) {
            System.out.println("An error occurred trying to upload:");
            e.printStackTrace();
        }


        //[SEARCH]
        //  Sam Spades identifier values:
        //  "system": "http://www.health.state.mn.us/diseases/reportable/medss/",
        //  "value": "45D783B1"
        Bundle samSpade = client.search().forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().systemAndCode("http://www.health.state.mn.us/diseases/reportable/medss/", "45D783B1"))
                .returnBundle(Bundle.class)
                .execute();

        try {
            Writer outf = new FileWriter("sam-spade.json");
            outf.write(jsonParser.encodeResourceToString(samSpade));
            outf.close();
        } catch (Exception e) {
            System.out.println("Well, now look what you've done.\n");
            e.getStackTrace();
        }
        System.out.println("sir samuel spade:\n" + jsonParser.encodeResourceToString(samSpade));

        // most of the functionality of capturer is delegated to the subroutines of the "getLastRequest" and "getLastResponse" methods
        System.out.println("[Capturer output]:\n" + "\tRequest Headers: \n \t" + capturer.getLastRequest().getAllHeaders());
        System.out.println("\n \tResponse Headers:\n \t" + capturer.getLastResponse().getAllHeaders());

        //[VALIDATOR]
        //FhirValidator validator = contextR4.newValidator();

        // this object supplies the structure definitions
        //DefaultProfileValidationSupport structures = new DefaultProfileValidationSupport(contextR4);

        //ValidationSupportContext
        //FhirInstanceValidator fiv = new FhirInstanceValidator(structures);
        //validator.registerValidatorModule(module);
        // get the status field from the encounter resource (that is obviously the patients)
        //ValidationResult result = validator.validateWithResult(jsonParser.encodeResourceToString(samSpade));

        // The result object now contains the validation results
        //for (SingleValidationMessage next : result.getMessages()) {
        //   System.out.println(next.getLocationString() + " " + next.getMessage());
        //}
    }
}