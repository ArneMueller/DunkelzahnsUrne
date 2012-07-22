package de.piratenpartei.id;

import gnu.jpdf.BoundingBox;
import gnu.jpdf.PDFJob;
import gnu.jpdf.StringTooLongException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.OutputStream;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * Every user has an account. These accounts are represented by this class.
 * The account of the user, who is running the client, has a special account, a {@link PrivateAccount}.
 * 
 * Account stores the public key and the hash of the key.
 * 
 * @author arne
 *
 */
public class Account {

	/** public key of the account */
	protected PublicKey publicKey;
	
	/** hash of the account */
	protected String hash;
	
	/**
	 * performs a lookup in the official list and retrieves corresponding key
	 * @param hash
	 */
	public Account(String hash) throws KeyException {
		this.hash = hash;
		PublishedAccounts pa = PublishedAccounts.getInstance();
		publicKey = pa.getKey(hash);
		// check if this is the correct public key
		Helper.verifyKey(publicKey, hash);
	}

	/**
	 * Only for PrivateAccount construction.
	 * {@link #init(PublicKey)} must be called immediatly after the key has been constructed. 
	 */
	protected Account() {
	}
	
	/**
	 * Only for PrivateAccount initialization. Stores the public key and computes the hash.
	 * 
	 * @param pk the public key of the account.
	 * @throws KeyException
	 */
	protected void init(PublicKey pk) throws KeyException {
		publicKey = pk;
		hash = Helper.computeHash(publicKey);
	}
	
	/**
	 * checks if the public key of this account has been published on the official Account list.
	 * @return true if it has been published
	 */
	public boolean isPublished() {
		PublishedAccounts pa = PublishedAccounts.getInstance();
		if(!pa.hasKey(hash)) {
			return false;
		}
		// check if this is the correct public key
		String test;
		try {
			PublicKey pk = pa.getKey(hash);
			if(!pk.equals(publicKey)) {
				return false;
			}
			test = Helper.computeHash(publicKey);
		} catch (KeyException e) {
			throw new RuntimeException(e);
		}
		if(!test.equals(hash)) {
			return(false);
		}
		return true;
	}
	
	/**
	 * checks if the hash of the publick key has been published on the official list of legitimized keys.
	 * @return
	 */
	public boolean isVerified() {
		return VerifiedAccounts.getInstance().hasEntry(hash);
	}
	
	/**
	 * returns the public key of this account.
	 * @return the public key
	 */
	public PublicKey getPublicKey() {
		return publicKey;
	}
	
	/**
	 * returns hash of the public key of this account.
	 * @return
	 */
	public String getHash() {
		return hash;
	}
	
