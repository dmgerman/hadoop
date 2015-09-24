begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|InetSocketAddress
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
name|mapreduce
operator|.
name|MRConfig
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
name|net
operator|.
name|NetUtils
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
name|SecurityUtil
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
name|yarn
operator|.
name|conf
operator|.
name|HAUtil
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|Master
specifier|public
class|class
name|Master
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
name|Master
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|enum|State
specifier|public
enum|enum
name|State
block|{
DECL|enumConstant|INITIALIZING
DECL|enumConstant|RUNNING
name|INITIALIZING
block|,
name|RUNNING
block|;   }
DECL|method|getMasterUserName (Configuration conf)
specifier|public
specifier|static
name|String
name|getMasterUserName
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|framework
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|,
name|MRConfig
operator|.
name|YARN_FRAMEWORK_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|framework
operator|.
name|equals
argument_list|(
name|MRConfig
operator|.
name|CLASSIC_FRAMEWORK_NAME
argument_list|)
condition|)
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|MASTER_USER_NAME
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_PRINCIPAL
argument_list|)
return|;
block|}
block|}
DECL|method|getMasterAddress (Configuration conf)
specifier|public
specifier|static
name|InetSocketAddress
name|getMasterAddress
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|masterAddress
decl_stmt|;
name|String
name|framework
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|FRAMEWORK_NAME
argument_list|,
name|MRConfig
operator|.
name|YARN_FRAMEWORK_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|framework
operator|.
name|equals
argument_list|(
name|MRConfig
operator|.
name|CLASSIC_FRAMEWORK_NAME
argument_list|)
condition|)
block|{
name|masterAddress
operator|=
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|MASTER_ADDRESS
argument_list|,
literal|"localhost:8012"
argument_list|)
expr_stmt|;
return|return
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|masterAddress
argument_list|,
literal|8012
argument_list|,
name|MRConfig
operator|.
name|MASTER_ADDRESS
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|framework
operator|.
name|equals
argument_list|(
name|MRConfig
operator|.
name|YARN_FRAMEWORK_NAME
argument_list|)
operator|&&
name|HAUtil
operator|.
name|isHAEnabled
argument_list|(
name|conf
argument_list|)
condition|)
block|{
name|YarnConfiguration
name|yarnConf
init|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
name|yarnConf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
index|[]
name|rmIds
init|=
name|yarnConf
operator|.
name|getStrings
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmIds
operator|!=
literal|null
operator|&&
name|rmIds
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// If RM_HA_ID is not configured, use the first one.
comment|// Because any valid RM HA ID should work.
name|yarnConf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|rmIds
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"RM_HA_IDS is not configured when RM HA is enabled"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|yarnConf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_PORT
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_PORT
argument_list|)
return|;
block|}
block|}
DECL|method|getMasterPrincipal (Configuration conf)
specifier|public
specifier|static
name|String
name|getMasterPrincipal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|masterHostname
init|=
name|getMasterAddress
argument_list|(
name|conf
argument_list|)
operator|.
name|getHostName
argument_list|()
decl_stmt|;
comment|// get kerberos principal for use as delegation token renewer
return|return
name|SecurityUtil
operator|.
name|getServerPrincipal
argument_list|(
name|getMasterUserName
argument_list|(
name|conf
argument_list|)
argument_list|,
name|masterHostname
argument_list|)
return|;
block|}
block|}
end_class

end_unit

