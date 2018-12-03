begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
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
comment|/**  * Result of the verification whether the current cluster setup can  * support all enabled EC policies.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ECTopologyVerifierResult
specifier|public
class|class
name|ECTopologyVerifierResult
block|{
DECL|field|resultMessage
specifier|private
specifier|final
name|String
name|resultMessage
decl_stmt|;
DECL|field|isSupported
specifier|private
specifier|final
name|boolean
name|isSupported
decl_stmt|;
DECL|method|ECTopologyVerifierResult (boolean isSupported, String resultMessage)
specifier|public
name|ECTopologyVerifierResult
parameter_list|(
name|boolean
name|isSupported
parameter_list|,
name|String
name|resultMessage
parameter_list|)
block|{
name|this
operator|.
name|resultMessage
operator|=
name|resultMessage
expr_stmt|;
name|this
operator|.
name|isSupported
operator|=
name|isSupported
expr_stmt|;
block|}
DECL|method|getResultMessage ()
specifier|public
name|String
name|getResultMessage
parameter_list|()
block|{
return|return
name|resultMessage
return|;
block|}
DECL|method|isSupported ()
specifier|public
name|boolean
name|isSupported
parameter_list|()
block|{
return|return
name|isSupported
return|;
block|}
block|}
end_class

end_unit

