package uk.ac.manchester.syntactic_locality;

import java.util.Calendar;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor;
import uk.ac.manchester.syntactic_locality.OntologyModuleExtractor.TYPEMODULE;


 
public class TestNewModuleExtractor {
		
	private OWLOntologyManager ontologyManager;
	
	private OWLOntology ontoToModularize;
	
	private IRI ontoToModularizeIRI;
	
	//private static final String defaultModuleIRI = "http://krono.act.uji.es/Links/ontologies/module.owl";
	private static final String defaultModuleIRI = "http://krono.act.uji.es/Links/ontologies/module_";
		
	
	private IRI physicalModuleIRI;
	
	private String moduleIRIstr;
	
	
	private OntologyModuleExtractor extractor;
	
	
	/**
	 * 
	 */
	public TestNewModuleExtractor() throws Exception{
		
		long init, fin;
		
		
		//LOAD ONTOLOGY		
		ontoToModularizeIRI=IRI.create("file:/home/ernesto/ontologies/FBbt_XP.owl");
		ontologyManager = OWLManager.createOWLOntologyManager();    	
    	ontoToModularize = ontologyManager.loadOntology(ontoToModularizeIRI);
    	    	
    	System.out.println("Ontology Axioms: " + ontoToModularize.getAxiomCount());
    	/*System.out.println("Ontology Logical Axioms: " + ontoToModularize.getLogicalAxiomCount());
    	System.out.println("Ontology TBOX Axioms: " + ontoToModularize.getTBoxAxioms(true).size());
    	System.out.println("Ontology RBOX Axioms: " + ontoToModularize.getRBoxAxioms(true).size());
    	System.out.println("Ontology ABOX Axioms: " + ontoToModularize.getABoxAxioms(true).size());*/
		
    	
    	init=Calendar.getInstance().getTimeInMillis();
    	
		
		//INIT EXTRACTOR
		//OWLOntology ontology, boolean considerImportsClosure, boolean considerEntityAnnotations, boolean ignoreAssertions, boolean useOPtimization
		extractor = new OntologyModuleExtractor(ontoToModularize, true, true, false, false);
		//extractor = new OntologyModuleExtractor(ontoToModularize.getAxioms(), true, false, true);
		
		
		int num_modules=0;
		
		for (OWLEntity ent : ontoToModularize.getClassesInSignature(true)){
			
			System.out.println("Extracting module for: " + getEntityLabel(ent.getIRI().toString()));

			//Extract module
			extractor.extractModule4Entity(ent, TYPEMODULE.BOTTOM_LOCALITY);
			//extractor.extractModule4Entity(ent, TYPEMODULE.TOP_LOCALITY);
			//extractor.extractModule4Entity(ent, TYPEMODULE.BOTTOM_TOP_LOCALITY); //Star with only 2 iterations
			//extractor.extractModule4Entity(ent, TYPEMODULE.STAR); //Start module
			
			
			System.out.println("\tSize module entities: " + extractor.getModuleEntities().size());
			System.out.println("\tSize module axioms: " + extractor.getModuleAxioms().size());
			
			moduleIRIstr = defaultModuleIRI + getEntityLabel(ent.getIRI().toString()) + ".owl";
			
			
			
			
			
			//Characteristics
			//module = extractor.getModuleOntology(moduleIRIstr)
			//module.getAxiomCount();
			//module.getSignature().size();
			//module.getClassesInSignature().size();
			//module.getDataPropertiesInSignature().size();
			//module.getObjectPropertiesInSignature().size();
			//module.getIndividualsInSignature().size();
			
			//STORE MODULE
			/*
			physicalModuleIRI = IRI.create("file:/home/ernesto/LocalityExtractor/modules/" + getEntityLabel(ent.getIRI().toString()) + ".owl");
						
			ontologyManager.saveOntology(
					extractor.getModuleOntology(moduleIRIstr), 
					new RDFXMLOntologyFormat(), 
					physicalModuleIRI);
			*/
			
			num_modules++;
			
			if (num_modules>25)
				break;
			
			
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		System.out.println("\nTime (ignoring ontology loading) (s): " + (double)((double)fin-(double)init)/1000.0);
		
	}
	
	
	
	
  
	
    
	private String getEntityLabel(String iriStr){
		if (iriStr.indexOf("#")>=0)
			return iriStr.split("#")[1];
		return iriStr;
	}
	
	
	
	public static void main(String[] args) {
		
		try{
			new TestNewModuleExtractor();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
		
	
}

