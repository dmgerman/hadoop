begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

begin_comment
comment|/**  * Standard strings to use in exception messages  * in {@link KerberosAuthException} when throwing.  */
end_comment

begin_class
DECL|class|UGIExceptionMessages
specifier|final
class|class
name|UGIExceptionMessages
block|{
DECL|field|FAILURE_TO_LOGIN
specifier|public
specifier|static
specifier|final
name|String
name|FAILURE_TO_LOGIN
init|=
literal|"failure to login:"
decl_stmt|;
DECL|field|FOR_USER
specifier|public
specifier|static
specifier|final
name|String
name|FOR_USER
init|=
literal|" for user: "
decl_stmt|;
DECL|field|FOR_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|FOR_PRINCIPAL
init|=
literal|" for principal: "
decl_stmt|;
DECL|field|FROM_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|FROM_KEYTAB
init|=
literal|" from keytab "
decl_stmt|;
DECL|field|LOGIN_FAILURE
specifier|public
specifier|static
specifier|final
name|String
name|LOGIN_FAILURE
init|=
literal|"Login failure"
decl_stmt|;
DECL|field|LOGOUT_FAILURE
specifier|public
specifier|static
specifier|final
name|String
name|LOGOUT_FAILURE
init|=
literal|"Logout failure"
decl_stmt|;
DECL|field|MUST_FIRST_LOGIN
specifier|public
specifier|static
specifier|final
name|String
name|MUST_FIRST_LOGIN
init|=
literal|"login must be done first"
decl_stmt|;
DECL|field|MUST_FIRST_LOGIN_FROM_KEYTAB
specifier|public
specifier|static
specifier|final
name|String
name|MUST_FIRST_LOGIN_FROM_KEYTAB
init|=
literal|"loginUserFromKeyTab must be done first"
decl_stmt|;
DECL|field|SUBJECT_MUST_CONTAIN_PRINCIPAL
specifier|public
specifier|static
specifier|final
name|String
name|SUBJECT_MUST_CONTAIN_PRINCIPAL
init|=
literal|"Provided Subject must contain a KerberosPrincipal"
decl_stmt|;
DECL|field|SUBJECT_MUST_NOT_BE_NULL
specifier|public
specifier|static
specifier|final
name|String
name|SUBJECT_MUST_NOT_BE_NULL
init|=
literal|"Subject must not be null"
decl_stmt|;
DECL|field|USING_TICKET_CACHE_FILE
specifier|public
specifier|static
specifier|final
name|String
name|USING_TICKET_CACHE_FILE
init|=
literal|" using ticket cache file: "
decl_stmt|;
comment|//checkstyle: Utility classes should not have a public or default constructor.
DECL|method|UGIExceptionMessages ()
specifier|private
name|UGIExceptionMessages
parameter_list|()
block|{   }
block|}
end_class

end_unit

