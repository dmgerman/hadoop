begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|protocol
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|scm
operator|.
name|client
operator|.
name|ScmClient
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
import|;
end_import

begin_comment
comment|/**  * ContainerLocationProtocol is used by an HDFS node to find the set of nodes  * that currently host a container.  */
end_comment

begin_interface
DECL|interface|StorageContainerLocationProtocol
specifier|public
interface|interface
name|StorageContainerLocationProtocol
block|{
comment|/**    * Asks SCM where a container should be allocated. SCM responds with the    * set of datanodes that should be used creating this container.    * @param containerName - Name of the container.    * @return Pipeline.    * @throws IOException    */
DECL|method|allocateContainer (String containerName)
name|Pipeline
name|allocateContainer
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Asks SCM where a container should be allocated. SCM responds with the    * set of datanodes that should be used creating this container.    * @param containerName - Name of the container.    * @param replicationFactor - replication factor.    * @return Pipeline.    * @throws IOException    */
DECL|method|allocateContainer (String containerName, ScmClient.ReplicationFactor replicationFactor)
name|Pipeline
name|allocateContainer
parameter_list|(
name|String
name|containerName
parameter_list|,
name|ScmClient
operator|.
name|ReplicationFactor
name|replicationFactor
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ask SCM the location of the container. SCM responds with a group of    * nodes where this container and its replicas are located.    *    * @param containerName - Name of the container.    * @return Pipeline - the pipeline where container locates.    * @throws IOException    */
DECL|method|getContainer (String containerName)
name|Pipeline
name|getContainer
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Ask SCM a list of pipelines with a range of container names    * and the limit of count.    * Search container names between start name(exclusive), and    * use prefix name to filter the result. the max size of the    * searching range cannot exceed the value of count.    *    * @param startName start name, if null, start searching at the head.    * @param prefixName prefix name, if null, then filter is disabled.    * @param count count, if count< 0, the max size is unlimited.(    *              Usually the count will be replace with a very big    *              value instead of being unlimited in case the db is very big)    *    * @return a list of pipeline.    * @throws IOException    */
DECL|method|listContainer (String startName, String prefixName, int count)
name|List
argument_list|<
name|Pipeline
argument_list|>
name|listContainer
parameter_list|(
name|String
name|startName
parameter_list|,
name|String
name|prefixName
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a container in SCM.    *    * @param containerName    * @throws IOException    *   if failed to delete the container mapping from db store    *   or container doesn't exist.    */
DECL|method|deleteContainer (String containerName)
name|void
name|deleteContainer
parameter_list|(
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

