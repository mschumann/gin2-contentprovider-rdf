package sf.net.plugin.rdf;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.iqser.core.exception.IQserException;
import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;
import com.iqser.core.model.Parameter;
import com.iqser.core.plugin.provider.AbstractContentProvider;

public class RDFContentProvider extends AbstractContentProvider {

	private static Logger LOGGER = Logger.getLogger(RDFContentProvider.class);
	
	private Model model = null;
	private long  modificationDate = -1;

	private String CONTENT_TYPE; //mandatory
	
	private static final String ATTRIBUTE_NAME = "NAME";
	private static final String ATTRIBUTE_RDFNAMESPACE = "RDFNAMESPACE";

	@Override
	public void init() {
		LOGGER.debug("Start init()");
		
		CONTENT_TYPE = getInitParams().getProperty("content-type");
		
		if (getInitParams().getProperty("sparqlQuery") != null) {
			// Using a SPARQL service
			Query query = QueryFactory.create(getInitParams().getProperty("sparqlQuery"));
			QueryExecution execution = QueryExecutionFactory.sparqlService(getInitParams().getProperty("modelURL"), query);
			ResultSet result = execution.execSelect();
			
			model = result.getResourceModel();
			modificationDate = System.currentTimeMillis();
		} else {
			// Using a remote RDF resource
			model = ModelFactory.createDefaultModel();
			model.read(getInitParams().getProperty("modelURL"));
		}
		
		LOGGER.debug("Finish init()");
	}

	@Override
	public void destroy() {
		model.close();
	}

	@Override
	public void doSynchronization() {
		LOGGER.debug("Start doSynchronization()");
		
		try {
			ResIterator iter = model.listResourcesWithProperty(null);
			
			while (iter.hasNext()) {
				Resource res = iter.nextResource();
				String uri = res.getURI();
				
				if (this.isExistingContent(uri)) {
					Content c = getExistingContent(uri);
					
					if (modificationDate > c.getModificationDate()) {
						this.updateContent(getContent(uri));
					}
				} else {
					Content c = getContent(uri);
					if (c != null) {
						this.addContent(c);
					}
				}
			}
		} catch (IQserException e) {
			LOGGER.error("Unable to perform synchronization - " + e.getLocalizedMessage());
		}
		
		LOGGER.debug("Finished doSynchronization()");
	}

	@Override
	public void doHousekeeping() {
		LOGGER.debug("Start doHousekeeping()");
		
		try {
			Collection<Content> col = this.getExistingContents();
			Iterator<Content> iter = col.iterator();
			
			while (iter.hasNext()) {
				Content c = (Content) iter.next();
				Resource res = model.getResource(c.getContentUrl());
				
				if (!res.listProperties().hasNext()) {
					this.removeContent(c.getContentUrl());
				}
			}
		} catch (IQserException e) {
			LOGGER.error("Unable to perform housekeeping - " + e.getLocalizedMessage());
		}
		
		LOGGER.debug("Finished doHousekeeping()");
	}

	public Content getContent(String url) {
		LOGGER.debug("Start getContent(" + url + ")");
		
		Content c = new Content();
		
		c.setContentUrl(url);
		c.setProvider(getName());
		c.setType(CONTENT_TYPE);
		
		Resource resource = model.getResource(url);
		StmtIterator iter = resource.listProperties();

		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object
		    
		    String	  name		= predicate.getLocalName();
		    String upperCaseName = name.toUpperCase().replace(' ', '_').replace("Ä", "AE").replace("Ö", "OE").replace("Ü", "UE").replace("ß", "SS").replaceAll("[^A-Z\\d-_.]", "");

		    
		    String	  value		= object.toString();
		    int       type		= Attribute.ATTRIBUTE_TYPE_TEXT;
		    boolean   isKey     = false;
		    
		    if (object.isLiteral()) {
		    	// Check, whether object is a number, boolean or byte
		    	// Use init params to check data and currency
		    	isKey = true;
		    }
		    
		    if (!upperCaseName.equalsIgnoreCase("type")) {
			    c.addAttribute(new Attribute(upperCaseName, value, type, isKey));
		    } else if (value.endsWith("Seq")) {
		    	return null;
		    } 
		}
		
		if (resource.getLocalName() != null && c.getAttributeByName("Name") == null) {
			c.addAttribute(new Attribute(ATTRIBUTE_NAME, resource.getLocalName(), Attribute.ATTRIBUTE_TYPE_TEXT, true));
		}
		
		if (resource.getNameSpace() != null) {
			c.addAttribute(new Attribute(ATTRIBUTE_RDFNAMESPACE, resource.getNameSpace(), Attribute.ATTRIBUTE_TYPE_TEXT));
		}

		LOGGER.debug("Finished getContent(" + url + ")");
		
		return c;
	}

	public Content getExistingContent(InputStream inputStream) throws IQserException {
		LOGGER.debug("Starting getContent(" + inputStream.toString() + ")");

		Model cModel = ModelFactory.createDefaultModel();
		cModel.read(inputStream, null);
		
		ResIterator iter = cModel.listResourcesWithProperty(null);
		Resource res = iter.next();
		Content c = this.getContent(res.getURI());
		
		LOGGER.debug("Finished getContent(" + inputStream.toString() + ")");
		
		return c;
	}

	public byte[] getBinaryData(Content c) {
		LOGGER.debug("Starting getBinaryData(" + c.getContentId() + ")");

		Resource resource = model.getResource(c.getContentUrl());
		SimpleSelector sel = new SimpleSelector(resource, (Property)null, (Object)null);
		Model resModel = model.query(sel);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		resModel.write(out);
		
		LOGGER.debug("Finishing getBinaryData(" + c.getContentId() + ")");
		
		return out.toByteArray();
	}

	@Override
	public Collection<String> getActions(Content content) {
		// No actions defined for this content provider
		return null;
	}


	@Override
	public void performAction(String arg0, Collection<Parameter> arg1, Content arg2) {
		// No actions defined for this content provider
	}

	@Override
	public Content createContent(String contentUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Content createContent(InputStream inStream) {
		// TODO Auto-generated method stub
		return null;
	}
}
