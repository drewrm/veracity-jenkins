/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drewrm.hudson.plugins.scm.veracity;

import java.util.Date;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 *
 * @author andrew
 */
@ExportedBean(defaultVisibility=999)
public class Changeset {
    
    private String hash;
    private int number;
    private String branch;
    private String who;
    private Date when;
    private String comment;
    private String parentNumber;
    private String parentHash;

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash the hash to set
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return the branch
     */
    public String getBranch() {
        return branch;
    }

    /**
     * @param branch the branch to set
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * @return the who
     */
    public String getWho() {
        return who;
    }

    /**
     * @param who the who to set
     */
    public void setWho(String who) {
        this.who = who;
    }

    /**
     * @return the when
     */
    public Date getWhen() {
        return when;
    }

    /**
     * @param when the when to set
     */
    public void setWhen(Date when) {
        this.when = when;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the parentNumber
     */
    public String getParentNumber() {
        return parentNumber;
    }

    /**
     * @param parentNumber the parentNumber to set
     */
    public void setParentNumber(String parentNumber) {
        this.parentNumber = parentNumber;
    }

    /**
     * @return the parentHash
     */
    public String getParentHash() {
        return parentHash;
    }

    /**
     * @param parentHash the parentHash to set
     */
    public void setParentHash(String parentHash) {
        this.parentHash = parentHash;
    }
}
