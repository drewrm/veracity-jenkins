/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drewrm.hudson.plugins.scm.veracity;

import com.sun.xml.internal.fastinfoset.tools.StAX2SAXReader;
import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import java.io.File;
import java.io.IOException;
import org.xml.sax.SAXException;

/**
 *
 * @author andrew
 */
public class ChangesetReader extends ChangeLogParser {

    @Override
    public ChangeLogSet<? extends Entry> parse(AbstractBuild ab, File changelogFile) throws IOException, SAXException {

        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
