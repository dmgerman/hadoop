begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|webapp
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
comment|/**  * Common web service parameters which could be used in  * RM/NM/AHS WebService.  *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"YARN"
block|}
argument_list|)
DECL|interface|YarnWebServiceParams
specifier|public
interface|interface
name|YarnWebServiceParams
block|{
comment|// the params used in container-log related web services
DECL|field|CONTAINER_ID
name|String
name|CONTAINER_ID
init|=
literal|"containerid"
decl_stmt|;
DECL|field|CONTAINER_LOG_FILE_NAME
name|String
name|CONTAINER_LOG_FILE_NAME
init|=
literal|"filename"
decl_stmt|;
DECL|field|RESPONSE_CONTENT_FORMAT
name|String
name|RESPONSE_CONTENT_FORMAT
init|=
literal|"format"
decl_stmt|;
DECL|field|RESPONSE_CONTENT_SIZE
name|String
name|RESPONSE_CONTENT_SIZE
init|=
literal|"size"
decl_stmt|;
DECL|field|NM_ID
name|String
name|NM_ID
init|=
literal|"nm.id"
decl_stmt|;
DECL|field|REDIRECTED_FROM_NODE
name|String
name|REDIRECTED_FROM_NODE
init|=
literal|"redirected_from_node"
decl_stmt|;
block|}
end_interface

end_unit

