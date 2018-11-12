begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.common.helpers
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
name|container
operator|.
name|common
operator|.
name|helpers
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
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
import|;
end_import

begin_comment
comment|/**  * Exceptions thrown when a container is in invalid state while doing a I/O.  */
end_comment

begin_class
DECL|class|InvalidContainerStateException
specifier|public
class|class
name|InvalidContainerStateException
extends|extends
name|StorageContainerException
block|{
comment|/**    * Constructs an {@code IOException} with the specified detail message.    *    * @param message The detail message (which is saved for later retrieval by    * the {@link #getMessage()} method)    */
DECL|method|InvalidContainerStateException (String message)
specifier|public
name|InvalidContainerStateException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|ContainerProtos
operator|.
name|Result
operator|.
name|INVALID_CONTAINER_STATE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

