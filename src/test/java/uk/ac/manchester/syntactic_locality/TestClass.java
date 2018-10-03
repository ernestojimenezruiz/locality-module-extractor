package uk.ac.manchester.syntactic_locality;

import java.util.Calendar;
import java.util.*;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.syntactic_locality.ModuleExtractor;
import uk.ac.manchester.syntactic_locality.ModuleExtractorManager;

public class TestClass {
	
	private OWLOntologyManager onto2modularizeManager;
	
	private OWLDataFactory datafactory;
	
	private OWLOntology onto2modularize;
	
	private IRI onto2modularizeIRI;
	
	private static final String defaultModuleIRI = "http://krono.act.uji.es/Links/ontologies/module.owl";
	
	
	
	private ModuleExtractor extractor;
	private ModuleExtractorManager extractorManager;
	
	
	//Consider annotation
	boolean withannotation = false;
	
	//Ignore assertions
	boolean ignoreassertions = true;
	
	
	public static long init, fin;

	public TestClass(String onto){
		
		onto2modularizeIRI=IRI.create(onto);
		
		init=Calendar.getInstance().getTimeInMillis();
		loadOntology2Modularize();
		fin=Calendar.getInstance().getTimeInMillis();
		System.out.println("Time loading ontology (s): " + (double)((double)fin-(double)init)/1000.0);

	}
	
	
	private boolean loadOntology2Modularize() {
		
    	onto2modularizeManager = OWLManager.createOWLOntologyManager();
    	datafactory = onto2modularizeManager.getOWLDataFactory();
    	
    	try {
    		onto2modularize = onto2modularizeManager.loadOntology(onto2modularizeIRI);
     		
    		return true;
    		
    	}
    	catch (Exception e) {
    		System.err.println("Error loading ontology form URI: " + onto2modularizeIRI.toString());
    		e.printStackTrace();
    		onto2modularize = null;
    		return false;
    	}	
    }
	
	private String getOntologyIRI(){
		return onto2modularize.getOntologyID().getOntologyIRI().toString();
	}
	
	
	private OWLEntity getOWLEntityFromIRI(IRI iri){
		return datafactory.getOWLClass(iri);
	}
	
	private void saveModuleToPhysicalIRI(OWLOntology module, String physicalModuleIRI) {
    	//OWLOntologyManager ontologyModuleManager = OWLManager.createOWLOntologyManager();
    	
        try {
        	onto2modularizeManager.saveOntology(module, new RDFXMLOntologyFormat(), IRI.create(physicalModuleIRI));
        }
        catch (Exception e) {
        	System.err.println("Error saving module\n" + e.getLocalizedMessage());
        	e.printStackTrace();
        }
        
    }
    
    
	
    
    /**
     * Top locality modules
     */
    private void initLowerModulextractor(){
    	//Lower module
		boolean dualConcepts=true;
		boolean dualRoles=true;
		extractor = new ModuleExtractor(onto2modularize, dualConcepts, dualRoles, false, withannotation, ignoreassertions);
		
    }
    
    /**
     * Bottom locality modules
     */
    private void initUpperModulextractor(){
    	//Upper module
		boolean dualConcepts=false;
		boolean dualRoles=false;
		extractor = new ModuleExtractor(onto2modularize, dualConcepts, dualRoles, false, withannotation, ignoreassertions);
		
    }
	
    
    /**
     * Lower of upper module
     */
    private void initLUMModulextractor(){
    	
    	String typeOfModule = "LUM";
    	
    	extractorManager =  new ModuleExtractorManager(onto2modularize, typeOfModule, false, withannotation, ignoreassertions);
    	
    }
    
    
    
    private OWLOntology extactModule4Entity(String type, OWLEntity entity){
    	
    	if (type.equals("UM")){
    		initUpperModulextractor();
    		return extractor.getModuleFromAxioms(extractor.extractModuleAxiomsForEntity(entity), IRI.create(defaultModuleIRI));
    	}
    	else if (type.equals("LM")){
    		initLowerModulextractor();
    		return extractor.getModuleFromAxioms(extractor.extractModuleAxiomsForEntity(entity), IRI.create(defaultModuleIRI));
    	}
    	else{
    		initLUMModulextractor();
    		HashSet<OWLEntity> signature = new HashSet<OWLEntity>();
    		signature.add(entity);
    		return extractorManager.extractModule(signature, defaultModuleIRI);
    	}
    	
    }
    
    
    private OWLOntology extactModule4entityset(String type, HashSet<OWLEntity> signature){
    	
    	if (type.equals("UM")){
    		initUpperModulextractor();
    		return extractor.getLocalityModuleForSignatureGroup(signature, defaultModuleIRI);
    	}
    	else if (type.equals("LM")){
    		initLowerModulextractor();
    		return extractor.getLocalityModuleForSignatureGroup(signature, defaultModuleIRI);
    	}
    	else{
    		initLUMModulextractor();
    		return extractorManager.extractModule(signature, defaultModuleIRI);
    	}
    	
    }
    
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String ontouri="file:/home/ernesto/FMA_3.0_noMTC_100702.owl";
		
		String moduletype;
		
		//Extract the 'upper hierarchy' (plus other necessary axioms) for the given concepts
		moduletype="UM";
		//Extract the 'lower hierarchy' (plus other necessary axioms) for the given concepts
		moduletype="LM";
		
		//Extract the lower of the upper module for the given concepts. Tends to extract rather small modules
		moduletype="LUM";
		
		TestClass test = new TestClass(ontouri);
		
		String class4module;
		//class4module="Anatomical_structure";
		class4module="Organ";
		//class4module="Heart";
		
		
		
		//Note that if the entity has a different namespace than the ontology we should indicate the concrete namespace
		OWLEntity entity = test.getOWLEntityFromIRI(IRI.create(test.getOntologyIRI()+ "#"+ class4module));
		
		//We extract module for given entity (we can also pass a set of owl entities)
		init=Calendar.getInstance().getTimeInMillis();
		OWLOntology module = test.extactModule4Entity(moduletype, entity);
		fin=Calendar.getInstance().getTimeInMillis();
		System.out.println("Time Extracting Module (s): " + (double)((double)fin-(double)init)/1000.0);
		
		
		test.saveModuleToPhysicalIRI(module, ontouri+class4module+moduletype+".owl");
		
		
		
	}

}
