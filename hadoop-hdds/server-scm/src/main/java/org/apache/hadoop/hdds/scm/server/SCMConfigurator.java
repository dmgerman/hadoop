begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|block
operator|.
name|BlockManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|net
operator|.
name|NetworkTopology
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|safemode
operator|.
name|SCMSafeModeManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ContainerManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|ReplicationManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|metadata
operator|.
name|SCMMetadataStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|pipeline
operator|.
name|PipelineManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|certificate
operator|.
name|authority
operator|.
name|CertificateServer
import|;
end_import

begin_comment
comment|/**  * This class acts as an SCM builder Class. This class is important for us  * from a resilience perspective of SCM. This class will allow us swap out  * different managers and replace with out on manager in the testing phase.  *<p>  * At some point in the future, we will make all these managers dynamically  * loadable, so other developers can extend SCM by replacing various managers.  *<p>  * TODO: Add different config keys, so that we can load different managers at  * run time. This will make it easy to extend SCM without having to replace  * whole SCM each time.  *<p>  * Different Managers supported by this builder are:  * NodeManager scmNodeManager;  * PipelineManager pipelineManager;  * ContainerManager containerManager;  * BlockManager scmBlockManager;  * ReplicationManager replicationManager;  * SCMSafeModeManager scmSafeModeManager;  * CertificateServer certificateServer;  * SCMMetadata scmMetadataStore.  *  * If any of these are *not* specified then the default version of these  * managers are used by SCM.  *  */
end_comment

