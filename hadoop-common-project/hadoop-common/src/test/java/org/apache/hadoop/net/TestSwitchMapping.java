begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|net
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Test some other details of the switch mapping  */
end_comment

begin_class
DECL|class|TestSwitchMapping
specifier|public
class|class
name|TestSwitchMapping
extends|extends
name|Assert
block|{
comment|/**    * Verify the switch mapping query handles arbitrary DNSToSwitchMapping    * implementations    *    * @throws Throwable on any problem    */
annotation|@
name|Test
DECL|method|testStandaloneClassesAssumedMultiswitch ()
specifier|public
name|void
name|testStandaloneClassesAssumedMultiswitch
parameter_list|()
throws|throws
name|Throwable
block|{
name|DNSToSwitchMapping
name|mapping
init|=
operator|new
name|StandaloneSwitchMapping
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Expected to be multi switch "
operator|+
name|mapping
argument_list|,
name|AbstractDNSToSwitchMapping
operator|.
name|isMappingSingleSwitch
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the cached mapper delegates the switch mapping query to the inner    * mapping, which again handles arbitrary DNSToSwitchMapping implementations    *    * @throws Throwable on any problem    */
annotation|@
name|Test
DECL|method|testCachingRelays ()
specifier|public
name|void
name|testCachingRelays
parameter_list|()
throws|throws
name|Throwable
block|{
name|CachedDNSToSwitchMapping
name|mapping
init|=
operator|new
name|CachedDNSToSwitchMapping
argument_list|(
operator|new
name|StandaloneSwitchMapping
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Expected to be multi switch "
operator|+
name|mapping
argument_list|,
name|mapping
operator|.
name|isSingleSwitch
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the cached mapper delegates the switch mapping query to the inner    * mapping, which again handles arbitrary DNSToSwitchMapping implementations    *    * @throws Throwable on any problem    */
annotation|@
name|Test
DECL|method|testCachingRelaysStringOperations ()
specifier|public
name|void
name|testCachingRelaysStringOperations
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|scriptname
init|=
literal|"mappingscript.sh"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
argument_list|,
name|scriptname
argument_list|)
expr_stmt|;
name|ScriptBasedMapping
name|scriptMapping
init|=
operator|new
name|ScriptBasedMapping
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not find "
operator|+
name|scriptname
operator|+
literal|" in "
operator|+
name|scriptMapping
argument_list|,
name|scriptMapping
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|scriptname
argument_list|)
argument_list|)
expr_stmt|;
name|CachedDNSToSwitchMapping
name|mapping
init|=
operator|new
name|CachedDNSToSwitchMapping
argument_list|(
name|scriptMapping
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not find "
operator|+
name|scriptname
operator|+
literal|" in "
operator|+
name|mapping
argument_list|,
name|mapping
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|scriptname
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify the cached mapper delegates the switch mapping query to the inner    * mapping, which again handles arbitrary DNSToSwitchMapping implementations    *    * @throws Throwable on any problem    */
annotation|@
name|Test
DECL|method|testCachingRelaysStringOperationsToNullScript ()
specifier|public
name|void
name|testCachingRelaysStringOperationsToNullScript
parameter_list|()
throws|throws
name|Throwable
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ScriptBasedMapping
name|scriptMapping
init|=
operator|new
name|ScriptBasedMapping
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not find "
operator|+
name|ScriptBasedMapping
operator|.
name|NO_SCRIPT
operator|+
literal|" in "
operator|+
name|scriptMapping
argument_list|,
name|scriptMapping
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|ScriptBasedMapping
operator|.
name|NO_SCRIPT
argument_list|)
argument_list|)
expr_stmt|;
name|CachedDNSToSwitchMapping
name|mapping
init|=
operator|new
name|CachedDNSToSwitchMapping
argument_list|(
name|scriptMapping
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Did not find "
operator|+
name|ScriptBasedMapping
operator|.
name|NO_SCRIPT
operator|+
literal|" in "
operator|+
name|mapping
argument_list|,
name|mapping
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|ScriptBasedMapping
operator|.
name|NO_SCRIPT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNullMapping ()
specifier|public
name|void
name|testNullMapping
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|AbstractDNSToSwitchMapping
operator|.
name|isMappingSingleSwitch
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * This class does not extend the abstract switch mapping, and verifies that    * the switch mapping logic assumes that this is multi switch    */
DECL|class|StandaloneSwitchMapping
specifier|private
specifier|static
class|class
name|StandaloneSwitchMapping
implements|implements
name|DNSToSwitchMapping
block|{
annotation|@
name|Override
DECL|method|resolve (List<String> names)
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
name|names
parameter_list|)
block|{
return|return
name|names
return|;
block|}
annotation|@
name|Override
DECL|method|reloadCachedMappings ()
specifier|public
name|void
name|reloadCachedMappings
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|reloadCachedMappings (List<String> names)
specifier|public
name|void
name|reloadCachedMappings
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

