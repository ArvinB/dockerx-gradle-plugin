/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*******************************************************************************/

package io.github.arvinb.dockerx;

import java.util.*;
import java.nio.file.*;
import javax.inject.Inject;
import org.gradle.api.*;
import org.gradle.api.file.*;
import org.gradle.api.model.*;
import org.gradle.api.provider.*;
import io.github.arvinb.dockerx.config.*;
import io.github.arvinb.dockerx.config.DockerXCredentialConfig.*;
import io.github.arvinb.dockerx.config.DockerXDigestConfig.DigestItem;
import io.github.arvinb.dockerx.config.DockerXDockerConfig.PlatformItem;
import io.github.arvinb.dockerx.config.DockerXDownloadConfig.DownloadItem;
import io.github.arvinb.dockerx.config.DockerXExecuteConfig.*;
import static io.github.arvinb.dockerx.DockerXConstants.*;

public abstract class DockerXExtension {

    private final ObjectFactory objectFactory;
    private final ProjectLayout projectLayout;
    
    private final Property<String> workingDir;
    private final Property<Boolean> dryrun;
    private final Property<Boolean> skip;

    private final DockerXCredentialConfig dockerCredConfig, downloadCredConfig, gitHubCredConfig;

    private final DockerXCatalogConfig  catalogConfig;
    private final DockerXDigestConfig   digestConfig;
    private final DockerXDockerConfig   dockerConfig;
    private final DockerXDownloadConfig downloadConfig;
    private final DockerXExecuteConfig  execConfig;
    private final DockerXGitHubConfig   gitHubConfig;
    private final DockerXOPMConfig      opmConfig;
    private final DockerXSQLiteConfig   sqliteConfig;

    @Inject
    public DockerXExtension(ObjectFactory objectFactory, ProjectLayout projectLayout) {
        
        this.objectFactory = objectFactory;
        this.projectLayout = projectLayout;

        this.workingDir = objectFactory.property(String.class);
        this.dryrun     = objectFactory.property(Boolean.class);
        this.skip       = objectFactory.property(Boolean.class);

        this.dockerCredConfig   = objectFactory.newInstance(DockerXCredentialConfig.class);
        this.downloadCredConfig = objectFactory.newInstance(DockerXCredentialConfig.class);
        this.gitHubCredConfig   = objectFactory.newInstance(DockerXCredentialConfig.class);

        this.catalogConfig  = objectFactory.newInstance(DockerXCatalogConfig.class);
        this.digestConfig   = objectFactory.newInstance(DockerXDigestConfig.class);
        this.dockerConfig   = objectFactory.newInstance(DockerXDockerConfig.class);
        this.downloadConfig = objectFactory.newInstance(DockerXDownloadConfig.class);
        this.execConfig     = objectFactory.newInstance(DockerXExecuteConfig.class);
        this.gitHubConfig   = objectFactory.newInstance(DockerXGitHubConfig.class);
        this.opmConfig      = objectFactory.newInstance(DockerXOPMConfig.class);
        this.sqliteConfig   = objectFactory.newInstance(DockerXSQLiteConfig.class);

        initDefaultValues();
    }

    ///

    public Property<String> getWorkingDir() { return workingDir; }
    public Property<Boolean> getDryrun()    { return dryrun;     }
    public Property<Boolean> getSkip()      { return skip;       }

    ///
    
    public void dockerCreds(Action<? super DockerXCredentialConfig> action)   { action.execute(dockerCredConfig);   }
    public void downloadCreds(Action<? super DockerXCredentialConfig> action) { action.execute(downloadCredConfig); }
    public void gitHubCreds(Action<? super DockerXCredentialConfig> action)   { action.execute(gitHubCredConfig);   }
    public void catalogSpecs(Action<? super DockerXCatalogConfig> action)     { action.execute(catalogConfig);      }
    public void digestSpecs(Action<? super DockerXDigestConfig> action)       { action.execute(digestConfig);       }
    public void dockerSpecs(Action<? super DockerXDockerConfig> action)       { action.execute(dockerConfig);       }
    public void downloadSpecs(Action<? super DockerXDownloadConfig> action)   { action.execute(downloadConfig);     }
    public void execSpecs(Action<? super DockerXExecuteConfig> action)        { action.execute(execConfig);         }
    public void gitHubSpecs(Action<? super DockerXGitHubConfig> action)       { action.execute(gitHubConfig);       }
    public void opmSpecs(Action<? super DockerXOPMConfig> action)             { action.execute(opmConfig);          }
    public void sqliteSpecs(Action<? super DockerXSQLiteConfig> action)       { action.execute(sqliteConfig);       }