begin_class
DECL|class|SCMConfigurator
specifier|public
specifier|final
class|class
name|SCMConfigurator
block|{
DECL|field|scmNodeManager
specifier|private
name|NodeManager
name|scmNodeManager
decl_stmt|;
DECL|field|pipelineManager
specifier|private
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|containerManager
specifier|private
name|ContainerManager
name|containerManager
decl_stmt|;
DECL|field|scmBlockManager
specifier|private
name|BlockManager
name|scmBlockManager
decl_stmt|;
DECL|field|replicationManager
specifier|private
name|ReplicationManager
name|replicationManager
decl_stmt|;
DECL|field|scmSafeModeManager
specifier|private
name|SCMSafeModeManager
name|scmSafeModeManager
decl_stmt|;
DECL|field|certificateServer
specifier|private
name|CertificateServer
name|certificateServer
decl_stmt|;
DECL|field|metadataStore
specifier|private
name|SCMMetadataStore
name|metadataStore
decl_stmt|;
DECL|field|networkTopology
specifier|private
name|NetworkTopology
name|networkTopology
decl_stmt|;
comment|/**    * Allows user to specify a version of Node manager to use with this SCM.    * @param scmNodeManager - Node Manager.    */
DECL|method|setScmNodeManager (NodeManager scmNodeManager)
specifier|public
name|void
name|setScmNodeManager
parameter_list|(
name|NodeManager
name|scmNodeManager
parameter_list|)
block|{
name|this
operator|.
name|scmNodeManager
operator|=
name|scmNodeManager
expr_stmt|;
block|}
comment|/**    * Allows user to specify a custom version of PipelineManager to use with    * this SCM.    * @param pipelineManager - Pipeline Manager.    */
DECL|method|setPipelineManager (PipelineManager pipelineManager)
specifier|public
name|void
name|setPipelineManager
parameter_list|(
name|PipelineManager
name|pipelineManager
parameter_list|)
block|{
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
block|}
comment|/**    *  Allows user to specify a custom version of containerManager to use with    *  this SCM.    * @param containerManager - Container Manager.    */
DECL|method|setContainerManager (ContainerManager containerManager)
specifier|public
name|void
name|setContainerManager
parameter_list|(
name|ContainerManager
name|containerManager
parameter_list|)
block|{
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
block|}
comment|/**    *  Allows user to specify a custom version of Block Manager to use with    *  this SCM.    * @param scmBlockManager - Block Manager    */
DECL|method|setScmBlockManager (BlockManager scmBlockManager)
specifier|public
name|void
name|setScmBlockManager
parameter_list|(
name|BlockManager
name|scmBlockManager
parameter_list|)
block|{
name|this
operator|.
name|scmBlockManager
operator|=
name|scmBlockManager
expr_stmt|;
block|}
comment|/**    * Allows user to specify a custom version of Replication Manager to use    * with this SCM.    * @param replicationManager - replication Manager.    */
DECL|method|setReplicationManager (ReplicationManager replicationManager)
specifier|public
name|void
name|setReplicationManager
parameter_list|(
name|ReplicationManager
name|replicationManager
parameter_list|)
block|{
name|this
operator|.
name|replicationManager
operator|=
name|replicationManager
expr_stmt|;
block|}
comment|/**    * Allows user to specify a custom version of Safe Mode Manager to use    * with this SCM.    * @param scmSafeModeManager - SafeMode Manager.    */
DECL|method|setScmSafeModeManager (SCMSafeModeManager scmSafeModeManager)
specifier|public
name|void
name|setScmSafeModeManager
parameter_list|(
name|SCMSafeModeManager
name|scmSafeModeManager
parameter_list|)
block|{
name|this
operator|.
name|scmSafeModeManager
operator|=
name|scmSafeModeManager
expr_stmt|;
block|}
comment|/**    * Allows user to specify a custom version of Certificate Server to use    * with this SCM.    * @param certificateAuthority - Certificate server.    */
DECL|method|setCertificateServer (CertificateServer certificateAuthority)
specifier|public
name|void
name|setCertificateServer
parameter_list|(
name|CertificateServer
name|certificateAuthority
parameter_list|)
block|{
name|this
operator|.
name|certificateServer
operator|=
name|certificateAuthority
expr_stmt|;
block|}
comment|/**    * Allows user to specify a custom version of Metadata Store to  be used    * with this SCM.    * @param scmMetadataStore - scm metadata store.    */
DECL|method|setMetadataStore (SCMMetadataStore scmMetadataStore)
specifier|public
name|void
name|setMetadataStore
parameter_list|(
name|SCMMetadataStore
name|scmMetadataStore
parameter_list|)
block|{
name|this
operator|.
name|metadataStore
operator|=
name|scmMetadataStore
expr_stmt|;
block|}
comment|/**    * Allows user to specify a custom version of Network Topology Cluster    * to  be used with this SCM.    * @param networkTopology - network topology cluster.    */
DECL|method|setNetworkTopology (NetworkTopology networkTopology)
specifier|public
name|void
name|setNetworkTopology
parameter_list|(
name|NetworkTopology
name|networkTopology
parameter_list|)
block|{
name|this
operator|.
name|networkTopology
operator|=
name|networkTopology
expr_stmt|;
block|}
comment|/**    * Gets SCM Node Manager.    * @return Node Manager.    */
DECL|method|getScmNodeManager ()
specifier|public
name|NodeManager
name|getScmNodeManager
parameter_list|()
block|{
return|return
name|scmNodeManager
return|;
block|}
comment|/**    * Get Pipeline Manager.    * @return pipeline manager.    */
DECL|method|getPipelineManager ()
specifier|public
name|PipelineManager
name|getPipelineManager
parameter_list|()
block|{
return|return
name|pipelineManager
return|;
block|}
comment|/**    * Get Container Manager.    * @return container Manger.    */
DECL|method|getContainerManager ()
specifier|public
name|ContainerManager
name|getContainerManager
parameter_list|()
block|{
return|return
name|containerManager
return|;
block|}
comment|/**    * Get SCM Block Manager.    * @return Block Manager.    */
DECL|method|getScmBlockManager ()
specifier|public
name|BlockManager
name|getScmBlockManager
parameter_list|()
block|{
return|return
name|scmBlockManager
return|;
block|}
comment|/**    * Get Replica Manager.    * @return Replica Manager.    */
DECL|method|getReplicationManager ()
specifier|public
name|ReplicationManager
name|getReplicationManager
parameter_list|()
block|{
return|return
name|replicationManager
return|;
block|}
comment|/**    * Gets Safe Mode Manager.    * @return Safe Mode manager.    */
DECL|method|getScmSafeModeManager ()
specifier|public
name|SCMSafeModeManager
name|getScmSafeModeManager
parameter_list|()
block|{
return|return
name|scmSafeModeManager
return|;
block|}
comment|/**    * Get Certificate Manager.    * @return Certificate Manager.    */
DECL|method|getCertificateServer ()
specifier|public
name|CertificateServer
name|getCertificateServer
parameter_list|()
block|{
return|return
name|certificateServer
return|;
block|}
comment|/**    * Get Metadata Store.    * @return SCMMetadataStore.    */
DECL|method|getMetadataStore ()
specifier|public
name|SCMMetadataStore
name|getMetadataStore
parameter_list|()
block|{
return|return
name|metadataStore
return|;
block|}
comment|/**    * Get network topology cluster tree.    * @return NetworkTopology.    */
DECL|method|getNetworkTopology ()
specifier|public
name|NetworkTopology
name|getNetworkTopology
parameter_list|()
block|{
return|return
name|networkTopology
return|;
block|}
block|}
end_class

end_unit

