package de.piratenpartei.id;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.URIReferenceException;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dom.DOMURIReference;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Message {
	
	Document doc;
	Element body;
	Element root;
	Account author;
	
	public Message(InputStream in) throws IOException, SAXException, KeyException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
		dbf.setNamespaceAware(true); 
		//TODO: Schema für den envelope (damit die Body-Id auch als solche erkannt wird.)
		DocumentBuilder builder;
		try {
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}  
		doc = builder.parse(in);
		
		findBodyElements();

		// get sender
		String hash = body.getAttributes().getNamedItemNS(Config.messageNamespace, "author").getNodeValue();
		author = new Account(hash);
	}
	
	private void findBodyElements() throws IOException {
		// get message root (which should contain body and signature)
		NodeList rnl = doc.getElementsByTagNameNS(Config.messageNamespace, "Message");
		if (rnl.getLength() == 0) {
			throw new IOException("Nachricht enthält kein Element \"Message\"");
		} 
		if (rnl.getLength() > 1) {
			throw new IOException("Nachricht enthält mehr als ein Element \"Message\"");
		}
		root = (Element) rnl.item(0);
		
		// get message body
		NodeList nl = doc.getElementsByTagNameNS(Config.messageNamespace, "Body");
		if (nl.getLength() == 0) {
			throw new IOException("Nachricht enthält kein Text");
		} 
		if (nl.getLength() > 1) {
			throw new IOException("Nachricht enthält mehr als einen Textteil");
		}
		body = (Element) nl.item(0);
	}
	
	// creates a new empty message
	public Message() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		doc = db.newDocument();
		root = doc.createElementNS(Config.messageNamespace, "Message");
		doc.appendChild(root);
		body = doc.createElementNS(Config.messageNamespace, "Body");
		body.setAttribute("id", "body");
		body.setIdAttribute("id", true);
		root.appendChild(body);
	}
	
	public Node getBody() {
		return body;
	}
	
	/**
	 * Important: Do not modify the body after calling sign.
	 * First of all it may modify the signature (recalling sign will fix that).
	 * But if you also have an old version of the body or other parts of the document flying around, they will be invalidated afterwards.
	 * This is due to an ugly hack that replaces the whole document by a new one.
	 * 
	 * @param account
	 * @throws KeyException
	 */
	public void sign(PrivateAccount account) throws KeyException {
		author = account;
		body.setAttributeNS(Config.messageNamespace, "author", author.getHash());
		
		// first get rid of old signatures
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(0);
			n.getParentNode().removeChild(n);
		}
		
		
		// THIS IS VERY UGLY!!
		// but we have to do it, because somehow the canonicalization doesn't have the namespaces otherwise.
		try {
	        Source source = new DOMSource(doc);
	
	        // Prepare the output file
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
			dbf.setNamespaceAware(true); 
			Document doc2 = dbf.newDocumentBuilder().newDocument();
	        DOMResult result = new DOMResult(doc2);
	
	        // Write the DOM from source to result
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer xformer = tf.newTransformer();
	        xformer.transform(source, result);
	        doc = doc2;
	        
	        try {
				findBodyElements();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	        
	    } catch (TransformerConfigurationException e) {
	    	throw new RuntimeException(e);
	    } catch (TransformerException e) {
	    	throw new RuntimeException(e);
	    } catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		XMLSignatureFactory xsf = XMLSignatureFactory.getInstance();
		CanonicalizationMethod cm;
		try {
			cm = xsf.newCanonicalizationMethod(Config.CANONICALIZATION_METHOD, (C14NMethodParameterSpec) null);
			SignatureMethod sm = xsf.newSignatureMethod(Config.SIGNATURE_METHOD, null);
			
			List<Transform> transforms = new ArrayList<Transform>();
			//transforms.add(xsf.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null));
			Reference ref = xsf.newReference("#body", xsf.newDigestMethod(Config.MESSAGE_DIGEST_ALGORITHM, null), transforms, null, null);
			List<Reference> refs = new ArrayList<Reference>();
			refs.add(ref);
			SignedInfo si = xsf.newSignedInfo(cm, sm, refs);
			XMLSignature xs = xsf.newXMLSignature(si, account.getKeyInfo());
			DOMSignContext dsc = new DOMSignContext(account.getPrivateKey(), root);
			xs.sign(dsc);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		} catch (MarshalException e) {
			throw new KeyException("Konnte Signatur nicht ins XML schreiben",e);
		} catch (XMLSignatureException e) {
			throw new KeyException("Konnte Signatur nicht erstellen",e);
		}
	}
	
	/**
	 * Prüft die Gültigkeit der Nachricht
	 * Wenn die Nachricht ungültig ist, wird eine VerificationException geworfen.
	 * 
	 * @throws VerificationException
	 */
	public void verify() throws VerificationException {
		// TODO: Change to only look at Signature element child of root node
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
		if (nl.getLength() == 0) {
			throw new VerificationException("Nachricht enthält keine Signatur");
		} 
		if (nl.getLength() > 1) {
			throw new VerificationException("Nachricht enthält zu viele Signaturen");
		}
		
		// Load signature from XML document
		Node signature = nl.item(0);
		
		DOMValidateContext valContext = new DOMValidateContext(author.getPublicKey(), signature); 
		
		XMLSignatureFactory factory = XMLSignatureFactory.getInstance(); 
		
		XMLSignature sig;
		try {
			sig = factory.unmarshalXMLSignature(valContext);
		} catch (MarshalException e) {
			throw new VerificationException("Konnte Signatur nicht einlesen", e);
		} 
		
		// check validity
		boolean coreValidity;
		try {
			coreValidity = sig.validate(valContext);
		} catch (XMLSignatureException e) {
			throw new VerificationException("Die Signatur konnte nicht geprüft werden", e);
		} 
		
		if(!coreValidity) {
			try {
				boolean sv = sig.getSignatureValue().validate(valContext);
				System.out.println("Signature status: "+sv);
			} catch (XMLSignatureException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Iterator i = sig.getSignedInfo().getReferences().iterator();
			for (int j=0; i.hasNext(); j++) {
				boolean refValid = true;
				try {
					Reference ref = (Reference) i.next();
					System.out.println("Type: "+ref.getType());
					System.out.println("URI: "+ref.getURI());
					System.out.println(ref.getDereferencedData());
					refValid = ref.validate(valContext);
				} catch (XMLSignatureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				System.out.println("ref["+j+"] validity status: " + refValid);
			} 
			throw new VerificationException("Das Dokument scheint modifiziert worden zu sein! Die Signatur stimmt nicht mehr mit dem Inhalt überein!");
		}
		
		if(!sig.getSignedInfo().getCanonicalizationMethod().getAlgorithm().equals(Config.CANONICALIZATION_METHOD)) {
			throw new VerificationException("Die Signatur verwendet falsche Canonicalisierung!");
		}
		if(!sig.getSignedInfo().getSignatureMethod().getAlgorithm().equals(Config.SIGNATURE_METHOD)) { // Ist diese Methode akzeptzabel?
			throw new VerificationException("Die Signatur verwendet falsche Signaturmethode!");
		}
		
		// check if signature really signs the important data
		// we only allow one reference (the body)
		List<?> refs = sig.getSignedInfo().getReferences();
		if(refs.size() == 0) {
			throw new VerificationException("Die Signatur signiert nichts");
		}
		if(refs.size() > 1) {
			throw new VerificationException("Die Signatur darf nur den Körper der Nachricht signieren!");
		}
		Reference ref = (Reference) refs.get(0);
		
		// check, if ref really references the body of the message
		if(!ref.getURI().equals("#"+body.getAttribute("id"))) {
			throw new VerificationException("Die Signatur signiert nicht den Körper der Nachricht, sondern irgendwas anderes!");
		}
		
		// check, if the reference does not erase important data by using a transformation
		// we only allow one specific type of canocalization
		List<?> transforms = ref.getTransforms();
		/*if(transforms.size() == 0) {
			throw new VerificationException("Die Nachricht wird zur Signatur nicht normalisiert");
		}
		if(transforms.size() > 1) {
			throw new VerificationException("Die Nachricht wird mehr transformiert als nötig!");
		}
		Transform transform = (Transform) transforms.get(0);
		if(!transform.getAlgorithm().equals(CanonicalizationMethod.EXCLUSIVE)) {
			throw new VerificationException("Die Nachricht wird mit einem nicht erlaubten Algorithmus bearbeitet: "+transform.getAlgorithm()+", erlaubt ist nur "+CanonicalizationMethod.EXCLUSIVE);
		}*/
		if(transforms.size() > 0) {
			throw new VerificationException("Die Nachricht wird mehr transformiert als nötig!");
		}
		
		// check, if ref uses a sufficiently strong Digest method
		if(!ref.getDigestMethod().getAlgorithm().equals(Config.MESSAGE_DIGEST_ALGORITHM)) {
			throw new VerificationException(Config.MESSAGE_DIGEST_ALGORITHM+" wird nicht als Digest-Methode verwendet!");
		}
		
		// validate correctness of digest etc.
		try {
			if(!ref.validate(valContext)) {
				throw new VerificationException("Prüfung der Referenz auf den Hauptteil der Nachricht schlug fehl!");
			}
		} catch (XMLSignatureException e) {
			throw new VerificationException("Referenz konnte nicht geprüft werden", e);
		}
	}
	
	public Account getAuthor() {
		return author;
	}
	
	public void send() {
		try {
	        // Prepare the DOM document for writing
	        Source source = new DOMSource(doc);

	        // Prepare the output file
	        FileOutputStream fos = new FileOutputStream("test_files/message2.xml");
	        Result result = new StreamResult(fos);

	        // Write the DOM document to the file
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer xformer = tf.newTransformer();
	        xformer.transform(source, result);
	    } catch (TransformerConfigurationException e) {
	    	throw new RuntimeException(e);
	    } catch (TransformerException e) {
	    	throw new RuntimeException(e);
	    } catch (FileNotFoundException e) {
	    	throw new RuntimeException(e);
		}
	}
}
