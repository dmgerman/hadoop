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
name|AmazonServiceException
import|;
end_import

begin_comment
comment|/**  * A 500 response came back from a service.  * This is considered<i>probably</i> retriable, That is, we assume  *<ol>  *<li>whatever error happened in the service itself to have happened  *    before the infrastructure committed the operation.</li>  *<li>Nothing else got through either.</li>  *</ol>  */
end_comment

begin_class
DECL|class|AWSStatus500Exception
specifier|public
class|class
name|AWSStatus500Exception
extends|extends
name|AWSServiceIOException
block|{
DECL|method|AWSStatus500Exception (String operation, AmazonServiceException cause)
specifier|public
name|AWSStatus500Exception
parameter_list|(
name|String
name|operation
parameter_list|,
name|AmazonServiceException
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|operation
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

