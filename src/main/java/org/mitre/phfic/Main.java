package org.mitre.phfic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.util.BundleBuilder;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.ContactPoint.*;

public class Main {
    public static void main(String[] args) {
        FhirContext contextR4 = FhirContext.forR4();

        // The following is an example of creating a Patient resource.
		Patient patient = new Patient();
		patient.addName().setFamily("Simpson").addGiven("Homer").addGiven("J");
		patient.addIdentifier().setSystem("http://acme.org/MRNs").setValue("MRN001");// Add an MRN (a patient identifier)
        // phone number
		patient.addTelecom().setUse(ContactPointUse.HOME).setSystem(ContactPointSystem.PHONE).setValue("1 (416) 340-4800");
		patient.setGender(Enumerations.AdministrativeGender.MALE);
        // We can now use a parser to encode this resource into a string.
        String encoded = contextR4.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
        System.out.println(encoded);
        // Convert the pt. to xml
        IParser xmlParser = contextR4.newXmlParser().setPrettyPrint(true);
        encoded = xmlParser.encodeResourceToString(patient);
        System.out.println(encoded);

        // Setting misc. client settings
        contextR4.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        contextR4.getRestfulClientFactory().setConnectTimeout(20 * 1000);
        contextR4.getRestfulClientFactory().setSocketTimeout(60 * 1000);
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLogRequestSummary(true);
        loggingInterceptor.setLogRequestBody(true);
        loggingInterceptor.setLogRequestHeaders(true);

        // Create a client factory so that it can be configured
        IRestfulClientFactory clientFactory = contextR4.getRestfulClientFactory();

        // Using the Inferno Reference Server "Base Server URL" FHIR endpoint
        String serverBase = "http://hapi.fhir.org/baseR4";

        // Create a generic (fluent) client
        IGenericClient client = contextR4.newRestfulGenericClient(serverBase);

        //register the interceptors for logging and bearer token auth
        client.registerInterceptor(loggingInterceptor);

        List<IBaseResource> patients = new ArrayList<>();

        // Perform a result with lots of results and extract the first page
        Bundle smith_bundle = client.search().forResource(Patient.class).where(Patient.NAME.matches().value("smith")).returnBundle(Bundle.class).execute();
        patients.addAll(BundleUtil.toListOfResources(contextR4, smith_bundle));

        // Print search result count
        System.out.println("Found " + patients.size() + " patients with last name smith");




        // Bundle Builder
        BundleBuilder builder = new BundleBuilder(contextR4);
        //set bundle type and give it an id so that you can reference it here in the source code
        builder.setBundleField("type","collection")
                .setBundleField("id", UUID.randomUUID().toString())
                .setMetaField("lastUpdated", builder.newPrimitive("instant", new Date()));

        // Create Bundle entry
        IBase entry = builder.addEntry();

        // Create a Patient to create as part of the bundle
        Patient pt = new Patient();
        pt.setActive(true);
        pt.addIdentifier().setSystem("http://localhost:8080/reference-server/r4").setValue("bar");
        builder.addToEntry(entry, "resource", pt);

        //Initiate a new JSON Parser
        IParser parser = contextR4.newJsonParser();
        parser.setPrettyPrint(true);

        IBaseBundle outcome = builder.getBundle();

        //and print a bundle in json yay
        System.out.println(parser.encodeResourceToString(outcome));



    }
}