/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drewrm.hudson.plugins.scm.veracity;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.TaskListener;
import hudson.util.ArgumentListBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author andrew
 */
public class VeracityCommand {

    private static final Logger log = Logger.getLogger(VeracityCommand.class.getName());
    private static final String VERACITY_COMMAND = "vv";

    private String repository;
    private String remote;
    private FilePath workspace;
    private String username;
    private String password;
    private Launcher launcher;
    private TaskListener listener;


    public VeracityCommand(String repository, String remote, FilePath workspace, String username, String password, Launcher launcher, TaskListener listener) {
        this.repository = repository;
        this.remote = remote;
        this.workspace = workspace;
        this.username = username;
        this.password = password;
        this.launcher = launcher;
        this.listener = listener;
    }

    public boolean cloneRepository() throws IOException, InterruptedException {
        if (repositoryExists()) {
            log.log(Level.INFO, "Repository {0} already exists.", new String[]{repository});
            return true;
        }

        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(VERACITY_COMMAND);
        args.add("clone");
        args.add(this.remote);
        args.add(this.repository);
        Proc proc = this.launcher.launch().cmds(args).stdout(this.listener).writeStdin().start();
        writeToStdin(username, proc.getStdin());
        writeToStdin(password, proc.getStdin());
        return proc.join() == 0;
    }

    public boolean checkoutRepository() throws IOException, InterruptedException {
        if (repositoryCheckedOut()) {
            log.log(Level.INFO, "Repository {0} already is already checked out in {1}.", new Object[]{repository, workspace});
            return true;
        }

        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(VERACITY_COMMAND);
        args.add("checkout");
        args.add(this.repository);
        args.add(this.workspace);
        return launcher.launch().cmds(args).stdout(listener).join() == 0;
    }

    public boolean repositoryExists() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(VERACITY_COMMAND);
        args.add("repo");
        args.add("info");
        args.add(this.repository);
        return runCommand(args) == 0;
    }

    public boolean repositoryCheckedOut() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(VERACITY_COMMAND);
        args.add("heads");
        return runCommand(args) == 0;
    }

    public boolean hasIncomingChanges() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(VERACITY_COMMAND);
        args.add("incoming");
        Proc proc = launcher.launch().cmds(args).pwd(workspace).readStdout().start();
        proc.join();
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getStdout()));
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.equals("No incoming changes.")) {
                return false;
            }
            line = reader.readLine();
        }
        return true;
    }

    public boolean pullAndUpdate() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(VERACITY_COMMAND);
        args.add("pull");
        args.add("--update");
        return runCommand(args) == 0;
    }

    public Changeset getChangesetForHead() throws IOException, InterruptedException {
        ArgumentListBuilder args = new ArgumentListBuilder();
        args.add(VERACITY_COMMAND);
        args.add("log");
        args.add("--max");
        args.add(1);
        Proc proc = launcher.launch().cmds(args).pwd(workspace).readStdout().start();
        proc.join();

        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getStdout()));
        String line = reader.readLine();
        Changeset cs = new Changeset();
        while (line != null) {
            line = line.trim();

            if (line.startsWith("revision:") || line.startsWith("parent:")) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String field = parts[0].trim();
                    String number = parts[1].trim();
                    String hash = parts[2].trim();

                    if (field.equalsIgnoreCase("revision")) {
                        cs.setNumber(Integer.parseInt(number));
                        cs.setHash(hash);
                    } else {
                        cs.setParentHash(hash);
                        cs.setParentNumber(number);
                    }
                }
            } else if (line.startsWith("branch:")) {
                cs.setBranch(line.substring(line.indexOf(":")).trim());
            } else if (line.startsWith("who:")) {
                cs.setWho(line.substring(line.indexOf(":")).trim());
            } else if (line.startsWith("comment:")) {
                cs.setWho(line.substring(line.indexOf(":")).trim());
            }
        }
        return cs;
    }

    private void writeToStdin(String input, OutputStream stream) throws IOException {
        stream.write(input.getBytes());
        stream.write(System.getProperty("line.separator").getBytes());
        stream.flush();
    }

    private int runCommand(ArgumentListBuilder args) throws IOException, InterruptedException {
        return launcher.launch().cmds(args).pwd(workspace).join();
    }
}
