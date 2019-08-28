begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * This exception is thrown by the Ozone Clients.  */
end_comment

begin_class
DECL|class|OzoneClientException
specifier|public
class|class
name|OzoneClientException
extends|extends
name|Exception
block|{
DECL|method|OzoneClientException ()
specifier|public
name|OzoneClientException
parameter_list|()
block|{   }
DECL|method|OzoneClientException (String s)
specifier|public
name|OzoneClientException
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|OzoneClientException (String s, Throwable throwable)
specifier|public
name|OzoneClientException
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|OzoneClientException (Throwable throwable)
specifier|public
name|OzoneClientException
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
DECL|method|OzoneClientException (String s, Throwable throwable, boolean b, boolean b1)
specifier|public
name|OzoneClientException
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|,
name|boolean
name|b
parameter_list|,
name|boolean
name|b1
parameter_list|)
block|{
name|super
argument_list|(
name|s
argument_list|,
name|throwable
argument_list|,
name|b
argument_list|,
name|b1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

