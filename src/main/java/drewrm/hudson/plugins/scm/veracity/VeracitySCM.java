/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drewrm.hudson.plugins.scm.veracity;

import com.ctc.wstx.sw.XmlWriter;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogParser;
import hudson.scm.PollingResult;
import hudson.scm.SCM;
import hudson.scm.SCMDescriptor;
import hudson.scm.SCMRevisionState;
import hudson.util.ArgumentListBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 *
 * @author andrew
 */
public class VeracitySCM extends SCM implements Serializable {

    private String source;
    private String username;
    private String password;
    private static final Logger log = Logger.getLogger(VeracitySCM.class.getName());
    private static final String REPOSITORY_PREFIX = "Jenkins-";

    @DataBoundConstructor
    public VeracitySCM(String source, String username, String password) {
        this.source = source;
        this.username = username;
        this.password = password;
    }

    /**
     * @return the repositoryUrl
     */
    public String getRepositoryUrl() {
        return source;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    @Override
    public SCMRevisionState calcRevisionsFromBuild(AbstractBuild<?, ?> build, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add("vv");
        args.add("head");
        Proc proc = launcher.launch().cmds(args).readStdout().pwd(build.getWorkspace().absolutize()).start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getStdout()));
        String line = reader.readLine();
        Pattern p = Pattern.compile("revision:(.*):(.*)");
        
        while (line != null) {
            line = line.trim();
            if (line.startsWith("revision:")) {
                String[] parts = line.split(":");
                return new VeracitySCMRevisionState(parts[1].trim(), parts[2].trim());
            }
            line = reader.readLine();
        }

        return SCMRevisionState.NONE;
    }

    @Override
    protected PollingResult compareRemoteRevisionWith(AbstractProject<?, ?> project, Launcher launcher, FilePath workspace, TaskListener listener, SCMRevisionState state) throws IOException, InterruptedException {
        VeracityCommand runner = new VeracityCommand(
                REPOSITORY_PREFIX + workspace.getName(),
                getRepositoryUrl(), workspace.absolutize(),
                getUsername(), getPassword(),
                launcher, listener);

        if (runner.hasIncomingChanges()) {
            runner.pullAndUpdate();
            return PollingResult.BUILD_NOW;
        }

        return PollingResult.NO_CHANGES;
    }

    @Override
    public boolean checkout(AbstractBuild<?, ?> build, Launcher launcher, FilePath workspace, BuildListener listener, File changelog) throws IOException, InterruptedException {
        VeracityCommand runner = new VeracityCommand(
                REPOSITORY_PREFIX + workspace.getName(),
                getRepositoryUrl(), workspace.absolutize(),
                getUsername(), getPassword(),
                launcher, listener);

        return runner.cloneRepository() && runner.checkoutRepository() && saveChangeset(runner, changelog);
    }
    
    private boolean saveChangeset(VeracityCommand runner, File changelog) {
        log.info("Not Implemented");
        return true;
    }

    @Override
    public ChangeLogParser createChangeLogParser() {
        return new ChangesetReader();
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends SCMDescriptor<VeracitySCM> {

        private String vvExe;

        public DescriptorImpl() {
            super(VeracitySCM.class, null);
        }

        public boolean configue(StaplerRequest request, JSONObject json) {
            vvExe = Util.fixEmpty(request.getParameter("vv.vvExecutable"));

            save();
            return true;
        }

        @Override
        public SCM newInstance(StaplerRequest request, JSONObject json) throws FormException {
            return super.newInstance(request, json);
        }

        @Override
        public String getDisplayName() {
            return "Veracity SCM Plugin";
        }

        /**
         * @return the vvExe
         */
        public String getVvExe() {
            return vvExe != null ? vvExe : "vv";
        }
    }
}
