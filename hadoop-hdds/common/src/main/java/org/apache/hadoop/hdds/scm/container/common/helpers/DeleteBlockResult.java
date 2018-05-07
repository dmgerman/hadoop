begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|client
operator|.
name|BlockID
import|;
end_import

begin_import
import|import static
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
name|proto
operator|.
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmBlockResult
import|;
end_import

begin_comment
comment|/**  * Class wraps storage container manager block deletion results.  */
end_comment

begin_class
DECL|class|DeleteBlockResult
specifier|public
class|class
name|DeleteBlockResult
block|{
DECL|field|blockID
specifier|private
name|BlockID
name|blockID
decl_stmt|;
DECL|field|result
specifier|private
name|DeleteScmBlockResult
operator|.
name|Result
name|result
decl_stmt|;
DECL|method|DeleteBlockResult (final BlockID blockID, final DeleteScmBlockResult.Result result)
specifier|public
name|DeleteBlockResult
parameter_list|(
specifier|final
name|BlockID
name|blockID
parameter_list|,
specifier|final
name|DeleteScmBlockResult
operator|.
name|Result
name|result
parameter_list|)
block|{
name|this
operator|.
name|blockID
operator|=
name|blockID
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Get block id deleted.    * @return block id.    */
DECL|method|getBlockID ()
specifier|public
name|BlockID
name|getBlockID
parameter_list|()
block|{
return|return
name|blockID
return|;
block|}
comment|/**    * Get key deletion result.    * @return key deletion result.    */
DECL|method|getResult ()
specifier|public
name|DeleteScmBlockResult
operator|.
name|Result
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

