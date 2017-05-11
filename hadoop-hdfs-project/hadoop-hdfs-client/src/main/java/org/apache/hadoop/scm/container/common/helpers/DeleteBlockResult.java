begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.scm.container.common.helpers
package|package
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerLocationProtocolProtos
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
DECL|field|key
specifier|private
name|String
name|key
decl_stmt|;
DECL|field|result
specifier|private
name|DeleteScmBlockResult
operator|.
name|Result
name|result
decl_stmt|;
DECL|method|DeleteBlockResult (final String key, final DeleteScmBlockResult.Result result)
specifier|public
name|DeleteBlockResult
parameter_list|(
specifier|final
name|String
name|key
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
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
block|}
comment|/**    * Get key deleted.    * @return key name.    */
DECL|method|getKey ()
specifier|public
name|String
name|getKey
parameter_list|()
block|{
return|return
name|key
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

