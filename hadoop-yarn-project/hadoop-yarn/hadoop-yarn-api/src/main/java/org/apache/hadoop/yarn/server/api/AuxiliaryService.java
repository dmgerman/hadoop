begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
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
name|fs
operator|.
name|Path
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
name|service
operator|.
name|AbstractService
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
name|yarn
operator|.
name|api
operator|.
name|ContainerManagementProtocol
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|StartContainersRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|StartContainersResponse
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_comment
comment|/**  * A generic service that will be started by the NodeManager. This is a service  * that administrators have to configure on each node by setting  * {@link YarnConfiguration#NM_AUX_SERVICES}.  *   */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|AuxiliaryService
specifier|public
specifier|abstract
class|class
name|AuxiliaryService
extends|extends
name|AbstractService
block|{
DECL|field|recoveryPath
specifier|private
name|Path
name|recoveryPath
init|=
literal|null
decl_stmt|;
DECL|field|auxiliaryLocalPathHandler
specifier|private
name|AuxiliaryLocalPathHandler
name|auxiliaryLocalPathHandler
decl_stmt|;
DECL|method|AuxiliaryService (String name)
specifier|protected
name|AuxiliaryService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the path specific to this auxiliary service to use for recovery.    *    * @return state storage path or null if recovery is not enabled    */
DECL|method|getRecoveryPath ()
specifier|protected
name|Path
name|getRecoveryPath
parameter_list|()
block|{
return|return
name|recoveryPath
return|;
block|}
comment|/**    * A new application is started on this NodeManager. This is a signal to    * this {@link AuxiliaryService} about the application initialization.    *     * @param initAppContext context for the application's initialization    */
DECL|method|initializeApplication ( ApplicationInitializationContext initAppContext)
specifier|public
specifier|abstract
name|void
name|initializeApplication
parameter_list|(
name|ApplicationInitializationContext
name|initAppContext
parameter_list|)
function_decl|;
comment|/**    * An application is finishing on this NodeManager. This is a signal to this    * {@link AuxiliaryService} about the same.    *     * @param stopAppContext context for the application termination    */
DECL|method|stopApplication ( ApplicationTerminationContext stopAppContext)
specifier|public
specifier|abstract
name|void
name|stopApplication
parameter_list|(
name|ApplicationTerminationContext
name|stopAppContext
parameter_list|)
function_decl|;
comment|/**    * Retrieve meta-data for this {@link AuxiliaryService}. Applications using    * this {@link AuxiliaryService} SHOULD know the format of the meta-data -    * ideally each service should provide a method to parse out the information    * to the applications. One example of meta-data is contact information so    * that applications can access the service remotely. This will only be called    * after the service's {@link #start()} method has finished. the result may be    * cached.    *     *<p>    * The information is passed along to applications via    * {@link StartContainersResponse#getAllServicesMetaData()} that is returned by    * {@link ContainerManagementProtocol#startContainers(StartContainersRequest)}    *</p>    *     * @return meta-data for this service that should be made available to    *         applications.    */
DECL|method|getMetaData ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getMetaData
parameter_list|()
function_decl|;
comment|/**    * A new container is started on this NodeManager. This is a signal to    * this {@link AuxiliaryService} about the container initialization.    * This method is called when the NodeManager receives the container launch    * command from the ApplicationMaster and before the container process is     * launched.    *    * @param initContainerContext context for the container's initialization    */
DECL|method|initializeContainer (ContainerInitializationContext initContainerContext)
specifier|public
name|void
name|initializeContainer
parameter_list|(
name|ContainerInitializationContext
name|initContainerContext
parameter_list|)
block|{   }
comment|/**    * A container is finishing on this NodeManager. This is a signal to this    * {@link AuxiliaryService} about the same.    *    * @param stopContainerContext context for the container termination    */
DECL|method|stopContainer (ContainerTerminationContext stopContainerContext)
specifier|public
name|void
name|stopContainer
parameter_list|(
name|ContainerTerminationContext
name|stopContainerContext
parameter_list|)
block|{   }
comment|/**    * Set the path for this auxiliary service to use for storing state    * that will be used during recovery.    *    * @param recoveryPath where recoverable state should be stored    */
DECL|method|setRecoveryPath (Path recoveryPath)
specifier|public
name|void
name|setRecoveryPath
parameter_list|(
name|Path
name|recoveryPath
parameter_list|)
block|{
name|this
operator|.
name|recoveryPath
operator|=
name|recoveryPath
expr_stmt|;
block|}
comment|/**    * Method that gets the local dirs path handler for this Auxiliary Service.    *    * @return auxiliaryPathHandler object that is used to read from and write to    * valid local Dirs.    */
DECL|method|getAuxiliaryLocalPathHandler ()
specifier|public
name|AuxiliaryLocalPathHandler
name|getAuxiliaryLocalPathHandler
parameter_list|()
block|{
return|return
name|this
operator|.
name|auxiliaryLocalPathHandler
return|;
block|}
comment|/**    * Method that sets the local dirs path handler for this Auxiliary Service.    *    * @param auxiliaryLocalPathHandler the pathHandler for this auxiliary service    */
DECL|method|setAuxiliaryLocalPathHandler ( AuxiliaryLocalPathHandler auxiliaryLocalPathHandler)
specifier|public
name|void
name|setAuxiliaryLocalPathHandler
parameter_list|(
name|AuxiliaryLocalPathHandler
name|auxiliaryLocalPathHandler
parameter_list|)
block|{
name|this
operator|.
name|auxiliaryLocalPathHandler
operator|=
name|auxiliaryLocalPathHandler
expr_stmt|;
block|}
block|}
end_class

end_unit

