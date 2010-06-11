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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|kerberos
operator|.
name|KerberosTicket
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|jgss
operator|.
name|krb5
operator|.
name|Krb5Util
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|krb5
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|security
operator|.
name|krb5
operator|.
name|PrincipalName
import|;
end_import

begin_class
DECL|class|SecurityUtil
specifier|public
class|class
name|SecurityUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SecurityUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Find the original TGT within the current subject's credentials. Cross-realm    * TGT's of the form "krbtgt/TWO.COM@ONE.COM" may be present.    *     * @return The TGT from the current subject    * @throws IOException    *           if TGT can't be found    */
DECL|method|getTgtFromSubject ()
specifier|private
specifier|static
name|KerberosTicket
name|getTgtFromSubject
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|KerberosTicket
argument_list|>
name|tickets
init|=
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
operator|.
name|getPrivateCredentials
argument_list|(
name|KerberosTicket
operator|.
name|class
argument_list|)
decl_stmt|;
for|for
control|(
name|KerberosTicket
name|t
range|:
name|tickets
control|)
block|{
if|if
condition|(
name|isOriginalTGT
argument_list|(
name|t
operator|.
name|getServer
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
return|return
name|t
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to find TGT from current Subject"
argument_list|)
throw|;
block|}
comment|// Original TGT must be of form "krbtgt/FOO@FOO". Verify this
DECL|method|isOriginalTGT (String name)
specifier|protected
specifier|static
name|boolean
name|isOriginalTGT
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|String
index|[]
name|components
init|=
name|name
operator|.
name|split
argument_list|(
literal|"[/@]"
argument_list|)
decl_stmt|;
return|return
name|components
operator|.
name|length
operator|==
literal|3
operator|&&
literal|"krbtgt"
operator|.
name|equals
argument_list|(
name|components
index|[
literal|0
index|]
argument_list|)
operator|&&
name|components
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|components
index|[
literal|2
index|]
argument_list|)
return|;
block|}
comment|/**    * Explicitly pull the service ticket for the specified host.  This solves a    * problem with Java's Kerberos SSL problem where the client cannot     * authenticate against a cross-realm service.  It is necessary for clients    * making kerberized https requests to call this method on the target URL    * to ensure that in a cross-realm environment the remote host will be     * successfully authenticated.      *     * This method is internal to Hadoop and should not be used by other     * applications.  This method should not be considered stable or open:     * it will be removed when the Java behavior is changed.    *     * @param remoteHost Target URL the krb-https client will access    * @throws IOException    */
DECL|method|fetchServiceTicket (URL remoteHost)
specifier|public
specifier|static
name|void
name|fetchServiceTicket
parameter_list|(
name|URL
name|remoteHost
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
return|return;
name|String
name|serviceName
init|=
literal|"host/"
operator|+
name|remoteHost
operator|.
name|getHost
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Fetching service ticket for host at: "
operator|+
name|serviceName
argument_list|)
expr_stmt|;
name|Credentials
name|serviceCred
init|=
literal|null
decl_stmt|;
try|try
block|{
name|PrincipalName
name|principal
init|=
operator|new
name|PrincipalName
argument_list|(
name|serviceName
argument_list|,
name|PrincipalName
operator|.
name|KRB_NT_SRV_HST
argument_list|)
decl_stmt|;
name|serviceCred
operator|=
name|Credentials
operator|.
name|acquireServiceCreds
argument_list|(
name|principal
operator|.
name|toString
argument_list|()
argument_list|,
name|Krb5Util
operator|.
name|ticketToCreds
argument_list|(
name|getTgtFromSubject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid service principal name: "
operator|+
name|serviceName
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|serviceCred
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't get service ticket for "
operator|+
name|serviceName
argument_list|)
throw|;
block|}
name|Subject
operator|.
name|getSubject
argument_list|(
name|AccessController
operator|.
name|getContext
argument_list|()
argument_list|)
operator|.
name|getPrivateCredentials
argument_list|()
operator|.
name|add
argument_list|(
name|Krb5Util
operator|.
name|credsToTicket
argument_list|(
name|serviceCred
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

