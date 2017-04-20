begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.utils
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|resource
operator|.
name|Application
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
name|Collections
import|;
end_import

begin_comment
comment|/**  * Test for some of the command test base operations.  */
end_comment

begin_class
DECL|class|TestAssertions
specifier|public
class|class
name|TestAssertions
block|{
DECL|field|CLUSTER_JSON
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_JSON
init|=
literal|"json/cluster.json"
decl_stmt|;
annotation|@
name|Test
DECL|method|testNoInstances ()
specifier|public
name|void
name|testNoInstances
parameter_list|()
throws|throws
name|Throwable
block|{
name|Application
name|application
init|=
operator|new
name|Application
argument_list|()
decl_stmt|;
name|application
operator|.
name|setContainers
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|SliderTestUtils
operator|.
name|assertContainersLive
argument_list|(
name|application
argument_list|,
literal|"example"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyInstances ()
specifier|public
name|void
name|testEmptyInstances
parameter_list|()
throws|throws
name|Throwable
block|{
name|Application
name|application
init|=
operator|new
name|Application
argument_list|()
decl_stmt|;
name|application
operator|.
name|setContainers
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|SliderTestUtils
operator|.
name|assertContainersLive
argument_list|(
name|application
argument_list|,
literal|"example"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// TODO test metrics retrieval
comment|//  @Test
comment|//  public void testLiveInstances() throws Throwable {
comment|//    InputStream stream = getClass().getClassLoader().getResourceAsStream(
comment|//        CLUSTER_JSON);
comment|//    assertNotNull("could not load " + CLUSTER_JSON, stream);
comment|//    ClusterDescription liveCD = ClusterDescription.fromStream(stream);
comment|//    assertNotNull(liveCD);
comment|//    SliderTestUtils.assertContainersLive(liveCD, "SLEEP_LONG", 4);
comment|//    assertEquals((Integer) 1, liveCD.statistics.get("SLEEP_LONG").get(
comment|//        StatusKeys.STATISTICS_CONTAINERS_ANTI_AFFINE_PENDING));
comment|//  }
block|}
end_class

end_unit

