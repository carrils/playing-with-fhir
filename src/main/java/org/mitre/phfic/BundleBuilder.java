package org.mitre.phfic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.r4.model.Patient;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

public class BundleBuilder {
    public static void side(String[] args){
        FhirContext contextR4 = FhirContext.forR4();
        ca.uhn.fhir.util.BundleBuilder builder = new ca.uhn.fhir.util.BundleBuilder(contextR4);

        // Make Bundle
        builder.setBundleField("type","collection")
                .setBundleField("id", UUID.randomUUID().toString())
                .setMetaField("lastUpdated", builder.newPrimitive("instant", new Date()));

        // Create entry
        IBase entry = builder.addEntry();
        // Create patient for entry "content" i guess
        Patient pt = new Patient();
        pt.setActive(true);
        pt.addIdentifier().setSystem("http://localhost:8080/reference-server/r4").setValue("128"); //pid == 128
        // +Patient in entry to Bundle
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
