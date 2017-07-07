begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|container
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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

begin_comment
comment|/**  * Mapping class contains the mapping from a name to a pipeline mapping. This is  * used by SCM when allocating new locations and when looking up a key.  */
end_comment

begin_interface
DECL|interface|Mapping
specifier|public
interface|interface
name|Mapping
extends|extends
name|Closeable
block|{
comment|/**    * Returns the Pipeline from the container name.    *    * @param containerName - Name    * @return - Pipeline that makes up this container.    * @throws IOException    */
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
comment|/**    * Returns pipelines under certain conditions.    * Search container names from start name(exclusive),    * and use prefix name to filter the result. The max    * size of the searching range cannot exceed the    * value of count.    *    * @param startName start name, if null, start searching at the head.    * @param prefixName prefix name, if null, then filter is disabled.    * @param count count, if count< 0, the max size is unlimited.(    *              Usually the count will be replace with a very big    *              value instead of being unlimited in case the db is very big)    *    * @return a list of pipeline.    * @throws IOException    */
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
comment|/**    * Allocates a new container for a given keyName.    *    * @param containerName - Name    * @return - Pipeline that makes up this container.    * @throws IOException    */
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
comment|/**    * Allocates a new container for a given keyName and replication factor.    *    * @param containerName - Name.    * @param replicationFactor - replication factor of the container.    * @return - Pipeline that makes up this container.    * @throws IOException    */
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
comment|/**    * Deletes a container from SCM.    *    * @param containerName - Container Name    * @throws IOException    */
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

