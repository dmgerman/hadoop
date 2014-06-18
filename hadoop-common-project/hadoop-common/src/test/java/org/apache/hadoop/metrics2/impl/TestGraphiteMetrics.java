begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
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
name|assertEquals
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
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|HashSet
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
name|Set
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
name|MetricsException
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|sink
operator|.
name|GraphiteSink
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
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_class
DECL|class|TestGraphiteMetrics
specifier|public
class|class
name|TestGraphiteMetrics
block|{
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
annotation|@
name|Test
DECL|method|testPutMetrics ()
specifier|public
name|void
name|testPutMetrics
parameter_list|()
block|{
name|GraphiteSink
name|sink
init|=
operator|new
name|GraphiteSink
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MetricsTag
argument_list|>
name|tags
init|=
operator|new
name|ArrayList
argument_list|<
name|MetricsTag
argument_list|>
argument_list|()
decl_stmt|;
name|tags
operator|.
name|add
argument_list|(
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
literal|"all"
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Hostname
argument_list|,
literal|"host"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|AbstractMetric
argument_list|>
name|metrics
init|=
operator|new
name|HashSet
argument_list|<
name|AbstractMetric
argument_list|>
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|makeMetric
argument_list|(
literal|"foo1"
argument_list|,
literal|1.25
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|makeMetric
argument_list|(
literal|"foo2"
argument_list|,
literal|2.25
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsRecord
name|record
init|=
operator|new
name|MetricsRecordImpl
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
operator|(
name|long
operator|)
literal|10000
argument_list|,
name|tags
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|writer
init|=
name|mock
argument_list|(
name|OutputStreamWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|String
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|sink
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|sink
operator|.
name|putMetrics
argument_list|(
name|record
argument_list|)
expr_stmt|;
try|try
block|{
name|verify
argument_list|(
name|writer
argument_list|)
operator|.
name|write
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|String
name|result
init|=
name|argument
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
operator|.
name|equals
argument_list|(
literal|"null.all.Context.Context=all.Hostname=host.foo1 1.25 10\n"
operator|+
literal|"null.all.Context.Context=all.Hostname=host.foo2 2.25 10\n"
argument_list|)
operator|||
name|result
operator|.
name|equals
argument_list|(
literal|"null.all.Context.Context=all.Hostname=host.foo2 2.25 10\n"
operator|+
literal|"null.all.Context.Context=all.Hostname=host.foo1 1.25 10\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPutMetrics2 ()
specifier|public
name|void
name|testPutMetrics2
parameter_list|()
block|{
name|GraphiteSink
name|sink
init|=
operator|new
name|GraphiteSink
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MetricsTag
argument_list|>
name|tags
init|=
operator|new
name|ArrayList
argument_list|<
name|MetricsTag
argument_list|>
argument_list|()
decl_stmt|;
name|tags
operator|.
name|add
argument_list|(
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
literal|"all"
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Hostname
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|AbstractMetric
argument_list|>
name|metrics
init|=
operator|new
name|HashSet
argument_list|<
name|AbstractMetric
argument_list|>
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|makeMetric
argument_list|(
literal|"foo1"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|makeMetric
argument_list|(
literal|"foo2"
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsRecord
name|record
init|=
operator|new
name|MetricsRecordImpl
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
operator|(
name|long
operator|)
literal|10000
argument_list|,
name|tags
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|writer
init|=
name|mock
argument_list|(
name|OutputStreamWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|ArgumentCaptor
argument_list|<
name|String
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|sink
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|sink
operator|.
name|putMetrics
argument_list|(
name|record
argument_list|)
expr_stmt|;
try|try
block|{
name|verify
argument_list|(
name|writer
argument_list|)
operator|.
name|write
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|String
name|result
init|=
name|argument
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|result
operator|.
name|equals
argument_list|(
literal|"null.all.Context.Context=all.foo1 1 10\n"
operator|+
literal|"null.all.Context.Context=all.foo2 2 10\n"
argument_list|)
operator|||
name|result
operator|.
name|equals
argument_list|(
literal|"null.all.Context.Context=all.foo2 2 10\n"
operator|+
literal|"null.all.Context.Context=all.foo1 1 10\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|MetricsException
operator|.
name|class
argument_list|)
DECL|method|testCloseAndWrite ()
specifier|public
name|void
name|testCloseAndWrite
parameter_list|()
throws|throws
name|IOException
block|{
name|GraphiteSink
name|sink
init|=
operator|new
name|GraphiteSink
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MetricsTag
argument_list|>
name|tags
init|=
operator|new
name|ArrayList
argument_list|<
name|MetricsTag
argument_list|>
argument_list|()
decl_stmt|;
name|tags
operator|.
name|add
argument_list|(
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
literal|"all"
argument_list|)
argument_list|)
expr_stmt|;
name|tags
operator|.
name|add
argument_list|(
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Hostname
argument_list|,
literal|"host"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|AbstractMetric
argument_list|>
name|metrics
init|=
operator|new
name|HashSet
argument_list|<
name|AbstractMetric
argument_list|>
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|makeMetric
argument_list|(
literal|"foo1"
argument_list|,
literal|1.25
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|makeMetric
argument_list|(
literal|"foo2"
argument_list|,
literal|2.25
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsRecord
name|record
init|=
operator|new
name|MetricsRecordImpl
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
operator|(
name|long
operator|)
literal|10000
argument_list|,
name|tags
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|writer
init|=
name|mock
argument_list|(
name|OutputStreamWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|sink
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|sink
operator|.
name|close
argument_list|()
expr_stmt|;
name|sink
operator|.
name|putMetrics
argument_list|(
name|record
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClose ()
specifier|public
name|void
name|testClose
parameter_list|()
block|{
name|GraphiteSink
name|sink
init|=
operator|new
name|GraphiteSink
argument_list|()
decl_stmt|;
name|Writer
name|mockWriter
init|=
name|mock
argument_list|(
name|Writer
operator|.
name|class
argument_list|)
decl_stmt|;
name|sink
operator|.
name|setWriter
argument_list|(
name|mockWriter
argument_list|)
expr_stmt|;
try|try
block|{
name|sink
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|verify
argument_list|(
name|mockWriter
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

