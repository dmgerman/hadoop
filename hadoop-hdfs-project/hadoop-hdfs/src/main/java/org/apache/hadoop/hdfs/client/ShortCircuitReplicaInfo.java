begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
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
name|security
operator|.
name|token
operator|.
name|SecretManager
operator|.
name|InvalidToken
import|;
end_import

begin_class
DECL|class|ShortCircuitReplicaInfo
specifier|public
specifier|final
class|class
name|ShortCircuitReplicaInfo
block|{
DECL|field|replica
specifier|private
specifier|final
name|ShortCircuitReplica
name|replica
decl_stmt|;
DECL|field|exc
specifier|private
specifier|final
name|InvalidToken
name|exc
decl_stmt|;
DECL|method|ShortCircuitReplicaInfo ()
specifier|public
name|ShortCircuitReplicaInfo
parameter_list|()
block|{
name|this
operator|.
name|replica
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|exc
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|ShortCircuitReplicaInfo (ShortCircuitReplica replica)
specifier|public
name|ShortCircuitReplicaInfo
parameter_list|(
name|ShortCircuitReplica
name|replica
parameter_list|)
block|{
name|this
operator|.
name|replica
operator|=
name|replica
expr_stmt|;
name|this
operator|.
name|exc
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|ShortCircuitReplicaInfo (InvalidToken exc)
specifier|public
name|ShortCircuitReplicaInfo
parameter_list|(
name|InvalidToken
name|exc
parameter_list|)
block|{
name|this
operator|.
name|replica
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|exc
operator|=
name|exc
expr_stmt|;
block|}
DECL|method|getReplica ()
specifier|public
name|ShortCircuitReplica
name|getReplica
parameter_list|()
block|{
return|return
name|replica
return|;
block|}
DECL|method|getInvalidTokenException ()
specifier|public
name|InvalidToken
name|getInvalidTokenException
parameter_list|()
block|{
return|return
name|exc
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"ShortCircuitReplicaInfo{"
argument_list|)
expr_stmt|;
if|if
condition|(
name|replica
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|", "
expr_stmt|;
block|}
if|if
condition|(
name|exc
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|exc
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|", "
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

