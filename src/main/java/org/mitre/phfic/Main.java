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
import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
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

/*
    ToDo:
    Create example resources in hapi fhir for
        - US Core Patient [ ]
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
    public static void main(String[] args) {
        String serverBase = "http://hapi.fhir.org/baseR4"; // the server base url for your server
        FhirContext contextR4 = FhirContext.forR4();

        // create a client factory
        IRestfulClientFactory clientFactory = contextR4.getRestfulClientFactory();
        // increase timeouts since the server might be powered down
        clientFactory.setServerValidationMode(ServerValidationModeEnum.NEVER);
        clientFactory.setConnectTimeout(60 * 1000);
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

        // create the patient as close as possible to us core then manually conform it.
        // upload that. after the patient resource you will have to try and create a resource for every profile
        // that is listed in the dictionary, modify it to fit us core, and upload it to hapi fhir referring the og patient.
        // they will all reference us core patient as fhir r4 does the same, so in theory
        // once we create each resource, and point it at the patient we created, we should be
        // able to pull it via a $everything server interaction for simplicity, on the patient?id=<whatever>$everything

        Patient pat = new Patient();
        HumanName name = pat.addName().setFamily("Aquato").addGiven("Razputin").addGiven("E");
        Identifier identifier = pat.addIdentifier().setSystem("MEDSS").setValue("45D783B1");

        ContactPoint contact = pat.addTelecom();
        contact.setUse(ContactPointUse.HOME);


    }
}