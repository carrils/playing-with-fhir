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

public class Main {

    public static void main(String[] args) throws IOException {
        FhirContext contextR4 = FhirContext.forR4();

        // [ --- Bundle Builder --- ]
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
        pt.addIdentifier().setSystem("http://localhost:8080/reference-server/r4").setValue("128"); //pid == 128
        builder.addToEntry(entry, "resource", pt);

        //Initiate a new JSON Parser
        IParser parser = contextR4.newJsonParser();
        parser.setPrettyPrint(true);
        IBaseBundle outcome = builder.getBundle();

        //print the bundle in json to sys.out
        //System.out.println(parser.encodeResourceToString(outcome));

        //print this to a json file
        File file = new File("bundle-test.json");


        try {
            PrintWriter print = new PrintWriter(file);
            print.println(parser.encodeResourceToString(outcome));
            print.close();

            //print.write(parser.encodeResourceToString(outcome));
            //System.out.println(parser.encodeResourceToString(outcome));
            //System.out.println(parser.encodeResourceToString(outcome).toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}