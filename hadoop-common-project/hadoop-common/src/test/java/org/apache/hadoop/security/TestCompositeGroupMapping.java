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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

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
name|conf
operator|.
name|Configurable
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
name|fs
operator|.
name|CommonConfigurationKeys
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCompositeGroupMapping
specifier|public
class|class
name|TestCompositeGroupMapping
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestCompositeGroupMapping
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|class|TestUser
specifier|private
specifier|static
class|class
name|TestUser
block|{
DECL|field|name
name|String
name|name
decl_stmt|;
DECL|field|group
name|String
name|group
decl_stmt|;
DECL|field|group2
name|String
name|group2
decl_stmt|;
DECL|method|TestUser (String name, String group)
specifier|public
name|TestUser
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|group
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
block|}
DECL|method|TestUser (String name, String group, String group2)
specifier|public
name|TestUser
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|group
parameter_list|,
name|String
name|group2
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|this
operator|.
name|group2
operator|=
name|group2
expr_stmt|;
block|}
block|}
empty_stmt|;
DECL|field|john
specifier|private
specifier|static
name|TestUser
name|john
init|=
operator|new
name|TestUser
argument_list|(
literal|"John"
argument_list|,
literal|"user-group"
argument_list|)
decl_stmt|;
DECL|field|hdfs
specifier|private
specifier|static
name|TestUser
name|hdfs
init|=
operator|new
name|TestUser
argument_list|(
literal|"hdfs"
argument_list|,
literal|"supergroup"
argument_list|)
decl_stmt|;
DECL|field|jack
specifier|private
specifier|static
name|TestUser
name|jack
init|=
operator|new
name|TestUser
argument_list|(
literal|"Jack"
argument_list|,
literal|"user-group"
argument_list|,
literal|"dev-group-1"
argument_list|)
decl_stmt|;
DECL|field|PROVIDER_SPECIFIC_CONF
specifier|private
specifier|static
specifier|final
name|String
name|PROVIDER_SPECIFIC_CONF
init|=
literal|".test.prop"
decl_stmt|;
DECL|field|PROVIDER_SPECIFIC_CONF_KEY
specifier|private
specifier|static
specifier|final
name|String
name|PROVIDER_SPECIFIC_CONF_KEY
init|=
name|GroupMappingServiceProvider
operator|.
name|GROUP_MAPPING_CONFIG_PREFIX
operator|+
name|PROVIDER_SPECIFIC_CONF
decl_stmt|;
DECL|field|PROVIDER_SPECIFIC_CONF_VALUE_FOR_USER
specifier|private
specifier|static
specifier|final
name|String
name|PROVIDER_SPECIFIC_CONF_VALUE_FOR_USER
init|=
literal|"value-for-user"
decl_stmt|;
DECL|field|PROVIDER_SPECIFIC_CONF_VALUE_FOR_CLUSTER
specifier|private
specifier|static
specifier|final
name|String
name|PROVIDER_SPECIFIC_CONF_VALUE_FOR_CLUSTER
init|=
literal|"value-for-cluster"
decl_stmt|;
DECL|class|GroupMappingProviderBase
specifier|private
specifier|static
specifier|abstract
class|class
name|GroupMappingProviderBase
implements|implements
name|GroupMappingServiceProvider
implements|,
name|Configurable
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|cacheGroupsRefresh ()
specifier|public
name|void
name|cacheGroupsRefresh
parameter_list|()
throws|throws
name|IOException
block|{            }
annotation|@
name|Override
DECL|method|cacheGroupsAdd (List<String> groups)
specifier|public
name|void
name|cacheGroupsAdd
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groups
parameter_list|)
throws|throws
name|IOException
block|{            }
DECL|method|toList (String group)
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|toList
parameter_list|(
name|String
name|group
parameter_list|)
block|{
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
name|group
block|}
argument_list|)
return|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
return|;
block|}
DECL|method|checkTestConf (String expectedValue)
specifier|protected
name|void
name|checkTestConf
parameter_list|(
name|String
name|expectedValue
parameter_list|)
block|{
name|String
name|configValue
init|=
name|getConf
argument_list|()
operator|.
name|get
argument_list|(
name|PROVIDER_SPECIFIC_CONF_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|configValue
operator|==
literal|null
operator|||
operator|!
name|configValue
operator|.
name|equals
argument_list|(
name|expectedValue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to find mandatory configuration of "
operator|+
name|PROVIDER_SPECIFIC_CONF_KEY
argument_list|)
throw|;
block|}
block|}
block|}
empty_stmt|;
DECL|class|UserProvider
specifier|private
specifier|static
class|class
name|UserProvider
extends|extends
name|GroupMappingProviderBase
block|{
annotation|@
name|Override
DECL|method|getGroups (String user)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|checkTestConf
argument_list|(
name|PROVIDER_SPECIFIC_CONF_VALUE_FOR_USER
argument_list|)
expr_stmt|;
name|String
name|group
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|john
operator|.
name|name
argument_list|)
condition|)
block|{
name|group
operator|=
name|john
operator|.
name|group
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|jack
operator|.
name|name
argument_list|)
condition|)
block|{
name|group
operator|=
name|jack
operator|.
name|group
expr_stmt|;
block|}
return|return
name|toList
argument_list|(
name|group
argument_list|)
return|;
block|}
block|}
DECL|class|ClusterProvider
specifier|private
specifier|static
class|class
name|ClusterProvider
extends|extends
name|GroupMappingProviderBase
block|{
annotation|@
name|Override
DECL|method|getGroups (String user)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getGroups
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|checkTestConf
argument_list|(
name|PROVIDER_SPECIFIC_CONF_VALUE_FOR_CLUSTER
argument_list|)
expr_stmt|;
name|String
name|group
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|hdfs
operator|.
name|name
argument_list|)
condition|)
block|{
name|group
operator|=
name|hdfs
operator|.
name|group
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|user
operator|.
name|equals
argument_list|(
name|jack
operator|.
name|name
argument_list|)
condition|)
block|{
comment|// jack has another group from clusterProvider
name|group
operator|=
name|jack
operator|.
name|group2
expr_stmt|;
block|}
return|return
name|toList
argument_list|(
name|group
argument_list|)
return|;
block|}
block|}
static|static
block|{
name|conf
operator|.
name|setClass
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|CompositeGroupsMapping
operator|.
name|class
argument_list|,
name|GroupMappingServiceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CompositeGroupsMapping
operator|.
name|MAPPING_PROVIDERS_CONFIG_KEY
argument_list|,
literal|"userProvider,clusterProvider"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|CompositeGroupsMapping
operator|.
name|MAPPING_PROVIDER_CONFIG_PREFIX
operator|+
literal|".userProvider"
argument_list|,
name|UserProvider
operator|.
name|class
argument_list|,
name|GroupMappingServiceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|CompositeGroupsMapping
operator|.
name|MAPPING_PROVIDER_CONFIG_PREFIX
operator|+
literal|".clusterProvider"
argument_list|,
name|ClusterProvider
operator|.
name|class
argument_list|,
name|GroupMappingServiceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CompositeGroupsMapping
operator|.
name|MAPPING_PROVIDER_CONFIG_PREFIX
operator|+
literal|".clusterProvider"
operator|+
name|PROVIDER_SPECIFIC_CONF
argument_list|,
name|PROVIDER_SPECIFIC_CONF_VALUE_FOR_CLUSTER
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CompositeGroupsMapping
operator|.
name|MAPPING_PROVIDER_CONFIG_PREFIX
operator|+
literal|".userProvider"
operator|+
name|PROVIDER_SPECIFIC_CONF
argument_list|,
name|PROVIDER_SPECIFIC_CONF_VALUE_FOR_USER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestMultipleGroupsMapping ()
specifier|public
name|void
name|TestMultipleGroupsMapping
parameter_list|()
throws|throws
name|Exception
block|{
name|Groups
name|groups
init|=
operator|new
name|Groups
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|getGroups
argument_list|(
name|john
operator|.
name|name
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|john
operator|.
name|group
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|getGroups
argument_list|(
name|hdfs
operator|.
name|name
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|hdfs
operator|.
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestMultipleGroupsMappingWithCombined ()
specifier|public
name|void
name|TestMultipleGroupsMappingWithCombined
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|CompositeGroupsMapping
operator|.
name|MAPPING_PROVIDERS_COMBINED_CONFIG_KEY
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|Groups
name|groups
init|=
operator|new
name|Groups
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|getGroups
argument_list|(
name|jack
operator|.
name|name
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// the configured providers list in order is "userProvider,clusterProvider"
comment|// group -> userProvider, group2 -> clusterProvider
name|assertTrue
argument_list|(
name|groups
operator|.
name|getGroups
argument_list|(
name|jack
operator|.
name|name
argument_list|)
operator|.
name|contains
argument_list|(
name|jack
operator|.
name|group
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|getGroups
argument_list|(
name|jack
operator|.
name|name
argument_list|)
operator|.
name|contains
argument_list|(
name|jack
operator|.
name|group2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|TestMultipleGroupsMappingWithoutCombined ()
specifier|public
name|void
name|TestMultipleGroupsMappingWithoutCombined
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|.
name|set
argument_list|(
name|CompositeGroupsMapping
operator|.
name|MAPPING_PROVIDERS_COMBINED_CONFIG_KEY
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|Groups
name|groups
init|=
operator|new
name|Groups
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// the configured providers list in order is "userProvider,clusterProvider"
comment|// group -> userProvider, group2 -> clusterProvider
name|assertTrue
argument_list|(
name|groups
operator|.
name|getGroups
argument_list|(
name|jack
operator|.
name|name
argument_list|)
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|getGroups
argument_list|(
name|jack
operator|.
name|name
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|jack
operator|.
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

