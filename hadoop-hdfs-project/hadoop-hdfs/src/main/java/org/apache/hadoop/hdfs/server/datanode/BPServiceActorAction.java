begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
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
name|hdfs
operator|.
name|protocolPB
operator|.
name|DatanodeProtocolClientSideTranslatorPB
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|DatanodeRegistration
import|;
end_import

begin_comment
comment|/**  * Base class for BPServiceActor class  * Issued by BPOfferSerivce class to tell BPServiceActor   * to take several actions.  */
end_comment

begin_interface
DECL|interface|BPServiceActorAction
specifier|public
interface|interface
name|BPServiceActorAction
block|{
DECL|method|reportTo (DatanodeProtocolClientSideTranslatorPB bpNamenode, DatanodeRegistration bpRegistration)
specifier|public
name|void
name|reportTo
parameter_list|(
name|DatanodeProtocolClientSideTranslatorPB
name|bpNamenode
parameter_list|,
name|DatanodeRegistration
name|bpRegistration
parameter_list|)
throws|throws
name|BPServiceActorActionException
function_decl|;
block|}
end_interface

end_unit

