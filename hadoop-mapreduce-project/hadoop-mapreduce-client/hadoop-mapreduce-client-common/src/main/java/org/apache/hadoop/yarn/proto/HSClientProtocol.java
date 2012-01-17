begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.proto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|proto
package|;
end_package

begin_comment
comment|/**  * Fake protocol to differentiate the blocking interfaces in the   * security info class loaders.  */
end_comment

begin_interface
DECL|interface|HSClientProtocol
specifier|public
interface|interface
name|HSClientProtocol
block|{
DECL|class|HSClientProtocolService
specifier|public
specifier|abstract
class|class
name|HSClientProtocolService
block|{
DECL|interface|BlockingInterface
specifier|public
interface|interface
name|BlockingInterface
extends|extends
name|MRClientProtocol
operator|.
name|MRClientProtocolService
operator|.
name|BlockingInterface
block|{     }
block|}
block|}
end_interface

end_unit

