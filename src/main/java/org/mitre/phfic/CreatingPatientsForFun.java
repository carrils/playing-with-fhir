package org.mitre.phfic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;

public class CreatingPatientsForFun {
    public static void notmain(String[] args) {
        FhirContext contextR4 = FhirContext.forR4();

        // The following is an example of creating a Patient resource.
        Patient patient = new Patient();
        patient.addName().setFamily("Simpson").addGiven("Homer").addGiven("J");
        patient.addIdentifier().setSystem("http://acme.org/MRNs").setValue("MRN001");// Add an MRN (a patient identifier)
        // phone number
        patient.addTelecom().setUse(ContactPoint.ContactPointUse.HOME).setSystem(ContactPoint.ContactPointSystem.PHONE).setValue("1 (416) 340-4800");
        patient.setGender(Enumerations.AdministrativeGender.MALE);
        // We can now use a parser to encode this resource into a string.
        String encoded = contextR4.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
        System.out.println(encoded);
        // Convert the pt. to xml
        IParser xmlParser = contextR4.newXmlParser().setPrettyPrint(true);
        encoded = xmlParser.encodeResourceToString(patient);
        System.out.println(encoded);
    }
}
