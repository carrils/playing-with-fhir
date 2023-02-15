package org.mitre.phfic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.util.BundleBuilder;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//logback output levels (asc precedence): TRACE, DEBUG, INFO, WARN, ERROR

public class Main {
    public static void main(String[] args) {
        String serverBase = "http://hapi.fhir.org/baseR4"; // the server base url for your server
        FhirContext contextR4 = FhirContext.forR4();

        // create a client factory for the above settings
        IRestfulClientFactory clientFactory = contextR4.getRestfulClientFactory();

        // set client settings
        clientFactory.setServerValidationMode(ServerValidationModeEnum.NEVER);
        clientFactory.setConnectTimeout(20 * 1000);
        clientFactory.setSocketTimeout(60 * 1000);

        // create logging interceptor
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLogRequestSummary(true);
        loggingInterceptor.setLogRequestBody(true);
        loggingInterceptor.setLogRequestHeaders(true);

        // create a generic client
        IGenericClient client = contextR4.newRestfulGenericClient(serverBase);

        // register the logging interceptor
        client.registerInterceptor(loggingInterceptor);

        List<IBaseResource> patients = new ArrayList<>(); // a container for the results of the search

        // Perform the search. client.search() returns a bundle hence the wierd notation...
        Bundle smith_bundle = client.search()
                .forResource(Patient.class)
                .where(Patient.NAME.matches().value("smith"))
                .returnBundle(Bundle.class)
                .execute();

        // print the results of the search
        // System.out.println("smith_bundle: \n" + smith_bundle); // org.hl7.fhir.r4.model.Bundle@78e16155

        // add the returned bundle to a List data structure for storage
        patients.addAll(BundleUtil.toListOfResources(contextR4, smith_bundle));
        // System.out.println("'patients' List: \n" + patients); // [org.hl7.fhir.r4.model.Patient@54a3ab8f, org.hl7.fhir.r4.model.Patient@1968a49c, ... ]

        // Print search result count
        System.out.println("Found " + patients.size() + " patients with last name smith");


    }
}