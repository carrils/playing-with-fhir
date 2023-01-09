package org.mitre.phfic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.util.ArrayList;
import java.util.List;

public class ClientAttempt {
    public static void alsoNotMain() {
        FhirContext contextR4 = FhirContext.forR4();

        // Setting misc. client settings
        contextR4.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        contextR4.getRestfulClientFactory().setConnectTimeout(20 * 1000);
        contextR4.getRestfulClientFactory().setSocketTimeout(60 * 1000);
        LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
        loggingInterceptor.setLogRequestSummary(true);
        loggingInterceptor.setLogRequestBody(true);
        loggingInterceptor.setLogRequestHeaders(true);

        IRestfulClientFactory clientFactory = contextR4.getRestfulClientFactory(); // Create a client factory so that it can be configured
        String serverBase = "http://hapi.fhir.org/baseR4"; // Using the Inferno Reference Server "Base Server URL" FHIR endpoint
        IGenericClient client = contextR4.newRestfulGenericClient(serverBase); // Create a generic (fluent) client
        client.registerInterceptor(loggingInterceptor); // Register the interceptors for logging and bearer token auth

        List<IBaseResource> patients = new ArrayList<>();

        // Perform a "search"
        Bundle smith_bundle = client.search().forResource(Patient.class).where(Patient.NAME.matches().value("smith")).returnBundle(Bundle.class).execute();
        patients.addAll(BundleUtil.toListOfResources(contextR4, smith_bundle)); // add the returned bundle to a List data structure for storage
        System.out.println("Found " + patients.size() + " patients with last name smith"); // Print search result count
    }
}
