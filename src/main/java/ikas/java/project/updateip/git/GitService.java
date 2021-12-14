package ikas.java.project.updateip.git;

import ikas.java.project.updateip.NonNull;
import ikas.java.project.updateip.Nullable;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.SshTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;


public class GitService {

    private final SshSessionFactory sshSessionFactory;

    private final static Logger logger = LoggerFactory.getLogger(GitService.class);

    private final Path workFolder;
    private final Path key;
    private final String repoUri;

    public GitService(@NonNull String repoUri, @NonNull Path workFolder, @NonNull Path key) {
        this.repoUri = repoUri;
        this.workFolder = workFolder;
        this.key = key;
        this.sshSessionFactory = new SshSessionFactory(key);
    }

    @Nullable
    public Repository buildRepository(@NonNull File workTree) {
        var repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setWorkTree(workTree);
        repositoryBuilder.setMustExist(false);
        Repository repository = null;
        try {
            repository = repositoryBuilder.build();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
        return repository;
    }


    public void cloneRepo() {
        if (Files.exists(workFolder)) {
            throw new RuntimeException("not empty");
        }
        try {
            var git = Git.cloneRepository()//
                    .setURI(repoUri)//
                    .setBare(false)//
                    .setDirectory(workFolder.toFile())//
                    .setTransportConfigCallback(transport -> ((SshTransport) transport).setSshSessionFactory(sshSessionFactory))//
                    .setCloneSubmodules(false)//
                    .call();
            git.checkout().setName("main").call();
        } catch (Exception e) {
            throw new RuntimeException(repoUri + "fork failed", e);
        }
    }

    public void commitAndPush() {
        Git git = null;
        if (Files.exists(workFolder)) {
            var repository = buildRepository(workFolder.toFile());
            if (null != repository && repository.getObjectDatabase() != null) {
                git = new Git(repository);
            }
        }

        if (null == git) {
            throw new RuntimeException("read repo failed");
        }

        try {
            git.add().addFilepattern(".").call();
            git.commit()//
                    .setAll(true)
                    .setCommitter("auto", "auto@auto.auto")//
                    .setMessage(LocalDateTime.now().toString()).call();//
        } catch (GitAPIException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        try {
            git.push()//
                    .setTransportConfigCallback(transport -> ((SshTransport) transport).setSshSessionFactory(sshSessionFactory))//
                    .setForce(true)//
                    .call();//
        } catch (GitAPIException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public void pull() {
        Git git = null;
        if (Files.exists(workFolder)) {
            var repository = buildRepository(workFolder.toFile());
            if (null != repository && repository.getObjectDatabase() != null) {
                git = new Git(repository);
            }
        }

        if (null == git) {
            throw new RuntimeException("repo is empty");
        }

        try {
            git.pull()//
                    .setTransportConfigCallback(transport -> ((SshTransport) transport).setSshSessionFactory(sshSessionFactory))//
                    .setRemoteBranchName("main")//
                    .setStrategy(MergeStrategy.THEIRS)//
                    .call();//
        } catch (Exception e) {
            throw new RuntimeException("pull failed", e);
        }
    }

}
