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
literal|"Expected to be multi switch"
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
literal|"Expected to be multi switch"
argument_list|,
name|mapping
operator|.
name|isSingleSwitch
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
end_class

end_unit

