begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics
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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|Collection
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|metrics
operator|.
name|MetricsServlet
operator|.
name|TagsMetricsPair
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
name|metrics
operator|.
name|spi
operator|.
name|NoEmitMetricsContext
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
name|metrics
operator|.
name|spi
operator|.
name|OutputRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_class
DECL|class|TestMetricsServlet
specifier|public
class|class
name|TestMetricsServlet
extends|extends
name|TestCase
block|{
DECL|field|nc1
name|MetricsContext
name|nc1
decl_stmt|;
DECL|field|nc2
name|MetricsContext
name|nc2
decl_stmt|;
comment|// List containing nc1 and nc2.
DECL|field|contexts
name|List
argument_list|<
name|MetricsContext
argument_list|>
name|contexts
decl_stmt|;
DECL|field|outputRecord
name|OutputRecord
name|outputRecord
decl_stmt|;
comment|/**    * Initializes, for testing, two NoEmitMetricsContext's, and adds one value     * to the first of them.    */
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|nc1
operator|=
operator|new
name|NoEmitMetricsContext
argument_list|()
expr_stmt|;
name|nc1
operator|.
name|init
argument_list|(
literal|"test1"
argument_list|,
name|ContextFactory
operator|.
name|getFactory
argument_list|()
argument_list|)
expr_stmt|;
name|nc2
operator|=
operator|new
name|NoEmitMetricsContext
argument_list|()
expr_stmt|;
name|nc2
operator|.
name|init
argument_list|(
literal|"test2"
argument_list|,
name|ContextFactory
operator|.
name|getFactory
argument_list|()
argument_list|)
expr_stmt|;
name|contexts
operator|=
operator|new
name|ArrayList
argument_list|<
name|MetricsContext
argument_list|>
argument_list|()
expr_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|nc1
argument_list|)
expr_stmt|;
name|contexts
operator|.
name|add
argument_list|(
name|nc2
argument_list|)
expr_stmt|;
name|MetricsRecord
name|r
init|=
name|nc1
operator|.
name|createRecord
argument_list|(
literal|"testRecord"
argument_list|)
decl_stmt|;
name|r
operator|.
name|setTag
argument_list|(
literal|"testTag1"
argument_list|,
literal|"testTagValue1"
argument_list|)
expr_stmt|;
name|r
operator|.
name|setTag
argument_list|(
literal|"testTag2"
argument_list|,
literal|"testTagValue2"
argument_list|)
expr_stmt|;
name|r
operator|.
name|setMetric
argument_list|(
literal|"testMetric1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|r
operator|.
name|setMetric
argument_list|(
literal|"testMetric2"
argument_list|,
literal|33
argument_list|)
expr_stmt|;
name|r
operator|.
name|update
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Collection
argument_list|<
name|OutputRecord
argument_list|>
argument_list|>
name|m
init|=
name|nc1
operator|.
name|getAllRecords
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|m
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|m
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|OutputRecord
argument_list|>
name|outputRecords
init|=
name|m
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|outputRecords
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|outputRecord
operator|=
name|outputRecords
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
DECL|method|testTagsMetricsPair ()
specifier|public
name|void
name|testTagsMetricsPair
parameter_list|()
throws|throws
name|IOException
block|{
name|TagsMetricsPair
name|pair
init|=
operator|new
name|TagsMetricsPair
argument_list|(
name|outputRecord
operator|.
name|getTagsCopy
argument_list|()
argument_list|,
name|outputRecord
operator|.
name|getMetricsCopy
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|JSON
operator|.
name|toString
argument_list|(
name|pair
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[{\"testTag1\":\"testTagValue1\",\"testTag2\":\"testTagValue2\"},"
operator|+
literal|"{\"testMetric1\":1,\"testMetric2\":33}]"
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|testGetMap ()
specifier|public
name|void
name|testGetMap
parameter_list|()
throws|throws
name|IOException
block|{
name|MetricsServlet
name|servlet
init|=
operator|new
name|MetricsServlet
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
argument_list|>
name|m
init|=
name|servlet
operator|.
name|makeMap
argument_list|(
name|contexts
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Map missing contexts"
argument_list|,
literal|2
argument_list|,
name|m
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|m
operator|.
name|containsKey
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TagsMetricsPair
argument_list|>
argument_list|>
name|m2
init|=
name|m
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Missing records"
argument_list|,
literal|1
argument_list|,
name|m2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|m2
operator|.
name|containsKey
argument_list|(
literal|"testRecord"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Wrong number of tags-values pairs."
argument_list|,
literal|1
argument_list|,
name|m2
operator|.
name|get
argument_list|(
literal|"testRecord"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testPrintMap ()
specifier|public
name|void
name|testPrintMap
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|MetricsServlet
name|servlet
init|=
operator|new
name|MetricsServlet
argument_list|()
decl_stmt|;
name|servlet
operator|.
name|printMap
argument_list|(
name|out
argument_list|,
name|servlet
operator|.
name|makeMap
argument_list|(
name|contexts
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|EXPECTED
init|=
literal|""
operator|+
literal|"test1\n"
operator|+
literal|"  testRecord\n"
operator|+
literal|"    {testTag1=testTagValue1,testTag2=testTagValue2}:\n"
operator|+
literal|"      testMetric1=1\n"
operator|+
literal|"      testMetric2=33\n"
operator|+
literal|"test2\n"
decl_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED
argument_list|,
name|sw
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

