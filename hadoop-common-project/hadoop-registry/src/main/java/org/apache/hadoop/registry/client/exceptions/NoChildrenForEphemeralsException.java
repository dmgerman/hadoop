begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|exceptions
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
import|;
end_import

begin_comment
comment|/**  * This is a manifestation of the Zookeeper restrictions about  * what nodes may act as parents.  *  * Children are not allowed under ephemeral nodes. This is an aspect  * of ZK which isn't directly exposed to the registry API. It may  * surface if the registry is manipulated outside of the registry API.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|NoChildrenForEphemeralsException
specifier|public
class|class
name|NoChildrenForEphemeralsException
extends|extends
name|RegistryIOException
block|{
DECL|method|NoChildrenForEphemeralsException (String path, Throwable cause)
specifier|public
name|NoChildrenForEphemeralsException
parameter_list|(
name|String
name|path
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|method|NoChildrenForEphemeralsException (String path, String error)
specifier|public
name|NoChildrenForEphemeralsException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|error
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
DECL|method|NoChildrenForEphemeralsException (String path, String error, Throwable cause)
specifier|public
name|NoChildrenForEphemeralsException
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|error
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|,
name|error
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

