package org.mitre.phfic;

// HAPI FHIR API
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import ca.uhn.fhir.util.BundleBuilder;

// Base HL7 FHIR
import ca.uhn.fhir.util.BundleUtil;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;

// Java API stuff
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

        // [ --- 01.27.23 --- ]
        // 'Main.java' will now hold a hapi fhir server, to interact with our tool kit.
        // After a server is successfully set up (making sure it runs, being able to upload bundle to its db,
        // and is talking to inferno (200 OKs))
        // You may spend time developing tests or switching gears and making a HAPI FHIR client (gui's cause yeee)
        // Now you have a path for writing tests, make sure to look at shaumiks test cases (and others).




    }
}