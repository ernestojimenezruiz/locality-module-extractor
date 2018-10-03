package uk.ac.manchester.syntactic_locality;

import java.util.HashMap;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import org.semanticweb.owlapi.util.DLExpressivityChecker;

import uk.ac.manchester.syntactic_locality.ModuleExtractor;




public class ExtractModules4AllOntologyEntities {

	
	
private OWLOntologyManager externalOntologyManager;
	
	private OWLOntology ontoToModularize;
	
	private IRI extOntoIRI;
	
	//private static final String defaultModuleIRI = "http://krono.act.uji.es/Links/ontologies/module.owl";
	private static final String defaultModuleIRI = "http://krono.act.uji.es/Links/ontologies/module_";
	
	private OWLOntology module;
		
	//private Set<String> signatureNames = new HashSet<String>();
	//private HashSet<OWLEntity> matchedSignature = new HashSet<OWLEntity>();
	//private Map<String, OWLEntity> name2entity = new HashMap<String, OWLEntity>();
	
	//private Set<OWLEntity> ontologyEntities;
	
	//private OWLDataFactory datafactory;
	
	
	private IRI physicalModuleIRI;
	
	private IRI moduleIRI;
	
	
	private ModuleExtractor extractor;
	
	
	/**
	 * 
	 */
	public ExtractModules4AllOntologyEntities(){
		
		
		long init, fin;
		init=Calendar.getInstance().getTimeInMillis();
		
		
		
		//LOAD ONTOLOGY
		extOntoIRI=IRI.create("https://raw.githubusercontent.com/ernestojimenezruiz/oaei-evaluation/master/ontologies/phenotype/2017/doid_noimports.owl");
		
		loadExternalOntology();
		

		initExtractor();
		
		Set<OWLAxiom> Axioms;
		
		//Available methods to extract a module for an entity
		//extractor.extractModuleAxiomsForEntity(entity);
		//extractor.extractModuleEntitiesForEntity(entity);
		
		//Available methods to extract a module for a set entity
		//extractor.extractModuleAxiomsForGroupSignature(signature);
		//extractor.extractModuleEntitiesForGroupSignature(signature);
		
		//Set <OWLOntology> set4Module = new HashSet<OWLOntology>();
		
		int num_modules=0;
		
		for (OWLEntity ent : ontoToModularize.getClassesInSignature(true)){
			
			System.out.println("Extracting module for: " + getEntityLabel(ent.getIRI().toString()));
			
			//Set of axioms
			Axioms = extractor.extractModuleAxiomsForEntity(ent);
			
			moduleIRI = IRI.create(defaultModuleIRI + getEntityLabel(ent.getIRI().toString()) + ".owl");
			
			//The module as OWLOntology
			module = extractor.getModuleFromAxioms(Axioms, moduleIRI);
			
			//set4Module.clear();
			//set4Module.add(module);
			
			System.out.println("\tSize module entities: " + module.getSignature().size());
			System.out.println("\tSize module axioms: " + module.getAxiomCount());
			
			//Characteristics
			//module.getAxiomCount();
			//module.getSignature().size();
			//module.getClassesInSignature().size();
			//module.getDataPropertiesInSignature().size();
			//module.getObjectPropertiesInSignature().size();
			//module.getIndividualsInSignature().size();
			
			//DLExpressivityChecker checker = new DLExpressivityChecker(set4Module);
			//System.out.println("\tExpressivity Ontology: " + checker.getDescriptionLogicName());
			
			
			//Store module as owl file
			physicalModuleIRI = IRI.create("file:/home/ernesto/LocalityExtractor/modules/" + getEntityLabel(ent.getIRI().toString()) + ".owl");
			//saveModuleToPhysicalIRI();
			
			num_modules++;
			
			if (num_modules>25)
				break;
			
			
		}
		
		fin = Calendar.getInstance().getTimeInMillis();
		System.out.println("\nTime (s): " + (double)((double)fin-(double)init)/1000.0);
		
	}
	
	
	
	private boolean loadExternalOntology() {
    	externalOntologyManager = OWLManager.createOWLOntologyManager();
    	try {
    		ontoToModularize = externalOntologyManager.loadOntology(extOntoIRI);
    		//ontologyEntities = ontoToModularize.getSignature();//.getReferencedEntities();//getAllReferencedEntitiesFromOntology();
    		
    		//datafactory = externalOntologyManager.getOWLDataFactory();
    		
    		
    		//Useful structure to get Matched signature
    		//for (OWLEntity ent: ontologyEntities) {
    			//name2entity.put(ent.toString(), ent);
    		//	name2entity.put(getEntityLabel(ent.getIRI().toString()), ent);
    		//}
    		
    		return true;
    		
    	}
    	catch (Exception e) {
    		System.err.println("Error loading ontology form URI: " + extOntoIRI.toString());
    		e.printStackTrace();
    		ontoToModularize = null;
    		return false;
    	}	
    }
	
	
    private void saveModuleToPhysicalIRI() {
    	//OWLOntologyManager ontologyModuleManager = OWLManager.createOWLOntologyManager();
    	
        try {
        	externalOntologyManager.saveOntology(module, new RDFXMLOntologyFormat(), physicalModuleIRI);
        }
        catch (Exception e) {
        	System.err.println("Error saving module\n" + e.getLocalizedMessage());
        	e.printStackTrace();
        }
        
    }
    
    
    private void initExtractor(){
		//Bottom module
		//boolean dualConcepts=false;
		//boolean dualRoles=false;
		
		boolean dualConcepts=true;
		boolean dualRoles=true;
		
		//Consider annotation
		boolean withannotation = true;
		
		//Ignore assertions
		boolean ignoreassertions = false;
		
		extractor = new ModuleExtractor(ontoToModularize, dualConcepts, dualRoles, true, withannotation, ignoreassertions);
		
    }
	
    
	private String getEntityLabel(String iriStr){
		if (iriStr.indexOf("#")>=0)
			return iriStr.split("#")[1];
		return iriStr;
	}
	
	
	
	public static void main(String[] args) {
		
		new ExtractModules4AllOntologyEntities();
		
		
	}
		
	
}
