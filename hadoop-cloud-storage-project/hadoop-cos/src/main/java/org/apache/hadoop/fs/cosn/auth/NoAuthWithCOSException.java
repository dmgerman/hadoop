begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn.auth
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
operator|.
name|auth
package|;
end_package

begin_import
import|import
name|com
operator|.
name|qcloud
operator|.
name|cos
operator|.
name|exception
operator|.
name|CosClientException
import|;
end_import

begin_comment
comment|/**  * Exception thrown when no credentials can be obtained.  */
end_comment

begin_class
DECL|class|NoAuthWithCOSException
specifier|public
class|class
name|NoAuthWithCOSException
extends|extends
name|CosClientException
block|{
DECL|method|NoAuthWithCOSException (String message, Throwable t)
specifier|public
name|NoAuthWithCOSException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
DECL|method|NoAuthWithCOSException (String message)
specifier|public
name|NoAuthWithCOSException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|NoAuthWithCOSException (Throwable t)
specifier|public
name|NoAuthWithCOSException
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|super
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