    public DockerXCredentialConfig getDockerCredConfig()   { return this.dockerCredConfig;   }
    public DockerXCredentialConfig getDownloadCredConfig() { return this.downloadCredConfig; }
    public DockerXCredentialConfig getGitHubCredConfig()   { return this.gitHubCredConfig;   }
    public DockerXCatalogConfig getCatalogConfig()         { return this.catalogConfig;      }
    public DockerXDigestConfig getDigestConfig()           { return this.digestConfig;       }
    public DockerXDockerConfig getDockerConfig()           { return this.dockerConfig;       }
    public DockerXDownloadConfig getDownloadConfig()       { return this.downloadConfig;     }
    public DockerXExecuteConfig getExecConfig()         { return this.execConfig;         }
    public DockerXGitHubConfig getGitHubConfig()           { return this.gitHubConfig;       }
    public DockerXOPMConfig getOpmConfig()                 { return this.opmConfig;          }
    public DockerXSQLiteConfig getSqliteConfig()           { return this.sqliteConfig;       }

    ///

    private void initDefaultValues() {

        getWorkingDir().convention(projectLayout.getBuildDirectory().getAsFile().get().getAbsolutePath());
        getDryrun().convention(DEFAULT_TRUE);
        getSkip().convention(DEFAULT_FALSE);

        getDockerConfig().getDevbuild().convention(DEFAULT_FALSE);
        getDockerConfig().getMultiArchReset().convention(DEFAULT_FALSE);
        getDockerConfig().getClean().convention(DEFAULT_TRUE);
        getDockerConfig().getPush().convention(DEFAULT_FALSE);
        getDockerConfig().getBuilder().convention(EMPTY_STRING);

        getDownloadConfig().getOverwrite().convention(DEFAULT_FALSE);
        getDownloadConfig().getIdentityToken().convention(DEFAULT_FALSE);

        getExecConfig().getHideArgs().convention(DEFAULT_TRUE);

        getOpmConfig().getCatalogIcon().convention(CATALOG_ICON);
        getOpmConfig().getCatalogReadme().convention(CATALOG_README);
        getOpmConfig().getClean().convention(DEFAULT_TRUE);
        getOpmConfig().getPush().convention(DEFAULT_FALSE);
        getOpmConfig().getOverwriteMetadata().convention(DEFAULT_FALSE);
        getOpmConfig().getTool().convention(DEFAULT_DOCKER);
        getOpmConfig().getMode().convention(DEFAULT_REPLACES);
        getOpmConfig().getOutputPrefix().convention(DEFAULT_OLM_PACKAGE);
        getOpmConfig().getOutputFormat().convention(DEFAULT_YAML);
        getOpmConfig().getChannels().convention(objectFactory.listProperty(String.class));
        getOpmConfig().getBundleImages().convention(objectFactory.listProperty(String.class));
        getOpmConfig().getBundleSources().convention(objectFactory.listProperty(String.class));

        getSqliteConfig().getDataSourceDb().convention(BUNDLES_DB);
        getSqliteConfig().getStatements().convention(objectFactory.listProperty(String.class));
    }

    ///
    
    public DirectoryProperty getWorkingDirectory() {
        DirectoryProperty workingDirectory = objectFactory.directoryProperty();
        workingDirectory.set( getWorkingDir().map( path -> projectLayout.getProjectDirectory().dir(path) ) );
        return workingDirectory;
    }
    
    public DirectoryProperty getGitHubRepoWorkingDir() {
        DirectoryProperty ghWorkingDir = objectFactory.directoryProperty();
        ghWorkingDir.set( projectLayout.getBuildDirectory() );
        if ( getGitHubConfig().getGitHubURI().isPresent() ) {
            String repoName = Paths.get(getGitHubConfig().getGitHubURI().get()).getFileName().toString();
            int index = repoName.lastIndexOf(".");
            repoName = index >= 1 ? repoName.substring(0, index) : repoName;
            ghWorkingDir.set( projectLayout.getBuildDirectory().dir(repoName) );
        }
        return ghWorkingDir;
    }

    public DirectoryProperty getBundleDirectory() {
        DirectoryProperty bundleDir = objectFactory.directoryProperty();
        bundleDir.set( projectLayout.getBuildDirectory() );
        if ( getOpmConfig().getBundleDir().isPresent() )
            bundleDir.set( getWorkingDirectory().dir(getOpmConfig().getBundleDir().get()) );
        return bundleDir;
    }

    public DirectoryProperty getManifestsDirectory() {
        DirectoryProperty manifestsDir = objectFactory.directoryProperty();
        manifestsDir.set( getBundleDirectory().dir(CMD_LINE_MANIFESTS) );
        return manifestsDir;
    }

    public RegularFileProperty getCatalogReadme() {
        RegularFileProperty catalogReadme = objectFactory.fileProperty();
        if ( getOpmConfig().getCatalogReadme().isPresent() )
            catalogReadme.set( getWorkingDirectory().file(getOpmConfig().getCatalogReadme().get()) );
        return catalogReadme;
    }

    public RegularFileProperty getCatalogIcon() {
        RegularFileProperty catalogIcon = objectFactory.fileProperty();
        if ( getOpmConfig().getCatalogIcon().isPresent() )
            catalogIcon.set( getWorkingDirectory().file(getOpmConfig().getCatalogIcon().get()) );
        return catalogIcon;
    }

