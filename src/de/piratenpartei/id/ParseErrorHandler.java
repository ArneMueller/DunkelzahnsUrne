package de.piratenpartei.id;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ParseErrorHandler implements ErrorHandler {

	@Override
	public void warning(SAXParseException exception) throws SAXException {
		throw new SAXException("warning", exception);
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		throw new SAXException("error", exception);
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		throw new SAXException("fatal error", exception);
	}

}
