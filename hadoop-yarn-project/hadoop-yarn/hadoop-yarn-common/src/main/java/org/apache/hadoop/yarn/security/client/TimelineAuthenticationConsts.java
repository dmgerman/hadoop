begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.security.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|security
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
import|;
end_import

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
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  * The constants that are going to be used by the timeline Kerberos + delegation  * token authentication.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineAuthenticationConsts
specifier|public
class|class
name|TimelineAuthenticationConsts
block|{
DECL|field|ERROR_EXCEPTION_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_EXCEPTION_JSON
init|=
literal|"exception"
decl_stmt|;
DECL|field|ERROR_CLASSNAME_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_CLASSNAME_JSON
init|=
literal|"javaClassName"
decl_stmt|;
DECL|field|ERROR_MESSAGE_JSON
specifier|public
specifier|static
specifier|final
name|String
name|ERROR_MESSAGE_JSON
init|=
literal|"message"
decl_stmt|;
DECL|field|OP_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|OP_PARAM
init|=
literal|"op"
decl_stmt|;
DECL|field|DELEGATION_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_PARAM
init|=
literal|"delegation"
decl_stmt|;
DECL|field|TOKEN_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_PARAM
init|=
literal|"token"
decl_stmt|;
DECL|field|RENEWER_PARAM
specifier|public
specifier|static
specifier|final
name|String
name|RENEWER_PARAM
init|=
literal|"renewer"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_URL
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_URL
init|=
literal|"url"
decl_stmt|;
DECL|field|DELEGATION_TOKEN_EXPIRATION_TIME
specifier|public
specifier|static
specifier|final
name|String
name|DELEGATION_TOKEN_EXPIRATION_TIME
init|=
literal|"expirationTime"
decl_stmt|;
block|}
end_class

end_unit

