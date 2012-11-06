/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drewrm.hudson.plugins.scm.veracity;

import hudson.scm.SCMRevisionState;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 *
 * @author andrew
 */
@ExportedBean(defaultVisibility=999)
public class VeracitySCMRevisionState extends SCMRevisionState {
    private final String hash;
    private final String number;
    
    public VeracitySCMRevisionState(String hash, String number) {
        this.hash = hash;
        this.number = number;
    }

    /**
     * @return the hash
     */
    @Exported(name = "veracityRevisionHash")
    public String getHash() {
        return hash;
    }

    /**
     * @return the number
     */
    @Exported(name = "veracityRevisionNumber")
    public String getNumber() {
        return number;
    }
    
    @Override
    public String toString() {
        return "[Veracity Revision] hash = " + hash + " number = " + number;
    }
    
    
}
