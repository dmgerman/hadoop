begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.restclient
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|restclient
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A response for use as a return value from operations  */
end_comment

begin_class
DECL|class|HttpOperationResponse
specifier|public
class|class
name|HttpOperationResponse
block|{
DECL|field|responseCode
specifier|public
name|int
name|responseCode
decl_stmt|;
DECL|field|lastModified
specifier|public
name|long
name|lastModified
decl_stmt|;
DECL|field|contentType
specifier|public
name|String
name|contentType
decl_stmt|;
DECL|field|data
specifier|public
name|byte
index|[]
name|data
decl_stmt|;
DECL|field|headers
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|headers
decl_stmt|;
block|}
end_class

end_unit

