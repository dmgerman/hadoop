begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test.system.process
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|system
operator|.
name|process
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Interface to manage the remote processes in the cluster.  */
end_comment

begin_interface
DECL|interface|ClusterProcessManager
specifier|public
interface|interface
name|ClusterProcessManager
block|{
comment|/**    * Initialization method to pass the configuration object which is required     * by the ClusterProcessManager to manage the cluster.<br/>    * Configuration object should typically contain all the parameters which are     * required by the implementations.<br/>    *      * @param conf configuration containing values of the specific keys which     * are required by the implementation of the cluster process manger.    *     * @throws IOException when initialization fails.    */
DECL|method|init (Configuration conf)
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the list of RemoteProcess handles of all the remote processes.    */
DECL|method|getAllProcesses ()
name|List
argument_list|<
name|RemoteProcess
argument_list|>
name|getAllProcesses
parameter_list|()
function_decl|;
comment|/**    * Get all the roles this cluster's daemon processes have.    */
DECL|method|getRoles ()
name|Set
argument_list|<
name|Enum
argument_list|<
name|?
argument_list|>
argument_list|>
name|getRoles
parameter_list|()
function_decl|;
comment|/**    * Method to start all the remote daemons.<br/>    *     * @throws IOException if startup procedure fails.    */
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Starts the daemon from the user specified conf dir.    * @param newConfLocation the dir where the new conf files reside.    * @throws IOException    */
DECL|method|start (String newConfLocation)
name|void
name|start
parameter_list|(
name|String
name|newConfLocation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Stops the daemon running from user specified conf dir.    *     * @param newConfLocation    *          the dir where ther new conf files reside.    * @throws IOException    */
DECL|method|stop (String newConfLocation)
name|void
name|stop
parameter_list|(
name|String
name|newConfLocation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Method to shutdown all the remote daemons.<br/>    *     * @throws IOException if shutdown procedure fails.    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets if multi-user support is enabled for this cluster.     *<br/>    * @return true if multi-user support is enabled.    * @throws IOException    */
DECL|method|isMultiUserSupported ()
name|boolean
name|isMultiUserSupported
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * The pushConfig is used to push a new config to the daemons.    * @param localDir    * @return is the remoteDir location where config will be pushed    * @throws IOException    */
DECL|method|pushConfig (String localDir)
name|String
name|pushConfig
parameter_list|(
name|String
name|localDir
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

