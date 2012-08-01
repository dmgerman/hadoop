begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
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
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * Service interface for components to be managed by the {@link Server} class.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|Service
specifier|public
interface|interface
name|Service
block|{
comment|/**    * Initializes the service. This method is called once, when the    * {@link Server} owning the service is being initialized.    *    * @param server the server initializing the service, give access to the    * server context.    *    * @throws ServiceException thrown if the service could not be initialized.    */
DECL|method|init (Server server)
specifier|public
name|void
name|init
parameter_list|(
name|Server
name|server
parameter_list|)
throws|throws
name|ServiceException
function_decl|;
comment|/**    * Post initializes the service. This method is called by the    * {@link Server} after all services of the server have been initialized.    *    * @throws ServiceException thrown if the service could not be    * post-initialized.    */
DECL|method|postInit ()
specifier|public
name|void
name|postInit
parameter_list|()
throws|throws
name|ServiceException
function_decl|;
comment|/**    * Destroy the services.  This method is called once, when the    * {@link Server} owning the service is being destroyed.    */
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
function_decl|;
comment|/**    * Returns the service dependencies of this service. The service will be    * instantiated only if all the service dependencies are already initialized.    *    * @return the service dependencies.    */
DECL|method|getServiceDependencies ()
specifier|public
name|Class
index|[]
name|getServiceDependencies
parameter_list|()
function_decl|;
comment|/**    * Returns the interface implemented by this service. This interface is used    * the {@link Server} when the {@link Server#get(Class)} method is used to    * retrieve a service.    *    * @return the interface that identifies the service.    */
DECL|method|getInterface ()
specifier|public
name|Class
name|getInterface
parameter_list|()
function_decl|;
comment|/**    * Notification callback when the server changes its status.    *    * @param oldStatus old server status.    * @param newStatus new server status.    *    * @throws ServiceException thrown if the service could not process the status change.    */
DECL|method|serverStatusChange (Server.Status oldStatus, Server.Status newStatus)
specifier|public
name|void
name|serverStatusChange
parameter_list|(
name|Server
operator|.
name|Status
name|oldStatus
parameter_list|,
name|Server
operator|.
name|Status
name|newStatus
parameter_list|)
throws|throws
name|ServiceException
function_decl|;
block|}
end_interface

end_unit

