LOCALITY EXTRACTOR

This extractor was originally implemented in 2007 by Ernesto Jimenez Ruiz in collaboration with Ulrike Sattler and Thomas Schneider (IMG Group, University of Manchester), Bernardo Cuenca Grau (KRR Group, University of Oxford), and Rafael Berlanga (TKBG group, Jaume I University of Castellon). Currently is maintained up-to-date by the KRR group in Oxford (contact: ernesto.jimenez.ruiz@gmail.com).

- The core class of the extractor is 'ModuleExtractor' which accepts the following parameters:
	- OWLOntology ontology: Ontology to be modularized
	- boolean dualConcepts Type of interpretation for concepts outside the signature (false for bottom locality module or Upper Module (UM))
	- boolean dualRoles Type of interpretation for properties outside the signature (false for bottom locality module or Upper Module (UM))
	- boolean considerImportsClosure Treats the imported ontologies as well
	- boolean considerEntityAnnotations Annotations are always local, so if required must be explicitely indicated
	- boolean ignoreAssertions The module will only extract the corresponding part of the TBOX (importaing if annotations are attached as assertions)


Within the extractor we include two main test classes

- The class 'CreatePhysicalOntologyModule'. This class can be used from the command line with these input parameters:

         Ontology URI i.e.:
                 http://krono.act.uji.es/Links/ontologies/gale_protege.owl/view
                 file:/tmp/MyOnt.owl
                 ftp://ftp.fao.org/gi/gil/gilws/aims/kos/agrovoc_formats/owl/agrovoc_20050401.owl
         File with signature (one entity per line):
                 Juvenile Idiopathic Arthritis
                 Heart
                 hasAttribute
                 # Commented line.
         Type of module: 'UPPER_MODULE' or 'UM', 'LOWER_MODULE' or 'LM', 'LOWER_UPPER_MODULE' or 'LUM', 'DUAL_CONCEPTS_MODULE' or 'DCM', 'DUAL_ROLES_MODULE' or 'DRM
                 See report documentation in http://krono.act.uji.es/people/Ernesto/safety-ontology-reuse for more information.
         Output Filepath: absolute path of the owl output module.
         Module URI (optional). Default: http://krono.act.uji.es/Links/ontologies/module_Signature_File.owl

- ExtractModules4AllOntologyEntities: extracts and stores a module for each class in the given ontology signature.

Large ontologies may require an increase of the allocated memory and the "-DentityExpansionLimit" parameter for the JVM:
-Xms500M -Xmx4G -DentityExpansionLimit=100000000

