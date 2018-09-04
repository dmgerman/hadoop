begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.replication
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|replication
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * Contract to prepare provide the container in binary form..  *<p>  * Prepare will be called when container is closed. An implementation could  * precache any binary representation of a container and store the pre packede  * images.  */
end_comment

begin_interface
DECL|interface|ContainerReplicationSource
specifier|public
interface|interface
name|ContainerReplicationSource
block|{
comment|/**    * Prepare for the replication.    *    * @param containerId The name of the container the package.    */
DECL|method|prepare (long containerId)
name|void
name|prepare
parameter_list|(
name|long
name|containerId
parameter_list|)
function_decl|;
comment|/**    * Copy the container data to an output stream.    *    * @param containerId Container to replicate    * @param destination   The destination stream to copy all the container data.    * @throws IOException    */
DECL|method|copyData (long containerId, OutputStream destination)
name|void
name|copyData
parameter_list|(
name|long
name|containerId
parameter_list|,
name|OutputStream
name|destination
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

