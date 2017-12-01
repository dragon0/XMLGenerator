package com.example.sraven0.xml;

import java.net.URL;
import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.*; 
import javax.xml.transform.sax.SAXSource; 
import javax.xml.transform.stream.StreamResult;

public class AddressBookReader implements XMLReader{

    ContentHandler handler;

    String nsu = "";  
    Attributes atts = new AttributesImpl();
    String rootElement = "addressbook";

    String indent = "\n ";

    public static void main(String argv[]) {
        /*
        // Check the arguments
        if (argv.length != 1) {
            System.err.println("Usage: java AddressBookReader01 filename");
            System.exit (1);
        }
        */

        try {
            //File f = new File(argv[0]);
            //FileReader fr = new FileReader(f);
            //BufferedReader br = new BufferedReader(fr);
            //InputSource inputSource = new InputSource(br);

            //XXX input source
            URL xdoc = AddressBookReader.class.getClassLoader().getResource(
                    "PersonalAddressBook.ldif");
            InputSource inputSource = new InputSource(xdoc.openStream());
            //XXX Create the sax "parser".
            AddressBookReader saxReader = new AddressBookReader();
            //XXX Use the parser as a SAX source for input
            SAXSource source = new SAXSource(saxReader, inputSource);

            //XXX output location
            StreamResult result = new StreamResult(System.out);

            //XXX Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            //XXX generate document
            transformer.transform(source, result);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // Parse the input file
    public void parse(InputSource input) throws IOException, SAXException{

        try {
            //XXX Get an efficient reader for the file
            //java.io.Reader r = input.getCharacterStream();
            Reader r = new InputStreamReader(input.getByteStream());
            BufferedReader br = new BufferedReader(r);

            // Read the file and display its contents.
            String line = "";
            while (null != (line = br.readLine())) {
                if (line.startsWith("xmozillanickname: "))
                    break;
            }

            //XXX check that handler was supplied
            if (handler == null) {
                throw new SAXException("No content handler");
            }

            //XXX start document and create root element
            handler.startDocument(); 
            handler.ignorableWhitespace("\n".toCharArray(), 
                0,  // start index
                1   // length
            ); 
            handler.startElement(nsu, rootElement, rootElement, atts);

            //XXX create elements
            output("nickname", "xmozillanickname", line);
            line = br.readLine();
            output("email",  "mail", line);

            line = br.readLine();
            output("html", "xmozillausehtmlmail", line);

            line = br.readLine();
            output("firstname","givenname", line);

            line = br.readLine();
            output("lastname", "sn", line);

            line = br.readLine();
            output("work", "telephonenumber", line);

            line = br.readLine();
            output("home", "homephone", line);

            line = br.readLine();
            output("fax", "facsimiletelephonenumber", line);

            line = br.readLine();
            output("pager", "pagerphone", line);

            line = br.readLine();
            output("cell", "cellphone", line);

            //XXX add final newline, end root element, and end document
            handler.ignorableWhitespace("\n".toCharArray(), 0, 1 ); 
            handler.endElement(nsu, rootElement, rootElement);
            handler.ignorableWhitespace("\n".toCharArray(), 0, 1 ); 
            handler.endDocument(); 
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //XXX generate each element
    void output(String name, String prefix, String line) 
            throws SAXException {

        //XXX start index for element #text
        int startIndex = 
            prefix.length() + 2;   // 2=length of ": "
        //XXX seems to be unused, WTF?
        String text = line.substring(startIndex);

        //XXX indentation for readability in final document
        int textLength = line.length() - startIndex;
        handler.ignorableWhitespace (indent.toCharArray(), 
            0,    // start index
            indent.length()
        );

        //XXX create element
        handler.startElement(
                nsu,   // Namespace URI
                name,  // local name
                name,  // qualified name
                atts); // empty attribute list
        //XXX add element/#text
        handler.characters(line.toCharArray(), 
            startIndex,
            textLength
        );
        //XXX end element
        handler.endElement(nsu, name, name);
    }

    // Allow an application to register a content event handler.
    public void setContentHandler(ContentHandler handler) {
        this.handler = handler;
    }  

    // Return the current content handler.
    public ContentHandler getContentHandler() {
        return this.handler;
    }

    //XXX IRL you will want to implement these
    // Allow an application to register an error event handler.
    public void setErrorHandler(ErrorHandler handler) { } 
    // Return the current error handler.
    public ErrorHandler getErrorHandler() { 
        return null; 
    }


    //XXX rest of XMLReader interface
    // Parse an XML document from a system identifier (URI).
    public void parse(String systemId) throws IOException, SAXException {
        parse(new InputSource(systemId));
    } 
    // Return the current DTD handler.
    public DTDHandler getDTDHandler() { return null; } 
    // Return the current entity resolver.
    public EntityResolver getEntityResolver() { return null; } 
    // Allow an application to register an entity resolver.
    public void setEntityResolver(EntityResolver resolver) { } 
    // Allow an application to register a DTD event handler.
    public void setDTDHandler(DTDHandler handler) { } 
    // Look up the value of a property.
    public Object getProperty(String name) { return null; } 
    // Set the value of a property.
    public void setProperty(String name, Object value) { }  
    // Set the state of a feature.
    public void setFeature(String name, boolean value) { } 
    // Look up the value of a feature.
    public boolean getFeature(String name) { return false; }

}

