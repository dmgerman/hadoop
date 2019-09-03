begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|configuration2
operator|.
name|SubsetConfiguration
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
name|MetricType
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
name|MetricsInfo
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
name|MetricsVisitor
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
name|KafkaSink
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kafka
operator|.
name|clients
operator|.
name|producer
operator|.
name|KafkaProducer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kafka
operator|.
name|clients
operator|.
name|producer
operator|.
name|Producer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kafka
operator|.
name|clients
operator|.
name|producer
operator|.
name|ProducerRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|kafka
operator|.
name|clients
operator|.
name|producer
operator|.
name|RecordMetadata
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

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringJoiner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
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
name|mock
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
name|verify
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
name|when
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * This tests that the KafkaSink properly formats the Kafka message.  */
end_comment

begin_class
DECL|class|TestKafkaMetrics
specifier|public
class|class
name|TestKafkaMetrics
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestKafkaMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|kafkaSink
specifier|private
name|KafkaSink
name|kafkaSink
decl_stmt|;
DECL|enum|KafkaMetricsInfo
enum|enum
name|KafkaMetricsInfo
implements|implements
name|MetricsInfo
block|{
DECL|enumConstant|KafkaMetrics
DECL|enumConstant|KafkaCounter
name|KafkaMetrics
argument_list|(
literal|"Kafka related metrics etc."
argument_list|)
block|,
name|KafkaCounter
argument_list|(
DECL|enumConstant|KafkaTag
literal|"Kafka counter."
argument_list|)
block|,
name|KafkaTag
argument_list|(
literal|"Kafka tag."
argument_list|)
block|;
comment|// metrics
DECL|field|desc
specifier|private
specifier|final
name|String
name|desc
decl_stmt|;
DECL|method|KafkaMetricsInfo (String desc)
name|KafkaMetricsInfo
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
name|this
operator|.
name|desc
operator|=
name|desc
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|description ()
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
name|desc
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringJoiner
argument_list|(
literal|", "
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{"
argument_list|,
literal|"}"
argument_list|)
operator|.
name|add
argument_list|(
literal|"name="
operator|+
name|name
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"description="
operator|+
name|desc
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|method|testPutMetrics ()
specifier|public
name|void
name|testPutMetrics
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create a record by mocking MetricsRecord class.
name|MetricsRecord
name|record
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
name|record
operator|.
name|tags
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
operator|new
name|MetricsTag
argument_list|(
name|KafkaMetricsInfo
operator|.
name|KafkaTag
argument_list|,
literal|"test_tag"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|record
operator|.
name|timestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
comment|// Create a metric using AbstractMetric class.
name|AbstractMetric
name|metric
init|=
operator|new
name|AbstractMetric
argument_list|(
name|KafkaMetricsInfo
operator|.
name|KafkaCounter
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Number
name|value
parameter_list|()
block|{
return|return
operator|new
name|Integer
argument_list|(
literal|123
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MetricType
name|type
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
name|MetricsVisitor
name|visitor
parameter_list|)
block|{        }
block|}
decl_stmt|;
comment|// Create a list of metrics.
name|Iterable
argument_list|<
name|AbstractMetric
argument_list|>
name|metrics
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|metric
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|record
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"Kafka record name"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|record
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
name|SubsetConfiguration
name|conf
init|=
name|mock
argument_list|(
name|SubsetConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|conf
operator|.
name|getString
argument_list|(
name|KafkaSink
operator|.
name|BROKER_LIST
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"localhost:9092"
argument_list|)
expr_stmt|;
name|String
name|topic
init|=
literal|"myTestKafkaTopic"
decl_stmt|;
name|when
argument_list|(
name|conf
operator|.
name|getString
argument_list|(
name|KafkaSink
operator|.
name|TOPIC
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|topic
argument_list|)
expr_stmt|;
comment|// Create the KafkaSink object and initialize it.
name|kafkaSink
operator|=
operator|new
name|KafkaSink
argument_list|()
expr_stmt|;
name|kafkaSink
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Create a mock KafkaProducer as a producer for KafkaSink.
name|Producer
argument_list|<
name|Integer
argument_list|,
name|byte
index|[]
argument_list|>
name|mockProducer
init|=
name|mock
argument_list|(
name|KafkaProducer
operator|.
name|class
argument_list|)
decl_stmt|;
name|kafkaSink
operator|.
name|setProducer
argument_list|(
name|mockProducer
argument_list|)
expr_stmt|;
comment|// Create the json object from the record.
name|StringBuilder
name|jsonLines
init|=
name|recordToJson
argument_list|(
name|record
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"kafka message: "
operator|+
name|jsonLines
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Send the record and store the result in a mock Future.
name|Future
argument_list|<
name|RecordMetadata
argument_list|>
name|f
init|=
name|mock
argument_list|(
name|Future
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|mockProducer
operator|.
name|send
argument_list|(
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|kafkaSink
operator|.
name|putMetrics
argument_list|(
name|record
argument_list|)
expr_stmt|;
comment|// Get the argument and verity it.
name|ArgumentCaptor
argument_list|<
name|ProducerRecord
argument_list|>
name|argument
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|ProducerRecord
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mockProducer
argument_list|)
operator|.
name|send
argument_list|(
name|argument
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
comment|// Compare the received data with the original one.
name|ProducerRecord
argument_list|<
name|Integer
argument_list|,
name|byte
index|[]
argument_list|>
name|data
init|=
operator|(
name|argument
operator|.
name|getValue
argument_list|()
operator|)
decl_stmt|;
name|String
name|jsonResult
init|=
operator|new
name|String
argument_list|(
name|data
operator|.
name|value
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"kafka result: "
operator|+
name|jsonResult
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|jsonLines
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|jsonResult
argument_list|)
expr_stmt|;
block|}
DECL|method|recordToJson (MetricsRecord record)
name|StringBuilder
name|recordToJson
parameter_list|(
name|MetricsRecord
name|record
parameter_list|)
block|{
comment|// Create a json object from a metrics record.
name|StringBuilder
name|jsonLines
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Long
name|timestamp
init|=
name|record
operator|.
name|timestamp
argument_list|()
decl_stmt|;
name|Date
name|currDate
init|=
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
decl_stmt|;
name|SimpleDateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd"
argument_list|)
decl_stmt|;
name|String
name|date
init|=
name|dateFormat
operator|.
name|format
argument_list|(
name|currDate
argument_list|)
decl_stmt|;
name|SimpleDateFormat
name|timeFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"HH:mm:ss"
argument_list|)
decl_stmt|;
name|String
name|time
init|=
name|timeFormat
operator|.
name|format
argument_list|(
name|currDate
argument_list|)
decl_stmt|;
name|String
name|hostname
init|=
operator|new
name|String
argument_list|(
literal|"null"
argument_list|)
decl_stmt|;
try|try
block|{
name|hostname
operator|=
name|InetAddress
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error getting Hostname, going to continue"
argument_list|)
expr_stmt|;
block|}
name|jsonLines
operator|.
name|append
argument_list|(
literal|"{\"hostname\": \""
operator|+
name|hostname
argument_list|)
expr_stmt|;
name|jsonLines
operator|.
name|append
argument_list|(
literal|"\", \"timestamp\": "
operator|+
name|timestamp
argument_list|)
expr_stmt|;
name|jsonLines
operator|.
name|append
argument_list|(
literal|", \"date\": \""
operator|+
name|date
argument_list|)
expr_stmt|;
name|jsonLines
operator|.
name|append
argument_list|(
literal|"\",\"time\": \""
operator|+
name|time
argument_list|)
expr_stmt|;
name|jsonLines
operator|.
name|append
argument_list|(
literal|"\",\"name\": \""
operator|+
name|record
operator|.
name|name
argument_list|()
operator|+
literal|"\" "
argument_list|)
expr_stmt|;
for|for
control|(
name|MetricsTag
name|tag
range|:
name|record
operator|.
name|tags
argument_list|()
control|)
block|{
name|jsonLines
operator|.
name|append
argument_list|(
literal|", \""
operator|+
name|tag
operator|.
name|name
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\p{Cc}]"
argument_list|,
literal|""
argument_list|)
operator|+
literal|"\": "
argument_list|)
expr_stmt|;
name|jsonLines
operator|.
name|append
argument_list|(
literal|" \""
operator|+
name|tag
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AbstractMetric
name|m
range|:
name|record
operator|.
name|metrics
argument_list|()
control|)
block|{
name|jsonLines
operator|.
name|append
argument_list|(
literal|", \""
operator|+
name|m
operator|.
name|name
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\p{Cc}]"
argument_list|,
literal|""
argument_list|)
operator|+
literal|"\": "
argument_list|)
expr_stmt|;
name|jsonLines
operator|.
name|append
argument_list|(
literal|" \""
operator|+
name|m
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|jsonLines
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|jsonLines
return|;
block|}
block|}
end_class

end_unit

