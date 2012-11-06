begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
comment|/** helper utils for tests */
end_comment

begin_class
DECL|class|SecurityUtilTestHelper
specifier|public
class|class
name|SecurityUtilTestHelper
block|{
comment|/**    * Allow tests to change the resolver used for tokens    * @param flag boolean for whether token services use ips or hosts    */
DECL|method|setTokenServiceUseIp (boolean flag)
specifier|public
specifier|static
name|void
name|setTokenServiceUseIp
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|SecurityUtil
operator|.
name|setTokenServiceUseIp
argument_list|(
name|flag
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return true if externalKdc=true and the location of the krb5.conf    * file has been specified, and false otherwise.    */
DECL|method|isExternalKdcRunning ()
specifier|public
specifier|static
name|boolean
name|isExternalKdcRunning
parameter_list|()
block|{
name|String
name|externalKdc
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"externalKdc"
argument_list|)
decl_stmt|;
name|String
name|krb5Conf
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.security.krb5.conf"
argument_list|)
decl_stmt|;
if|if
condition|(
name|externalKdc
operator|==
literal|null
operator|||
operator|!
name|externalKdc
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
operator|||
name|krb5Conf
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

