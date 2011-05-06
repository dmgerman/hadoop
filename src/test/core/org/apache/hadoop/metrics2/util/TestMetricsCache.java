begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
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
name|Arrays
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|metrics2
operator|.
name|AbstractMetric
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
name|metrics2
operator|.
name|MetricsRecord
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
name|metrics2
operator|.
name|MetricsTag
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
operator|.
name|Interns
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestMetricsCache
specifier|public
class|class
name|TestMetricsCache
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
name|TestMetricsCache
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|testUpdate ()
annotation|@
name|Test
specifier|public
name|void
name|testUpdate
parameter_list|()
block|{
name|MetricsCache
name|cache
init|=
operator|new
name|MetricsCache
argument_list|()
decl_stmt|;
name|MetricsRecord
name|mr
init|=
name|makeRecord
argument_list|(
literal|"r"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t"
argument_list|,
literal|"tv"
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeMetric
argument_list|(
literal|"m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|makeMetric
argument_list|(
literal|"m1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MetricsCache
operator|.
name|Record
name|cr
init|=
name|cache
operator|.
name|update
argument_list|(
name|mr
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mr
argument_list|)
operator|.
name|name
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mr
argument_list|)
operator|.
name|tags
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|mr
argument_list|)
operator|.
name|metrics
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"same record size"
argument_list|,
name|cr
operator|.
name|metrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
operator|(
operator|(
name|Collection
argument_list|<
name|AbstractMetric
argument_list|>
operator|)
name|mr
operator|.
name|metrics
argument_list|()
operator|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"same metric value"
argument_list|,
literal|0
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m"
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsRecord
name|mr2
init|=
name|makeRecord
argument_list|(
literal|"r"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t"
argument_list|,
literal|"tv"
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeMetric
argument_list|(
literal|"m"
argument_list|,
literal|2
argument_list|)
argument_list|,
name|makeMetric
argument_list|(
literal|"m2"
argument_list|,
literal|42
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|cr
operator|=
name|cache
operator|.
name|update
argument_list|(
name|mr2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"contains 3 metric"
argument_list|,
literal|3
argument_list|,
name|cr
operator|.
name|metrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"updated metric value"
argument_list|,
literal|2
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"old metric value"
argument_list|,
literal|1
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new metric value"
argument_list|,
literal|42
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m2"
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsRecord
name|mr3
init|=
name|makeRecord
argument_list|(
literal|"r"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t"
argument_list|,
literal|"tv3"
argument_list|)
argument_list|)
argument_list|,
comment|// different tag value
name|Arrays
operator|.
name|asList
argument_list|(
name|makeMetric
argument_list|(
literal|"m3"
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|cr
operator|=
name|cache
operator|.
name|update
argument_list|(
name|mr3
argument_list|)
expr_stmt|;
comment|// should get a new record
name|assertEquals
argument_list|(
literal|"contains 1 metric"
argument_list|,
literal|1
argument_list|,
name|cr
operator|.
name|metrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"updated metric value"
argument_list|,
literal|3
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m3"
argument_list|)
argument_list|)
expr_stmt|;
comment|// tags cache should be empty so far
name|assertEquals
argument_list|(
literal|"no tags"
argument_list|,
literal|0
argument_list|,
name|cr
operator|.
name|tags
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// until now
name|cr
operator|=
name|cache
operator|.
name|update
argument_list|(
name|mr3
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got 1 tag"
argument_list|,
literal|1
argument_list|,
name|cr
operator|.
name|tags
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Tag value"
argument_list|,
literal|"tv3"
argument_list|,
name|cr
operator|.
name|getTag
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Metric value"
argument_list|,
literal|3
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testGet ()
annotation|@
name|Test
specifier|public
name|void
name|testGet
parameter_list|()
block|{
name|MetricsCache
name|cache
init|=
operator|new
name|MetricsCache
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"empty"
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"r"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t"
argument_list|,
literal|"t"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsRecord
name|mr
init|=
name|makeRecord
argument_list|(
literal|"r"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t"
argument_list|,
literal|"t"
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeMetric
argument_list|(
literal|"m"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|cache
operator|.
name|update
argument_list|(
name|mr
argument_list|)
expr_stmt|;
name|MetricsCache
operator|.
name|Record
name|cr
init|=
name|cache
operator|.
name|get
argument_list|(
literal|"r"
argument_list|,
name|mr
operator|.
name|tags
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"tags="
operator|+
name|mr
operator|.
name|tags
argument_list|()
operator|+
literal|" cr="
operator|+
name|cr
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Got record"
argument_list|,
name|cr
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"contains 1 metric"
argument_list|,
literal|1
argument_list|,
name|cr
operator|.
name|metrics
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new metric value"
argument_list|,
literal|1
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure metrics tag has a sane hashCode impl    */
DECL|method|testNullTag ()
annotation|@
name|Test
specifier|public
name|void
name|testNullTag
parameter_list|()
block|{
name|MetricsCache
name|cache
init|=
operator|new
name|MetricsCache
argument_list|()
decl_stmt|;
name|MetricsRecord
name|mr
init|=
name|makeRecord
argument_list|(
literal|"r"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t"
argument_list|,
literal|null
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeMetric
argument_list|(
literal|"m"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|makeMetric
argument_list|(
literal|"m1"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|MetricsCache
operator|.
name|Record
name|cr
init|=
name|cache
operator|.
name|update
argument_list|(
name|mr
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"t value should be null"
argument_list|,
literal|null
operator|==
name|cr
operator|.
name|getTag
argument_list|(
literal|"t"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testOverflow ()
annotation|@
name|Test
specifier|public
name|void
name|testOverflow
parameter_list|()
block|{
name|MetricsCache
name|cache
init|=
operator|new
name|MetricsCache
argument_list|()
decl_stmt|;
name|MetricsCache
operator|.
name|Record
name|cr
decl_stmt|;
name|Collection
argument_list|<
name|MetricsTag
argument_list|>
name|t0
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t0"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|MetricsCache
operator|.
name|MAX_RECS_PER_NAME_DEFAULT
operator|+
literal|1
condition|;
operator|++
name|i
control|)
block|{
name|cr
operator|=
name|cache
operator|.
name|update
argument_list|(
name|makeRecord
argument_list|(
literal|"r"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeTag
argument_list|(
literal|"t"
operator|+
name|i
argument_list|,
literal|""
operator|+
name|i
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|makeMetric
argument_list|(
literal|"m"
argument_list|,
name|i
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"new metrics value"
argument_list|,
name|i
argument_list|,
name|cr
operator|.
name|getMetric
argument_list|(
literal|"m"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|MetricsCache
operator|.
name|MAX_RECS_PER_NAME_DEFAULT
condition|)
block|{
name|assertNotNull
argument_list|(
literal|"t0 is still there"
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"r"
argument_list|,
name|t0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNull
argument_list|(
literal|"t0 is gone"
argument_list|,
name|cache
operator|.
name|get
argument_list|(
literal|"r"
argument_list|,
name|t0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeRecord (String name, Collection<MetricsTag> tags, Collection<AbstractMetric> metrics)
specifier|private
name|MetricsRecord
name|makeRecord
parameter_list|(
name|String
name|name
parameter_list|,
name|Collection
argument_list|<
name|MetricsTag
argument_list|>
name|tags
parameter_list|,
name|Collection
argument_list|<
name|AbstractMetric
argument_list|>
name|metrics
parameter_list|)
block|{
name|MetricsRecord
name|mr
init|=
name|mock
argument_list|(
name|MetricsRecord
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mr
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mr
operator|.
name|tags
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|tags
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mr
operator|.
name|metrics
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
return|return
name|mr
return|;
block|}
DECL|method|makeTag (String name, String value)
specifier|private
name|MetricsTag
name|makeTag
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|MetricsTag
argument_list|(
name|info
argument_list|(
name|name
argument_list|,
literal|""
argument_list|)
argument_list|,
name|value
argument_list|)
return|;
block|}
DECL|method|makeMetric (String name, Number value)
specifier|private
name|AbstractMetric
name|makeMetric
parameter_list|(
name|String
name|name
parameter_list|,
name|Number
name|value
parameter_list|)
block|{
name|AbstractMetric
name|metric
init|=
name|mock
argument_list|(
name|AbstractMetric
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|metric
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|metric
operator|.
name|value
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|metric
return|;
block|}
block|}
end_class

end_unit

