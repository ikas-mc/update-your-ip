package ikas.java.project.updateip.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;

import java.nio.file.Files;
import java.nio.file.Path;

public class SshSessionFactory extends JschConfigSessionFactory {
    private final Path key;

    public SshSessionFactory(Path key) {
        if (!Files.exists(key)) {
            throw new RuntimeException("key not found");
        }

        this.key = key;
    }

    @Override
    protected void configure(OpenSshConfig.Host host, Session session) {
        session.setConfig("StrictHostKeyChecking", "no");
    }

    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
        var jsch = new JSch();
        JSch.setConfig("ssh-rsa", JSch.getConfig("signature.rsa"));
        JSch.setConfig("ssh-dss", JSch.getConfig("signature.dss"));
        configureJSch(jsch);
        jsch.addIdentity(key.toAbsolutePath().toString());
        var hosts = key.resolveSibling("known_hosts");
        jsch.setKnownHosts(hosts.toAbsolutePath().toString());
        return jsch;
    }


    protected JSch getJSch(OpenSshConfig.Host hc, FS fs) throws JSchException {
        //new OpenSshConfig.Host()
        //ignored .ssh folder
        return super.getJSch(new OpenSshConfig.Host(), fs);
    }

}
