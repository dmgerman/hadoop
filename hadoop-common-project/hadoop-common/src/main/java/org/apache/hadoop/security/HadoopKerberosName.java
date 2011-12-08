begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|conf
operator|.
name|Configuration
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
name|authentication
operator|.
name|util
operator|.
name|KerberosName
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
name|Config
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
name|KrbException
import|;
end_import

begin_comment
comment|/**  * This class implements parsing and handling of Kerberos principal names. In   * particular, it splits them apart and translates them down into local  * operating system names.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"all"
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HadoopKerberosName
specifier|public
class|class
name|HadoopKerberosName
extends|extends
name|KerberosName
block|{
static|static
block|{
try|try
block|{
name|Config
operator|.
name|getInstance
argument_list|()
operator|.
name|getDefaultRealm
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KrbException
name|ke
parameter_list|)
block|{
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can't get Kerberos configuration"
argument_list|,
name|ke
argument_list|)
throw|;
block|}
block|}
comment|/**    * Create a name from the full Kerberos principal name.    * @param name    */
DECL|method|HadoopKerberosName (String name)
specifier|public
name|HadoopKerberosName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the static configuration to get the rules.    *<p/>    * IMPORTANT: This method does a NOP if the rules have been set already.    * If there is a need to reset the rules, the {@link KerberosName#setRules(String)}    * method should be invoked directly.    *     * @param conf the new configuration    * @throws IOException    */
DECL|method|setConfiguration (Configuration conf)
specifier|public
specifier|static
name|void
name|setConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hasRulesBeenSet
argument_list|()
condition|)
block|{
name|String
name|ruleString
init|=
name|conf
operator|.
name|get
argument_list|(
literal|"hadoop.security.auth_to_local"
argument_list|,
literal|"DEFAULT"
argument_list|)
decl_stmt|;
name|setRules
argument_list|(
name|ruleString
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|setConfiguration
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
name|HadoopKerberosName
name|name
init|=
operator|new
name|HadoopKerberosName
argument_list|(
name|arg
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Name: "
operator|+
name|name
operator|+
literal|" to "
operator|+
name|name
operator|.
name|getShortName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

