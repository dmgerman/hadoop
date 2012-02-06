begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

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
name|List
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
name|CommonConfigurationKeysPublic
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
name|DNSToSwitchMapping
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
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
DECL|class|TestRackResolver
specifier|public
class|class
name|TestRackResolver
block|{
DECL|class|MyResolver
specifier|public
specifier|static
specifier|final
class|class
name|MyResolver
implements|implements
name|DNSToSwitchMapping
block|{
DECL|field|numHost1
name|int
name|numHost1
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
DECL|method|resolve (List<String> hostList)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|hostList
parameter_list|)
block|{
comment|// Only one host at a time
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"hostList size is "
operator|+
name|hostList
operator|.
name|size
argument_list|()
argument_list|,
name|hostList
operator|.
name|size
argument_list|()
operator|<=
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|returnList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|hostList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|returnList
return|;
block|}
if|if
condition|(
name|hostList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"host1"
argument_list|)
condition|)
block|{
name|numHost1
operator|++
expr_stmt|;
name|returnList
operator|.
name|add
argument_list|(
literal|"/rack1"
argument_list|)
expr_stmt|;
block|}
comment|// I should not be reached again as RackResolver is supposed to do
comment|// caching.
name|Assert
operator|.
name|assertTrue
argument_list|(
name|numHost1
operator|<=
literal|1
argument_list|)
expr_stmt|;
return|return
name|returnList
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testCaching ()
specifier|public
name|void
name|testCaching
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY
argument_list|,
name|MyResolver
operator|.
name|class
argument_list|,
name|DNSToSwitchMapping
operator|.
name|class
argument_list|)
expr_stmt|;
name|RackResolver
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|RackResolver
operator|.
name|resolve
argument_list|(
literal|"host1"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/rack1"
argument_list|,
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|RackResolver
operator|.
name|resolve
argument_list|(
literal|"host1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"/rack1"
argument_list|,
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

