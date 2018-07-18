package CMSPROT.CMSPROT_ZK_FragmentsGen_PageAsm;

import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import org.zkoss.zk.ui.WebApps;

public class FragmentGenerator {
	
	private final VelocityEngine ve;
	
	//CONSTRUCTOR
	
	public FragmentGenerator(String templatesFolderName) throws Exception {
		
		ve = new VelocityEngine();
		
		//set Velocity web resource loader (that is, where to find the Velocity template files)
		Properties p = new Properties();
	    p.setProperty("resource.loader", "webapp");
	    p.setProperty("webapp.resource.loader.class", "org.apache.velocity.tools.view.WebappResourceLoader");
	    p.setProperty("webapp.resource.loader.path", "/WEB-INF/" + templatesFolderName + "/");
	    
	    //get servlet context and give it to Velocity settings
	    ServletContext sc = WebApps.getCurrent().getServletContext();	
		ve.setApplicationAttribute("javax.servlet.ServletContext", sc);
	    /* DEBUG 
	     * System.out.println("SERVLET: " + sc);		
	     * System.out.println("SERVLET CONTEXT: " + sc.getServletContextName());
         */
	
	    //init Velocity
		ve.init(p);
	    /* DEBUG 
	     * System.out.println("PROPERTY \"resource.loader\": "+vc.getProperty("resource.loader"));  
	     * System.out.println("PROPERTY \"webapp.resource.loader.class\": "+vc.getProperty("webapp.resource.loader.class"));  
	     * System.out.println("PROPERTY \"webapp.resource.loader.path\" "+vc.getProperty("webapp.resource.loader.path"));  
	     */
	}
	
	//PAGE GENERATION
	
	public String generateFragmentHtml(FragmentType type, Map<String,String> inputData) throws Exception {
		
		String templateFileName = type.toString().toLowerCase() + ".vm";	//example:      type      template
																			//            FOO_BAR -> foo_bar.vm
																			//note also that the templates folder was specified before, during Velocity web resource loader setup
		Template template = ve.getTemplate(templateFileName);
		
		VelocityContext context = new VelocityContext();
		context.put("map", inputData);		//we'll use $map in the .vm file
		
		StringWriter outputHtml = new StringWriter();
		template.merge(context,outputHtml);
		return outputHtml.toString();
		
	}
	
}