    public RegularFileProperty getDataSourceDbFile() {
        RegularFileProperty dataSourceDb = objectFactory.fileProperty();
        if ( getSqliteConfig().getDataSourceDb().isPresent() )
            dataSourceDb.set( getWorkingDirectory().file(getSqliteConfig().getDataSourceDb().get()) );
        return dataSourceDb;
    }

    public RegularFileProperty getDockerfile() {
        RegularFileProperty dockerfile = objectFactory.fileProperty();
        if ( getDockerConfig().getDockerfile().isPresent() )
            dockerfile.set( getWorkingDirectory().file(getDockerConfig().getDockerfile().get()) );
        return dockerfile;
    }
    
    public ListProperty<RegistryCredential> getRegistryCredentials(DockerXCredentialConfig credConfig) {
        ListProperty<RegistryCredential> registryCredentials = objectFactory.listProperty(RegistryCredential.class);
        // Key = Registry | Value = (Username, Password)
        if ( credConfig.getRegistryCredentials().isPresent() ) {
            for ( Map.Entry<String, Map<String, String>> registryCred : credConfig.getRegistryCredentials().get().entrySet() ) {
                for ( Map.Entry<String, String> credEntry : registryCred.getValue().entrySet() ) {
                    Property<String> registry = objectFactory.property(String.class);
                    Property<String> username = objectFactory.property(String.class);
                    Property<String> password = objectFactory.property(String.class);
                    registry.set(registryCred.getKey());
                    username.set(credEntry.getKey());
                    password.set(credEntry.getValue());
                    registryCredentials.add(new RegistryCredential(username, password, registry));
                }
            }
        }
        return registryCredentials;
    }

    public ListProperty<ActionItem> getActionItems() {
        ListProperty<ActionItem> actions = objectFactory.listProperty(DockerXExecuteConfig.ActionItem.class);
        // Key == Action to execute (Default: Working Dir) | Value == Action arguments
        if ( getExecConfig().getActions().isPresent() ) {
            for ( Map.Entry<String, String> actionItem : getExecConfig().getActions().get().entrySet() ) {
                if ( actionItem.getValue() != null ) {
                    RegularFileProperty actionFile = objectFactory.fileProperty();
                    Property<String> actionArgs = objectFactory.property(String.class);
                    actionFile.set(getWorkingDirectory().file(actionItem.getKey()));
                    actionArgs.set(actionItem.getValue());
                    actions.add(new ActionItem(getWorkingDirectory(), actionFile, actionArgs));
                }
            }
        }
        return actions;
    }

    public ListProperty<DigestItem> getDigests() {
        ListProperty<DigestItem> digests = objectFactory.listProperty(DigestItem.class);
        // Key == Image | Value == List of Files (Relative path to working dir)
        if ( getDigestConfig().getDigests().isPresent() ) {
            getDigestConfig().getDigests().get().forEach( (image, targetFiles) -> {
                if ((targetFiles == null) || (targetFiles.isEmpty())) return; // Skip this iteration
                java.util.List<RegularFileProperty> files = new java.util.ArrayList<RegularFileProperty>();
                targetFiles.forEach( targetFile -> {
                    RegularFileProperty digestFile = objectFactory.fileProperty();
                    digestFile.set(getWorkingDirectory().file(targetFile));
                    files.add(digestFile);
                } );
                digests.add( new DigestItem( image, files ) );
            } );
        }
        return digests;
    }

    public ListProperty<DownloadItem> getDownloads() {
        ListProperty<DownloadItem> downloads = objectFactory.listProperty(DownloadItem.class);
        // Key == Download Dir (Default: Working Dir) | Value == Download URL
        if ( getDownloadConfig().getDownloads().isPresent() ) {
            getDownloadConfig().getDownloads().get().forEach( (downloadFolder, downloadUrl) -> {
                if ( downloadUrl != null ) {
                    DirectoryProperty downloadDir = objectFactory.directoryProperty();
                    downloadDir.set( getWorkingDirectory().dir(downloadFolder) );
                    downloads.add( new DownloadItem(downloadDir, downloadUrl) );
                }
            });
        }
        return downloads;
    }

    public ListProperty<PlatformItem> getPlatformItems() {
        ListProperty<PlatformItem> platforms = objectFactory.listProperty(PlatformItem.class);
        if ( dockerConfig.getPlatforms().isPresent() ) {
            dockerConfig.getPlatforms().get().forEach( platform -> {
                PlatformItem platformItem = DockerXDockerConfig.PlatformItem.fromString(platform);
                if ( platformItem != null )
                    platforms.add(platformItem);
            });
        }
        return platforms;
    }

    public MapProperty<String, RegularFileProperty> getSecrets() {
        MapProperty<String, RegularFileProperty> secrets = objectFactory.mapProperty(String.class, RegularFileProperty.class);
        if ( getDockerConfig().getSecrets().isPresent() ) {
            getDockerConfig().getSecrets().get().forEach( (secretId, secretFilename) -> {
                RegularFileProperty secret = objectFactory.fileProperty();
                secret.set( projectLayout.getProjectDirectory().file(secretFilename) );
                secrets.put(secretId, secret);
            });
        }
        return secrets;
    }
}
