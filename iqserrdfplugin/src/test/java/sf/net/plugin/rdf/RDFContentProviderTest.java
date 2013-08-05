package sf.net.plugin.rdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;

import org.junit.Test;

import com.iqser.core.model.Attribute;
import com.iqser.core.model.Content;
import com.iqser.core.model.Parameter;
import com.iqser.gin.developer.test.plugin.provider.ContentProviderTestCase;

/**
 * Recommended test-workflow:
 * <ul>
 *   <li>Prepare your testdata</li>
 *   <li>Add your expectations</li>
 *   <li>Initialize the test</li>
 *   <li>Execute the method(s) under test</li>
 *   <li>Destroy the plugin</li>
 *   <li>Verify if your expectations were met</li>
 *  </ul>
 */
public class RDFContentProviderTest extends ContentProviderTestCase {
	
	private static final String ATTRIBUTE_CLASS = "CLASS";
	private static final String ATTRIBUTE_SPECIES = "SPECIES";
	private static final String ATTRIBUTE_NAME = "NAME";
	private static final String ATTRIBUTE_RDFNAMESPACE = "RDFNAMESPACE";
	private static final String ATTRIBUTE_CONTENT_TYPE = "Animal";

	/**
	 * Test method for {@link RDFContentProvider#doSynchronization()}.
	 * @throws Exception 
	 */
	@Test
	public void testDoSynchronization() throws Exception {		
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata
		Content content1 = new Content();
		content1.setContentUrl("urn:animals:lion");
		content1.setProvider("provider");
		content1.setType(ATTRIBUTE_CONTENT_TYPE);
		content1.addAttribute(new Attribute(ATTRIBUTE_CLASS, "Mammal", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content1.addAttribute(new Attribute(ATTRIBUTE_SPECIES, "Panthera leo", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content1.addAttribute(new Attribute(ATTRIBUTE_NAME, "Lion", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content1.addAttribute(new Attribute(ATTRIBUTE_RDFNAMESPACE, "urn:animals:", Attribute.ATTRIBUTE_TYPE_TEXT, false));
		
		Content content2 = new Content();
		content2.setContentUrl("urn:animals:tarantula");
		content2.setProvider("provider");
		content2.setType(ATTRIBUTE_CONTENT_TYPE);
		content2.addAttribute(new Attribute(ATTRIBUTE_CLASS, "Arachnid", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content2.addAttribute(new Attribute(ATTRIBUTE_SPECIES, "Avicularia avicularia", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content2.addAttribute(new Attribute(ATTRIBUTE_NAME, "Tarantula", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content2.addAttribute(new Attribute(ATTRIBUTE_RDFNAMESPACE, "urn:animals:", Attribute.ATTRIBUTE_TYPE_TEXT, false));
		
		Content content3 = new Content();
		content3.setContentUrl("urn:animals:hippopotamus");
		content3.setProvider("provider");
		content3.setType(ATTRIBUTE_CONTENT_TYPE);
		content3.addAttribute(new Attribute(ATTRIBUTE_CLASS, "Mammal", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content3.addAttribute(new Attribute(ATTRIBUTE_SPECIES, "Hippopotamus amphibius", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content3.addAttribute(new Attribute(ATTRIBUTE_NAME, "Hippopotamus", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content3.addAttribute(new Attribute(ATTRIBUTE_RDFNAMESPACE, "urn:animals:", Attribute.ATTRIBUTE_TYPE_TEXT, false));

		// Add your expectations
		this.expectsIsExistingContent("provider", "urn:animals:lion", false);
		this.expectsIsExistingContent("provider", "urn:animals:tarantula", false);
		this.expectsIsExistingContent("provider", "urn:animals:hippopotamus", false);
		this.expectsIsExistingContent("provider", "urn:animals:data", false);
		
		this.expectsAddContent(content1);
		this.expectsAddContent(content2);
		this.expectsAddContent(content3);
	
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		providerUnderTest.doSynchronization();
		
		// Destroy the plugin
		providerUnderTest.destroy();

		// Verify if your expectations were met
		verify(); 
	}	

	/**
	 * Test method for {@link RDFContentProvider#doHousekeeping()}.
	 * @throws Exception 
	 */
	@Test
	public void testDoHousekeeping() throws Exception {		
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata
		Content content = new Content();
		content.setContentUrl("url");
		content.setProvider("provider");
		
		Collection<Content> existingContents = new LinkedList<Content>();
		existingContents.add(content);
		
		// Add your expectations
		this.expectsGetExistingContents("provider", existingContents);
		this.expectsRemoveContent(content.getProvider(), content.getContentUrl());
		
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		providerUnderTest.doHousekeeping();
		
		// Destroy the plugin
		providerUnderTest.destroy();
	
		// Verify if your expectations were met
		verify(); 
	}

	/**
	 * Test method for {@link RDFContentProvider#getBinaryData(com.iqser.core.model.Content)}.
	 * @throws Exception 
	 */
	@Test
	public void testGetBinaryData() throws Exception {		
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata
		Content content = new Content();
		content.setContentUrl("urn:animals:lion");
		content.setProvider("provider");
		
		// Add your expectations
		String text = "<rdf:RDF\n" +
				"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
				"    xmlns:j.0=\"http://www.some-ficticious-zoo.com/rdf#\" > \n" + 
				"  <rdf:Description rdf:about=\"urn:animals:lion\">\n" + 
				"    <j.0:name>Lion</j.0:name>\n" + 
				"    <j.0:species>Panthera leo</j.0:species>\n" +
				"    <j.0:class>Mammal</j.0:class>\n" + 
				"  </rdf:Description>\n" + 
				"</rdf:RDF>\n";
		
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		byte[] bytes = providerUnderTest.getBinaryData(content);
	
		String rawContent = new String(bytes);
		assertEquals(text, rawContent);
		
		// Destroy the plugin
		providerUnderTest.destroy();
	
		// Verify if your expectations were met
		verify(); 
	}

	/**
	 * Test method for {@link RDFContentProvider#getContent(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testGetContentString() throws Exception {		
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata
		Content content = new Content();
		content.setContentUrl("urn:animals:lion");
		content.setProvider("provider");
		content.setType(ATTRIBUTE_CONTENT_TYPE);
		content.addAttribute(new Attribute(ATTRIBUTE_CLASS, "Mammal", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content.addAttribute(new Attribute(ATTRIBUTE_SPECIES, "Panthera leo", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content.addAttribute(new Attribute(ATTRIBUTE_NAME, "Lion", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content.addAttribute(new Attribute(ATTRIBUTE_RDFNAMESPACE, "urn:animals:", Attribute.ATTRIBUTE_TYPE_TEXT, false));
		
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		Content contentFromProvider = providerUnderTest.getContent(content.getContentUrl());
		assertEquals(content, contentFromProvider);
		
		// Destroy the plugin
		providerUnderTest.destroy();
	
		// Verify if your expectations were met
		verify(); 
	}

	/**
	 * Test method for {@link RDFContentProvider#getContent(java.io.InputStream)}.
	 * @throws Exception 
	 */
	@Test
	public void testGetContentInputStream() throws Exception {		
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata		
		Content content = new Content();
		content.setContentUrl("urn:animals:lion");
		content.setProvider("provider");
		content.setType(ATTRIBUTE_CONTENT_TYPE);
		content.addAttribute(new Attribute(ATTRIBUTE_CLASS, "Mammal", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content.addAttribute(new Attribute(ATTRIBUTE_SPECIES, "Panthera leo", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content.addAttribute(new Attribute(ATTRIBUTE_NAME, "Lion", Attribute.ATTRIBUTE_TYPE_TEXT, true));
		content.addAttribute(new Attribute(ATTRIBUTE_RDFNAMESPACE, "urn:animals:", Attribute.ATTRIBUTE_TYPE_TEXT, false));

		String rawContent = "<rdf:RDF\n" +
				"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
				"    xmlns:j.0=\"http://www.some-ficticious-zoo.com/rdf#\" > \n" + 
				"  <rdf:Description rdf:about=\"urn:animals:lion\">\n" + 
				"    <j.0:name>Lion</j.0:name>\n" + 
				"    <j.0:species>Panthera leo</j.0:species>\n" +
				"    <j.0:class>Mammal</j.0:class>\n" + 
				"  </rdf:Description>\n" + 
				"</rdf:RDF>\n";
		
		InputStream inputStream = new ByteArrayInputStream(rawContent.getBytes());
				
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		Content contentFromProvider = providerUnderTest.getExistingContent(inputStream);
		assertEquals(content, contentFromProvider);
		
		// Destroy the plugin
		providerUnderTest.destroy();
	
		// Verify if your expectations were met
		verify(); 
	}

	/**
	 * Test method for {@link RDFContentProvider#getActions(com.iqser.core.model.Content)}.
	 * @throws Exception 
	 */
	@Test
	public void testGetActions() throws Exception {
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata
		Content content = new Content();
		content.setContentUrl("url");
		content.setProvider("provider");
				
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		Collection<String> actions = providerUnderTest.getActions(content);
		assertNull(actions);
		
		// Destroy the plugin
		providerUnderTest.destroy();
	
		// Verify if your expectations were met
		verify(); 
	}

	/**
	 * Test method for {@link RDFContentProvider#performAction(java.lang.String, com.iqser.core.model.Content)}.
	 * @throws Exception 
	 */
	@Test
	public void testPerformActionStringContent() throws Exception {
		
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata
		Content content = new Content();
		content.setContentUrl("url");
		content.setProvider("provider");
				
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		providerUnderTest.performAction("delete", null, content);
		
		// Destroy the plugin
		providerUnderTest.destroy();
	
		// Verify if your expectations were met
		verify(); 
	}

	/**
	 * Test method for {@link RDFContentProvider#performAction(java.lang.String, java.util.Collection, com.iqser.core.model.Content)}.
	 * @throws Exception 
	 */
	@Test
	public void testPerformActionStringCollectionOfParameterContent() throws Exception {		
		// The ContentProvider to test
		File file = new File(System.getProperty("user.dir") + "/src/test/animals.rdf");
		Properties initParams = new Properties();
		initParams.setProperty("modelURL", file.toURI().toURL().toString());
		initParams.setProperty("content-type", ATTRIBUTE_CONTENT_TYPE);
		
		RDFContentProvider providerUnderTest = new RDFContentProvider();
		providerUnderTest.setId(1);
		providerUnderTest.setName("provider");
		providerUnderTest.setInitParams(initParams);
		
		// Prepare your testdata
		Content content = new Content();
		content.setContentUrl("url");
		content.setProvider("provider");
				
		// Initialize the test
		prepare(); 
		providerUnderTest.init();		
		
		// Execute the method(s) under test
		providerUnderTest.performAction("move", Arrays.asList(new Parameter[] {new Parameter("sourcePath", "/test.pdf"), new Parameter("targetPath", "/tmp/test.pdf")}), content);
		
		// Destroy the plugin
		providerUnderTest.destroy();
	
		// Verify if your expectations were met
		verify(); 
	}
}