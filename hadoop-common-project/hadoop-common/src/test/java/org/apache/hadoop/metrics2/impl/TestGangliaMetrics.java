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
name|net
operator|.
name|DatagramPacket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|DatagramSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|annotation
operator|.
name|Metric
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
name|annotation
operator|.
name|Metrics
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
name|lib
operator|.
name|MetricsRegistry
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
name|lib
operator|.
name|MutableCounterLong
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
name|lib
operator|.
name|MutableGaugeLong
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
name|lib
operator|.
name|MutableRate
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
name|ganglia
operator|.
name|AbstractGangliaSink
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
name|ganglia
operator|.
name|GangliaSink30
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
name|ganglia
operator|.
name|GangliaSink31
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
name|ganglia
operator|.
name|GangliaMetricsTestHelper
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
DECL|class|TestGangliaMetrics
specifier|public
class|class
name|TestGangliaMetrics
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
name|TestMetricsSystemImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// This is the prefix to locate the config file for this particular test
comment|// This is to avoid using the same config file with other test cases,
comment|// which can cause race conditions.
DECL|field|testNamePrefix
specifier|private
name|String
name|testNamePrefix
init|=
literal|"gangliametrics"
decl_stmt|;
DECL|field|expectedMetrics
specifier|private
specifier|final
name|String
index|[]
name|expectedMetrics
init|=
block|{
name|testNamePrefix
operator|+
literal|".s1rec.C1"
block|,
name|testNamePrefix
operator|+
literal|".s1rec.G1"
block|,
name|testNamePrefix
operator|+
literal|".s1rec.Xxx"
block|,
name|testNamePrefix
operator|+
literal|".s1rec.Yyy"
block|,
name|testNamePrefix
operator|+
literal|".s1rec.S1NumOps"
block|,
name|testNamePrefix
operator|+
literal|".s1rec.S1AvgTime"
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testTagsForPrefix ()
specifier|public
name|void
name|testTagsForPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|ConfigBuilder
name|cb
init|=
operator|new
name|ConfigBuilder
argument_list|()
operator|.
name|add
argument_list|(
name|testNamePrefix
operator|+
literal|".sink.ganglia.tagsForPrefix.all"
argument_list|,
literal|"*"
argument_list|)
operator|.
name|add
argument_list|(
name|testNamePrefix
operator|+
literal|".sink.ganglia.tagsForPrefix.some"
argument_list|,
literal|"NumActiveSinks, "
operator|+
literal|"NumActiveSources"
argument_list|)
operator|.
name|add
argument_list|(
name|testNamePrefix
operator|+
literal|".sink.ganglia.tagsForPrefix.none"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|GangliaSink30
name|sink
init|=
operator|new
name|GangliaSink30
argument_list|()
decl_stmt|;
name|sink
operator|.
name|init
argument_list|(
name|cb
operator|.
name|subset
argument_list|(
name|testNamePrefix
operator|+
literal|".sink.ganglia"
argument_list|)
argument_list|)
expr_stmt|;
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
name|NumActiveSources
argument_list|,
literal|"foo"
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
name|NumActiveSinks
argument_list|,
literal|"bar"
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
name|NumAllSinks
argument_list|,
literal|"haa"
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
literal|1
argument_list|,
name|tags
argument_list|,
name|metrics
argument_list|)
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sink
operator|.
name|appendPrefix
argument_list|(
name|record
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".NumActiveSources=foo.NumActiveSinks=bar.NumAllSinks=haa"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tags
operator|.
name|set
argument_list|(
literal|0
argument_list|,
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
literal|"some"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|sink
operator|.
name|appendPrefix
argument_list|(
name|record
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".NumActiveSources=foo.NumActiveSinks=bar"
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tags
operator|.
name|set
argument_list|(
literal|0
argument_list|,
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
literal|"none"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|sink
operator|.
name|appendPrefix
argument_list|(
name|record
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tags
operator|.
name|set
argument_list|(
literal|0
argument_list|,
operator|new
name|MetricsTag
argument_list|(
name|MsInfo
operator|.
name|Context
argument_list|,
literal|"nada"
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|sink
operator|.
name|appendPrefix
argument_list|(
name|record
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGangliaMetrics2 ()
annotation|@
name|Test
specifier|public
name|void
name|testGangliaMetrics2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setting long interval to avoid periodic publishing.
comment|// We manually publish metrics by MeticsSystem#publishMetricsNow here.
name|ConfigBuilder
name|cb
init|=
operator|new
name|ConfigBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"*.period"
argument_list|,
literal|120
argument_list|)
operator|.
name|add
argument_list|(
name|testNamePrefix
operator|+
literal|".sink.gsink30.context"
argument_list|,
name|testNamePrefix
argument_list|)
comment|// filter out only "test"
operator|.
name|add
argument_list|(
name|testNamePrefix
operator|+
literal|".sink.gsink31.context"
argument_list|,
name|testNamePrefix
argument_list|)
comment|// filter out only "test"
operator|.
name|save
argument_list|(
name|TestMetricsConfig
operator|.
name|getTestFilename
argument_list|(
literal|"hadoop-metrics2-"
operator|+
name|testNamePrefix
argument_list|)
argument_list|)
decl_stmt|;
name|MetricsSystemImpl
name|ms
init|=
operator|new
name|MetricsSystemImpl
argument_list|(
name|testNamePrefix
argument_list|)
decl_stmt|;
name|ms
operator|.
name|start
argument_list|()
expr_stmt|;
name|TestSource
name|s1
init|=
name|ms
operator|.
name|register
argument_list|(
literal|"s1"
argument_list|,
literal|"s1 desc"
argument_list|,
operator|new
name|TestSource
argument_list|(
literal|"s1rec"
argument_list|)
argument_list|)
decl_stmt|;
name|s1
operator|.
name|c1
operator|.
name|incr
argument_list|()
expr_stmt|;
name|s1
operator|.
name|xxx
operator|.
name|incr
argument_list|()
expr_stmt|;
name|s1
operator|.
name|g1
operator|.
name|set
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|s1
operator|.
name|yyy
operator|.
name|incr
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|s1
operator|.
name|s1
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|int
name|expectedCountFromGanglia30
init|=
name|expectedMetrics
operator|.
name|length
decl_stmt|;
specifier|final
name|int
name|expectedCountFromGanglia31
init|=
literal|2
operator|*
name|expectedMetrics
operator|.
name|length
decl_stmt|;
comment|// Setup test for GangliaSink30
name|AbstractGangliaSink
name|gsink30
init|=
operator|new
name|GangliaSink30
argument_list|()
decl_stmt|;
name|gsink30
operator|.
name|init
argument_list|(
name|cb
operator|.
name|subset
argument_list|(
name|testNamePrefix
argument_list|)
argument_list|)
expr_stmt|;
name|MockDatagramSocket
name|mockds30
init|=
operator|new
name|MockDatagramSocket
argument_list|()
decl_stmt|;
name|GangliaMetricsTestHelper
operator|.
name|setDatagramSocket
argument_list|(
name|gsink30
argument_list|,
name|mockds30
argument_list|)
expr_stmt|;
comment|// Setup test for GangliaSink31
name|AbstractGangliaSink
name|gsink31
init|=
operator|new
name|GangliaSink31
argument_list|()
decl_stmt|;
name|gsink31
operator|.
name|init
argument_list|(
name|cb
operator|.
name|subset
argument_list|(
name|testNamePrefix
argument_list|)
argument_list|)
expr_stmt|;
name|MockDatagramSocket
name|mockds31
init|=
operator|new
name|MockDatagramSocket
argument_list|()
decl_stmt|;
name|GangliaMetricsTestHelper
operator|.
name|setDatagramSocket
argument_list|(
name|gsink31
argument_list|,
name|mockds31
argument_list|)
expr_stmt|;
comment|// register the sinks
name|ms
operator|.
name|register
argument_list|(
literal|"gsink30"
argument_list|,
literal|"gsink30 desc"
argument_list|,
name|gsink30
argument_list|)
expr_stmt|;
name|ms
operator|.
name|register
argument_list|(
literal|"gsink31"
argument_list|,
literal|"gsink31 desc"
argument_list|,
name|gsink31
argument_list|)
expr_stmt|;
name|ms
operator|.
name|publishMetricsNow
argument_list|()
expr_stmt|;
comment|// publish the metrics
name|ms
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// check GanfliaSink30 data
name|checkMetrics
argument_list|(
name|mockds30
operator|.
name|getCapturedSend
argument_list|()
argument_list|,
name|expectedCountFromGanglia30
argument_list|)
expr_stmt|;
comment|// check GanfliaSink31 data
name|checkMetrics
argument_list|(
name|mockds31
operator|.
name|getCapturedSend
argument_list|()
argument_list|,
name|expectedCountFromGanglia31
argument_list|)
expr_stmt|;
block|}
comment|// check the expected against the actual metrics
DECL|method|checkMetrics (List<byte[]> bytearrlist, int expectedCount)
specifier|private
name|void
name|checkMetrics
parameter_list|(
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|bytearrlist
parameter_list|,
name|int
name|expectedCount
parameter_list|)
block|{
name|boolean
index|[]
name|foundMetrics
init|=
operator|new
name|boolean
index|[
name|expectedMetrics
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|byte
index|[]
name|bytes
range|:
name|bytearrlist
control|)
block|{
name|String
name|binaryStr
init|=
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|expectedMetrics
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
if|if
condition|(
name|binaryStr
operator|.
name|indexOf
argument_list|(
name|expectedMetrics
index|[
name|index
index|]
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|foundMetrics
index|[
name|index
index|]
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|foundMetrics
operator|.
name|length
condition|;
name|index
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|foundMetrics
index|[
name|index
index|]
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Missing metrics: "
operator|+
name|expectedMetrics
index|[
name|index
index|]
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|"Mismatch in record count: "
argument_list|,
name|expectedCount
argument_list|,
name|bytearrlist
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Metrics
argument_list|(
name|context
operator|=
literal|"gangliametrics"
argument_list|)
DECL|class|TestSource
specifier|private
specifier|static
class|class
name|TestSource
block|{
DECL|field|c1
annotation|@
name|Metric
argument_list|(
literal|"C1 desc"
argument_list|)
name|MutableCounterLong
name|c1
decl_stmt|;
DECL|field|xxx
annotation|@
name|Metric
argument_list|(
literal|"XXX desc"
argument_list|)
name|MutableCounterLong
name|xxx
decl_stmt|;
DECL|field|g1
annotation|@
name|Metric
argument_list|(
literal|"G1 desc"
argument_list|)
name|MutableGaugeLong
name|g1
decl_stmt|;
DECL|field|yyy
annotation|@
name|Metric
argument_list|(
literal|"YYY desc"
argument_list|)
name|MutableGaugeLong
name|yyy
decl_stmt|;
DECL|field|s1
annotation|@
name|Metric
name|MutableRate
name|s1
decl_stmt|;
DECL|field|registry
specifier|final
name|MetricsRegistry
name|registry
decl_stmt|;
DECL|method|TestSource (String recName)
name|TestSource
parameter_list|(
name|String
name|recName
parameter_list|)
block|{
name|registry
operator|=
operator|new
name|MetricsRegistry
argument_list|(
name|recName
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This class is used to capture data send to Ganglia servers.    *    * Initial attempt was to use mockito to mock and capture but    * while testing figured out that mockito is keeping the reference    * to the byte array and since the sink code reuses the byte array    * hence all the captured byte arrays were pointing to one instance.    */
DECL|class|MockDatagramSocket
specifier|private
class|class
name|MockDatagramSocket
extends|extends
name|DatagramSocket
block|{
DECL|field|capture
specifier|private
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|capture
decl_stmt|;
comment|/**      * @throws SocketException      */
DECL|method|MockDatagramSocket ()
specifier|public
name|MockDatagramSocket
parameter_list|()
throws|throws
name|SocketException
block|{
name|capture
operator|=
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see java.net.DatagramSocket#send(java.net.DatagramPacket)      */
annotation|@
name|Override
DECL|method|send (DatagramPacket p)
specifier|public
specifier|synchronized
name|void
name|send
parameter_list|(
name|DatagramPacket
name|p
parameter_list|)
throws|throws
name|IOException
block|{
comment|// capture the byte arrays
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|p
operator|.
name|getLength
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|p
operator|.
name|getData
argument_list|()
argument_list|,
name|p
operator|.
name|getOffset
argument_list|()
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|p
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|capture
operator|.
name|add
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return the captured byte arrays      */
DECL|method|getCapturedSend ()
specifier|synchronized
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|getCapturedSend
parameter_list|()
block|{
return|return
name|capture
return|;
block|}
block|}
block|}
end_class

end_unit