	/**
	 * Prints the pdf that should be but into the ballot box.
	 * @param out used to write the pdf-file
	 */
	public void print(OutputStream out) {
		PDFJob job = new PDFJob(out, "geheimes Akkreditierungsdokument");
		PageFormat pf = new PageFormat();
		Paper p = new Paper();
		double mm = 0.0393700787*72;
		p.setSize(210*mm, 297*mm); // fix the paper size, so that we always know that we have sufficient space.
		p.setImageableArea(30*mm, 30*mm, (210-60)*mm, (297-60)*mm);
		pf.setPaper(p);
		pf.setOrientation(PageFormat.PORTRAIT);
		//Graphics g = job.getGraphics(pf);
		Graphics g = job.getGraphics();
		
		Dimension d = job.getPageDimension();
		
		Font title = g.getFont().deriveFont(Font.BOLD, 24); // title font
		Font text = g.getFont().deriveFont(Font.PLAIN, 12); // normal font
		Font id = g.getFont().deriveFont(Font.PLAIN, 10); // small font for Hash string
	    int padding = 5;
		
		System.out.println("total width: "+d.getWidth());
		System.out.println("total height: "+d.getHeight());
		
		g.setColor(Color.black);
		BoundingBox box = new BoundingBox(new Point(20, 20), 
				new Dimension((int) d.getWidth()-20, 30));
		/*g.drawRect((int)box.getAbsoluteLocation().getX(),
				(int)box.getAbsoluteLocation().getY(),
				(int)box.getSize().getWidth(),
				(int)box.getSize().getHeight());*/
		g.setFont(text);
		FontMetrics fm = g.getFontMetrics();

		BoundingBox child = null;
		try {
			child = box.getStringBounds("Bitte auf Papier Ausdrucken!",
					BoundingBox.HORIZ_ALIGN_LEFT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_LEFT);
			child = box.getStringBounds("Bitte auf Papier Ausdrucken!",
					BoundingBox.HORIZ_ALIGN_RIGHT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_RIGHT);
		}
		catch (StringTooLongException stle) {
			throw new RuntimeException(stle);
		}
		
		box = new BoundingBox(new Point(20, 50), 
				new Dimension((int) d.getWidth()-20, 150));
		/*g.drawRect((int)box.getAbsoluteLocation().getX(),
				(int)box.getAbsoluteLocation().getY(),
				(int)box.getSize().getWidth(),
				(int)box.getSize().getHeight());*/
		g.setFont(title);
		fm = g.getFontMetrics();

		try {
			child = box.getStringBounds("Geheimes Akkreditierungsdokument",
					BoundingBox.HORIZ_ALIGN_LEFT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_LEFT);
		}
		catch (StringTooLongException stle) {
			throw new RuntimeException(stle);
		}

		box.subtract(child, BoundingBox.SUBTRACT_FROM_BOTTOM);
		g.setFont(text);
		fm = g.getFontMetrics();
		try {
			child = box.getStringBounds("Dieses PDF enthält das Erkennungsmerkmal (ID) deines Accounts. Um mit verifizierten Account (und somit stimmberechtigt) am Online-Beteiligungssystem teilnehmen zu können, musst du dieses Dokument ausdrucken und in die dafür vorgesehene Akkreditierungsurne werfen.",
					BoundingBox.HORIZ_ALIGN_LEFT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_LEFT);
		}
		catch (StringTooLongException stle) {
			throw new RuntimeException(stle);
		}

		box.subtract(child, BoundingBox.SUBTRACT_FROM_BOTTOM);
		try {
			child = box.getStringBounds("Wenn du pseudonym an dem Online-Beteiligungstool teilnehmen möchtest, darfst du niemanden dieses Dokument zeigen. Es ist wie ein ausgefüllter, geheimer Stimmzettel.",
					BoundingBox.HORIZ_ALIGN_LEFT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_LEFT);
		}
		catch (StringTooLongException stle) {
			throw new RuntimeException(stle);
		}

		g.setFont(id);
		fm = g.getFontMetrics();
		box = new BoundingBox(new Point(20, 250), 
				new Dimension((int) d.getWidth()-20, 50));

		/*g.drawRect((int)box.getAbsoluteLocation().getX(),
				(int)box.getAbsoluteLocation().getY(),
				(int)box.getSize().getWidth(),
				(int)box.getSize().getHeight());*/
		try {
			child = box.getStringBounds("ID: "+getHash(),
					BoundingBox.HORIZ_ALIGN_LEFT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_LEFT);
		}
		catch (StringTooLongException stle) {
			throw new RuntimeException(stle);
		}

	    QRCodeWriter qw = new QRCodeWriter();
	    BitMatrix bm;
	    try {
	    	Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
	    	hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // shouldn't actually matter since only ascii symbols
	    	hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // use the highest possible
			bm = qw.encode(getHash(), BarcodeFormat.QR_CODE, (int) d.getWidth()-100, (int) d.getWidth()-100);
			
		} catch (WriterException e) {
			throw new RuntimeException(e);
		}
	    
	    BufferedImage buff = MatrixToImageWriter.toBufferedImage(bm);
	    g.drawImage(buff, 50, 270, null);
	    
		try {
		    box = new BoundingBox(new Point(20, (int) d.getHeight()-50), 
					new Dimension((int) d.getWidth()-20, 30));
			g.setFont(text);
			fm = g.getFontMetrics();
			child = box.getStringBounds("Bitte auf Papier Ausdrucken!",
					BoundingBox.HORIZ_ALIGN_LEFT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_LEFT);
			child = box.getStringBounds("Bitte auf Papier Ausdrucken!",
					BoundingBox.HORIZ_ALIGN_RIGHT,
					BoundingBox.VERT_ALIGN_TOP,
					fm, 
					padding);
			child.drawWrappedString(g, fm, padding, BoundingBox.HORIZ_ALIGN_RIGHT);
		}
		catch (StringTooLongException stle) {
			throw new RuntimeException(stle);
		}
	    
		// end the page
		g.dispose();
		job.end();
	}
}
