begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
import|;
end_import

begin_comment
comment|/**  * The protocol name that is used when a client and server connect.  * By default the class name of the protocol interface is the protocol name.  *   * Why override the default name (i.e. the class name)?  * One use case overriding the default name (i.e. the class name) is when  * there are multiple implementations of the same protocol, each with say a  *  different version/serialization.  * In Hadoop this is used to allow multiple server and client adapters  * for different versions of the same protocol service.  */
end_comment

begin_annotation_defn
annotation|@
name|Retention
argument_list|(
name|RetentionPolicy
operator|.
name|RUNTIME
argument_list|)
DECL|annotation|ProtocolInfo
specifier|public
annotation_defn|@interface
name|ProtocolInfo
block|{
DECL|method|protocolName ()
name|String
name|protocolName
parameter_list|()
function_decl|;
comment|// the name of the protocol (i.e. rpc service)
DECL|method|protocolVersion ()
name|long
name|protocolVersion
parameter_list|()
default|default
operator|-
literal|1
function_decl|;
comment|// default means not defined use old way
block|}
end_annotation_defn

end_unit

