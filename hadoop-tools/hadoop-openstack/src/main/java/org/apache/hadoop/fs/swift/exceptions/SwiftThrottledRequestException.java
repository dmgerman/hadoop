begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|exceptions
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Exception raised if a Swift endpoint returned a HTTP response indicating  * the caller is being throttled.  */
end_comment

begin_class
DECL|class|SwiftThrottledRequestException
specifier|public
class|class
name|SwiftThrottledRequestException
extends|extends
name|SwiftInvalidResponseException
block|{
DECL|method|SwiftThrottledRequestException (String message, String operation, URI uri, HttpResponse resp)
specifier|public
name|SwiftThrottledRequestException
parameter_list|(
name|String
name|message
parameter_list|,
name|String
name|operation
parameter_list|,
name|URI
name|uri
parameter_list|,
name|HttpResponse
name|resp
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|operation
argument_list|,
name|uri
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

