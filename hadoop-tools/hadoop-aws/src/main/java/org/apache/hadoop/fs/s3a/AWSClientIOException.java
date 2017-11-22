begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|SdkBaseException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * IOException equivalent of an {@link AmazonClientException}.  */
end_comment

begin_class
DECL|class|AWSClientIOException
specifier|public
class|class
name|AWSClientIOException
extends|extends
name|IOException
block|{
DECL|field|operation
specifier|private
specifier|final
name|String
name|operation
decl_stmt|;
DECL|method|AWSClientIOException (String operation, SdkBaseException cause)
specifier|public
name|AWSClientIOException
parameter_list|(
name|String
name|operation
parameter_list|,
name|SdkBaseException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|operation
operator|!=
literal|null
argument_list|,
literal|"Null 'operation' argument"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|cause
operator|!=
literal|null
argument_list|,
literal|"Null 'cause' argument"
argument_list|)
expr_stmt|;
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
block|}
DECL|method|getCause ()
specifier|public
name|AmazonClientException
name|getCause
parameter_list|()
block|{
return|return
operator|(
name|AmazonClientException
operator|)
name|super
operator|.
name|getCause
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|operation
operator|+
literal|": "
operator|+
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
end_class

end_unit

